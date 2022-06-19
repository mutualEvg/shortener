package services

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Logger
import play.api.test._
import service.{CustomCache, ShortenerService}

import scala.collection.mutable

class ShortenerServiceSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  val logger: Logger = Logger(this.getClass)

  "ShortenerService" should {

    "generate values of 10 symbols with letters and digits" in {
      val list = new mutable.ListBuffer[String]()
      val service = new ShortenerService(new CustomCache)
      for (i <- 0 to 9) list += service.generateKey()

      list.count(el => el.length == 10) mustBe 10
      logger.info(s"generated values: ${list.mkString(", ")}")
    }


  }
}
