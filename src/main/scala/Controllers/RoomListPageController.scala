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

import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image



@sfxml
class RoomListPageController(
	val mainVbox: VBox,
	val createRoomButton: Button, 
	val joinButton: Button,
	val backButton: Button
	 )
{	

	//Initialize graphic
	createRoomButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomListPage/createRoom.png"))))
	joinButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomListPage/join.png"))));
	backButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomListPage/back.png"))));

	def backAction() = {
		MainApp.goToMainPage()
	}

	def joinAction() = {
		
	}




	

	

	
	
	

}