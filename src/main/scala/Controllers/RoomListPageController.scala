package Controllers

import MainSystem.MainApp
import Models.Room

import scalafxml.core.macros.sfxml
import scalafx.scene.control._
import scalafx.scene.input._
import scalafx.scene.layout.AnchorPane
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType //contail all the implicits to change javafx classes to scalafx classes as necessary
import scalafx.application.Platform
import scalafx.scene.layout.VBox
import scalafx.scene.control.{Alert,TableColumn,TableView,TableCell}
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.{StringProperty, ObjectProperty}

import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image

import akka.actor.{Actor, ActorRef,Props}




@sfxml
class RoomListPageController(
	val mainVbox: VBox,
	val createRoomButton: Button, 
	val joinButton: Button,
	val backButton: Button,
	val roomTable: TableView[Room],
	val roomNoColumn: TableColumn[Room, Int],
	val hostNameColumn: TableColumn[Room, String],
	val noOfPlayerColumn: TableColumn[Room, String],
	val nameInput: TextField
	 )
{	

	//Initialize graphic
	createRoomButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomListPage/createRoom.png"))))
	joinButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomListPage/join.png"))));
	backButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/RoomListPage/back.png"))));


	//Kind of arraylist for displaying data to table
	var roomList:ObservableBuffer[Room] = new ObservableBuffer[Room]()
	initializeRoomList()

	//this store the roomListClientRef
	var roomListClientRef: ActorRef = null

	/**********************Function For Network**********************************************/
	initializeData()
	def initializeData() = {
		//initialize list of room available in the server

		//maybe ask server for room list available here
		// and also server can keep track of akka actor and provide update as needed

		//this actor will responsible for all function of this page including instruction from server
		roomListClientRef = MainApp.system.actorOf(Props[ds.client.roomListClientActor](), "client")
		
		//get room list will update the roomlist to UI and subscribe for receiving update from server
		roomListClientRef ! "getRoomList"


		//***sample***
		

	}

	//this function will be called when the createRoom button is clicked
	def createRoomAction() = {
		//this variable store the name input by user in the User Interface
		var userName:String = nameInput.text.value
		if (userName == "") {
			val alert = new Alert(AlertType.Error){
		        initOwner(MainApp.stage)
		        title       = "No Username"
		        headerText  = "UserName is Empty"
		        contentText = "Please Enter username with at least one char"
		      }.showAndWait()


			
		} else{
			//create room in server
			










			//chg to room detail page
			MainApp.goToRoomDetailPage(1,true,userName)
		}
	}

	//this function will be called when join button clicked
	def joinAction() = {
		val selectedIndex = roomTable.selectionModel().selectedIndex.value

		if (selectedIndex >= 0) {
			var selectedRoom:Room = roomList.get(selectedIndex)

			var userName:String = nameInput.text.value
			if (userName == "") {
				val alert = new Alert(AlertType.Error){
			        initOwner(MainApp.stage)
			        title       = "No Username"
			        headerText  = "UserName is Empty"
			        contentText = "Please Enter username with at least one char"
			      }.showAndWait()	
			} else{
				//join room in server
				





				//chg to room detail page
				MainApp.goToRoomDetailPage(selectedRoom.roomNo,false,userName)
			}
			
		} else{

			val alert = new Alert(AlertType.Error){
		        initOwner(MainApp.stage)
		        title       = "No Selection"
		        headerText  = "No Room Selected"
		        contentText = "Please select a Room in the table."
		      }.showAndWait()
		}
	}


	/***************************************************************************************/

	
	/**********************Function For editing the UI**********************************************/

	//call this function to add a row to the table
	//Note calling addNewRoomOrUpdate(1,"John",6) will update the row instead of adding new row if table already have a row with id 1
	def addNewRoomOrUpdate(roomNo:Int,hostName:String,noOfPlayer:Int) = {
		var foundRoom:Room = null
		for(room <- roomList){
			if(room.roomNo == roomNo)
				foundRoom = room
		}

		//if not exist add new
		if(foundRoom == null){
			roomList += new Room(roomNo,hostName,noOfPlayer)
		}else{
			foundRoom.roomNo = roomNo
			foundRoom.hostName = hostName
			foundRoom.noOfPlayer = noOfPlayer
		}
	}

	//call this function delete a row with a id in the table 
	def deleteRoom(roomNo:Int) = {
		var foundRoom:Room = null
		for(room <- roomList){
			if(room.roomNo == roomNo)
				foundRoom = room
		}
		roomList-=foundRoom
	}

	def initializeRoomList() = {
		
		//show to tableview
		roomTable.items = roomList

		// initialize columns's cell values
	  	roomNoColumn.cellValueFactory = { _.value.roomNoProperty}
	  	hostNameColumn.cellValueFactory = { _.value.hostNameProperty}
	  	noOfPlayerColumn.cellValueFactory = { _.value.noOfPlayerProperty}
	}
	/**************************************************************************************************/


	
	def backAction() = {
		MainApp.goToMainPage()
	}
}