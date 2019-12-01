package kz.adverts.repo

import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID

import AdvertsRepository.AdvertNotFound

object Fuel extends Enumeration {
    type Fuel = Value
  
    val Gasoline  = Value("Gasoline")
    val Diesel  = Value("Diesel")
}

trait CarAdvert {
    def id: String
    def title: String
    def fuel: String
    def price: Int
    def isNew: Boolean
}
    
trait Used {
    def mileage: Int
    def firstReg: Long
} 

case class NewCar(id: String,title: String, fuel: String, price: Int, isNew: Boolean = true) extends CarAdvert

case class UsedCar(id: String,title: String, fuel: String, price: Int, mileage: Int, firstReg: Long,isNew: Boolean = false)  extends CarAdvert with Used

case class CreateAdvert(title: String, fuel: String, price: Int, isNew: Boolean, mileage: Option[Int], firstReg: Option[Long]) {
    require((isNew && !mileage.isDefined && !firstReg.isDefined) || (!isNew) && mileage.isDefined && firstReg.isDefined) 
}
case class UpdateAdvert(title: Option[String], fuel: Option[String], price: Option[Int], mileage: Option[Int], firstReg: Option[Long])

trait AdvertsRepository {

  def all(): Future[List[CarAdvert]]
  def get(id: String): Future[CarAdvert]
  def newOnly(): Future[List[CarAdvert]]
  def usedOnly(): Future[List[CarAdvert]]

  def save(createAd: CreateAdvert): Future[CarAdvert]
  def update(id: String, updateAd: UpdateAdvert): Future[CarAdvert]

}

object AdvertsRepository {
    def props(implicit ec: ExecutionContext) = new InMemoryAdvertsRepository()(ec)

    final case class AdvertNotFound(id: String) extends Exception("")
}


class InMemoryAdvertsRepository () (implicit ec: ExecutionContext) extends AdvertsRepository {

    private var items: List[CarAdvert] = List[CarAdvert]()

    def all(): Future[List[CarAdvert]] = {
        Future.successful(items.sortBy(_.id))
    }

    def get(id: String): Future[CarAdvert] = {
        items.find(_.id == id) match {
          case Some(foundAd) => Future.successful(foundAd)
          case None => Future.failed(AdvertNotFound(id))
        }
    }

    def newOnly(): Future[List[CarAdvert]] = ???
    def usedOnly(): Future[List[CarAdvert]] = ???

    def save(createAd: CreateAdvert): Future[CarAdvert] = Future.successful {
        val id = UUID.randomUUID().toString
        val car = createAd.isNew match {
            case true => NewCar(id,createAd.title,createAd.fuel,createAd.price)
            case false => UsedCar(id,createAd.title,createAd.fuel,createAd.price,createAd.mileage.get,createAd.firstReg.get)
        }
        items = items :+ car
        car
    }
    
    def update(id: String, updateAd: UpdateAdvert): Future[CarAdvert] = {
        items.find(_.id == id) match {
            case Some(foundAd) =>
              val newAd = updateHelper(foundAd, updateAd)
              items = items.map(t => if (t.id == id) newAd else t)
              Future.successful(newAd)
            case None =>
              Future.failed(AdvertNotFound(id))
          }
    }

    def updateHelper (ad: CarAdvert, updateAd: UpdateAdvert): CarAdvert = {        
         val newAd =  ad.isNew match {
             case true => updateNewCar(ad.asInstanceOf[NewCar],updateAd)
             case false => updateUsedCar(ad.asInstanceOf[UsedCar],updateAd)
         }
        newAd
    }

    def updateNewCar (ad: NewCar, updateAd: UpdateAdvert): CarAdvert = {
        val t1 = updateAd.title.map(title => ad.copy(title = title)).getOrElse(ad)
        val t2 = updateAd.fuel.map(fuel => t1.copy(fuel = fuel)).getOrElse(t1)
        val t3 = updateAd.price.map(price => t2.copy(price = price)).getOrElse(t2)
        t3
    }

    def updateUsedCar (ad: UsedCar, updateAd: UpdateAdvert): CarAdvert = {
        val t1 = updateAd.title.map(title => ad.copy(title = title)).getOrElse(ad)
        val t2 = updateAd.fuel.map(fuel => t1.copy(fuel = fuel)).getOrElse(t1)
        val t3 = updateAd.price.map(price => t2.copy(price = price)).getOrElse(t2)
        val t4 = updateAd.mileage.map(mileage => t3.copy(mileage = mileage)).getOrElse(t3)
        val t5 = updateAd.firstReg.map(firstReg => t4.copy(firstReg = firstReg)).getOrElse(t4)
        t5
    }
}