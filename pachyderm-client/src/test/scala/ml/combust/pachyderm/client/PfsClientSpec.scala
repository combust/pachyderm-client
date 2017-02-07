package ml.combust.pachyderm.client

import java.io.ByteArrayOutputStream
import java.nio.file.{FileSystems, Files}

import org.scalatest.FunSpec
import org.scalatest.concurrent.ScalaFutures
import Implicits._
import pfs.pfs.Delimiter

import scala.concurrent.duration._

/**
  * Created by hollinwilkins on 2/3/17.
  */
class PfsClientSpec extends FunSpec
  with ScalaFutures {
  val client = PfsClient(TestUtil.minikubeIp, TestUtil.port)
  val testRepo = "test_data"

  implicit val defaultPatience = PatienceConfig(timeout = 2.seconds, interval = 100.millis)

  describe("#createRepo") {
    it("creates a repository") {
      whenReady(client.deleteAll()) {
        _ => whenReady(client.createRepo(testRepo)) {
          _ => whenReady(client.inspectRepo(testRepo)) {
            info => assert(info.repo.get.name == testRepo)
          }
        }
      }
    }
  }

  describe("#deleteRepo") {
    it("deletes a repository") {
      whenReady(client.deleteAll()) {
        _ => whenReady(client.createRepo(testRepo)) {
          _ => whenReady(client.deleteRepo(testRepo)) {
            _ => whenReady(client.inspectRepo(testRepo).failed) {
              error => assert(error.getMessage.contains("repo test_data not found"))
            }
          }
        }
      }
    }
  }

  describe("#listRepo") {
    it("lists all of the repositories in Pachyderm") {
      whenReady(client.deleteAll()) {
        _ => whenReady(client.createRepo(testRepo)) {
          _ => whenReady(client.listRepo()) {
            repos => assert(repos.repoInfo.size == 1)
          }
        }
      }
    }
  }

  describe("#inspectRepo") {
    it("returns information about the repo") {
      whenReady(client.deleteAll()) {
        _ => whenReady(client.createRepo(testRepo)) {
          _ => whenReady(client.inspectRepo(testRepo)) {
            info => assert(info.repo.get.name == testRepo)
          }
        }
      }
    }
  }

  describe("creating a commit") {
    describe("finishing a commit") {
      it("sends over the files to commit then finishes it") {
        whenReady(client.deleteAll()) {
          _ => whenReady(client.createRepo(testRepo)) {
            _ => whenReady(client.startCommit(testRepo, "master")) {
              commit =>
                val file = new java.io.File("pachyderm-client/src/test/resources/commit1/test.txt")
                whenReady(client.putLocalFile(commit,
                  "test.txt",
                  file,
                  Delimiter.LINE)) {
                  _ => whenReady(client.finishCommit(commit)) {
                    _ => whenReady(client.inspectCommit(commit)) {
                      info =>
                        assert(info.branch == "master")
                        assert(info.sizeBytes == file.length())
                    }
                  }
                }
            }
          }
        }
      }
    }

    describe("forking a commit") {
      it("forks the commit and adds a file") {
        whenReady(client.deleteAll()) {
          _ => whenReady(client.createRepo(testRepo)) {
            _ => whenReady(client.startCommit(testRepo, "master")) {
              commit =>
                val file = new java.io.File("pachyderm-client/src/test/resources/commit1/test.txt")
                whenReady(client.putLocalFile(commit,
                  "test.txt",
                  file,
                  Delimiter.LINE)) {
                  _ => whenReady(client.finishCommit(commit)) {
                    _ => whenReady(client.forkCommit(testRepo, commit.id, "a_branch")) {
                      forkCommit => whenReady(client.putLocalFile(forkCommit,
                        "test2.txt",
                        file,
                        Delimiter.LINE)) {
                        _ => whenReady(client.finishCommit(forkCommit)) {
                          _ =>
                            whenReady(client.inspectCommit(forkCommit)) {
                              info =>
                                assert(info.branch == "a_branch")
                                assert(info.sizeBytes == file.length())
                                assert(info.parentCommit.contains(commit))
                            }
                        }
                      }
                    }
                  }
                }
            }
          }
        }
      }
    }

    describe("getting a file from a commit") {
      it("gets the contents of the file from the commit") {
        whenReady(client.deleteAll()) {
          _ => whenReady(client.createRepo(testRepo)) {
            _ => whenReady(client.startCommit(testRepo, "master")) {
              commit =>
                val file = new java.io.File("pachyderm-client/src/test/resources/commit1/test.txt")
                whenReady(client.putLocalFile(commit,
                  "test.txt",
                  file,
                  Delimiter.LINE)) {
                  _ => whenReady(client.finishCommit(commit)) {
                    _ =>
                      whenReady(client.inspectCommit(commit)) {
                        info =>
                          val out = new ByteArrayOutputStream()
                          whenReady(client.getFullFile(testRepo, "master", "test.txt", out)) {
                            _ =>
                              assert(out.toByteArray sameElements Files.readAllBytes(FileSystems.getDefault.getPath(file.getAbsolutePath)))
                          }
                      }
                  }
                }
            }
          }
        }
      }
    }
  }
}
