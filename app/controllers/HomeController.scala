package controllers

import java.io.FileInputStream
import java.util.Properties

import javax.inject._
import models.{Car, CarForm}
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json.toJson
import play.api.libs.json.{Json, Writes}
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  implicit val writes: Writes[Car] = Json.writes[Car]

  val carForm: Form[CarForm] = Form(
    mapping(
      "carNumber" -> text,
      "carModel" -> text,
      "carColor" -> text,
      "carYear" -> number
    )(CarForm.apply)(CarForm.unapply))

  def index() = Action { implicit request =>
    Ok(views.html.index("Car Directory"))
  }

  def getAllCars() = Action.async {
    val future = Future( Json.toJson(DB.allCars()) )
    future.map(Ok(_))
  }

  def searchCars(number: String, model: String, color: String, year: Option[Int]) = Action.async {
    val future = Future( Json.toJson(DB.searchCar(number, model, color, year)) )
    future.map(Ok(_))
  }

  def addNewCar()= Action.async { implicit request =>
    carForm.bindFromRequest.fold(
      _ => Future( BadRequest("Errors in form") ),
      form =>
        Future( DB.addCar(form) )
          .map{ isAdded =>
            if (isAdded)
              Ok
            else
              BadRequest("Number already exists")
          }
    )
  }

  def deleteCar(id: Long) = Action.async {
    val future = Future( DB.delCar(id) )
    future.map{ isDeleted =>
      if (isDeleted)
        Ok
      else
        BadRequest("Entry not found")
    }
  }

  def jsRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.HomeController.getAllCars,
        routes.javascript.HomeController.searchCars,
        routes.javascript.HomeController.addNewCar,
        routes.javascript.HomeController.deleteCar
      )).as("text/javascript")
  }
}

object HomeController {
  val logger: Logger = Logger(this.getClass())

  lazy val dbUrl =
    try {
      val prop = new Properties()
      prop.load(new FileInputStream("application.properties"))
      prop.getProperty("url")
    } catch { case e: Exception =>
      HomeController.logger.error("Properties file cannot be loaded")
      e.printStackTrace()
      sys.exit(1)
    }
}