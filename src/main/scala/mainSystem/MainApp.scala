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
  stage.setResizable(false);
  goToRoomDetailPage()

  def goToRoomListPage() = {
    val resource = getClass.getResource("/Views/RoomListPage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    
    stage.getScene().setRoot(roots)
  } 

  def goToRoomDetailPage() = {
    val resource = getClass.getResource("/Views/RoomDetailPage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    
    stage.getScene().setRoot(roots)
  } 

}