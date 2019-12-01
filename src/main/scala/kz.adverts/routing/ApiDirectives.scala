package kz.adverts.routing
import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.{StatusCode, StatusCodes}

import akka.http.scaladsl.server.{Directive1, Directives}

trait ApiDirectives extends Directives {
  
    def handle[T]
    (f: Future[T])
    (e: Throwable => ApiError): Directive1[T] = onComplete(f) flatMap {
      case Success(t) =>
        provide(t)
      case Failure(error) =>
        val apiError = e(error)
        complete(apiError.statusCode, apiError.message)
    }
  
    def handleWithGeneric[T](f: Future[T]): Directive1[T] =
      handle[T](f)(_ => ApiError.generic)
  
  }


final case class ApiError private(statusCode: StatusCode, message: String)

object ApiError {
  private def apply(statusCode: StatusCode, message: String): ApiError = new ApiError(statusCode, message)

  val generic: ApiError = new ApiError(StatusCodes.InternalServerError, "Unknown error.")

  val emptyTitleField: ApiError = new ApiError(StatusCodes.BadRequest, "The title field must not be empty.")

  def advertNotFound(id: String): ApiError =
    new ApiError(StatusCodes.NotFound, s"The advert with id $id could not be found.")
}


// trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
//  implicit val newCarFormat = jsonFormat5(NewCar)
//  implicit val usedCarFormat = jsonFormat7(UsedCar) 
//}
