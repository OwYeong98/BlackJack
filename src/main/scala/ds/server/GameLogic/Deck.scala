package ds.server.GameLogic

import scala.collection.mutable
import scala.util.Random

class Deck {
  var deck = mutable.Stack[Tuple3[String,Integer,String]]()

  def makeDeck() = {
    deck = Card.makeCard()
  }

  def shuffle() = {
    for(shuf <- 0 to 9){
      deck = Random.shuffle(deck)
    }
  }

  def draw() = {
    var cardDrawn = deck.pop()
    cardDrawn
  }

}

