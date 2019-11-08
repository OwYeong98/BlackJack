package GameLogic

import scala.collection.mutable.ArrayBuffer

class Player (pname: String){
  var handCard = ArrayBuffer[Tuple2[String,Integer]]()
  var handValue = None: Option[Integer]
  var playerName = pname
  var handWorth = None: Option[Integer]

  def assignHandCard(card: Tuple2[String,Integer])= {
    handCard.append(card)
  }

  def assignHandValue(value: Integer) = {
    handValue = Some(value)
  }

  def assignHandWorth(worth: Integer) = {
    handWorth = Some(worth)
  }

  def getHandValue() = {
    handValue.get
  }

  def getHandWorth() = {
    handWorth.get
  }

}
