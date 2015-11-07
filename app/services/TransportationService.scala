package services

/**
  * Created by Norman on 07.11.15.
  */
object TransportationService {

  def getCurrentLineForTrain(trainId: String): String =
    trainId match {
      case "1" => "U9 direction Vogelsang"
      case "2" => "U4 direction Hölderlinplatz"
      case "3" => "U15 direction Stammheim"
      case _ => "Unkown"
    }


}
