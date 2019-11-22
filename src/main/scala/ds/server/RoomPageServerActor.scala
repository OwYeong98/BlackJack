package ds.server

import MainSystem.MainApp
import akka.actor.{Actor, ActorRef, Props,ActorSelection}
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
import ds.server.RoomPageServerActor.ServerAskRoomClosed
import ds.server.RoomPageServerActor.ServerAskYouAreKicked

import ds.server.RoomListServerActor
import java.util.UUID.randomUUID

class RoomPageServerActor(val hostName:String) extends Actor {
  implicit val timeout = Timeout(10 second)

  val playerListInRoom = Map[String,ActorRef]()
  val playerReadyState = Map[String,Boolean]()


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
        
        //if is host he should be ready for default
        if(name.toLowerCase().equals(hostName.toLowerCase())){
            playerReadyState += (name -> true)
        }else{
            playerReadyState += (name -> false)
        }
        

        //send player list to client
        clientRef ! ServerAskSetHost(hostName)

        if(playerListInRoom.isEmpty ==false ){
            println(playerListInRoom)
            for((playerName,playerActorRef) <- playerListInRoom){
                //if not host we ask them to add because host is add using ServerAskSetHost
                if(!playerName.toLowerCase().equals(hostName.toLowerCase())){
                    clientRef ! ServerAskAddPlayer(playerName)

                    //if the player is already ready ask UI to update it
                    if(playerReadyState(playerName) == true){
                        clientRef ! ServerAskSetPlayerReady(playerName)
                    }
                }
            }

            //update Other Client new player joined
            for((clientName,actorRef) <- playerListInRoom){
                if(actorRef != clientRef){
                    actorRef ! ServerAskAddPlayer(name)
                }
            }
        }
        

        
    case CloseRoom(roomNo) =>
        //cause room closed if we doesnt care if it can be send
        //if cannot contact means they already leave room
        try{
            for((clientName,clientActorRef) <- playerListInRoom){
                //notify everyone
                clientActorRef ! ServerAskRoomClosed()
            }
            val roomListserver: ActorSelection = context.actorSelection(s"akka.tcp://blackjack@${MainApp.ipAddress}:${MainApp.port.toString}/user/roomlistserver")
            roomListserver ! RoomListServerActor.RemoveRoom(roomNo)
        }catch{
            case _: Throwable=>
        }
        

    case KickPlayer(name,roomNo) =>
        for((clientName,clientActorRef) <- playerListInRoom){
            //if it is the player that we want to kick 
            if(clientName.toLowerCase().equals(name.toLowerCase())){
                //tell them they are kicked
                clientActorRef ! ServerAskYouAreKicked()
            }

            //notify everyone
            clientActorRef ! ServerAskRemovePlayer(name)
        }
        playerListInRoom.remove(name)

        //tell room list server to remove room from list
        val roomListserver: ActorSelection = context.actorSelection(s"akka.tcp://blackjack@${MainApp.ipAddress}:${MainApp.port.toString}/user/roomlistserver")
        roomListserver ! RoomListServerActor.PlayerLeaveRoom(roomNo,name)
  

    case PlayerReady(name) =>
        playerReadyState(name) = true
        for((clientName,clientActorRef) <- playerListInRoom){
            //notify everyone
            clientActorRef ! ServerAskSetPlayerReady(name)
        }

    case PlayerNotReady(name) =>
        playerReadyState(name) = false
        for((clientName,clientActorRef) <- playerListInRoom){
            //notify everyone
            clientActorRef ! ServerAskSetPlayerUnReady(name)
        }

    case PlayerLeaveRoom(name,roomNo) =>
        if(name.toLowerCase().equals(hostName.toLowerCase())){
            for((clientName,clientActorRef) <- playerListInRoom){
                //notify everyone
                clientActorRef ! ServerAskRoomClosed()
            }
            //tell room list server to remove room from list
            val roomListserver: ActorSelection = context.actorSelection(s"akka.tcp://blackjack@${MainApp.ipAddress}:${MainApp.port.toString}/user/roomlistserver")
            roomListserver ! RoomListServerActor.RemoveRoom(roomNo)
        }else{
            for((clientName,clientActorRef) <- playerListInRoom){
                //notify everyone
                clientActorRef ! ServerAskRemovePlayer(name)
            }
            //tell room list server to someone leaved room 
            val roomListserver: ActorSelection = context.actorSelection(s"akka.tcp://blackjack@${MainApp.ipAddress}:${MainApp.port.toString}/user/roomlistserver")
            roomListserver ! RoomListServerActor.PlayerLeaveRoom(roomNo,name)
        }
        playerListInRoom.remove(name)
        

    case StartGame() =>
        
        var playerNotReady: String = ""
        var isAllReady:Boolean = true
        //if one of the player is not ready then cannot start
        for((name,isReady) <- playerReadyState){
            if(isReady == false){
                isAllReady=false
                playerNotReady+=name+", "
            }
        }
        
        if(isAllReady == true){
            val gamePageServerActorRef = MainApp.system.actorOf(Props(new ds.server.GamePageServerActor()), "gamepageserver"+randomUUID().toString)

            for((clientName,clientActorRef) <- playerListInRoom){
                clientActorRef ! ServerAskStartGame(gamePageServerActorRef,clientName)
            }

            //wait 2 second and start the game
            Thread.sleep(2000)
            gamePageServerActorRef ! "startGame"
            sender ! "ok"
        }else{
            sender ! playerNotReady+" are not Ready."
        }

        
    

    case _=>
      println("nothing")
  }
}
object RoomPageServerActor {
  final case class JoinRoomAndSubscribeForUpdate(name:String,ref: ActorRef)
  final case class CloseRoom(roomNo:Int)
  final case class KickPlayer(name: String,roomNo:Int)
  final case class StartGame()
  final case class PlayerReady(name: String)
  final case class PlayerNotReady(name: String)
  final case class PlayerLeaveRoom(name: String, roomNo: Int)
  final case class ServerAskAddPlayer(name:String)
  final case class ServerAskRemovePlayer(name:String)
  final case class ServerAskSetHost(name:String)
  final case class ServerAskYouAreKicked()
  final case class ServerAskSetPlayerReady(name:String)
  final case class ServerAskSetPlayerUnReady(name:String)
  final case class ServerAskStartGame(gameServerActorRef:ActorRef,playerName:String)
  final case class ServerAskRoomClosed()
}