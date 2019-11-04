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
import scalafx.stage.{Modality, Stage}


@sfxml
class settingPageController(
	val ipAddressTextField: TextField
	 )
{	
	var dialogStage:Stage =null
	

	def okAction() = {
        MainApp.ipAddress = ipAddressTextField.text.value
        dialogStage.close()
	}
	
	def cancelAction() = {
        
		dialogStage.close()
	}


}