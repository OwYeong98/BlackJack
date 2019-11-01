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


@sfxml
class RoomDetailPageController(
	val playerListVBoxContainer: VBox,
    val dealerIcon: ImageView,
	val dealerContainer: AnchorPane,
    val actionButton: Button,
	val leaveRoomButton: Button
	 )
{	
    //initialize graphic
    dealerIcon.image = new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/dealer.png"))
    leaveRoomButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/leaveroom.png"))))
    actionButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/ready.png"))))

    //this variable indicite whether user is Host of the room
    var isHost: Boolean = true

    var playerInRoomList:ListBuffer[PlayerDetailRowController#Controller] = ListBuffer[PlayerDetailRowController#Controller]()

    addPlayerToList("Jack")
    addPlayerToList("OwYeong")
    addPlayerToList("Gilbert")
    addPlayerToList("Erncheng")
    
    

	def setIsHost(value:Boolean) = {
		if(value == true){
			actionButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/start.png"))))
		}else{
			actionButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/ready.png"))))
		}
	}

    def readyAction() = {
        actionButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/unready.png"))))

    }

    def unreadyAction() = {
        actionButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/ready.png"))))
    }

    def leaveRoomAction() = {

    }

    def addPlayerToList(name: String){
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

    def removePlayerFromList(name: String){
        for(row <- playerInRoomList){
            if(row.name.text.value == name){
                playerListVBoxContainer.getChildren().remove(row.dealerContainer)
                playerInRoomList -= row
            }
        }
    }





	

	

	
	
	

}