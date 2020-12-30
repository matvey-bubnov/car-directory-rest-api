package controllers

import java.sql.Timestamp

import play.api.libs.json.{JsString, Reads, Writes}

object CustomMappers {
  implicit val tsreads: Reads[Timestamp] = Reads.of[Long] map (new Timestamp(_))
  implicit val tswrites: Writes[Timestamp] = Writes { (ts: Timestamp) => JsString(ts.toString)}
}
