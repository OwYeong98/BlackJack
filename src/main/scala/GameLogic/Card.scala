package GameLogic

import scala.collection.mutable

object Card {
  val symbol = List("diamond","club","heart","spade")
  val value = List(1,2,3,4,5,6,7,8,9,10,10,10,10)
  val digit = List("A","2","3","4","5","6","7","8","9","10","J","Q","K")

  def makeCard(): mutable.Stack[Tuple3[String,Integer,String]]= {
    var temp = mutable.Stack[Tuple3[String,Integer,String]]()
      for (s <- symbol) {
        //println(s)
        for (c <- 0 to 12) {
          //println(d)
          temp.push(Tuple3(s,value(c),digit(c)))
      }
    }
    //println(s"$temp")

    return temp

  }

  //makeCard()

}
