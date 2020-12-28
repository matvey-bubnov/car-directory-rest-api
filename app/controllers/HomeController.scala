package controllers

import java.io.FileInputStream
import java.util.Properties

import javax.inject._
import models.Car
import play.api._
import play.api.libs.json.{Json, Writes}
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
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

  def getAll() = Action {
    Ok(Json.toJson(DB.all()))
  }

  def jsRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.HomeController.getAll
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