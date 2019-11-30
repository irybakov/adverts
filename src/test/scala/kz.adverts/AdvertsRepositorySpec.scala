package kz.adverts

import org.scalatest.{FlatSpec, Matchers}
import kz.adverts.repo
import kz.adverts.repo._
import scala.util.Success
import scala.util.Failure
import org.scalatest._
import java.util.concurrent._

class AdvertsRepositorySpec extends FlatSpec with Matchers {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

    "CRUD for repository" should "save NewCar to empty list" in {   
        val repo = AdvertsRepository.props

        val newCar = CreateAdvert("Audi A4 Avant",Fuel.Gasoline,1000,true,None,None)
        repo.save(newCar)
        val items = repo.all()
          
        items.onComplete {
            case Success(value) => {
                assert(value.size==1)
                assert(value(0).title==newCar.title)
                assert(value(0).isNew==true)
            }
            case Failure(exception) => assert(false)
        }
    }
    it should "save UsedCar to empty list" in {
        val repo = AdvertsRepository.props

        val usedCar = CreateAdvert("Audi A4 Avant",Fuel.Gasoline,1000,false,Some(10000),Some(1575126012644L))
        repo.save(usedCar)
        val items = repo.all()
          
        items.onComplete {
            case Success(value) => {
                
                assert(value.size==1)
                val used = value(0).asInstanceOf[UsedCar]
                assert(used.title==usedCar.title)
                assert(used.isNew==false)
                assert(used.mileage==usedCar.mileage.get)
                assert(used.firstReg==usedCar.firstReg.get)
            }
            case Failure(exception) => assert(false)
        }
        
    }
    it should "update UsedCar" in {
        val repo = AdvertsRepository.props

        val usedCar = CreateAdvert("Audi A4 Avant",Fuel.Gasoline,1000,false,Some(10000),Some(1575126012644L))
        val item = repo.save(usedCar)
        item.onComplete {
            case Success(value) => {
                val update = UpdateAdvert(title = Some("BMW i3"),fuel = Some(Fuel.Diesel),None,None,None)
                val result = repo.update(value.id,update)

                result.onComplete {
                    case Success(value) => {
                        val target = value.asInstanceOf[UsedCar]
                        assert(target.id == value.id)
                        assert(target.title == update.title.get)
                        assert(target.fuel== update.fuel.get)
                    }
                    case Failure(exception) => assert(false)   
                }
                
            }
            case Failure(exception) => assert(false)
        }
    }
    it should "update NewCar" in {
        val repo = AdvertsRepository.props
        val newCar = CreateAdvert("Audi A4 Avant",Fuel.Gasoline,1000,true,None,None)
        val item = repo.save(newCar)
        item.onComplete {
            case Success(value) => {
                val update = UpdateAdvert(title = Some("BMW i3"),fuel = Some(Fuel.Diesel),None,None,None)
                val result = repo.update(value.id,update)

                result.onComplete {
                    case Success(value) => {
                        val target = value.asInstanceOf[NewCar]
                        assert(target.id == value.id)
                        assert(target.title == update.title.get)
                        assert(target.fuel== update.fuel.get)
                    }
                    case Failure(exception) => assert(false)   
                }

            }
            case Failure(exception) => assert(false)
        }
    }
    it should "get All" in {
        val repo = AdvertsRepository.props

        val usedCar = CreateAdvert("Audi A4 Avant",Fuel.Gasoline,1000,false,Some(10000),Some(1575126012644L))
        repo.save(usedCar)

        val newCar = CreateAdvert("BMW  i3",Fuel.Gasoline,1000,true,None,None)
        repo.save(newCar)   

        val items= repo.all()
          
        items.onComplete {
            case Success(value) => {
                assert(value.size==2)
            }
            case Failure(exception) => assert(false)
        }
    }

}
