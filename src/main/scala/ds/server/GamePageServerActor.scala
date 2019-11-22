package ds.server

import akka.actor.{Actor, ActorRef}
import scalafx.collections.ObservableHashSet
import akka.pattern.ask
import akka.remote.DisassociatedEvent
import akka.util.Timeout
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

import scala.concurrent.Await
import scala.concurrent.duration._

import ds.server.GamePageServerActor.JoinGameAndSubscribeForUpdate

class GamePageServerActor extends Actor {
  implicit val timeout = Timeout(10 second)

  //variable playerListInRoom contain a Map where key is name and value is ActorRef
  //example playerListInRoom("John") will return GamePageClientActorRef of John
  val playerListInRoom = Map[String,ActorRef]()

  override def preStart(): Unit = {


  }

  def receive = {
    //this startGame will be called when all player successfully connected
    case "startGame" =>
        //this will be called when the player press startgame in roomdetailpage  
        //place game logic here maybe

        //test whether all client can be contacted
        for((name,clientRef) <- playerListInRoom){
            clientRef ! "testingConnection"
        }
    
    case JoinGameAndSubscribeForUpdate(name,clientRef) =>
        //add player ref into the room
        playerListInRoom += (name -> clientRef)
        println(name+" Joined")





    

    case _=>
      
  }
}
object GamePageServerActor {
  final case class JoinGameAndSubscribeForUpdate(name:String,ref: ActorRef)
}