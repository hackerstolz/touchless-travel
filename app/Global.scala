import actors.MainActors
import play.api.{Application, GlobalSettings}

/**
  * Created by Norman on 07.11.15.
  */
object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {
    // Init the main actors
    val userActorManager = MainActors.userActorManager
  }

}
