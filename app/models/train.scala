package models


import scala.collection.mutable.ArrayBuffer

/**
  * Created by Norman on 06.11.15.
  */
case class Train(id: Int, line: String) {

}


object Trains {

  val trains = ArrayBuffer.empty[Train]

  def initPredefinedData = {
    trains.append(Train(1, "S2"))
  }


  def get(id: Int): Train = ???

}