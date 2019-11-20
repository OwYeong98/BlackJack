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

import ds._
import java.net.NetworkInterface
import akka.actor.{ActorSystem, Props, ActorRef}
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._




object MainApp extends JFXApp {
  var roomListPageControllerRef:RoomListPageController#Controller = null
  var roomDetailPageControllerRef:RoomDetailPageController#Controller = null
  var gamePageControllerRef:GamePageController#Controller = null

  var ipAddress:String = ""
  var port:Int = -1
  
  /*********Ask for what port to bind*******/
  var count = -1
  val addresses = (for (inf <- NetworkInterface.getNetworkInterfaces.asScala;
                        add <- inf.getInetAddresses.asScala) yield {
    count = count + 1
    (count -> add)
  }).toMap
  for((i, add) <- addresses){
    println(s"$i = $add")
  }
  println("please select which interface to bind")
  var selection: Int = 0
  do {
    selection = scala.io.StdIn.readInt()
  } while(!(selection >= 0 && selection < addresses.size))

  val ipaddress = addresses(selection)

  val overrideConf = ConfigFactory.parseString(
    s"""
       |akka {
       |  loglevel = "INFO"
       |
 |  actor {
       |    provider = "akka.remote.RemoteActorRefProvider"
       |  }
       |
 |  remote {
       |    enabled-transports = ["akka.remote.netty.tcp"]
       |    netty.tcp {
       |      hostname = "${ipaddress.getHostAddress}"
       |      port = 0
       |    }
       |
 |    log-sent-messages = on
       |    log-received-messages = on
       |  }
       |
 |}
       |
     """.stripMargin)
  val myConf = overrideConf.withFallback(ConfigFactory.load())
  val system = ActorSystem("blackjack", myConf)
  val roomListServerRef = system.actorOf(Props[ds.server.RoomListServerActor](), "roomlistserver")
  




  /***Show Stage UI***/
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
   /******************/

  def goToMainPage() = {
    val resource = getClass.getResource("/Views/MainPage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.VBox]
    val scene = new Scene(roots)
    
    stage.setScene(scene)

    roomListPageControllerRef = null
    roomDetailPageControllerRef = null
    gamePageControllerRef = null

  } 
  
  def goToRoomListPage() = {
    val resource = getClass.getResource("/Views/RoomListPage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    var controller = loader.getController[RoomListPageController#Controller]
    val scene = new Scene(roots)
    
    stage.setScene(scene)
    roomListPageControllerRef = controller
    roomDetailPageControllerRef = null
    gamePageControllerRef = null
  } 

  def goToRoomDetailPage(roomNo:Int,isHost:Boolean, playerName: String, hostActorRef:ActorRef) = {
    val resource = getClass.getResource("/Views/RoomDetailPage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    var controller = loader.getController[RoomDetailPageController#Controller]
    controller.setIsHost(isHost)
    controller.initializeRoomDetail(roomNo,hostActorRef)
    controller.playerName = playerName

    val scene = new Scene(roots)
    
    stage.setScene(scene)

    roomListPageControllerRef = null
    roomDetailPageControllerRef = controller
    gamePageControllerRef = null
  } 

   def goToGamePage(roomNo: Int, playerName: String) = {
    val resource = getClass.getResource("/Views/GamePage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    var controller = loader.getController[GamePageController#Controller]
    controller.initializeData(roomNo,playerName)

    val scene = new Scene(roots)
    
    stage.setScene(scene)

    roomListPageControllerRef = null
    roomDetailPageControllerRef = null
    gamePageControllerRef = controller
  } 

  def showSettingDialog() = {

    val resource = getClass.getResource("/Views/settingPage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots2  = loader.getRoot[jfxs.Parent]
    val control = loader.getController[settingPageController#Controller]

    val dialog = new Stage() {
      initModality(Modality.APPLICATION_MODAL)
      initOwner(stage)
      scene = new Scene {
        root = roots2
      }
    }
    control.dialogStage = dialog
    dialog.showAndWait()
  } 
  

}