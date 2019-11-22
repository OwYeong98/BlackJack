package ds.server.ServerModels

import akka.actor.{Actor, ActorRef}
import scala.collection.mutable.ArrayBuffer

class RoomInfo(var roomNo:Int, var hostName:String, var playerList:ArrayBuffer[String], var hostActorRef: ActorRef)