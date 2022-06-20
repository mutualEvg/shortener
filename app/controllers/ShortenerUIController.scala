package controllers

import model.{CustomNewUrl, ShortLink}
import play.api.Logger
import play.api.data._
import play.api.mvc._
import service.ShortenerService

import javax.inject.Inject

class ShortenerUIController @Inject()(cc: MessagesControllerComponents,
                                      val shortenerService: ShortenerService,
                                     ) extends MessagesAbstractController(cc) {
  import LinkForm._


  val logger = Logger(this.getClass)

  private val urls = scala.collection.mutable.ArrayBuffer[ShortLink]()

  private val postUrl = routes.ShortenerUIController.createShortUrlUI()

  def index = Action {
    Ok(views.html.index())
  }

  def listUrlWidgets = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.listUrls(urls.toList, form, postUrl))
  }

  def createShortUrlUI = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[Data] =>
      logger.info(s"check creator = $formWithErrors")
      BadRequest(views.html.listUrls(urls.toList, formWithErrors, postUrl))
    }

    val successFunction = { data: Data =>
      logger.info(s"input data = $data")
      val createdUrl = shortenerService.createShortLink(Some(CustomNewUrl("", data.url, 172800L)))
      createdUrl match {
        case Some(shortUrl) =>
          urls.append(shortUrl)
          Redirect(routes.ShortenerUIController.listUrlWidgets()).flashing("info" -> "Link added!")
        case None => BadRequest
      }

    }

    val formValidationResult = form.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }
}
