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
