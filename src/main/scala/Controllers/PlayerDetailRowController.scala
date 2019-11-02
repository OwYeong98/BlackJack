package Controllers

import MainSystem.MainApp

import scalafxml.core.macros.sfxml
import scalafx.scene.control._
import scalafx.scene.input._
import scalafx.scene.layout.AnchorPane
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType //contail all the implicits to change javafx classes to scalafx classes as necessary
import scalafx.application.Platform
import scalafx.scene.layout.VBox

import scalafx.scene.control.Label
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image



@sfxml
class PlayerDetailRowController(
	val dealerContainer: AnchorPane,
	val userIcon: ImageView, 
	val name: Label,
	val readyStatus: ImageView,
	val kickButton: Button
	 )
{	
	var roomDetailControllerReference: RoomDetailPageController#Controller = null

	//Initialize graphic
	kickButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/kick.png"))))
	userIcon.image = new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/player.png"))
	readyStatus.image = new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/notreadyStatus.png"))

	def hideKickButton() = {
		//hide the kick button if this is not
		kickButton.visible = false
	}

	def setReady() = {
		readyStatus.image = new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/readyStatus.png"))
	}
	
	def setNotReady() = {
		readyStatus.image = new Image(getClass.getResourceAsStream("/Images/RoomDetailPage/notreadyStatus.png"))
	}

	def kick()={

		roomDetailControllerReference.kicked(name.text.value)
	}
	

}