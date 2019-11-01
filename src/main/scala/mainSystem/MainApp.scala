package MainSystem

import Controllers._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{NoDependencyResolver, FXMLView, FXMLLoader}
import javafx.{scene => jfxs}
import scalafx.stage.{Modality, Stage}
import java.net.{ServerSocket, Socket, InetAddress}





object MainApp extends JFXApp {
 
  val rootResource = getClass.getResource("/Views/MainPage.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load();
  val roots = loader.getRoot[jfxs.layout.VBox]
  
  stage = new PrimaryStage {
    title = "BlackJack"
    scene = new Scene {
      root = roots
    }
  }
  stage.setResizable(false)
  //goToRoomDetailPage(false)
  //goToRoomListPage()

  def goToMainPage() = {
    val resource = getClass.getResource("/Views/MainPage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.VBox]
    val scene = new Scene(roots)
    
    stage.setScene(scene)
  } 
  
  def goToRoomListPage() = {
    val resource = getClass.getResource("/Views/RoomListPage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    val scene = new Scene(roots)
    
    stage.setScene(scene)
  } 

  def goToRoomDetailPage(isHost:Boolean) = {
    val resource = getClass.getResource("/Views/RoomDetailPage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    var controller = loader.getController[RoomDetailPageController#Controller]
    controller.setIsHost(isHost)

    val scene = new Scene(roots)
    
    stage.setScene(scene)
  } 

}