package ds.server.GameLogic

import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn.readLine

object BlackJack{
  /*
  var players = ArrayBuffer[Player]()
  var pname = ArrayBuffer("Player1","Player2","Player3","Player4")
  var winner = ArrayBuffer[String]()

  //Instantiating Players
  pname.map((n: String) => {
    players.append(new Player(n))
  })

  //Making the Deck
  val deck = new Deck()
  deck.makeDeck()
  //Can shuffle multiple times for more randomness?
  deck.shuffle()

  //Distributing the cards to the players
  for (c <- 0 to 1){
    players.map((p: Player) => {
      p.assignHandCard(deck.draw())
    })
  }

  //Looping through all the players
  players.map((p: Player) => {
    askDraw(p)
  })

  players.map((p: Player) => {
    checkValue(p)
  })

  players.map((p: Player) => {
    checkWorth(p)
  })

  winner = determineWinner()
  println(s"winner list contains $winner")

  declareWinner(winner, players)

  */

  /*winner match {
    case None => println("Game resulted in a draw")
    case _ => {
      println(s"${winner.get} has won the game with a total hand value of ${(players.find(_.playerName==winner.get).get).getHandValue()}")
      (players.find(_.playerName==winner.get).get).handCard.map((hc: Tuple3[String,Integer]) => {print(s"${hc}")})
    }
  }*/

  //Below are functions

  //Ask players whether they want to draw or not
  /*
  def askDraw(p: Player) = {
    var askDraw = true
    displayHand(p)
    while(askDraw){
      var drawOrNot = readLine(s"Would you like to draw a card ${p.playerName}? (Y/N)")
      if (drawOrNot.toLowerCase == "y") {
        p.assignHandCard(deck.draw())
        displayHand(p)
      }
      else if (drawOrNot.toLowerCase == "n"){
        askDraw = false
      }
    }
  }
  */

  //Display player's cards
  def displayHand(p: Player) = {
    println(s"${p.playerName} your hand value is ")
    p.handCard.map((hc: Tuple3[String,Integer,String]) => {print(s"${hc}")})
    println()
  }

  //Check the sum of the player's cards
  def checkValue(p: Player) = {
    var haveAce = checkAce(p.handCard)
    var value = 0
    p.handCard.map((c: Tuple3[String,Integer,String]) => {
      value += c._2
    })
    if(haveAce){
      var c = value + 10
      if(c <= 21){
        value = c
      }
    }
    p.assignHandValue(value)
  }

  //Determine how strong is the player's card
  def checkWorth(p: Player) = {
    var worth = 0
    val value = p.getHandValue()

    if(p.handCard.size == 5 && p.getHandValue() < 22){
      worth = 7
    }
    else {
      value match {
        case a if ( value == 16 ) => { worth = 1 }
        case b if ( value == 17 ) => { worth = 2 }
        case c if ( value == 18 ) => { worth = 3 }
        case d if ( value == 19 ) => { worth = 4 }
        case e if ( value == 20 ) => { worth = 5 }
        case f if ( value == 21 ) => { if (p.handCard.size == 2){
          worth = 8
        }else { worth = 6 }}
        case _ => worth = 0
      }
    }

    p.assignHandWorth(worth)
  }

  //Check the existence of Ace
  def checkAce(playerCard: ArrayBuffer[Tuple3[String,Integer,String]]): Boolean = {
    var haveAce = false
    var numAce = 0
    playerCard.map((c: Tuple3[String,Integer,String]) => {
      if ( c._2 == 1 ){
        haveAce = true
      }
    })
    haveAce
  }

  //Determine the Winner with the STRONGEST hand
  def determineWinner(players: ArrayBuffer[Player]): ArrayBuffer[String] = {
    var winner = ArrayBuffer[String]()
    var bestScore = 0
    players.map((p: Player) => {
      if(bestScore == 0){
        if(p.getHandWorth() > bestScore){
          bestScore = p.getHandWorth()
          winner.insert(0, p.playerName)
        }
      }
      else{
        if(p.getHandWorth() > bestScore){
          bestScore = p.getHandWorth()
          winner.clear()
          winner.insert(0, p.playerName)
        }
        else if(p.getHandWorth() == bestScore){
          winner.append(p.playerName)
        }
      }
    })
    winner
  }

  //Declare the winner
  def declareWinner(w: ArrayBuffer[String], p: ArrayBuffer[Player]) = {
    w match {
      case a if (w.size <= 0) => println("Game resulted in a draw")
      case b if (w.size < 2) => {
        println(s"${w(0)} has won the game with a total hand value of ${(p.find(_.playerName==w(0)).get).getHandValue()}")
        (p.find(_.playerName==w(0)).get).handCard.map((hc: Tuple3[String,Integer,String]) => {print(s"${hc}")})
      }
      case c if (w.size >= 2) => {
        print("Game resulted in a draw between")
        w.map((f: String) => {
          print(s" || $f ||")
        })
        println()
      }
      case _ => println("Something wrong?")
    }
  }

}
