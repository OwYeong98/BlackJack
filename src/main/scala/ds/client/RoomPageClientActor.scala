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

import ds.client.RoomPageClientActor.Join
import ds.client.RoomPageClientActor.Start
import ds.client.RoomPageClientActor.Ready
import ds.client.RoomPageClientActor.NotReady
import ds.client.RoomPageClientActor.LeaveRoom
import ds.client.RoomPageClientActor.Kick
import ds.server.RoomPageServerActor
import ds.server.GamePageServerActor
import ds.client.GamePageClientActor

import java.util.UUID.randomUUID

class RoomPageClientActor(var hostServerActorRef:ActorRef) extends Actor {
  implicit val timeout = Timeout(10 second)

  override def preStart(): Unit = {
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
    case Join(name) =>
        hostServerActorRef ! RoomPageServerActor.JoinRoomAndSubscribeForUpdate(name,context.self)
    case Start() =>
        hostServerActorRef ! RoomPageServerActor.StartGame()

    case Ready(name) =>
        hostServerActorRef ! RoomPageServerActor.PlayerReady(name)

    case NotReady(name) =>
        hostServerActorRef ! RoomPageServerActor.PlayerNotReady(name)

    case LeaveRoom(name) =>
        hostServerActorRef ! RoomPageServerActor.PlayerLeaveRoom(name)

    case Kick(name) =>
        hostServerActorRef ! RoomPageServerActor.KickPlayer(name)



    /*********call from server*************************/
    case RoomPageServerActor.ServerAskAddPlayer(name) =>
        println("add plater")
        if(MainApp.roomDetailPageControllerRef != null){
            Platform.runLater{
                MainApp.roomDetailPageControllerRef.addPlayerToList(name)
            }
        }
    case RoomPageServerActor.ServerAskSetHost(name) =>
        if(MainApp.roomDetailPageControllerRef != null){
            Platform.runLater{
                MainApp.roomDetailPageControllerRef.setHostName(name)
               
            }
        }

    case RoomPageServerActor.ServerAskRemovePlayer(name) =>
        if(MainApp.roomDetailPageControllerRef != null){
            Platform.runLater{
                MainApp.roomDetailPageControllerRef.removePlayerFromList(name)
            }
        }

    case RoomPageServerActor.ServerAskYouAreKicked() =>
        if(MainApp.roomDetailPageControllerRef != null){
            Platform.runLater{
                MainApp.roomDetailPageControllerRef.youHaveBeenKicked()
            }
        }
    case RoomPageServerActor.ServerAskSetPlayerReady(name)=>
        if(MainApp.roomDetailPageControllerRef != null){
            Platform.runLater{
                MainApp.roomDetailPageControllerRef.setPlayerStatusToReady(name)
            }
        }
    case RoomPageServerActor.ServerAskSetPlayerUnReady(name)=>
        if(MainApp.roomDetailPageControllerRef != null){
            Platform.runLater{
                MainApp.roomDetailPageControllerRef.setPlayerStatusToNotReady(name)
            }
        }
    case RoomPageServerActor.ServerAskStartGame(gameServerActorRef,yourName)=>
        val gamePageClientActorRef = MainApp.system.actorOf(Props(new ds.client.GamePageClientActor(gameServerActorRef)), "gamepageclient"+randomUUID().toString)
        gamePageClientActorRef ! GamePageClientActor.InitialConnectionWithServer(yourName)

        MainApp.goToGamePage(yourName,gamePageClientActorRef)

    case RoomPageServerActor.ServerAskRoomClosed()=>
        if(MainApp.roomDetailPageControllerRef != null){
            Platform.runLater{
                MainApp.roomDetailPageControllerRef.roomHadClosed()
            }
        }



    case _=>
  }
}

object RoomPageClientActor {
  final case class Join(name:String)
  final case class Start()
  final case class Ready(name:String)
  final case class NotReady(name:String)
  final case class LeaveRoom(name:String)
  final case class Kick(name:String)




}