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
        val phones = TableQuery[CarDirectory]

        phones.list.map { case (id, number, model, color, year) => Car(id, number, model, color, year) }
    }
  }

  def delCar(id: Long): Unit = {
    db.withSession{
      implicit session =>
        val phones = TableQuery[CarDirectory]

        phones
          .filter(_.id === id)
          .delete
    }
  }

}

object DB extends Database