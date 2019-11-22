package Controllers

import MainSystem.MainApp

import scalafxml.core.macros.sfxml
import scalafx.scene.control._
import scalafx.scene.input._
import scalafx.scene.layout.AnchorPane
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType 
import scalafx.application.Platform
import scalafx.scene.layout.VBox

import scalafx.scene.control.Label
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image
import scalafx.stage.{Modality, Stage}

import java.lang.NumberFormatException


@sfxml
class settingPageController(
	val ipAddressTextField: TextField,
	val portTextField: TextField
	 )
{	
	var dialogStage:Stage =null
	

	def okAction() = {
		try{
			var portNum: Int = portTextField.text.value.toInt
			MainApp.ipAddress = ipAddressTextField.text.value
			MainApp.port = portNum
			dialogStage.close()
		}catch{
			case e: NumberFormatException =>
				val alert = new Alert(AlertType.Error){
		        initOwner(MainApp.stage)
		        title       = "Error"
		        headerText  = "Port Number must be number"
		        contentText = "Cannot convert port to Integer!"
		      }.showAndWait()
		}
	}
	
	def cancelAction() = {
        
		dialogStage.close()
	}


}