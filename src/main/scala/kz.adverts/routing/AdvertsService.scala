package kz.adverts.routing

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import kz.adverts.repo.{AdvertsRepository,CreateAdvert,NewCar,UsedCar}
import org.json4s.{ DefaultFormats, native}
import akka.http.scaladsl.unmarshalling.Unmarshal
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import kz.adverts.repo.UpdateAdvert


trait  RestRoutes {
    def route: Route
}

object AdvertsService {
    def props(repo: AdvertsRepository) = new AdvertsService(repo)
}

trait JsonSupport extends Json4sSupport {
  implicit val serialization = org.json4s.native.Serialization
  implicit val json4sFormats = org.json4s.DefaultFormats
}

class AdvertsService(repo: AdvertsRepository)  extends RestRoutes with Directives with ApiDirectives with JsonSupport {

    val route = pathPrefix("v1"/"adverts") {
      pathEndOrSingleSlash {
        get {
          handleWithGeneric(repo.all()) { items =>
            complete(items)
          }
        } ~ post {
          entity(as[CreateAdvert]) { createAd =>
            handleWithGeneric(repo.save(createAd)) { ad =>
              complete(StatusCodes.Created -> ad)
            }
          }
        } 
      } ~ path(Segment) { id: String =>
        put {
          entity(as[UpdateAdvert]) { updateAd =>
              handle(repo.update(id, updateAd)) {
                case AdvertsRepository.AdvertNotFound(_) =>
                  ApiError.advertNotFound(id)
                case _ =>
                  ApiError.generic
              } { ad =>
                complete(ad)
              }
          }
        } ~ get {
          handle(repo.get(id)) {
            case AdvertsRepository.AdvertNotFound(_) =>
              ApiError.advertNotFound(id)
            case _ =>
              ApiError.generic
          } { item =>
            complete(item)
          }
        }
      } 

    }      

}