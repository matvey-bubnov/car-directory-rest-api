package models

case class Car(
                id: Long,
                number: String,
                model: String,
                color: String,
                year: Int
              )

case class CarForm(
                number: String,
                model: String,
                color: String,
                year: Int
              )
