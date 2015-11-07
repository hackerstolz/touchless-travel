package models

import services.LocationService

/**
  * Created by Norman on 07.11.15.
  */
object DemoData {

  var demoDataPopulated: Boolean = false

  def populate = {

    val train1 = Train("1", "S2")

    val beacon1 = "sdf"
    val beacon2 = "sf"
    val beacon3 = "sdfdasd"

    Trains.add(train1)

    Trains.addBeacon(beacon1, train1.id)
    Trains.addBeacon(beacon2, train1.id)
    Trains.addBeacon(beacon3, train1.id)


    val user1 = User("1", "Sophie")

    Users.add(user1)

    val startStation = Station("reitelsberg", "Reitelsberg", Geo("48.788891", "9.212422"))
    val zielStation = Station("bergfriedhof", "Bergfriedhof", Geo("48.789033", "9.206393"))
    Stations.add(startStation)
    Stations.add(zielStation)
    LocationService.startStation = Some(startStation)
    LocationService.endStation = Some(zielStation)

    val controlGeo = Some(Geo("48.788941", "9.209225"))
    LocationService.controlGeo = controlGeo

  }

}
