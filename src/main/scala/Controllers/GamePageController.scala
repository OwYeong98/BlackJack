package Controllers

import MainSystem.MainApp
import scala.collection.mutable.ListBuffer

import scalafxml.core.macros.sfxml
import scalafx.scene.control._
import scalafx.scene.input._
import scalafx.scene.layout.AnchorPane
import scalafx.scene.layout.FlowPane
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType //contail all the implicits to change javafx classes to scalafx classes as necessary
import scalafx.application.Platform
import scalafx.scene.layout.VBox


import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.image.Image
import javafx.scene.layout.BackgroundImage
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundSize
import javafx.scene.layout.Background

import scalafx.animation.ScaleTransition
import scalafx.animation.TranslateTransition
import scalafx.animation.ParallelTransition
import scalafx.animation.RotateTransition

import scalafx.util.Duration
import scalafx.event.EventHandler
import scalafx.event.ActionEvent
import javafx.scene.paint.Color
import scala.collection.mutable.Map

@sfxml
class GamePageController(
	val mainAnchorPane: AnchorPane,
	val userIcon: ImageView,
	val userNameLabel: Label,
	val messageLabel: Label,
	val player0VBox: VBox,
	val player0Name: Label,
	val player0CardHolder: AnchorPane,
	val player0CardList: FlowPane,
	val player1VBox: VBox,
	val player1Name: Label,
	val player1CardHolder: AnchorPane,
	val player1CardList: FlowPane,
	val player2VBox: VBox,
	val player2Name: Label,
	val player2CardHolder: AnchorPane,
	val player2CardList: FlowPane,
	val player3VBox: VBox,
	val player3Name: Label,
	val player3CardHolder: AnchorPane,
	val player3CardList: FlowPane,
	val player4VBox: VBox,
	val player4Name: Label,
	val player4CardHolder: AnchorPane,
	val player4CardList: FlowPane,
	val player5VBox: VBox,
	val player5Name: Label,
	val player5CardHolder: AnchorPane,
	val player5CardList: FlowPane,
	val player6VBox: VBox,
	val player6Name: Label,
	val player6CardHolder: AnchorPane,
	val player6CardList: FlowPane,
	val player7VBox: VBox,
	val player7Name: Label,
	val player7CardHolder: AnchorPane,
	val player7CardList: FlowPane,
	val drawCardButton: Button,
	val passButton: Button,
	val forfietButton: Button,
	val dealerCardHolderIcon: ImageView

	 )
{	

	// Initialize Graphic	
	//set Game board Anchor Pane background Image
	var myBackgroundImage= new BackgroundImage(new Image(getClass.getResourceAsStream("/Images/GamePage/gamebg.png")),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          new BackgroundSize(100, 100, true, true, true, true));

	//set background image to anchorpane
	mainAnchorPane.setBackground(new Background(myBackgroundImage));
	dealerCardHolderIcon.image = new Image(getClass.getResourceAsStream("/Images/GamePage/dealer.png"))

	drawCardButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/drawcard.png"))));
	passButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/pass.png"))));
	forfietButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/forfiet.png"))));

	//this variable store the roomNo of this game
	var roomNo: Int = 0
	//this variable store whether the player is a dealer
	var isDealer: Boolean = false
	//this variable store the player name of this client
	var playerName: String = null

	//this list store player name of all player
	var playerLists:ListBuffer[String] = ListBuffer[String]() 
	


	/**********************Function For Network**************************************************/

	//this function will be called when this page run
	def initializeData(roomNo:Int, isDealer: Boolean, playerName: String) = {
		this.roomNo = roomNo
		this.isDealer = isDealer
		this.playerName = playerName

		//set userIcon
		setIsDealer(isDealer)
		setUserName(playerName)

		/******sample*******/
		//intialize player to the list so that animation function can recognize the player name
		//Note player 0 must be name of this client and player 1 must be the dealer

		//player 0 which is the player name of this client
		addPlayer("Eric")
		//player 1 which is dealer name
		addPlayer("John")
		//player 2
		addPlayer("Jack")
		addPlayer("Kelvin")
		
		addPlayer("Jason")
		addPlayer("Nick")
		addPlayer("Jeff")
		

		val cards = Map("Eric" -> "K_club", 
                        "John" -> "A_diamond", 
                        "Jack" -> "K_club",
						"Kelvin" -> "2_heart", 
						"Jason" -> "Q_diamond", 
						"Nick" -> "J_spade", 
						"Jeff" -> "3_diamond", 
						"Edwin" -> "6_heart")
        
		initializeCard(cards)
		
		/*************************/





	}

	def drawCardAction() = {

	}
	
	def passAction() = {
		
		
	}

	def forfietAction() = {
		
	}
	hideActionButtonAndShowMessage("Player 1 Turns!")
	showActionButton(true)
	/********************************************************************************************/


	/**********************Function For editing the UI**********************************************/
	/*
	List of Function

	-> addPlayer(name) 
	This function add a player to the UI

	-> initializeCard(cards)
	This function will give two card to all player one card is open and another card is closed
	The parameter cards is a Map ("Eric"=>"K_club") which key is playername and value is card

	-> showActionButton(canForfiet)
	This function show the action button at bottom right corner
	If parameter true is passed in The Forfiet Button will be show

	-> hideActionButtonAndShowMessage(message)
	This function will hide all button at bottom right corner and add a Message Text at botoom right corner
	Hide the button when it is not the player turn like hideActionButtonAndShowMessage("Player 1 Turn!")

	->animation_sendHiddenCard(playername)
	This function will play the animation send card to the specified player and add the card to the player card list
	The playername must match the name that added using addPlayer(name)

	->animation_sendCardWithReveal(playername,card)
	This function will play the animation send card to the specified player and flip the card for reveal then add card to player card list
	The playername must match the name that added using addPlayer(name)
	The card must match the name below

	->animation_getCard(card)
	This function will play the animation draw card from card deck and then reveal it
	The card will be send to player 0 and add to the card list
	The card must match the name below

	cardName
	card A name = "A_heart","A_spade","A_club","A_diamond"
	card K name = "K_heart","K_spade","K_club","K_diamond"
	card Q name = "Q_heart","Q_spade","Q_club","Q_diamond"
	card J name = "J_heart","J_spade","J_club","J_diamond"
	card 10 name = "10_heart","10_spade","10_club","10_diamond"
	card 9 name = "9_heart","9_spade","9_club","9_diamond"
	card 8 name = "8_heart","8_spade","8_club","8_diamond"
	card 7 name = "7_heart","7_spade","7_club","7_diamond"
	card 6 name = "6_heart","6_spade","6_club","6_diamond"
	card 5 name = "5_heart","5_spade","5_club","5_diamond"
	card 4 name = "4_heart","4_spade","4_club","4_diamond"
	card 3 name = "3_heart","3_spade","3_club","3_diamond"
	card 2 name = "2_heart","2_spade","2_club","2_diamond"
	card 1 name = "1_heart","1_spade","1_club","1_diamond"
	
	*/

	def initializeCard(playerCards:Map[String,String]) = {
		//this sequence is clockwise
		var cardSequence = ListBuffer[Int]()
		cardSequence += (1,5,3,7,0,6,2,4)


		//use new Thread because cannot sleep current thread because it will hangs the User Interface
		new Thread {
		    override def run {
		        Thread.sleep(200)

		        //must put platform runlater if modify User Interface object in non javafx Thread
       			for(index <- cardSequence){
       				Platform.runLater(new Runnable() {
       					override def run {
       						//if index is small the the length then only its occurs
       						if(index <= playerLists.length-1)
       							animation_sendCardWithReveal(playerLists(index),playerCards(playerLists(index)))       						
       					}
       				});
       				Thread.sleep(1000)
       			}

       			for(index <- cardSequence){
       				Platform.runLater(new Runnable() {
       					override def run {
       						if(index <= playerLists.length-1)
       							animation_sendHiddenCard(playerLists(index))
       					}
       				});
       				
       				Thread.sleep(1000)
       			}
		    }
		}.start()
	}


	var numofPlayer:Int = 0
	var playerNameLabelList = new ListBuffer[Label]()
	playerNameLabelList += (player0Name,player1Name,player2Name,player3Name,player4Name,player5Name,player6Name,player7Name)
	var playerVBoxList = new ListBuffer[VBox]()
	playerVBoxList += (player0VBox,player1VBox,player2VBox,player3VBox,player4VBox,player5VBox,player6VBox,player7VBox)

	def addPlayer(name:String) = {
		playerLists+=name
		playerNameLabelList(numofPlayer).text = name
		playerVBoxList(numofPlayer).visible = true
		numofPlayer+=1

	}
	def setIsDealer(isDealer: Boolean) = {
		if(isDealer)
			userIcon.image = new Image(getClass.getResourceAsStream("/Images/GamePage/dealer.png"))
		else
			userIcon.image = new Image(getClass.getResourceAsStream("/Images/GamePage/player.png"))
	}

	def setUserName(name: String) = {
		userNameLabel.text = name
	}

	def showActionButton(canForfiet: Boolean) = {
		drawCardButton.setManaged(true)
		passButton.setManaged(true)
		forfietButton.setManaged(true)

		drawCardButton.visible = true
		passButton.visible = true

		if(canForfiet)
			forfietButton.visible = true
		else
			forfietButton.visible = false

		messageLabel.visible = false
	}

	def hideActionButtonAndShowMessage(message: String) = {
		drawCardButton.visible = false
		drawCardButton.setManaged(false)
		passButton.visible = false
		passButton.setManaged(false)
		forfietButton.visible = false
		forfietButton.setManaged(false)

		messageLabel.visible = true
		messageLabel.text = message
	}

	//a 2d array that store the transition value needed to move card and rotatecard to the player card player7CardHolder
	//first dimension determine the player example playerAnimationValue[0][0] means its the value for player 0
	//second dimension has size 3 which are (translateX,translateY,rotateAngle) 
	//so playerAnimationValue[1][2] will get the value translateY for player 1
	val playerAnimationValue= Array.ofDim[Int](8, 3)
	//player 0
	playerAnimationValue(0)(0) = 0
	playerAnimationValue(0)(1) = 160
	playerAnimationValue(0)(2) = 0
	//player 1
	playerAnimationValue(1)(0) = 0
	playerAnimationValue(1)(1) = -180
	playerAnimationValue(1)(2) = 0
	//player 2
	playerAnimationValue(2)(0) = -210
	playerAnimationValue(2)(1) = 0
	playerAnimationValue(2)(2) = -90
	//player 3
	playerAnimationValue(3)(0) = 210
	playerAnimationValue(3)(1) = 0
	playerAnimationValue(3)(2) = 90
	//player 4
	playerAnimationValue(4)(0) = -210
	playerAnimationValue(4)(1) = -180
	playerAnimationValue(4)(2) = -90
	//player 5
	playerAnimationValue(5)(0) = 210
	playerAnimationValue(5)(1) = -180
	playerAnimationValue(5)(2) = 90
	//player 6
	playerAnimationValue(6)(0) = -210
	playerAnimationValue(6)(1) = 180
	playerAnimationValue(6)(2) = -90
	//player 7
	playerAnimationValue(7)(0) = 210
	playerAnimationValue(7)(1) = 180
	playerAnimationValue(7)(2) = 90

	var cardListFlowPanes = new ListBuffer[FlowPane]()
	cardListFlowPanes += (player0CardList,player1CardList,player2CardList,player3CardList,player4CardList,player5CardList,player6CardList,player7CardList)


	def animation_sendHiddenCard(playerName:String) = {
		var playerIndex: Int = -1
		for(index <- 0 to playerLists.length-1){
			if(playerLists(index).toLowerCase() == playerName.toLowerCase()){
				playerIndex = index
			}
		}

		/*********************Move and rotate card animation************************************/
		var cardback: ImageView = new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/Card/cardback.png")))

		cardback.layoutX = 368
		cardback.layoutY = 250
		
		var movecard:TranslateTransition = new TranslateTransition(new Duration(1000), cardback)
		movecard.toX = playerAnimationValue(playerIndex)(0)
		movecard.toY = playerAnimationValue(playerIndex)(1)

		var rotatecard:RotateTransition = new RotateTransition(new Duration(1000), cardback)
		rotatecard.toAngle = playerAnimationValue(playerIndex)(2)
		
		var combineAnimation:ParallelTransition = new ParallelTransition()
		combineAnimation.getChildren().addAll(movecard,rotatecard)
		/******************************************************************************/

		combineAnimation.onFinished = (event: ActionEvent) =>  {  
			new Thread {
			    override def run {
			        Thread.sleep(200)

			        //must put platform runlater if modify User Interface object in non javafx Thread
			        Platform.runLater(new Runnable() {
			       		override def run {
			       			
			       			//remove card from UI
			       			mainAnchorPane.getChildren().remove(cardback)

			       			var newhiddencard: ImageView = new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/Card/cardback.png")))
			       			newhiddencard.fitWidth = 57
			       			newhiddencard.fitHeight = 82
			       			cardListFlowPanes(playerIndex).getChildren().addAll(newhiddencard)

							
			       		}
			       	});
			        
			    }
			}.start()
			
		}


		mainAnchorPane.getChildren().addAll(cardback)
		combineAnimation.play()	
	}


	def animation_sendCardWithReveal(playerName:String,card:String) = {

		var playerIndex: Int = -1
		for(index <- 0 to playerLists.length-1){
			if(playerLists(index).toLowerCase() == playerName.toLowerCase()){
				playerIndex = index
			}
		}

		/*********************Move and rotate card animation************************************/
		var cardback: ImageView = new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/Card/cardback.png")))

		cardback.layoutX = 368
		cardback.layoutY = 250
		
		var movecard:TranslateTransition = new TranslateTransition(new Duration(1000), cardback)
		movecard.toX = playerAnimationValue(playerIndex)(0)
		movecard.toY = playerAnimationValue(playerIndex)(1)

		var rotatecard:RotateTransition = new RotateTransition(new Duration(1000), cardback)
		rotatecard.toAngle = playerAnimationValue(playerIndex)(2)
		
		var combineAnimation:ParallelTransition = new ParallelTransition()
		combineAnimation.getChildren().addAll(movecard,rotatecard)
		/******************************************************************************/

		//after the card is moved to its right location flip it
		combineAnimation.onFinished = (event: ActionEvent) =>  {  
			/****************Flip card animation***************************************/
			var cardfront: ImageView = new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/Card/"+card+".png")))
			cardfront.layoutX=cardback.layoutX.value + cardback.translateX.value
			cardfront.layoutY=cardback.layoutY.value + cardback.translateY.value
			cardfront.rotate = cardback.rotate.value
			cardfront.scaleX=0
			mainAnchorPane.getChildren().addAll(cardfront);

			var hideBack:ScaleTransition = new ScaleTransition(new Duration(400), cardback)
			hideBack.setFromX(1)
			hideBack.setToX(0)
			var showFront:ScaleTransition = new ScaleTransition(new Duration(400), cardfront)
			showFront.setFromX(0)
			showFront.setToX(1)

			hideBack.onFinished = (event: ActionEvent) =>  {  
				mainAnchorPane.getChildren().remove(cardback)
				showFront.play();
			}

			showFront.onFinished = (event: ActionEvent) =>  {
				new Thread {
				    override def run {
				        Thread.sleep(200)

				        //must put platform runlater if modify User Interface object in non javafx Thread
				        Platform.runLater(new Runnable() {
				       		override def run {
				       			mainAnchorPane.getChildren().remove(cardfront)

				       			var newcard: ImageView = new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/Card/"+card+".png")))
				       			newcard.fitWidth = 57
				       			newcard.fitHeight = 82
				       			cardListFlowPanes(playerIndex).getChildren().addAll(newcard)
								
				       		}
				       	});
				        
				    }
				}.start()
				
			}
			
			hideBack.play();
			/********************************************************************************/
		}


		mainAnchorPane.getChildren().addAll(cardback)
		combineAnimation.play()	
	}

	def animation_getCard(card:String) = {
		/*********************Move and rotate card animation************************************/
		var cardback: ImageView = new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/Card/cardback.png")))


		cardback.layoutX = 368
		cardback.layoutY = 250
		
		var movecard:TranslateTransition = new TranslateTransition(new Duration(1000), cardback)
		movecard.toX = playerAnimationValue(0)(0)
		movecard.toY = playerAnimationValue(0)(1)

		/******************************************************************************/
		
		movecard.onFinished = (event: ActionEvent) =>  {  
			/****************Flip card animation***************************************/
			var cardfront: ImageView = new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/Card/"+card+".png")))
			cardfront.layoutX=cardback.layoutX.value + cardback.translateX.value
			cardfront.layoutY=cardback.layoutY.value + cardback.translateY.value
			cardfront.rotate = cardback.rotate.value
			cardfront.scaleX=0
			mainAnchorPane.getChildren().addAll(cardfront);

			var hideBack:ScaleTransition = new ScaleTransition(new Duration(500), cardback)
			hideBack.setFromX(1)
			hideBack.setToX(0)
			var showFront:ScaleTransition = new ScaleTransition(new Duration(500), cardfront)
			showFront.setFromX(0)
			showFront.setToX(1)
			var showBack:ScaleTransition = new ScaleTransition(new Duration(500), cardback)
			showBack.setFromX(0)
			showBack.setToX(1)
			var hideFront:ScaleTransition = new ScaleTransition(new Duration(500), cardfront)
			hideFront.setFromX(1)
			hideFront.setToX(0)

			var playedTime:Int = 0
			hideBack.onFinished = (event: ActionEvent) =>  {  
				showFront.play();
			}

			hideFront.onFinished = (event: ActionEvent) =>  {  
				showBack.play();
			}

			showBack.onFinished = (event: ActionEvent) =>  {  
				hideBack.play();
			}

			showFront.onFinished = (event: ActionEvent) =>  {
				if(playedTime < 2){
					hideFront.play();
					playedTime+=1
				}else{
					
					mainAnchorPane.getChildren().remove(cardback)
					var scaleDownCardSize:ScaleTransition = new ScaleTransition(new Duration(700), cardfront)
					scaleDownCardSize.setFromX(1)
					scaleDownCardSize.setToX(0.6)
					scaleDownCardSize.setFromY(1)
					scaleDownCardSize.setToY(0.6)

					var movecardToCardList:TranslateTransition = new TranslateTransition(new Duration(700), cardfront)

					movecardToCardList.toX = -140
					movecardToCardList.toY = 100

					var combineAnimation:ParallelTransition = new ParallelTransition()
					combineAnimation.getChildren().addAll(movecardToCardList,scaleDownCardSize)

					combineAnimation.onFinished = (event: ActionEvent) =>  {
						mainAnchorPane.getChildren().remove(cardfront)

						var newcard: ImageView = new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/Card/"+card+".png")))
		       			newcard.fitWidth = 57
		       			newcard.fitHeight = 82
		       			cardListFlowPanes(0).getChildren().addAll(newcard)
					}

					combineAnimation.play()
					

				}
			}
			
			hideBack.play();
			/********************************************************************************/
		}
		
		mainAnchorPane.getChildren().addAll(cardback)
		movecard.play()

	}




	

	

	
	
	

}