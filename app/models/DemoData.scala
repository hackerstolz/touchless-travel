package models

import org.joda.time.DateTime
import services.LocationService

/**
  * Created by Norman on 07.11.15.
  */
object DemoData {

  var demoDataPopulated: Boolean = false

  def populate = {

    // Vehicles
    val train1 = Train("1", "U9")
    val beacon1 = "63333"

    Vehicles.add(train1)
    Vehicles.addBeacon(beacon1, train1.id)

    val train2 = Train("2", "U2")
    Vehicles.add(train2)
    val train3 = Train("3", "U4")
    Vehicles.add(train3)

    val taxi1 = Taxi("4", "Car No 34")
    Vehicles.add(train1)
    val taxi2 = Taxi("5", "Car No 50")
    Vehicles.add(train2)


    //Users
    val user1 = User("1", "Sophie")
    Users.add(user1)

    val startR1 = StartStopStamp(new DateTime, Geo("00.00", "00.00"), Some("Rathaus"))
    val stopR1 = StartStopStamp(new DateTime, Geo("00.00", "00.00"), Some("Kursaal"))
    val ride1 = UserRide(user1, train2, "6 Zones", 3.4, startR1, Some(stopR1))
    UserRides.add(ride1)

    val startR2 = StartStopStamp(new DateTime, Geo("00.00", "00.00"), Some("Untertürkheim"))
    val stopR2 = StartStopStamp(new DateTime, Geo("00.00", "00.00"), Some("Berliner Platz"))
    val ride2 = UserRide(user1, train3, "3 Zones", 2.8, startR2, Some(stopR2))
    UserRides.add(ride2)

    val startR3 = StartStopStamp(new DateTime, Geo("00.00", "00.00"), Some("Mercedesstraße 100, 70372 Stuttgart"))
    val stopR3 = StartStopStamp(new DateTime, Geo("00.00", "00.00"), Some("Ulmer Straße 23, 73728 Esslingen"))
    val ride3 = UserRide(user1, taxi1, "15 km", 35, startR3, Some(stopR3))
    UserRides.add(ride3)



    // Stations + Geo
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
