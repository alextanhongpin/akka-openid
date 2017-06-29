// Main.scala is the main for our application. It handles routing and initialization of our services.

package com.alextanhongpin

import akka.http.scaladsl.Http

import scala.io.StdIn
import scala.util.{Failure, Success}

import com.alextanhongpin.service._
import com.alextanhongpin.app._

// https://spindance.com/reactive-rest-services-akka-http/
// FooRouter.scala

// object FooRouter {
//   val route = path("foo") {
//       complete {
//          Ok -> "foo"
//       } 
//   }       
// }

// object MainRouter {
//    val routes = FooRouter.route ~ BarRouter.route
// }

object AkkaHttpHelloWorld extends HealthService {
    val host = "localhost"
    val port = 8080
    val routes = route

    def main(args: Array[String]): Unit = {
        val bindingFuture = Http().bindAndHandle(routes, host, port)
        println("listening to port *:8080. press RETURN to cancel")
        StdIn.readLine() // let it run until user presses return

        // Shutdown
        bindingFuture
            .flatMap(_.unbind()) // Trigger unbinding from the port
            .onComplete(_ => system.terminate()) // and shutdown when done
    }
}
 