package models


import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

/**
  * Created by Norman on 06.11.15.
  */
case class Train(id: String, line: String) {
  def getTransportationType: String = Trains.transportationType

}


object Trains {

  val transportationType = "TRAM"

  val beaconMappings: Map[String, String] = Map()
  val trains = ArrayBuffer.empty[Train]


  def add(train: Train) = trains.append(train)

  def addBeacon(beaconId: String, trainId: String) = beaconMappings += (beaconId -> trainId)

  def get(id: Int): Option[Train] = trains.find(_.id == id)

  def getTrainForBeacon(beaconId: String): Option[Train] =
    beaconMappings.get(beaconId).flatMap {
      trainId => trains.find(_.id == trainId)
    }

}