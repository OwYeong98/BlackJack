package ds.client

import MainSystem.MainApp

import scalafx.scene.control._
import scalafx.scene.input._

import akka.actor.{Actor, ActorRef,ActorSelection}
import ds.client.roomListClientActor.GetRoomListAndSubscribeForUpdate
import scalafx.collections.ObservableHashSet
import akka.pattern.ask
import akka.remote.DisassociatedEvent
import akka.util.Timeout
import scala.collection.mutable.ArrayBuffer
import scalafx.scene.control.Alert.AlertType 

import scala.concurrent.Await
import scala.concurrent.duration._
import Models.Room
import ds.client.roomListClientActor.Join

import scala.concurrent.ExecutionContext.Implicits._


class roomListClientActor extends Actor {
  implicit val timeout = Timeout(10 second)

  var serverOpt: Option[ActorSelection] = None    

  override def preStart(): Unit = {
    val server: ActorSelection = context.actorSelection(s"akka.tcp://blackjack@${MainApp.ipAddress}:${MainApp.port.toString}/user/roomlistserver")
    serverOpt = Option(server)
    context.system.eventStream.subscribe(self, classOf[akka.remote.DisassociatedEvent])
    context.system.eventStream.subscribe(self, classOf[akka.remote.AssociatedEvent])

  }

  def receive = {
    //remove to the client that close connection
    case DisassociatedEvent(localAddress, remoteAddress, _) =>
      val alert = new Alert(AlertType.Error){
		        initOwner(MainApp.stage)
		        title       = "Lost connection to server"
		        headerText  = "Server Connection Lost"
		        contentText = "Could Not Connect to Server!"
		      }.showAndWait()
    
    /*********Call from controller************************/
    case "getRoomList" =>
        for (server <- serverOpt){
            var result = server ? GetRoomListAndSubscribeForUpdate(context.self)

            Await.result(result,10 second)

            result.foreach(x => {
                val roomList:ArrayBuffer[Room] = x.asInstanceOf[ArrayBuffer[Room]]
                println("Get Room List"+roomList)
                
                
            })
            
        }

    case Join(roomNo) =>


    case _=>
  }
}
object roomListClientActor {
  final case class Join(roomNo:Int)
  final case class GetRoomListAndSubscribeForUpdate(ref: ActorRef)


}