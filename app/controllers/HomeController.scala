package controllers

import java.io.FileInputStream
import java.util.Properties

import javax.inject._
import models.{Car, CarForm}
import play.api._
import play.api.data.Form
import play.api.data.Forms._
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

  def searchCars(number: String) = Action {
    Ok(
      Json.toJson(
        //DB.searchCar(number, model, color, year)
        DB.searchCar(number)
      )
    )
  }

  def addNewCar()= Action { implicit request =>
    carForm.bindFromRequest.fold(
      _ => { BadRequest(views.html.index("Car Directory")) },
      car => {
        DB.addCar(car)
        Redirect(routes.HomeController.index)
      }
    )
  }

  def deleteCar(id: Long) = Action.async {
    Future( DB.delCar(id) )
    Future(Ok)
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