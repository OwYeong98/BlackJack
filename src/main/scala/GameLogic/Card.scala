package GameLogic

import scala.collection.mutable

object Card{
  val symbol = List("Diamond","Club","Love","Spade")
  val digit = List(1,2,3,4,5,6,7,8,9,10,10,10,10)
  //val symbolx = mutable.Stack[Tuple2[String,Integer]]

  def makeCard(): mutable.Stack[Tuple2[String,Integer]]= {
    var temp = mutable.Stack[Tuple2[String,Integer]]()
      for (s <- symbol) {
        //println(s)
        for (d <- digit) {
          //println(d)
          temp.push(Tuple2(s,d))
      }
    }
    //println(s"$temp")

    return temp

  }

  //makeCard()

}
