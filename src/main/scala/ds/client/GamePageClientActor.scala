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

import ds.client.GamePageClientActor.InitialConnectionWithServer
import ds.server.GamePageServerActor

class GamePageClientActor(var hostServerActorRef:ActorRef) extends Actor {
  implicit val timeout = Timeout(10 second)

  //hostServerActorRef store the GamePageServerActor reference that you can communicate with
  //example hostServerActorRef ! "haha"

  override def preStart(): Unit = {
 

  }

  def receive = {

    
    /*********Call from controller************************/
    case InitialConnectionWithServer(name) =>
      hostServerActorRef ! GamePageServerActor.JoinGameAndSubscribeForUpdate(name,context.self)
    



    /*********call from server*************************/
    case _=>
  }
}

object GamePageClientActor {
  final case class InitialConnectionWithServer(name:String)




}