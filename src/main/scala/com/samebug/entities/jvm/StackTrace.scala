package com.samebug.entities.jvm

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.samebug.entities.jvm.StackTrace.Frame

trait StackTrace {
  def typeName: String
  def message: Option[String]
  def cause: Option[StackTrace]
  def frames: Seq[StackTrace.Frame]
  def root: StackTrace
  def causeChain: Seq[StackTrace]

  def stackTrace: String = {
    val tracelines = frames map { frame => s"\tat $frame" } mkString "\n"
    s"$typeNameAndMessage\n$tracelines${cause map { c => s"\n${StackTrace.stacktraceAsCause(c, Some(this))}" } getOrElse ""}"
  }

  protected def typeNameAndMessage: String = typeName + (message map { m => s": $m" } getOrElse "")

  final override def equals(other: Any): Boolean = other match {
    case that: StackTrace => this.typeName == that.typeName && this.message == that.message && this.frames == that.frames && this.cause == that.cause
    case _ => false
  }

  def selfFrames(catcher: Option[StackTrace]): (Seq[Frame], Option[Int]) = catcher match {
    case Some(parent) =>
      val canonizedThis = this.frames.reverse map {Some(_)}
      val canonizedThat = parent.frames.reverse map {Some(_)}
      val (common, self) = canonizedThis.zipAll(canonizedThat, None, None) takeWhile {_._1.isDefined} span { case (thisFrame, thatFrame) => thisFrame == thatFrame }
      val selfFrames = self.unzip._1.reverse map {_.get}
      val commonFrames = common.length

      (selfFrames, Some(commonFrames))
    case None =>
      (frames, None)
  }

  final override def hashCode: Int = 31 * (typeName.hashCode + 31 * (message.hashCode + 31 * (frames.hashCode + 31 * cause.hashCode)))
}

object StackTrace {
  protected def stacktraceAsCause(exception: StackTrace, catcher: Option[StackTrace]): String = {
    val (selfFrames, commonFrames) = exception.selfFrames(catcher)

    val tr = if (selfFrames.isEmpty) "" else "\n" + (selfFrames map { frame => s"\tat $frame" } mkString "\n")
    val more = commonFrames match {
      case Some(0) | None => ""
      case Some(n) => s"\t... $n more"
    }
    s"Caused by: ${exception.typeNameAndMessage}$tr\n$more${exception.cause map { c => s"\n${stacktraceAsCause(c, Some(exception))}" } getOrElse ""}"
  }

  import StackTrace.Frame._

  case class Frame(call: QualifiedCall, location: Location, jarName: Option[String]) {
    override def toString = s"$call($location)$jarLocation"

    def qualifiedMethodName: String = call.toString

    def jarLocation: String = jarName match {
      case Some(jar) => s"[$jar]"
      case None => ""
    }

    def packageName: Option[String] = call.packageName

    def className: String = call.className

    def methodName: String = call.methodName

    /**
      *
      * @return the qualified name of the method's call for generated and non-generated classes.
      *
      *         In case of runtime generated proxies the runtime class has a runtime generated name. This method
      *
      */
    lazy val canonicalCall: QualifiedCall = (call.packageName, location.locationType, call.className) match {
      case (_, LocationType.Generated, GeneratedClass(_, _, _)) => QualifiedCall(call.packageName, call.className.replaceAll(generated("\\$\\$"), ""), call.methodName)
      case (_, LocationType.Generated, GeneratedClassSingleDollar(_, _, _)) => QualifiedCall(call.packageName, call.className.replaceAll(generated("\\$"), ""), call.methodName)
      case (_, LocationType.Generated, GeneratedClassUnderline(_, _, _)) => QualifiedCall(call.packageName, call.className.replaceAll(generated("_"), ""), call.methodName)
      case (Some("com.sun.proxy"), LocationType.UnknownSource, Proxy(id)) => QualifiedCall(call.packageName, "$Proxy", call.methodName)
      case (Some("com.amazonaws.http.conn"), LocationType.UnknownSource, Proxy(id)) => QualifiedCall(call.packageName, "$Proxy", call.methodName)
      case (Some("sun.reflect"), LocationType.UnknownSource, GeneratedMethodAccessor(id)) => QualifiedCall(call.packageName, "GeneratedMethodAccessor", call.methodName)
      case _ => call
    }
  }

  case class Problem(call: QualifiedCall, exceptionType: String)

  object Frame {

    case class QualifiedCall(
      @JsonInclude(Include.NON_EMPTY)
      packageName: Option[String], className: String, methodName: String) {

      import StackTrace.Frame.MethodType._

      def qualifiedClassName = s"$packagePrefix$className"

      def methodType: MethodType.Value = methodName match {
        case "<init>" => Constructor
        case "<clinit>" => StaticInit
        case _ => Method
      }

      def method: Option[String] = methodType match {
        case Method => Some(methodName)
        case _ => None
      }

      private def packagePrefix = packageName map { pn => s"$pn." } getOrElse ""

      override def toString = s"$qualifiedClassName.$methodName"
    }

    object QualifiedCall {

      import StackTrace.Frame.MethodType._

      def apply(packageList: Seq[String], className: String, methodType: MethodType.Value, methodName: Option[String]): QualifiedCall = apply(
        if (packageList.isEmpty) None else Some(packageList mkString "."),
        className,
        methodType,
        methodName
      )

      def apply(packageName: Option[String], className: String, methodType: MethodType.Value, methodName: Option[String]): QualifiedCall = {
        def method = methodType match {
          case Constructor => "<init>"
          case StaticInit => "<clinit>"
          case Method => methodName.get
        }

        apply(packageName, className, method)
      }
    }

    object MethodType extends Enumeration {
      type MethodType = Value
      val Method, Constructor, StaticInit = Value
    }

    case class Location(locationType: LocationType.Value, fileName: Option[String], lineNumber: Option[Int]) {

      import StackTrace.Frame.LocationType._

      override def toString: String = locationType match {
        case NativeMethod => "Native Method"
        case Generated => "<generated>"
        case UnknownSource => "Unknown Source"
        case Console => "<console>" + (lineNumber map (":" + _) getOrElse "")
        case File if fileName.nonEmpty && lineNumber.nonEmpty => s"${fileName.get}:${lineNumber.get}"
        case File if fileName.nonEmpty => s"${fileName.get}"
      }
    }

    object LocationType extends Enumeration {
      type LocationType = Value
      val UnknownSource, NativeMethod, Generated, Console, File = Value
    }

    def locationIgnorantEquals(f1: Frame, f2: Frame): Boolean = f1.call == f2.call

    private def generated(delimiter: String) = s"$delimiter((?:Enhancer|FastClass|KeyFactory)By(?:CGLIB|SpringCGLIB|Guice|MUNIT|MockitoWithCGLIB|Proxool|CloudStack|ModelMapper))$delimiter([a-f0-9]{1,8})"

    private val Proxy = "\\$Proxy(\\d+)".r
    private val GeneratedMethodAccessor = "GeneratedMethodAccessor(\\d+)".r
    private val GeneratedClass = s"(.*)${generated("\\$\\$")}".r
    private val GeneratedClassSingleDollar = s"(.*)${generated("\\$")}".r
    private val GeneratedClassUnderline = s"(.*)${generated("_")}".r
  }

}
