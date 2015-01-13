import com.typesafe.sbt.git.GitRunner
import com.typesafe.sbt.SbtGit.GitKeys
import com.typesafe.sbt.SbtSite.SiteKeys

organization := "org.typelevel"
name := "scodec-website"

site.settings
site.pamfletSupport()
SiteKeys.siteMappings ++= Seq(sourceDirectory.value / "pamflet" / "CNAME" -> "CNAME")

ghpages.settings
git.remoteRepo := "git@github.com:scodec/scodec.github.io.git"
GitKeys.gitBranch in GhPagesKeys.updatedRepository := Some("master")
GhPagesKeys.synchLocal := {

  // From https://github.com/sbt/website/blob/4ff41b9ad8b9a3613e559429555689090cb9fa29/project/Docs.scala
  def gitRemoveFiles(dir: File, files: List[File], git: GitRunner, s: TaskStreams): Unit = {
    if (files.nonEmpty)
      git(("rm" :: "-r" :: "-f" :: "--ignore-unmatch" :: files.map(_.getAbsolutePath)) :_*)(dir, s.log)
  }

  // Adapted from https://github.com/sbt/website/blob/4ff41b9ad8b9a3613e559429555689090cb9fa29/project/Docs.scala
  val repo = GhPagesKeys.updatedRepository.value
  val git = GitKeys.gitRunner.value

  val nonApiFiles = (repo * ("*" -- "api")).get.toList
  gitRemoveFiles(repo, nonApiFiles, git, streams.value)

  val mappings =  for {
    (file, target) <- SiteKeys.siteMappings.value
  } yield (file, repo / target)
  IO.copy(mappings)

  repo
}
