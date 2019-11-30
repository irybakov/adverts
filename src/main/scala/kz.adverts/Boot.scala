package kz.adverts

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.concurrent.Await
import kz.adverts.routing.RestRoutes
import scala.util.{Failure, Success, Try}
import scala.io.StdIn


object Boot extends App with RestRoutes {

    implicit val system = ActorSystem("adverts-system")
    implicit val materializer = ActorMaterializer()
    
    implicit val executionContext = system.dispatcher

    val host = "0.0.0.0"
    val port = 8080
    val bindingFuture = Http().bindAndHandle(route, host, port)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
      
    bindingFuture.onComplete {
        case Success(_) => println("Success!")
        case Failure(error) => println(s"Failed: ${error.getMessage}")
    }
    
    import scala.concurrent.duration._
    Await.result(bindingFuture, 3.seconds)  

}