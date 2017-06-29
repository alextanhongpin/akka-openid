package com.alextanhongpin.app


import akka.http.scaladsl.Http

import scala.io.StdIn
import scala.util.{Failure, Success}

// Import all services
import com.alextanhongpin.service._

// https://spindance.com/reactive-rest-services-akka-http/
object AkkaHttpHelloWorld extends HealthService {
    val host = "localhost"
    val port = 8080

    def main(args: Array[String]): Unit = {
        val bindingFuture = Http().bindAndHandle(route, host, port)
        println("listening to port *:8080. press RETURN to cancel")
        StdIn.readLine() // let it run until user presses return

        // Shutdown
        bindingFuture
            .flatMap(_.unbind()) // Trigger unbinding from the port
            .onComplete(_ => system.terminate()) // and shutdown when done
    }
}
 