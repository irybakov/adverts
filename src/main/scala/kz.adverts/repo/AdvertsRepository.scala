package kz.adverts.repo

import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID

import AdvertsRepository.AdvertNotFound

object Fuel extends Enumeration {
    type Fuel = Value
  
    val Gasoline  = Value("Gasoline")
    val Diesel  = Value("Diesel")
  }

trait  Advert {
    def id: String
    def title: String
    def fuel: Fuel.Fuel
    def price: Int
    def isNew: Boolean
}

case class NewCar(id: String,title: String, fuel: Fuel.Fuel, price: Int) extends Advert {
    val isNew = true
}

case class UsedCar(id: String,title: String, fuel: Fuel.Fuel, price: Int, mileage: Int, firstReg: Long) extends Advert {
    val isNew = false
}

case class CreateAdvert(title: String, fuel: Fuel.Fuel, price: Int, isNew: Boolean, mileage: Option[Int], firstReg: Option[Long]) {
    require((isNew && !mileage.isDefined && !firstReg.isDefined) || (!isNew) && mileage.isDefined && firstReg.isDefined) 
}
case class UpdateAdvert(title: Option[String], fuel: Option[Fuel.Fuel], price: Option[Int], mileage: Option[Int], firstReg: Option[Long])



trait AdvertsRepository {

  def all(): Future[List[Advert]]
  def newOnly(): Future[List[Advert]]
  def usedOnly(): Future[List[Advert]]

  def save(createAd: CreateAdvert): Future[Advert]
  def update(id: String, updateAd: UpdateAdvert): Future[Advert]

}

object AdvertsRepository {
    def props = new InMemoryAdvertsRepository()

    final case class AdvertNotFound(id: String) extends Exception("")
}


class InMemoryAdvertsRepository  extends AdvertsRepository {

    private var items: List[Advert] = List[Advert]()

    def all(): Future[List[Advert]] = {
        Future.successful(items.sortBy(_.id))
    }

    def newOnly(): Future[List[Advert]] = ???
    def usedOnly(): Future[List[Advert]] = ???

    def save(createAd: CreateAdvert): Future[Advert] = Future.successful {
        val id = UUID.randomUUID().toString
        val ad = createAd.isNew match {
            case true => NewCar(id,createAd.title,createAd.fuel,createAd.price)
            case false => UsedCar(id,createAd.title,createAd.fuel,createAd.price,createAd.mileage.get,createAd.firstReg.get)
        }
        items = items :+ ad
        ad
    }
    
    def update(id: String, updateAd: UpdateAdvert): Future[Advert] = {
        items.find(_.id == id) match {
            case Some(foundAd) =>
              val newAd = updateHelper(foundAd, updateAd)
              items = items.map(t => if (t.id == id) newAd else t)
              Future.successful(newAd)
            case None =>
              Future.failed(AdvertNotFound(id))
          }
    }

    def updateHelper (ad: Advert, updateAd: UpdateAdvert): Advert = {        
         val newAd =  ad.isNew match {
             case true => updateNewCar(ad.asInstanceOf[NewCar],updateAd)
             case false => updateUsedCar(ad.asInstanceOf[UsedCar],updateAd)
         }
        newAd
    }

    def updateNewCar (ad: NewCar, updateAd: UpdateAdvert): NewCar = {
        val t1 = updateAd.title.map(title => ad.copy(title = title)).getOrElse(ad)
        val t2 = updateAd.fuel.map(fuel => t1.copy(fuel = fuel)).getOrElse(t1)
        val t3 = updateAd.price.map(price => t2.copy(price = price)).getOrElse(t2)
        t3
    }

    def updateUsedCar (ad: UsedCar, updateAd: UpdateAdvert): UsedCar = {
        val t1 = updateAd.title.map(title => ad.copy(title = title)).getOrElse(ad)
        val t2 = updateAd.fuel.map(fuel => t1.copy(fuel = fuel)).getOrElse(t1)
        val t3 = updateAd.price.map(price => t2.copy(price = price)).getOrElse(t2)
        val t4 = updateAd.mileage.map(mileage => t3.copy(mileage = mileage)).getOrElse(t3)
        val t5 = updateAd.firstReg.map(firstReg => t4.copy(firstReg = firstReg)).getOrElse(t4)
        t5
    }
}