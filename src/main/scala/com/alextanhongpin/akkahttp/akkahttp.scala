package com.alextanhongpin.akkahttp

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._ // For the .seconds
import scala.io.StdIn

import com.alextanhongpin.route._
// import com.alextanhongpin.db._


trait HealthJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val healthFormat = jsonFormat2(Health)
}
trait HealthService extends HealthJsonSupport {
    // implicit val system: ActorSystem
    // implicit val materializer: ActorMaterializer
    // needed for the future flatMap/onComplete in the end
    // implicit val executionContext: ExecutionContextExecutor
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher
    val requestHandler = system.actorOf(RequestHandler.props(), "requestHandler")
    val route : Route = {
        // The ask method requires a timeout which is used to limit the amount of waiting time for the response
        path("health") {
            get {    
                implicit val timeout: Timeout = 5.seconds
                onSuccess(requestHandler ? GetHealthRequest) {
                    case response: HealthResponse =>
                        complete(StatusCodes.OK, s"Everything is ${response.health.status}")
                    case _ =>
                        complete(StatusCodes.InternalServerError)
                }
            }
            post {
                implicit val timeout: Timeout = 5.seconds
                entity(as[Health]) { statusReport => 
                    onSuccess(requestHandler ? SetStatusRequest(statusReport)) {
                        case response: HealthResponse =>
                            complete(StatusCodes.OK, s"Posted health as ${response.health.status}")   
                        case _ =>
                            complete(StatusCodes.InternalServerError)
                    }
                }
            }
        }
    }

}
// https://spindance.com/reactive-rest-services-akka-http/
object AkkaHttpHelloWorld extends App with HealthService {
    val host = "localhost"
    val port = 8080

    // def main(args: Array[String]): Unit = {
        // override implicit val system = ActorSystem("my-system")
        // override implicit val materializer = ActorMaterializer()
        // needed for the future flatMap/onComplete in the end
        // override implicit val executionContext = system.dispatcher
        // Startup, and listen for requests
        val bindingFuture = Http().bindAndHandle(route, host, port)
        println("listening to port *:8080. press RETURN to cancel")
        StdIn.readLine() // let it run until user presses return

        // Shutdown
        bindingFuture
            .flatMap(_.unbind()) // Trigger unbinding from the port
            .onComplete(_ => system.terminate()) // and shutdown when done
        // system.terminate()
    // }
}
 