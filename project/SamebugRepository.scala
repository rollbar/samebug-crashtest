import sbt._

object SamebugRepository {
  def publishTo(isSnapshot: Boolean) = if (isSnapshot) snapshots else releases

  val snapshots = "Samebug Snapshots" at s"$root/repository/snapshots"
  val releases = "Samebug Releases" at s"$root/repository/releases"
  val public = "Samebug Artifact Repository" at s"$root/repository/public/"

  val credentials = Credentials(Path.userHome / ".ivy2" / ".credentials")

  private lazy val root = "http://nexus.samebug.com"
}
