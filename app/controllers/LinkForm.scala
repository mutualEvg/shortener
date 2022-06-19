package controllers

import java.util.concurrent.atomic.AtomicInteger

object LinkForm {
  import play.api.data.Form
  import play.api.data.Forms._


  /**
   * A form processing DTO that maps to the form below.
   *
   * Using a class specifically for form binding reduces the chances
   * of a parameter tampering attack and makes code clearer.
   */
  //case class Data(url: String, number: Int)
  case class Data(url: String)

  /**
   * The form definition for the "create a widget" form.
   * It specifies the form fields and their types,
   * as well as how to convert from a Data to form data and vice versa.
   */
  val form = Form(
    mapping(
      "url" -> nonEmptyText,
//      "number" -> number(min = 0)
    )(Data.apply)(Data.unapply)
  )
}
