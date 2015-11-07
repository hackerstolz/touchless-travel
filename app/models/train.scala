package models


import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

/**
  * Created by Norman on 06.11.15.
  */
case class Vehicle(id: String, vtype: String, name: String)

object Train {
  def apply(id: String, name: String) = {
    Vehicle(id, "TRAM", name)
  }
}
object Taxi {
 def apply(id: String, name: String) = {
   Vehicle(id, "TAXI", name)
 }
}


object Vehicles {

  val beaconMappings: Map[String, String] = Map()
  val vehicles = ArrayBuffer.empty[Vehicle]


  def add(train: Vehicle) = vehicles.append(train)

  def addBeacon(beaconId: String, trainId: String) = beaconMappings += (beaconId -> trainId)

  def get(id: Int): Option[Vehicle] = vehicles.find(_.id == id)

  def getTrainForBeacon(beaconId: String): Option[Vehicle] =
    beaconMappings.get(beaconId).flatMap {
      trainId => vehicles.find(_.id == trainId)
    }

}