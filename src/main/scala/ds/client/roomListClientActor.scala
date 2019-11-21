package ds.client

import MainSystem.MainApp

import scalafx.scene.control._
import scalafx.scene.input._

import akka.actor.{Actor, ActorRef,ActorSelection, Props}
import scalafx.collections.ObservableHashSet
import akka.pattern.ask
import akka.remote.DisassociatedEvent
import akka.util.Timeout
import scala.collection.mutable.ArrayBuffer
import scalafx.scene.control.Alert.AlertType 
import scalafx.application.Platform

import scala.concurrent.Await
import scala.concurrent.duration._
import Models.Room

import scala.concurrent.ExecutionContext.Implicits._

import ds.client.RoomListClientActor.Join
import ds.client.RoomListClientActor.CreateRoom
import ds.server.RoomListServerActor

import java.util.UUID.randomUUID

class RoomListClientActor extends Actor {
  implicit val timeout = Timeout(10 second)

  var serverOpt: Option[ActorSelection] = None    

  override def preStart(): Unit = {
    val server: ActorSelection = context.actorSelection(s"akka.tcp://blackjack@${MainApp.ipAddress}:${MainApp.port.toString}/user/roomlistserver")
    serverOpt = Option(server)
    context.system.eventStream.subscribe(self, classOf[akka.remote.DisassociatedEvent])
    context.system.eventStream.subscribe(self, classOf[akka.remote.AssociatedEvent])

  }

  def receive = {
    //remove to the client that close connection
    case DisassociatedEvent(localAddress, remoteAddress, _) =>
      val alert = new Alert(AlertType.Error){
		        initOwner(MainApp.stage)
		        title       = "Lost connection to server"
		        headerText  = "Server Connection Lost"
		        contentText = "Could Not Connect to Server!"
		      }.showAndWait()
    
    /*********Call from controller************************/
    case "getRoomList" =>
        for (server <- serverOpt){
            var result = server ? RoomListServerActor.GetRoomListAndSubscribeForUpdate(context.self)

            Await.result(result,1 second)

            result.foreach(x => {
                val roomList:ArrayBuffer[RoomListServerActor.RoomUpdate] = x.asInstanceOf[ArrayBuffer[RoomListServerActor.RoomUpdate]]
                
                for(room <- roomList){
                  Platform.runLater{
                    MainApp.roomListPageControllerRef.addNewRoomOrUpdate(room.no,room.hostName,room.noOfPlayer)
                  }
                }
                
                
                
            })
            
        }

    case Join(roomNo,name) =>
      for (server <- serverOpt){
        var result = server ? RoomListServerActor.Join(roomNo,name)

        result.foreach(x => {
          x match {
              case hostActor: ActorRef =>
                Platform.runLater{
                  MainApp.goToRoomDetailPage(roomNo,false,name,hostActor)
                }
              case error:String =>
                Platform.runLater {
                  val alert = new Alert(AlertType.Error){
                    initOwner(MainApp.stage)
                    title       = "Error Join Room"
                    headerText  = "Error:"
                    contentText = error
                  }.showAndWait()	
                }
              case _ => 
          }
        })


      }

    case CreateRoom(name) =>
      for (server <- serverOpt){
        //since user create room he will be responsible as a server
        val roomServer:ActorRef = MainApp.system.actorOf(Props(new ds.server.RoomPageServerActor(name)), "roompageserver"+randomUUID().toString)
        
        var result = server ? RoomListServerActor.CreateRoom(name,roomServer)

        result.foreach(x => {
          x match {
              case RoomListServerActor.SuccessCreateRoom(roomNo) =>
                Platform.runLater{
                  MainApp.goToRoomDetailPage(roomNo,true,name,roomServer)
                }
              case error:String =>
                Platform.runLater {
                  val alert = new Alert(AlertType.Error){
                    initOwner(MainApp.stage)
                    title       = "Error Create Room"
                    headerText  = "Error"
                    contentText = x.asInstanceOf[String]
                  }.showAndWait()	
                }
              case _ => 
          }
        })
      }



    /*********call from server*************************/
    case RoomListServerActor.RoomUpdate(no,hostName,noOfPlayer) =>
      if(MainApp.roomListPageControllerRef != null){
        Platform.runLater{
          MainApp.roomListPageControllerRef.addNewRoomOrUpdate(no,hostName,noOfPlayer)
        }
      }
      
    
    case RoomListServerActor.RemoveRoom(roomNo) =>
      if(MainApp.roomListPageControllerRef != null){
        Platform.runLater{
          MainApp.roomListPageControllerRef.deleteRoom(roomNo)
        }
      }
      
    case _=>
  }
}

object RoomListClientActor {
  final case class Join(roomNo:Int,name:String)
  final case class CreateRoom(name:String)



}