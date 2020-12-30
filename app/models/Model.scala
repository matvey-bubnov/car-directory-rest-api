package models

import java.sql.Timestamp

case class Car(
                id: Long,
                number: String,
                model: String,
                color: String,
                year: Int,
                time: Timestamp
              )

case class CarForm(
                number: String,
                model: String,
                color: String,
                year: Int
              )
