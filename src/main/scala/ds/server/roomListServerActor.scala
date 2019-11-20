package ds.server

import akka.actor.{Actor, ActorRef}
import scalafx.collections.ObservableHashSet
import akka.pattern.ask
import akka.remote.DisassociatedEvent
import akka.util.Timeout
import scala.collection.mutable.ArrayBuffer

import scala.concurrent.Await
import scala.concurrent.duration._

import Models.Room
import ds.server.ServerModels.RoomInfo
import ds.server.RoomListServerActor.GetRoomListAndSubscribeForUpdate
import ds.server.RoomListServerActor.RoomUpdate
import ds.server.RoomListServerActor.CreateRoom
import ds.server.RoomListServerActor.Join
import ds.server.RoomListServerActor.RemoveRoom
import ds.server.RoomListServerActor.SuccessCreateRoom

class RoomListServerActor extends Actor {
  implicit val timeout = Timeout(10 second)

  //this variable store all the client actor Ref viewing the roomlist page
  val clientViewingRoomListPage = new ObservableHashSet[ActorRef]()
  
  //this variable will store all room list that are created
  val roomList: ArrayBuffer[RoomInfo] = new ArrayBuffer[RoomInfo]()


  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[akka.remote.DisassociatedEvent])
    context.system.eventStream.subscribe(self, classOf[akka.remote.AssociatedEvent])

  }

  def receive = {
    //remove to the client that close connection
    case DisassociatedEvent(localAddress, remoteAddress, _) =>
      if(clientViewingRoomListPage.exists(x => x.path.address.equals(remoteAddress))){
        val refOpt = clientViewingRoomListPage.find(x => x.path.address.equals(remoteAddress))
        for (ref <- refOpt){
          clientViewingRoomListPage.remove(ref)
        }
      }
    case GetRoomListAndSubscribeForUpdate(actorRef) =>
      //add the client ActorRef to the list so that if room got any update we ill notify them
      clientViewingRoomListPage += actorRef

      var roomListUpdate: ArrayBuffer[RoomUpdate] = new ArrayBuffer[RoomUpdate]()

      for(room <- roomList){
        roomListUpdate+=RoomUpdate(room.roomNo,room.hostName,room.playerList.length)
      }
      sender ! roomListUpdate
    case CreateRoom(hostName,hostActorRef) =>
      var maxRoomNo: Int = 0;

      for(room <- roomList){
        if(room.roomNo > maxRoomNo){
          maxRoomNo = room.roomNo
        }
      }

      var newRoomNo = maxRoomNo +1

      var playerList = ArrayBuffer[String]()
      playerList += hostName  
      var newRoom = new RoomInfo(newRoomNo,hostName,playerList,hostActorRef)
      roomList +=newRoom

      //notify all client about new room
      for(clientRef <- clientViewingRoomListPage){
        clientRef ! RoomUpdate(newRoom.roomNo,newRoom.hostName,newRoom.playerList.length)
      }

      sender ! SuccessCreateRoom(newRoomNo)
    case Join(roomNo,name) =>
      var roomRef:RoomInfo = null

      for(room <- roomList){
        if(room.roomNo == roomNo){
          roomRef = room
        }
      }

      if(roomRef == null){
        sender ! "Room Not Found!"
      }else if(roomRef.playerList.length>= 8){
        sender ! "Room is Full!"
      }else if(roomRef.playerList.exists(x=> x.toLowerCase().equals(name.toLowerCase()))){
        sender ! s"SomeOne already used the Name '$name'"
      }else{
        roomRef.playerList += name
        //notify all client about num of player in room
        for(clientRef <- clientViewingRoomListPage){
          clientRef ! RoomUpdate(roomRef.roomNo,roomRef.hostName,roomRef.playerList.length)
        }

        //send the host Actor Ref so client know who to contact
        sender ! roomRef.hostActorRef
      }
    case RemoveRoom(roomNo) =>
      var roomRef:RoomInfo = null

      for(room <- roomList){
        if(room.roomNo == roomNo){
          roomRef = room
        }
      }
      //remove the room from list
      roomList -= roomRef

    case _=>
      println("nothing")
  }
}
object RoomListServerActor {
  final case class GetRoomListAndSubscribeForUpdate(ref: ActorRef)
  final case class RoomUpdate(no: Int,hostName: String,noOfPlayer: Int)
  final case class Join(roomNo: Int,name: String)
  final case class CreateRoom(hostName: String, hostActorRef: ActorRef)
  final case class RemoveRoom(roomNo: Int)
  final case class SuccessCreateRoom(assignedRoomNo:Int)


}