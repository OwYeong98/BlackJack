package ds.server.ServerModels

import akka.actor.{Actor, ActorRef}

class RoomDetail(var roomNo:Int, var hostName:String, var noOfPlayer:Int, var hostActorRef: ActorRef)