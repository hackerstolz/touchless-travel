package services

import models.{Geo, Station}

/**
  * Created by Norman on 07.11.15.
  */
object LocationService {

  var callCounter = 0
  var startStation: Option[Station] = None
  var controlGeo: Option[Geo] = None
  var endStation: Option[Station] = None

  val fallbackGeo = Geo("48.788941", "9.209225")

  def getTrainLocation(trainId: String): (Geo, Option[Station]) = {
    callCounter match {
      case 0 =>
        // Start
        callCounter = callCounter + 1
        val geo = startStation.flatMap(s => Some(s.geo)).getOrElse(fallbackGeo)
        (geo, startStation)
      case 1 =>
        // Kontrolle
        callCounter = callCounter + 1
        val geo = controlGeo.getOrElse(fallbackGeo)
        (geo, None)
      case 2 =>
        // Ziel
        callCounter = 0
        val geo = endStation.flatMap(s => Some(s.geo)).getOrElse(fallbackGeo)
        (geo, endStation)
    }
  }

}
