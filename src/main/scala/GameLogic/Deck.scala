package GameLogic

import scala.collection.mutable
import scala.util.Random

class Deck {
  var deck = mutable.Stack[Tuple3[String,Integer,String]]()

  def makeDeck() = {
    deck = Card.makeCard()
  }

  def shuffle() = {
    deck = Random.shuffle(deck)
  }

  def draw() = {
    var cardDrawn = deck.pop()
    cardDrawn
  }

}

