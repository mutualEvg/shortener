package service

import model.{CustomNewUrl, ShortLink}
import play.api.Logger

import java.util.concurrent.atomic.AtomicInteger
import javax.inject.{Inject, Singleton}

@Singleton
class ShortenerService  @Inject()(val linksMap: CustomCache) {

  val logger: Logger = Logger(this.getClass)
  private val maxEpochDay = 365241780471L
  private val defaultLifeTime = 172800L
  val linksCounter = new AtomicInteger(0)

  private val letters: List[Char] = ('a' to 'z').toList
  private val lettersCapital: List[Char] = ('A' to 'Z').toList
  private val numbers: List[Char] = (0 to 9).map(el => el.toString.charAt(0)).toList
  private val vocabulary = (letters ::: lettersCapital ::: numbers).zipWithIndex

  def generateKey(): String = {
    val random = scala.util.Random
    var builder = new StringBuilder()
    for (_ <- 0 until 10) {
      val rand = random.nextInt(62)
      builder = builder += vocabulary.find(el => el._2 == rand).get._1
    }
    builder.toString()
  }

  def createShortLink(objectToStore: Option[CustomNewUrl]): Option[ShortLink] = {
    objectToStore match {
      case Some(obj) =>
        if (linksMap.get(obj.shortUrl).isDefined) {
          Some(ShortLink("", Integer.MIN_VALUE,
            s"Short url { ${obj.shortUrl} } is already defined for url { ${linksMap.get(obj.shortUrl)} }"))
        } else {
          val lifetime = if (obj.lifeTime == 0L) defaultLifeTime else obj.lifeTime
          val shortUrl = if (obj.shortUrl.isEmpty) generateKey() else obj.shortUrl
          if (linksMap.get(shortUrl).isDefined) createShortLink(objectToStore)
          linksMap.put(shortUrl, obj.url, lifetime)
          logger.info(s"Check if here = ${linksMap.get(shortUrl)}")
          Option(ShortLink(shortUrl, linksCounter.incrementAndGet(), ""))
        }
      case None => None
    }
  }
}


