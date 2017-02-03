package ml.combust.pachyderm.client

import pfs.pfs.Repo
import pps.pps.Pipeline

import scala.language.implicitConversions

/**
  * Created by hollinwilkins on 2/3/17.
  */
trait Implicits {
  implicit def pachydermStringToRepo(name: String): Repo = Repo(name = name)
  implicit def pachydermStringToPipeline(name: String): Pipeline = Pipeline(name = name)
}
object Implicits extends Implicits
