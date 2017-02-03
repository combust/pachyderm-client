package ml.combust.pachyderm.client

import org.scalatest.FunSpec
import org.scalatest.concurrent.ScalaFutures
import Implicits._

import scala.concurrent.duration._

/**
  * Created by hollinwilkins on 2/3/17.
  */
class ClientSpec extends FunSpec
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
}
