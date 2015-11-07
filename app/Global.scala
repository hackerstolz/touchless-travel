import actors.MainActors
import models.DemoData
import play.api.{Logger, Application, GlobalSettings}

/**
  * Created by Norman on 07.11.15.
  */
object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {
    // Init the main actors
    val userActorManager = MainActors.userActorManager

    if (DemoData.demoDataPopulated == false) {
      DemoData.populate
      DemoData.demoDataPopulated = true
      Logger.info("populated demo data")
    }

  }
}
