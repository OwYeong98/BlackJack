package ds.server

import akka.actor.{Actor, ActorRef}
import ds.server.roomListServerActor.GetRoomListAndSubscribeForUpdate
import scalafx.collections.ObservableHashSet
import akka.pattern.ask
import akka.remote.DisassociatedEvent
import akka.util.Timeout
import scala.collection.mutable.ArrayBuffer

import scala.concurrent.Await
import scala.concurrent.duration._
import Models.Room

class roomListServerActor extends Actor {
  implicit val timeout = Timeout(10 second)

  //this variable store all the client actor Ref viewing the roomlist page
  val clientViewingRoomListPage = new ObservableHashSet[ActorRef]()
  
  //this variable will store all room list that are created
  val roomList: ArrayBuffer[Room] = new ArrayBuffer[Room]()


  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[akka.remote.DisassociatedEvent])
    context.system.eventStream.subscribe(self, classOf[akka.remote.AssociatedEvent])

    roomList+=new Room(1,"Jack",3)
    roomList+=new Room(2,"haha",6)
    roomList+=new Room(3,"noob",7)
  }

  def receive = {
    //remove to the client that close connection
    case DisassociatedEvent(localAddress, remoteAddress, _) =>
      if(clientViewingRoomListPage.exists(x => x.path.address.equals(remoteAddress))){
        val refOpt = clientViewingRoomListPage.find(x => x.path.address.equals(remoteAddress))
        for (ref <- refOpt){
          clientViewingRoomListPage.remove(ref)
        }
      }
    case GetRoomListAndSubscribeForUpdate(actorRef) =>
      //add the client ActorRef to the list so that if room got any update we ill notify them
      clientViewingRoomListPage += actorRef

      
      sender ! "haha"

    case _=>
  }
}
object roomListServerActor {
  final case class GetRoomListAndSubscribeForUpdate(ref: ActorRef)


}