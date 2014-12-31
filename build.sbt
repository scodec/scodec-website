import com.typesafe.sbt.SbtGit.GitKeys

organization := "org.typelevel"
name := "scodec-website"

site.settings
site.pamfletSupport()

ghpages.settings
git.remoteRepo := "git@github.com:scodec/scodec.github.io.git"
GitKeys.gitBranch in GhPagesKeys.updatedRepository := Some("master")
