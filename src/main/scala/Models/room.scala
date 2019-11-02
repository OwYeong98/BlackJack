package Models

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}

class room(private var _roomNo: Int, private var _hostName: String,private var _noOfPlayer: Int){

	
	//property are for table use
	var roomNoProperty = ObjectProperty[Int](roomNo)
	var hostNameProperty = new StringProperty(hostName)
	var noOfPlayerProperty = new StringProperty(noOfPlayer+" / 7")


	//getter
	def roomNo = _roomNo
	def hostName =_hostName
	def noOfPlayer = _noOfPlayer

	//setter
	def roomNo_=(newValue: Int){
		_roomNo=newValue
		roomNoProperty = ObjectProperty[Int](newValue)
	}
	def hostName_=(newValue: String){
		_hostName=newValue
		hostNameProperty =  new StringProperty(newValue)
	}
	def noOfPlayer_=(newValue: Int){
		_noOfPlayer=newValue
		noOfPlayerProperty = new StringProperty(newValue+" / 7")
	}


}