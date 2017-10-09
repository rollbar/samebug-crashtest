package com.samebug.entities.jvm

import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer, JsonSerializer, SerializerProvider}
import com.samebug.entities.jvm.StackTrace.Frame

@JsonTypeName("Exception")
@JsonInclude(JsonInclude.Include.NON_NULL)
case class NormalizedStackTrace(
  @JsonProperty("typeName")
  typeName: String,

  @JsonProperty("message")
  message: Option[String],

  @JsonProperty("cause")
  cause: Option[NormalizedStackTrace],

  @JsonProperty("common")
  commonFrames: Option[Int],

  @JsonProperty("frames")
  @JsonSerialize(using = classOf[NormalizedStackTrace.FramesSerializer])
  @JsonDeserialize(using = classOf[NormalizedStackTrace.FramesDeserializer])
  selfTrace: Seq[StackTrace.Frame]) extends StackTrace {

  def root: NormalizedStackTrace = cause map {_.root} getOrElse this

  def frames = cause match {
    case None => selfTrace
    case Some(c) => selfTrace ++ c.frames.takeRight(commonFrames.getOrElse(0))
  }

  def causeChain: Seq[NormalizedStackTrace] = this +: (cause match {
    case None => Stream.empty[NormalizedStackTrace]
    case Some(c) => c.causeChain
  })
}

object NormalizedStackTrace {
  private val FrameRegex = """([^\(]*)\(([^\)]*)\)(.*)""".r
  private val LocationRegex = """([^:]*):?(-?\d+)?""".r
  private val JarRegex = """\[(.*)\]""".r

  def serializeFrame(frame: Frame): String = frame.toString

  def deserializeFrame(str: String): Frame = {
    str match {
      case FrameRegex(callStr, locationStr, jarStr) =>
        val call = {
          val callParts = callStr.split('.')
          val pkg = callParts.dropRight(2).mkString(".")
          val packageName = if (pkg.isEmpty) None else Some(pkg)
          val className = callParts(callParts.length - 2)
          Frame.QualifiedCall(packageName, className, callParts.last)
        }
        val location = {
          val (locationType, fileName, lineNumber) = locationStr match {
            case LocationRegex(fn, ln) =>
              (fn, Option(ln) map {_.toInt}) match {
                case ("Unknown Source", _) => (Frame.LocationType.UnknownSource, None, None)
                case ("Native Method", _) => (Frame.LocationType.NativeMethod, None, None)
                case ("<generated>", _) => (Frame.LocationType.Generated, None, None)
                case ("<console>", ln: Option[Int]) => (Frame.LocationType.Console, None, ln)
                case (fn: String, ln: Option[Int]) => (Frame.LocationType.File, Some(fn), ln)
              }
          }
          Frame.Location(locationType, fileName, lineNumber)
        }
        val jarName = {
          Option(jarStr) map {_.trim} filter {_.nonEmpty} map { jar: String =>
            jar match {
              case JarRegex(jarName: String) => jarName
            }
          }
        }
        Frame(call, location, jarName)
    }
  }

  class FramesSerializer extends JsonSerializer[Seq[StackTrace.Frame]] {
    def serialize(frames: Seq[StackTrace.Frame], gen: JsonGenerator, provider: SerializerProvider): Unit = {
      val fs = frames map serializeFrame mkString "\n"
      provider.defaultSerializeValue(fs, gen)
    }
  }

  class FramesDeserializer extends JsonDeserializer[Seq[StackTrace.Frame]] {
    def deserialize(p: JsonParser, ctxt: DeserializationContext): Seq[StackTrace.Frame] = {
      p.getValueAsString.split("\n").filter{_.nonEmpty}.par.map(deserializeFrame).seq
    }
  }

}
