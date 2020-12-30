package controllers

import java.sql.Timestamp
import java.time.LocalDateTime

import models._

import scala.slick.driver.PostgresDriver.simple._

class CarDirectory (tag: Tag) extends Table[(Long, String, String, String, Int, Timestamp)](tag, "cars") {
  def id: Column[Long] = column[Long]("id")
  def number: Column[String] = column[String]("number")
  def model: Column[String] = column[String]("model")
  def color: Column[String] = column[String]("color")
  def year: Column[Int] = column[Int]("year")
  def time: Column[Timestamp] = column[Timestamp]("time")
  def * = (id, number, model, color, year, time)
}

trait Database {

  lazy val db = Database.forURL(HomeController.dbUrl, driver = "org.postgresql.Driver")

  def allCars(): List[Car] = {
    db.withSession { implicit session =>
      val cars = TableQuery[CarDirectory]

      cars
        .sortBy(_.number)
        .list
        .map { case (id, number, model, color, year, time) => Car(id, number, model, color, year, time) }
    }
  }

  def searchCar(number: String, model: String, color: String, year: Option[Int]): List[Car] = {
    db.withSession { implicit session =>
      val cars = TableQuery[CarDirectory]

      cars
        .filter(_.number.toLowerCase like s"%${number.toLowerCase}%")
        .filter(_.model.toLowerCase like s"%${model.toLowerCase}%")
        .filter(_.color.toLowerCase like s"%${color.toLowerCase}%")
        .filter(_.year === year || year.isEmpty)
        .sortBy(_.number)
        .list.map { case (id, number, model, color, year, time) => Car(id, number, model, color, year, time) }
    }
  }

  def addCar(form: CarForm): Boolean = {
    db.withSession { implicit session =>
      val cars = TableQuery[CarDirectory]

      val car = cars.filter(_.number === form.number)
      car.firstOption match {
        case Some(_) =>
          false
        case _ =>
          cars
            .map(car => (car.number, car.model, car.color, car.year, car.time)) += (form.number, form.model, form.color, form.year, Timestamp.valueOf(LocalDateTime.now()))
          true
      }

    }
  }

  def delCar(id: Long): Boolean = {
    db.withSession { implicit session =>
      val cars = TableQuery[CarDirectory]

      val car = cars.filter(_.id === id)
      car.firstOption match {
        case Some(_) =>
          car.delete
          true
        case _ => false
      }
    }
  }

  def statistics(): (Int, Timestamp, Timestamp) = {
    db.withSession { implicit session =>
      val cars = TableQuery[CarDirectory]

      val count = cars.length.run
      val first =
        cars
        .sortBy(_.time)
        .firstOption match {
          case Some(car) => car._6
          case _ => Timestamp.valueOf("1970-01-01 00:00:01")
        }
      val last =
        cars
          .sortBy( _.time.desc)
          .firstOption match {
          case Some(car) => car._6
          case _ => Timestamp.valueOf("2038-01-19 03:14:07")
        }
      (count, first, last)
    }
  }

}

object DB extends Database