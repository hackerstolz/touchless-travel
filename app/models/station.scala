package models

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Norman on 07.11.15.
  */
case class Station(id: String, name: String, geo: Geo)


object Stations {

  val stations = ArrayBuffer.empty[Station]

  def add(station: Station) = stations.append(station)
  def get(stationId: String) = stations.find(_.id == stationId)
}
