package kz.adverts.routing

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

trait  RestRoutes {
    val route =
      path("adverts") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>adverts on akka-http</h1>"))
        }
      }
}