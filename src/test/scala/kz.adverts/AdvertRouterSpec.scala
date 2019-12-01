package kz.adverts
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import kz.adverts.repo._
import kz.adverts.routing._
import scala.util.Success
import scala.util.Failure

class AdvertRouterSpec extends WordSpec with Matchers with ScalatestRouteTest with FailedMocks  with JsonSupport {

    "A AdvertRouter" should {
        
        "create a new Car ads" in {
            val repository = new InMemoryAdvertsRepository()
            val router = new AdvertsService(repository)

            val newCar = CreateAdvert("Audi A4 Avant",Fuel.Gasoline.toString(),1000,true,None,None)
      
            Post("/v1/adverts", newCar) ~> router.route ~> check {
              status shouldBe StatusCodes.Created
              val resp = responseAs[NewCar]
              resp.title shouldBe newCar.title
              resp.fuel shouldBe newCar.fuel
              resp.price shouldBe newCar.price
              newCar.isNew shouldBe true
              resp.isNew shouldBe newCar.isNew
            }
        }

        "create a used Car ads" in {
            val repository = new InMemoryAdvertsRepository()
            val router = new AdvertsService(repository)

            val usedCar = CreateAdvert("Audi A4 Avant",Fuel.Gasoline.toString(),1000,false,Some(10000),Some(1575126012644L))
      
            Post("/v1/adverts", usedCar) ~> router.route ~> check {
              status shouldBe StatusCodes.Created
              val resp = responseAs[UsedCar]
              resp.title shouldBe usedCar.title
              resp.fuel shouldBe usedCar.fuel
              resp.price shouldBe usedCar.price
              usedCar.isNew shouldBe false
              resp.isNew shouldBe usedCar.isNew
              resp.mileage shouldBe usedCar.mileage.get
              resp.firstReg shouldBe usedCar.firstReg.get
            }
        }

        "return all the adverts" in {

            val repository = new InMemoryAdvertsRepository()
            val router = new AdvertsService(repository)


            val usedCar = CreateAdvert("Audi A4 Avant",Fuel.Gasoline.toString(),1000,false,Some(10000),Some(1575126012644L))
            val newCar = CreateAdvert("Audi A4 Avant",Fuel.Gasoline.toString(),1000,true,None,None)
            repository.save(usedCar)
            repository.save(newCar)
            val asd = repository.all()

            asd.onComplete {
                case Success(value) => {
                    Get("/v1/adverts") ~> router.route ~> check {
                        status shouldBe StatusCodes.OK
                        val resp = responseAs[List[CarAdvert]]
                        resp shouldBe value
                    }
                }
                case Failure(exception) => assert(false)
            }
        }

        "return one advert by id" in {

            val repository = new InMemoryAdvertsRepository()
            val router = new AdvertsService(repository)


            val usedCar = CreateAdvert("Audi A4 Avant",Fuel.Gasoline.toString(),1000,false,Some(10000),Some(1575126012644L))
        
            val ad = repository.save(usedCar)
            ad.onComplete{
                case Success(value) => {
                    Get(s"/v1/adverts/${value.id}") ~> router.route ~> check {
                        status shouldBe StatusCodes.OK
                        val resp = responseAs[CarAdvert]
                        resp shouldBe value
                    }
                }
                case Failure(exception) => assert(false)
            }
        }

        "return return http code 404 if advert was not found" in {

            val repository = new InMemoryAdvertsRepository()
            val router = new AdvertsService(repository)
            
            Get(s"/v1/adverts/fakeId") ~> router.route ~> check {
                status shouldBe ApiError.advertNotFound("fakeId").statusCode
            }
        }


        "update advert by id" in {

            val repository = new InMemoryAdvertsRepository()
            val router = new AdvertsService(repository)

            val usedCar = CreateAdvert("Audi A4 Avant",Fuel.Gasoline.toString(),1000,false,Some(10000),Some(1575126012644L))
        
            val ad = repository.save(usedCar)
            ad.onComplete{
                case Success(value) => {

                    val update = UpdateAdvert(title = Some("BMW 4-series"),fuel = Some(Fuel.Diesel.toString()),None,None,None)
                    Put(s"/v1/adverts/${value.id}") ~> router.route ~> check {
                        status shouldBe StatusCodes.OK
                        val resp = responseAs[CarAdvert]
                        resp.title shouldBe update.title.get
                        resp.fuel shouldBe update.fuel.get
                    }
                }
                case Failure(exception) => assert(false)
            }
        }
        

    }

}