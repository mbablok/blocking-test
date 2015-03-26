import akka.actor.{ActorRef, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

object Boot extends App {

  val waitingTime = 50

  implicit val system = ActorSystem("on-spray-can")
  val service = system.actorOf(Props[ServiceActor], "test-service")
  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}


import akka.actor.Actor
import spray.http.MediaTypes._
import spray.routing._

class ServiceActor extends Actor with TestService {
  def actorRefFactory = context
  def receive = runRoute(routes)
}


trait TestService extends HttpService {

  val routes =
    path("sleep") {
      get {
        respondWithMediaType(`text/plain`) {
          onComplete(Future.successful {
            Thread.sleep(Boot.waitingTime)
            "sleeping"
          }) {
            case a =>
              complete(a)
          }
        }
      }
    } ~
      path("akka") {
        get {
          respondWithMediaType(`text/plain`) {
            onComplete {
              import akka.pattern.ask

              import scala.concurrent.duration._
              implicit val timeout = Timeout(15.seconds)
              val response = "akka"
              actorRefFactory.actorOf(Props[WaitingActor]).ask(response).map(_.asInstanceOf[String])
            } {
              case a =>
                complete(a)
            }
          }
        }
      }
}

class WaitingActor extends Actor {
  val random = new Random()
  var receiver: ActorRef = null

  override def receive: Receive = {
    case a =>
      receiver = sender()
      import scala.concurrent.duration._
      context.system.scheduler.scheduleOnce(Boot.waitingTime.milliseconds) {
        receiver ! a
      }

  }
}

