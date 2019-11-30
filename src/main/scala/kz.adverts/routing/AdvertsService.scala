package kz.adverts.routing

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import kz.adverts.repo.AdvertsRepository

trait  RestRoutes {
    def route: Route
}

object AdvertsService {
    def props(repo: AdvertsRepository) = new AdvertsService(repo)
}

class AdvertsService(repo: AdvertsRepository)  extends RestRoutes with Directives {

    val route =
      path("adverts") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>adverts on akka-http</h1>"))
        }
      }

}