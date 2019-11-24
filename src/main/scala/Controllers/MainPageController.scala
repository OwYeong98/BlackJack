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

import akka.actor.{Actor, ActorRef,ActorSelection}
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Await
import MainSystem.MainApp
import java.time.Duration
import scala.util.Success
import scala.util.Failure

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
	var myBackgroundImage= new BackgroundImage(new Image(getClass.getResourceAsStream("/Images/mainpagebg.png")),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          new BackgroundSize(100, 100, true, true, true, true));


	//set background image to anchorpane
	mainVbox.setBackground(new Background(myBackgroundImage));

	startGameButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/StartGame.png"))));
	settingsButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/Settings.png"))));


	def startAction() = {
		//test connection
		try{
			//test connection
			//actorselection will throw error if cant connect after 2 second
			val server = Await.result(MainApp.system.actorSelection(s"akka.tcp://blackjack@${MainApp.ipAddress}:${MainApp.port.toString}/user/roomlistserver").resolveOne(1 second),1 seconds)
			
			//if can go here means connection success can go roomlistpage
			MainApp.goToRoomListPage()
		}catch{
			case e:Exception=>
				val alert = new Alert(AlertType.Error){
		        initOwner(MainApp.stage)
		        title       = "Connection error"
		        headerText  = "Could not connect to server"
		        contentText = "Please check ip address and port in settings"
		      }.showAndWait()
		}	

	}

	def settingAction() = {
		MainApp.showSettingDialog()
	}


	

	

	
	
	

}