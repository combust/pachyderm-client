package ml.combust.pachyderm.client

import com.google.protobuf.empty.Empty

import scala.concurrent.{ExecutionContext, Future}
import sys.process._

/**
  * Created by hollinwilkins on 2/3/17.
  */
object TestUtil {
  lazy val minikubeIp: String = {
    Process("minikube ip").lineStream.head
  }

  def port: Int = 30650

  def clearRepos(client: Client)
                (implicit ec: ExecutionContext): Future[Any] = {
    client.listRepo().flatMap {
      infos =>
        val deletes = infos.repoInfo.map {
          info =>
            client.deleteRepo(info.repo.get)
        }

        Future.sequence(deletes)
    }
  }
}
