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
import javafx.scene.layout.BackgroundImage
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundSize
import javafx.scene.layout.Background


@sfxml
class MainPageController(
	val mainVbox: VBox,
	val startGameButton: Button, 
	val settingsButton: Button,
	val background: ImageView
	 )
{	

	//Initialize graphic
	//set Anchor Pane background Image
	//set Game board Anchor Pane background Image
	var myBackgroundImage= new BackgroundImage(new Image(getClass.getResourceAsStream("/Images/MainpageBackground.png")),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          new BackgroundSize(100, 100, true, true, true, true));


	//set background image to anchorpane
	mainVbox.setBackground(new Background(myBackgroundImage));

	startGameButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/StartGame.png"))));
	settingsButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/Settings.png"))));


	def startAction() = {
		MainApp.goToRoomListPage()
	}

	def settingAction() = {

	}


	

	

	
	
	

}