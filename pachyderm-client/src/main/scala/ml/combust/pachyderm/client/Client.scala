package ml.combust.pachyderm.client

import pfs.pfs.APIGrpc.APIStub
import pfs.pfs._
import Implicits._
import com.google.protobuf.empty.Empty
import io.grpc.ManagedChannelBuilder

import scala.concurrent.Future

/**
  * Created by hollinwilkins on 2/3/17.
  */
object Client {
  def apply(host: String,
            port: Int): Client = {
    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build()
    apply(channel)
  }

  def apply(channel: _root_.io.grpc.Channel,
            options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT): Client = {
    new Client(channel, options)
  }
}

class Client(channel: _root_.io.grpc.Channel,
             options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends APIStub(channel, options) {
  def listRepo(): Future[RepoInfos] = {
    listRepo(ListRepoRequest(Seq()))
  }

  def listRepo(repo: Repo, repos: Repo *): Future[RepoInfos] = {
    listRepo(ListRepoRequest(repo +: repos))
  }

  def createRepo(repo: Repo, provenance: Seq[Repo] = Seq()): Future[Empty] = {
    createRepo(CreateRepoRequest(repo = Some(repo), provenance = provenance))
  }

  def deleteRepo(repo: Repo): Future[Empty] = {
    deleteRepo(DeleteRepoRequest(repo = Some(repo)))
  }

  def inspectRepo(repo: Repo): Future[RepoInfo] = {
    inspectRepo(InspectRepoRequest(repo = Some(repo)))
  }
}
