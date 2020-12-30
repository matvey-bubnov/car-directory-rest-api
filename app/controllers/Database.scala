package controllers

import models._

import scala.slick.driver.PostgresDriver.simple._

class CarDirectory (tag: Tag) extends Table[(Long, String, String, String, Int)](tag, "cars") {
  def id: Column[Long] = column[Long]("id")
  def number: Column[String] = column[String]("number")
  def model: Column[String] = column[String]("model")
  def color: Column[String] = column[String]("color")
  def year: Column[Int] = column[Int]("year")
  def * = (id, number, model, color, year)
}

trait Database {

  lazy val db = Database.forURL(HomeController.dbUrl, driver = "org.postgresql.Driver")

  def allCars(): List[Car] = {
    db.withSession{
      implicit session =>
        val cars = TableQuery[CarDirectory]

        cars
          .sortBy(_.number)
          .list
          .map { case (id, number, model, color, year) => Car(id, number, model, color, year) }
    }
  }

  def searchCar(number: String, model: String, color: String, year: Option[Int]): List[Car] = {
    db.withSession{
      implicit session =>
        val cars = TableQuery[CarDirectory]

        cars
          .filter(_.number.toLowerCase like s"%${number.toLowerCase}%")
          .filter(_.model.toLowerCase like s"%${model.toLowerCase}%")
          .filter(_.color.toLowerCase like s"%${color.toLowerCase}%")
          .filter(_.year === year || year.isEmpty)
          .sortBy(_.number)
          .list.map { case (id, number, model, color, year) => Car(id, number, model, color, year) }
    }
  }

  def addCar(form: CarForm): Unit = {
    db.withSession{
      implicit session =>
        val cars = TableQuery[CarDirectory]

        cars
          .map(car => (car.number, car.model, car.color, car.year)) += (form.number, form.model, form.color, form.year)
    }
  }

  def delCar(id: Long): Boolean = {
    db.withSession{
      implicit session =>
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

}

object DB extends Database