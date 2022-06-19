package controllers

import model.{CustomNewUrl, ShortLink}
import play.api.Logger
import play.api.data._
import play.api.mvc._
import service.ShortenerService

import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

/**
 * The classic WidgetController using MessagesAbstractController.
 *
 * Instead of MessagesAbstractController, you can use the I18nSupport trait,
 * which provides implicits that create a Messages instance from a request
 * using implicit conversion.
 *
 * See https://www.playframework.com/documentation/2.6.x/ScalaForms#passing-messagesprovider-to-form-helpers
 * for details.
 */
class ShortenerUIController @Inject()(cc: MessagesControllerComponents,
                                      val shortenerService: ShortenerService,
                                     ) extends MessagesAbstractController(cc) {
  import LinkForm._


  val logger = Logger(this.getClass)

  private val urls = scala.collection.mutable.ArrayBuffer[ShortLink]()


  // The URL to the widget.  You can call this directly from the template, but it
  // can be more convenient to leave the template completely stateless i.e. all
  // of the "WidgetController" references are inside the .scala file.
  private val postUrl = routes.ShortenerUIController.createShortUrlUI()

  def index = Action {
    Ok(views.html.index())
  }

  def listUrlWidgets = Action { implicit request: MessagesRequest[AnyContent] =>
    // Pass an unpopulated form to the template
    Ok(views.html.listUrls(urls.toList, form, postUrl))
  }

  // This will be the action that handles our form post
  def createShortUrlUI = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[Data] =>
      logger.info(s"check creator = $formWithErrors")
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      BadRequest(views.html.listUrls(urls.toList, formWithErrors, postUrl))
    }

    val successFunction = { data: Data =>
      logger.info(s"input data = $data")
      // This is the good case, where the form was successfully parsed as a Data object.
      //val widget = ShortLink(content = data.url, number = data.number)
      // Default lifetime is two days
      val createdUrl = shortenerService.createShortLink(Some(CustomNewUrl("", data.url, 172800L)))
      //val widget = ShortLink(content = data.url, counter.incrementAndGet())
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
