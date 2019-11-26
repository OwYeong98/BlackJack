package ds.server

import akka.actor.{Actor, ActorRef, Terminated}
import scalafx.collections.ObservableHashSet
import akka.pattern.ask
import akka.remote.DisassociatedEvent
import akka.util.Timeout
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

import scala.concurrent.Await
import scala.concurrent.duration._

import ds.server.GamePageServerActor.{JoinGameAndSubscribeForUpdate,Draw}
import ds.client.GamePageClientActor

//GameLogicImports
import ds.server.GameLogic._

class GamePageServerActor extends Actor {
  implicit val timeout = Timeout(10 second)
  
  //ArrayBuffer for players and winner
  var players = ArrayBuffer[Player]()
  var winners = ArrayBuffer[String]()
  var playerName = ArrayBuffer[String]()
  var deck: Deck = null

  //variable playerListInRoom contain a Map where key is name and value is ActorRef
  //example playerListInRoom("John") will return GamePageClientActorRef of John
  val playerListInRoom = Map[String,ActorRef]()

  override def preStart(): Unit = {


  }

  def receive = {
    case Terminated(actorRef) =>{
      for((name, clientRef) <- playerListInRoom){
        if(clientRef == actorRef){
          playerListInRoom.remove(name)
          players -= (players.find(_.playerName==name).get)
          playerName -= name
          
          var playerCards = Map[String,ArrayBuffer[Tuple3[String,Integer,String]]]()
          //Mapping existing cards to corresponding players to send them over
          for(player <- players){
            //make map ("Player1"->CardList)
            playerCards+=(player.playerName -> player.handCard)
          }
          //Reinitializing Player UI with their own cards
          for((name,clientRef) <- playerListInRoom){
            clientRef ! GamePageClientActor.PlayerDisconnected(playerName,playerCards)
          }
        }
      }
    }
        
    //this startGame will be called when all player successfully connected
    case "startGame" =>
        //this will be called when the player press startgame in roomdetailpage  
        //place game logic here maybe
        //test whether all client can be contacted
        /*for((name,clientRef) <- playerListInRoom){
            clientRef ! "testingConnection"
        }
        */
      //Initializing the list of player objects
      for((name,clientRef) <- playerListInRoom){
        players.append(new Player(name))
        playerName.append(name)
      }
      //Sending player list to clients
      for((name,clientRef) <- playerListInRoom){
        clientRef ! GamePageClientActor.ReceivePlayerList(playerName)
      }
      //Making the Deck
      deck = new Deck()
      deck.makeDeck()
      //Shuffle deck
      deck.shuffle()

      //Distributing the cards to the players
      for (c <- 0 to 1){
        players.map((p: Player) => {
          p.assignHandCard(deck.draw())
        })
      }

      var playerCards = Map[String,ArrayBuffer[Tuple3[String,Integer,String]]]()
      //Mapping drawn cards to corresponding players to send them over
      for(player <- players){
        //make map ("Player1"->CardList)
        playerCards+=(player.playerName -> player.handCard)
      }
      //Initializing Player UI with their own cards
      for((name,clientRef) <- playerListInRoom){
        clientRef ! GamePageClientActor.ReceiveInitialCards(playerCards)
      }
    
    case JoinGameAndSubscribeForUpdate(name,clientRef) =>
        //add player ref into the room
        playerListInRoom += (name -> clientRef)
        println(name+" Joined")
        context.watch(clientRef)


    case "Initialized" => {
      if(!playerName.isEmpty) {
        var playername = playerName(0)
          for((name,clientRef) <- playerListInRoom){
            clientRef ! GamePageClientActor.PlayerTurn(playername)
          }
          println(s"$playername turn before switching to DrawingStage context")
        context.become(DrawingStage)
      } 
      else {
        println("Server entering DeclareWinnerStage")
        for((name,clientRef) <- playerListInRoom){
            clientRef ! "Round Ends"
        }

        players.map((p: Player) => {
          BlackJack.checkValue(p)
        })

        players.map((p: Player) => {
          BlackJack.checkWorth(p)
        })

        winners = BlackJack.determineWinner(players)
        var playerCards = Map[String,ArrayBuffer[Tuple3[String,Integer,String]]]()
        for(player <- players){
        //make map ("Player1"->CardList)
        playerCards+=(player.playerName -> player.handCard)
      }

        for((name,clientRef) <- playerListInRoom){
            clientRef ! GamePageClientActor.DeclareWinner(winners,playerCards)
        }
      }
    }


    

    case _=>
      
  }

  def DrawingStage: Receive = {
    case Draw(pname) => {
      if((players.find(_.playerName==pname).get).handCard.size < 5){
        val cardDrawn = deck.draw()
        (players.find(_.playerName==pname).get).assignHandCard(cardDrawn)

        for((name,clientRef) <- playerListInRoom){
          var cardString: String = cardDrawn._3+"_"+cardDrawn._1
          clientRef ! GamePageClientActor.CardDrawn(pname, cardString)
        }

      }
    }

    case "Turn Ends" => {
      context.become(receive)
      context.self ! "Initialized"
      println("Server turn end")
      playerName.remove(0)
    }

    case Terminated(actorRef) =>{
      for((name, clientRef) <- playerListInRoom){
        if(clientRef == actorRef){
          playerListInRoom.remove(name)
          players -= (players.find(_.playerName==name).get)
          playerName -= name
          context.become(receive)
          context.self ! "Initialized"

          var playerCards = Map[String,ArrayBuffer[Tuple3[String,Integer,String]]]()
          //Mapping existing cards to corresponding players to send them over
          for(player <- players){
            //make map ("Player1"->CardList)
            playerCards+=(player.playerName -> player.handCard)
          }
          
          var playersAlive = ArrayBuffer[String]()
          for(player <- players){
            playersAlive += player.playerName
          }
          //Reinitializing Player UI with their own cards
          for((name,clientRef) <- playerListInRoom){
            clientRef ! GamePageClientActor.PlayerDisconnected(playersAlive,playerCards)
          }
        }
      }
    }

    case _=>
  }
}
object GamePageServerActor {
  final case class JoinGameAndSubscribeForUpdate(name:String,ref: ActorRef)
  final case class Draw(name: String)
}