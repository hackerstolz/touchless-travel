package models

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Norman on 07.11.15.
  */
case class User(id: String, name: String ) {

}

object Users {

  val users = ArrayBuffer.empty[User]

  def add(user: User) = users.append(user)
  def get(userId: String) = users.find(_.id == userId)

}
