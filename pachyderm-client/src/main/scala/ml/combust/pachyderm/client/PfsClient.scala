package ml.combust.pachyderm.client

import java.io.OutputStream
import java.nio.file.{FileSystems, Files}

import com.google.protobuf.ByteString
import pfs.pfs.APIGrpc.APIStub
import pfs.pfs._
import com.google.protobuf.empty.Empty
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver

import scala.concurrent.{Future, Promise}

/**
  * Created by hollinwilkins on 2/3/17.
  */
object PfsClient {
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

class PfsClient(channel: _root_.io.grpc.Channel,
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

  def startCommit(repo: Repo,
                  parentId: String,
                  provenance: Seq[Commit] = Seq()): Future[Commit] = {
    startCommit(StartCommitRequest(parent = Some(Commit(Some(repo), parentId)), provenance = provenance))
  }

  def finishCommit(commit: Commit, cancel: Boolean = false): Future[Empty] = {
    finishCommit(FinishCommitRequest(commit = Some(commit),
      cancel = cancel))
  }

  def flushCommit(commits: Seq[Commit], toRepo: Seq[Repo] = Seq()): Future[CommitInfos] = {
    flushCommit(FlushCommitRequest(commit = commits, toRepo = toRepo))
  }

  def archiveCommit(commits: Seq[Commit] = Seq()): Future[Empty] = {
    archiveCommit(ArchiveCommitRequest(commits = commits))
  }

  def deleteCommit(commit: Commit): Future[Empty] = {
    deleteCommit(DeleteCommitRequest(commit = Some(commit)))
  }

  def forkCommit(repo: Repo,
                 parentId: String,
                 branch: String,
                 provenance: Seq[Commit] = Seq()): Future[Commit] = {
    forkCommit(ForkCommitRequest(parent = Some(Commit(Some(repo), parentId)),
      branch = branch,
      provenance = provenance))
  }

  def inspectCommit(commit: Commit): Future[CommitInfo] = {
    inspectCommit(InspectCommitRequest(commit = Some(commit)))
  }

  def listCommit(exclude: Seq[Commit] = Seq(),
                 include: Seq[Commit] = Seq(),
                 provenance: Seq[Commit] = Seq(),
                 commitType: CommitType = CommitType.COMMIT_TYPE_NONE,
                 status: CommitStatus = CommitStatus.ALL,
                 block: Boolean = false): Future[CommitInfos] = {
    listCommit(ListCommitRequest(exclude = exclude,
      include = include,
      provenance = provenance,
      commitType = commitType,
      status = status,
      block = block))
  }

  def replayCommit(fromCommits: Seq[Commit],
                   toBranch: String): Future[Commits] = {
    replayCommit(ReplayCommitRequest(fromCommits = fromCommits, toBranch = toBranch))
  }

  def squashCommit(fromCommits: Seq[Commit],
                   toCommit: Commit): Future[Empty] = {
    squashCommit(SquashCommitRequest(fromCommits = fromCommits,
      toCommit = Some(toCommit)))
  }

  def putLocalFile(commit: Commit,
                   path: String,
                   file: java.io.File,
                   delimiter: Delimiter = Delimiter.NONE): Future[Empty] = {
    putFile(File(Some(commit), path),
      FileType.FILE_TYPE_REGULAR,
      value = Some(Files.readAllBytes(FileSystems.getDefault.getPath(file.getPath))),
      recursive = false)
  }

  def putFileUrl(commit: Commit,
                 path: String,
                 url: String,
                 delimiter: Delimiter = Delimiter.NONE,
                 recursive: Boolean = false): Future[Empty] = {
    putFile(File(commit = Some(commit), path = path),
      FileType.FILE_TYPE_REGULAR,
      url = Some(url),
      recursive = recursive)
  }

  def putFile(file: File,
              fileType: FileType,
              value: Option[Array[Byte]] = None,
              url: Option[String] = None,
              delimiter: Delimiter = Delimiter.NONE,
              recursive: Boolean = false): Future[Empty] = {
    val promise = Promise[Empty]()
    val responseObserver = new StreamObserver[Empty] {
      override def onError(t: Throwable): Unit = promise.failure(t)
      override def onCompleted(): Unit = promise.success(Empty.defaultInstance)
      override def onNext(value: Empty): Unit = { /* do nothing */ }
    }

    val bytes = value.map(ByteString.copyFrom).getOrElse(ByteString.EMPTY)
    val observer = putFile(responseObserver)
    observer.onNext(PutFileRequest(file = Some(file),
      fileType = fileType,
      value = bytes,
      delimiter = delimiter,
      url = url.getOrElse(""),
      recursive = recursive))
    observer.onCompleted()

    promise.future
  }

  def getFullFile(repo: Repo,
                  commitId: String,
                  path: String,
                  out: OutputStream): Future[Empty] = {
    getFile(commit = Commit(Some(repo), commitId),
      path = path,
      out = out)
  }

  def getFile(commit: Commit,
              path: String,
              offsetBytes: Long = 0,
              sizeBytes: Long = 0,
              shard: Option[Shard] = None,
              fromCommitId: Option[String] = None,
              fullFile: Boolean = true,
              out: OutputStream): Future[Empty] = {
    val promise = Promise[Empty]()
    val responseObserver = new StreamObserver[com.google.protobuf.wrappers.BytesValue] {
      override def onError(t: Throwable): Unit = promise.failure(t)
      override def onCompleted(): Unit = promise.success(Empty.defaultInstance)
      override def onNext(value: com.google.protobuf.wrappers.BytesValue): Unit = {
        out.write(value.value.toByteArray)
      }
    }

    getFile(GetFileRequest(file = Some(File(Some(commit), path)),
      offsetBytes = offsetBytes,
      sizeBytes = sizeBytes,
      shard = shard,
      diffMethod = Some(DiffMethod(fromCommitId.map(fci => Commit(commit.repo, fci)),
        fullFile = fullFile))), responseObserver)

    promise.future
  }

  def inspectFile(commit: Commit,
                  path: String,
                  shard: Option[Shard] = None,
                  fromCommitId: Option[String] = None,
                  fullFile: Boolean = true): Future[FileInfo] = {
    inspectFile(InspectFileRequest(file = Some(File(Some(commit), path)),
      shard = shard,
      diffMethod = Some(DiffMethod(Some(Commit(commit.repo, fromCommitId.getOrElse(""))),
        fullFile = fullFile))))
  }

  def deleteFile(commit: Commit,
                 path: String): Future[Empty] = {
    deleteFile(DeleteFileRequest(file = Some(File(Some(commit), path))))
  }

  def listFile(repo: Repo,
               commitId: String,
               path: String,
               shard: Option[Shard] = None,
               fromCommitId: Option[String] = None,
               fullFile: Boolean = true,
               mode: ListFileMode = ListFileMode.ListFile_NORMAL): Future[FileInfos] = {
    listFile(ListFileRequest(file = Some(File(Some(Commit(Some(repo), commitId)), path)),
      shard = shard,
      diffMethod = Some(DiffMethod(fromCommitId.map(fci => Commit(Some(repo), fci)),
        fullFile = fullFile)),
      mode = mode))
  }

  def deleteAll(): Future[Empty] = {
    deleteAll(Empty.defaultInstance)
  }

  def archiveAll(): Future[Empty] = {
    archiveAll(Empty.defaultInstance)
  }
}
