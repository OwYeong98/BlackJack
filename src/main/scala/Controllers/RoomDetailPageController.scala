package Controllers

import MainSystem.MainApp
import scala.collection.mutable.ListBuffer

import scalafxml.core.macros.sfxml
import scalafx.scene.control._
import scalafx.scene.input._
import scalafx.scene.layout.AnchorPane
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType //contail all the implicits to change javafx classes to scalafx classes as necessary
import scalafx.application.Platform
import scalafx.scene.layout.VBox

import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image

import scalafxml.core.{NoDependencyResolver, FXMLView, FXMLLoader}
import javafx.{scene => jfxs}

import akka.actor.{Actor, ActorRef,Props}

import ds.client.RoomPageClientActor

import java.util.UUID.randomUUID

@sfxml
class RoomDetailPageController(
	val playerListVBoxContainer: VBox,
    val hostIcon: ImageView,
	val dealerContainer: AnchorPane,
    val readyButton: Button,
    val unreadyButton: Button,
    val startButton: Button,
	val leaveRoomButton: Button,
    val hostNameLabel: Label,
    val roomNoLabel: Label
	 )
{	
    //initialize graphic
    hostIcon.image = new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/host.png"))
    leaveRoomButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/leaveroom.png"))))
    readyButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/ready.png"))))
    unreadyButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/unready.png"))))
    startButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/start.png"))))

    //this variable indicite whether user is Host of the room
    var isHost: Boolean = true
    var roomId:Int = 0
    //this variable store the player name of this client
    var playerName:String = null

    //List for string player in the room
    var playerInRoomList:ListBuffer[PlayerDetailRowController#Controller] = ListBuffer[PlayerDetailRowController#Controller]()  

    //Actor Ref Client
    var roomPageClientRef: ActorRef = null

    /**********************Function For Network**********************************************/

    //this function will be called when the page load
    def initializeRoomDetail(roomId: Int, hostActorRef:ActorRef) = {
        //save this roodId as class variable so we can access it anywhere in the class
        this.roomId = roomId
        roomNoLabel.text = roomId.toString()

        roomPageClientRef = MainApp.system.actorOf(Props(new ds.client.RoomPageClientActor(hostActorRef)), "roompageclient-"+randomUUID().toString)
        
        //this call will ask server to initialze player detail
        roomPageClientRef ! RoomPageClientActor.Join(playerName)
    }

    //this function will be called when player press start button
    def startAction() = {
        roomPageClientRef ! RoomPageClientActor.Start()

        //MainApp.goToGamePage(roomId,playerName)
    }

    //this function will be called when player press ready button
    def readyAction() = {
        roomPageClientRef ! RoomPageClientActor.Ready(playerName)

        //show unready button and hide unready
        readyButton.visible=false
        unreadyButton.visible=true
    }

    //this function will be called when player press unready button
    def unreadyAction() = {
        roomPageClientRef ! RoomPageClientActor.NotReady(playerName)
        
        //show ready button and hide unready
        readyButton.visible=true
        unreadyButton.visible=false
    }

    //this function will be called when player press leaveroom button
    def leaveRoomAction() = {
        roomPageClientRef ! RoomPageClientActor.LeaveRoom(playerName,roomId)
        MainApp.system.stop(roomPageClientRef)
        MainApp.goToRoomListPage()
    }

    //this function will be called when host kick someone
    //nameOfPlayer store the name of the player who get kicked
    def kicked(nameOfPlayer: String){
        roomPageClientRef ! RoomPageClientActor.Kick(nameOfPlayer,roomId)

        
    }


    /*****************************************************************************************/



    
    /**********************Function For editing the UI**********************************************/

    //this function change the bottom right button to Start if user is a host else the bottom right button will be ready buttom
	def setIsHost(value:Boolean) = {
        isHost = value
		if(value == true){
            startButton.visible=true
            readyButton.visible=false
            unreadyButton.visible=false
		}else{
            readyButton.visible=true
            unreadyButton.visible=false
            startButton.visible=false
            
		}
	}
    def setHostName(name:String) = {
        hostNameLabel.text = name
    }

    def addPlayerToList(name: String)={
        //load the player detail row fxml template
        val resource = getClass.getResource("/Views/PlayerDetailRow.fxml")
        val loader = new FXMLLoader(resource, NoDependencyResolver)
        loader.load();

        //set the name of the newly joined player
        var controller = loader.getController[PlayerDetailRowController#Controller]
        controller.name.text = name
        controller.roomDetailControllerReference = this
        
        //save controller reference
        playerInRoomList+= controller

        //if not host hide the kick button
        if(isHost == false){
            controller.hideKickButton()
        }
        
        //add the detail template to the list
        val newPlayerDetail:AnchorPane = loader.getRoot[jfxs.layout.AnchorPane]
        playerListVBoxContainer.getChildren().add(newPlayerDetail)
    }

    //call this function to remove player from list providing the playername
    def removePlayerFromList(name: String)={
        for(row <- playerInRoomList){
            if(row.name.text.value.toLowerCase() == name.toLowerCase()){
                playerListVBoxContainer.getChildren().remove(row.dealerContainer)
                playerInRoomList -= row
            }
        }
    }

    //call this function to set player to ready  providing the playername
    def setPlayerStatusToReady(name: String) = {
        for(row <- playerInRoomList){
            if(row.name.text.value.toLowerCase() == name.toLowerCase()){
                row.setReady()
            }
        }

    }

    //call this function to set player to not ready  providing the playername
     def setPlayerStatusToNotReady(name: String) = {
         for(row <- playerInRoomList){
            if(row.name.text.value.toLowerCase() == name.toLowerCase()){
                row.setNotReady()
            }
        }        
    }

    def youHaveBeenKicked() = {
        val alert = new Alert(AlertType.Error){
			        initOwner(MainApp.stage)
			        title       = "Kicked"
			        headerText  = "Kicked By Host"
			        contentText = "Opps! You have been kicked by host."
			      }.showAndWait()	
        MainApp.system.stop(roomPageClientRef)
        MainApp.goToRoomListPage()
    }

    def roomHadClosed() = {
        val alert = new Alert(AlertType.Error){
			        initOwner(MainApp.stage)
			        title       = "Closed"
			        headerText  = "Room has benn Closed!"
			        contentText = "Host has Closed the Room!"
			      }.showAndWait()	
        MainApp.system.stop(roomPageClientRef)
        MainApp.goToRoomListPage()
    }
    /*******************************************************************************************************/





	

	

	
	
	

}