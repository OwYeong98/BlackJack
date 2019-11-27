package Models

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}

class Room(private var _roomNo: Int, private var _hostName: String,private var _noOfPlayer: Int) {

	
	//property are for table use
	var roomNoProperty = ObjectProperty[Int](roomNo)
	var hostNameProperty = new StringProperty(hostName)
	var noOfPlayerProperty = new StringProperty(noOfPlayer+" / 8")


	//getter
	def roomNo = _roomNo
	def hostName =_hostName
	def noOfPlayer = _noOfPlayer

	//setter
	def roomNo_=(newValue: Int){
		_roomNo=newValue
		roomNoProperty.value = newValue
	}
	def hostName_=(newValue: String){
		_hostName=newValue
		hostNameProperty.value =  newValue
	}
	def noOfPlayer_=(newValue: Int){
		_noOfPlayer=newValue
		noOfPlayerProperty.value = newValue+" / 8"
	}


}