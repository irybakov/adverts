package kz.adverts
import kz.adverts.repo.AdvertsRepository
import scala.concurrent.Future
import kz.adverts.repo.CarAdvert
import kz.adverts.repo.CreateAdvert
import kz.adverts.repo.UpdateAdvert

trait FailedMocks {

    class FailingRepository extends AdvertsRepository {
      override def all(): Future[List[CarAdvert]] = Future.failed(new Exception("Mocked exception"))

      override def get(id: String): Future[CarAdvert] = Future.failed(new Exception("Mocked exception"))
  
      override def newOnly(): Future[List[CarAdvert]] = Future.failed(new Exception("Mocked exception"))
  
      override def usedOnly(): Future[List[CarAdvert]] = Future.failed(new Exception("Mocked exception"))
  
      override def save(createAd: CreateAdvert): Future[CarAdvert] = Future.failed(new Exception("Mocked exception"))
  
      override def update(id: String, updateAd: UpdateAdvert): Future[CarAdvert] = Future.failed(new Exception("Mocked exception"))
    }
  
  }