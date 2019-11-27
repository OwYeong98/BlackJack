package ds.client

import MainSystem.MainApp
import scalafx.scene.control._
import scalafx.scene.input._
import akka.actor.{Actor, ActorRef, ActorSelection, Props, Terminated}
import scalafx.collections.ObservableHashSet
import akka.pattern.ask
import akka.remote.DisassociatedEvent
import akka.util.Timeout

import scala.collection.mutable.{ArrayBuffer, Map}
import scalafx.scene.control.Alert.AlertType
import scalafx.application.Platform

import scala.concurrent.Await
import scala.concurrent.duration._
import Models.Room

import scala.concurrent.ExecutionContext.Implicits._
import ds.client.GamePageClientActor.{InitialConnectionWithServer, ReceiveInitialCards, ReceivePlayerList,PlayerTurn, Draw, CardDrawn, DeclareWinner, PlayerDisconnected}
import ds.server.GamePageServerActor

class GamePageClientActor(var hostServerActorRef:ActorRef) extends Actor {
  implicit val timeout = Timeout(2 second)

  //hostServerActorRef store the GamePageServerActor reference that you can communicate with
  //example hostServerActorRef ! "haha"

  override def preStart(): Unit = {
    context.watch(hostServerActorRef)

  }

  def receive = {
    case Terminated(actorRef) => {
      Platform.runLater{
        val alert = new Alert(AlertType.Error){
                    initOwner(MainApp.stage)
                    title = "Lost connection to Server"
                    headerText = "Server Connection Lost"
                    contentText = "Connection to Host lost!"
                    }.showAndWait()
        MainApp.goToMainPage()
      }
    }

    case PlayerDisconnected(disconnectedPlayer) => {
      Platform.runLater{
        MainApp.gamePageControllerRef.removePlayer(disconnectedPlayer)
      }
    }
    
    /*********Call from controller************************/
    case InitialConnectionWithServer(name) =>
      hostServerActorRef ! GamePageServerActor.JoinGameAndSubscribeForUpdate(name,context.self)
    
    /*case "testingConnection" =>
      Platform.runLater{
        //MainApp.gamePageControllerRef is the controller reference u can use to call function in controller
        val alert = new Alert(AlertType.Error){
          initOwner(MainApp.stage)
          title       = "Receive call from server"
          headerText  = "Testing"
          contentText = "can Received Message from servver"
        }.showAndWait()	
      }
    */
    case ReceivePlayerList(playerlist) => {
      Platform.runLater{
        MainApp.gamePageControllerRef.initializePlayer(playerlist)
      }
    }

    case ReceiveInitialCards(startingCards) => {
      Platform.runLater{
        MainApp.gamePageControllerRef.initializeCard(startingCards)
      }
      hostServerActorRef ! "Initialized"
    }
    
    case PlayerTurn(name) => {
      Platform.runLater{
        MainApp.gamePageControllerRef.changePlayer(name)
      println(s"successfully changed to player $name")
      }
      println(s"successfully changed to player $name")
      context.become(myTurn)
    }

    case CardDrawn(pname, cardDrawn) => {
      Platform.runLater{
        MainApp.gamePageControllerRef.sendCard(pname,cardDrawn)
      }
    }
    
    case "Round Ends" => {
      println("Round ended changing to DeclareWinnerStage now!")
      context.become(DeclareWinnerStage)
    }


    /*********call from server*************************/
    case _=>
  }

  def myTurn: Receive = {
    case Terminated(actorRef) => {
      Platform.runLater{
        val alert = new Alert(AlertType.Error){
                    initOwner(MainApp.stage)
                    title = "Lost connection to Server"
                    headerText = "Server Connection Lost"
                    contentText = "Connection to Host lost!"
                    }.showAndWait()
        MainApp.goToMainPage()
      }
    }

    case PlayerDisconnected(disconnectedPlayer) => {
      Platform.runLater{
        MainApp.gamePageControllerRef.removePlayer(disconnectedPlayer)
      }
    }
    /*
    case PlayerDisconnected(playerlist, playercards) => {
      Platform.runLater{
        println("My Player List: "+playerlist)
        println("My Player List: "+playercards)
        MainApp.gamePageControllerRef.initializePlayer(playerlist)
        MainApp.gamePageControllerRef.initializeCard(playercards)
      }
    }
    */
    case Draw(name) => {
      hostServerActorRef ! GamePageServerActor.Draw(name)
    }

    case CardDrawn(pname, cardDrawn) => {
      Platform.runLater{
        MainApp.gamePageControllerRef.sendCard(pname,cardDrawn)
      }
    }

    case "Passed" => {
      context.become(receive)
      hostServerActorRef ! "Turn Ends"
      println("Turn has ended")
    }

    case "Not My Turn" => {
      context.become(receive)
    }

    case _=>
  }

  def DeclareWinnerStage: Receive = {
    case Terminated(actorRef) => {
      Platform.runLater{
        val alert = new Alert(AlertType.Error){
                    initOwner(MainApp.stage)
                    title = "Lost connection to Server"
                    headerText = "Server Connection Lost"
                    contentText = "Connection to Host lost!"
                    }.showAndWait()
        MainApp.goToMainPage()
      }
    }
    
    case PlayerDisconnected(disconnectedPlayer) => {
      Platform.runLater{
        MainApp.gamePageControllerRef.removePlayer(disconnectedPlayer)
      }
    }
    case DeclareWinner(winnerlists, winnercards) => {
      Platform.runLater{
        MainApp.gamePageControllerRef.showResult(winnerlists, winnercards)
      }
    }

    case _=>
  }
}

object GamePageClientActor {
  final case class InitialConnectionWithServer(name:String)
  final case class ReceivePlayerList(playerList: ArrayBuffer[String])
  final case class ReceiveInitialCards(initialcards: Map[String,ArrayBuffer[Tuple3[String,Integer,String]]])
  final case class PlayerTurn(name: String)
  final case class Draw(name: String)
  final case class CardDrawn(name: String, cards: String)
  final case class DeclareWinner(winnerLists: ArrayBuffer[String], winnerCards: Map[String,ArrayBuffer[Tuple3[String,Integer,String]]])
  final case class PlayerDisconnected(name: String)
  //final case class PlayerDisconnected(updatedPlayers: ArrayBuffer[String], updatedCards: Map[String,ArrayBuffer[Tuple3[String,Integer,String]]])



}