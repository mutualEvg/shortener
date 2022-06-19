package controllers

import model.{CustomNewUrl, NewUrl, ShortLink, ShortenedLink}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import service._

import javax.inject.{Inject, _}

@Singleton
class ShortenerController @Inject()(val controllerComponents: ControllerComponents,
                                    val shortenerService: ShortenerService)
  extends BaseController {

  implicit val shortenedLink = Json.format[ShortenedLink]
  implicit val randomShortenedLink = Json.format[NewUrl]

  val logger: Logger = Logger(this.getClass)
  private val maxEpochDay = 365241780471L
  private val defaultLifeTime = 172800L // 2 days


  /*
   * Example link
   * curl localhost:8080/shorturls/qwerty1235
   */
  def goToUrl(url: String): Action[AnyContent] = Action {
    val foundItem = shortenerService.getById(url)
    foundItem match {
      case Some(item) =>
        val redirectUrl = if (item.startsWith("http")) item else s"https://$item"
        logger.info(s"Link to redirect = $redirectUrl")
        Redirect(redirectUrl)
      case None => NotFound
    }
  }

  /*
   * Example link
   * curl localhost:8080/shorturls/qwerty1235
   */
  def getById(url: String): Action[AnyContent] = Action {
   shortenerService.getById(url) match {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  /*
   * Example link
   * curl -v -d '{"shortUrl": "qwerty6543", "url": "https://teitter.com", "lifeTime": 10}' -H 'Content-Type: application/json' -X POST localhost:8080/shorturls/custom
   */
  def addCustomShortUrl(): Action[AnyContent] = Action { implicit request =>

    implicit val readsPerson = Json.reads[CustomNewUrl]
    val content = request.body
    val jsonObject = content.asJson
    val objectToStore: Option[CustomNewUrl] = {
      jsonObject.flatMap(
        Json.fromJson[CustomNewUrl](_).asOpt
      )
    }
    shortenerService.createShortLink(objectToStore) match {
      case Some(shortLink) =>
        if (shortLink.message.nonEmpty) Created(shortLink.message)
        else Created(shortLink.content)
      case None => BadRequest
    }
  }

  /*
   * Example link
   * curl -v -d '{"description": "https://teitter.com", "lifeTime": 10}' -H 'Content-Type: application/json' -X POST localhost:8080/shorturls
   */
  def addNewUrl(): Action[AnyContent] = Action { implicit request =>

  val content = request.body
  val jsonObject = content.asJson
  val linkToStore: Option[NewUrl] = {
    jsonObject.flatMap(
      Json.fromJson[NewUrl](_).asOpt
    )
  }
    linkToStore match {
      case Some(value) =>
        val objectToStore = Some(CustomNewUrl("", value.url, value.lifeTime))
        shortenerService.createShortLink(objectToStore) match {
          case Some(shortLink) =>
            if (shortLink.message.nonEmpty) Created(shortLink.message)
            else Created(shortLink.content)
          case None => BadRequest
        }
      case None => BadRequest
    }

  }

  /*
   * Example link
   * curl -X POST 'localhost:8080/shorturls/clear'
   */
  def clearCache(): Action[AnyContent] = Action {
    Ok(Json.toJson(shortenerService.clearCache()))
  }

}
