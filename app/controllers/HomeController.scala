package controllers

import java.io.FileInputStream
import java.util.Properties

import javax.inject._
import models.Car
import play.api._
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

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index("Car Directory"))
  }

  def getAllCars() = Action.async {
    val future = Future( Json.toJson(DB.allCars()) )
    future.map(Ok(_))
  }

  def deleteCar(id: Long) = Action.async {
    Future( DB.delCar(id) )
    Future(Ok)
  }

  def jsRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.HomeController.getAllCars,
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