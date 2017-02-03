package ml.combust.pachyderm.client

import com.google.protobuf.empty.Empty
import io.grpc.ManagedChannelBuilder
import pps.pps.APIGrpc.APIStub
import pps.pps._

import scala.concurrent.Future

/**
  * Created by hollinwilkins on 2/3/17.
  */
object PpsClient {
  def apply(host: String,
            port: Int): PfsClient = {
    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build()
    apply(channel)
  }

  def apply(channel: _root_.io.grpc.Channel,
            options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT): PfsClient = {
    new PfsClient(channel, options)
  }
}

class PpsClient(channel: _root_.io.grpc.Channel,
                options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends APIStub(channel, options) {
  def createPipeline(pipeline: Pipeline,
                     transform: Transform,
                     parallelismSpec: Option[ParallelismSpec] = None,
                     inputs: Seq[PipelineInput] = Seq(),
                     output: Option[Output] = None,
                     gcPolicy: Option[GCPolicy] = None,
                     update: Boolean = false,
                     noArchive: Boolean = false): Future[Empty] = {
    createPipeline(CreatePipelineRequest(pipeline = Some(pipeline),
      transform = Some(transform),
      parallelismSpec = parallelismSpec,
      inputs = inputs,
      output = output,
      gcPolicy = gcPolicy,
      update = update,
      noArchive = noArchive))
  }

  def inspectPipeline(pipeline: Pipeline): Future[PipelineInfo] = {
    inspectPipeline(InspectPipelineRequest(pipeline = Some(pipeline)))
  }

  def listPipeline(): Future[PipelineInfos] = {
    listPipeline(ListPipelineRequest())
  }

  def deletePipeline(pipeline: Pipeline): Future[Empty] = {
    deletePipeline(DeletePipelineRequest(pipeline = Some(pipeline)))
  }

  def startPipeline(pipeline: Pipeline): Future[Empty] = {
    startPipeline(StartPipelineRequest(pipeline = Some(pipeline)))
  }

  def stopPipeline(pipeline: Pipeline): Future[Empty] = {
    stopPipeline(StopPipelineRequest(pipeline = Some(pipeline)))
  }

  def deleteAll(): Future[Empty] = {
    deleteAll(Empty.defaultInstance)
  }
}
