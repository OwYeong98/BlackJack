package ds.server

import MainSystem.MainApp
import akka.actor.{Actor, ActorRef, Props}
import scalafx.collections.ObservableHashSet
import akka.pattern.ask
import akka.remote.DisassociatedEvent
import akka.util.Timeout
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

import scala.concurrent.Await
import scala.concurrent.duration._

import ds.server.RoomPageServerActor.JoinRoomAndSubscribeForUpdate
import ds.server.RoomPageServerActor.CloseRoom
import ds.server.RoomPageServerActor.KickPlayer
import ds.server.RoomPageServerActor.StartGame
import ds.server.RoomPageServerActor.PlayerReady
import ds.server.RoomPageServerActor.PlayerNotReady
import ds.server.RoomPageServerActor.PlayerLeaveRoom
import ds.server.RoomPageServerActor.ServerAskAddPlayer
import ds.server.RoomPageServerActor.ServerAskRemovePlayer
import ds.server.RoomPageServerActor.ServerAskSetHost
import ds.server.RoomPageServerActor.ServerAskSetPlayerReady
import ds.server.RoomPageServerActor.ServerAskSetPlayerUnReady
import ds.server.RoomPageServerActor.ServerAskStartGame

class RoomPageServerActor(val hostName:String) extends Actor {
  implicit val timeout = Timeout(10 second)

  val playerListInRoom = Map[String,ActorRef]()


  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[akka.remote.DisassociatedEvent])
    context.system.eventStream.subscribe(self, classOf[akka.remote.AssociatedEvent])
  }

  def receive = {
    //remove to the client that close connection
    case DisassociatedEvent(localAddress, remoteAddress, _) =>
      if(playerListInRoom.exists(x => x._2.path.address.equals(remoteAddress))){
        val refOpt = playerListInRoom.find(x => x._2.path.address.equals(remoteAddress))
        for (ref <- refOpt){
          playerListInRoom.remove(ref._1)
        }
      }
    case JoinRoomAndSubscribeForUpdate(name,clientRef) =>
        //add player ref into the room
        playerListInRoom += (name -> clientRef)

        //update player that are in the room
        clientRef ! ServerAskSetHost(hostName)

        for((playerName,playerActorRef) <- playerListInRoom){
            //if not host we ask them to add because host is add using ServerAskSetHost
            if(!playerName.toLowerCase().equals(hostName.toLowerCase())){
                clientRef ! ServerAskAddPlayer(playerName)
            }
        }
    case CloseRoom() =>

    case KickPlayer(name) =>

    case PlayerReady(name) =>

    case PlayerNotReady(name) =>

    case PlayerLeaveRoom(name) =>

    case StartGame() =>
        val gamePageServerActorRef = MainApp.system.actorOf(Props(new ds.server.GamePageServerActor()), "gamepageserver")

        for((clientName,clientActorRef) <- playerListInRoom){
            clientActorRef ! ServerAskStartGame(gamePageServerActorRef,clientName)
        }


    

    case _=>
      println("nothing")
  }
}
object RoomPageServerActor {
  final case class JoinRoomAndSubscribeForUpdate(name:String,ref: ActorRef)
  final case class CloseRoom()
  final case class KickPlayer(name: String)
  final case class StartGame()
  final case class PlayerReady(name: String)
  final case class PlayerNotReady(name: String)
  final case class PlayerLeaveRoom(name: String)
  final case class ServerAskAddPlayer(name:String)
  final case class ServerAskRemovePlayer(name:String)
  final case class ServerAskSetHost(name:String)
  final case class ServerAskYouAreKicked()
  final case class ServerAskSetPlayerReady(name:String)
  final case class ServerAskSetPlayerUnReady(name:String)
  final case class ServerAskStartGame(gameServerActorRef:ActorRef,playerName:String)
}