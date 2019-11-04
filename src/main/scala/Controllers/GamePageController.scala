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
import scala.collection.mutable.ArrayBuffer
import javafx.scene.paint.Color

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
	val dealerCardHolderIcon: ImageView,
	val popUpTitle: Label,
	val winTypeImageView:ImageView,
	val leaveRoomButton:Button

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
	leaveRoomButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/leaveroom.png"))));

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
		hideActionButtonAndShowMessage("Player 1 Turns!")
		showActionButton(true)

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
		
		//2D Map that store what card they get
		val cards:Map[String,ArrayBuffer[String]] = Map("Eric" -> ArrayBuffer("K_club","Q_club"), 
							                        "John" -> ArrayBuffer("A_diamond"), 
							                        "Jack" -> ArrayBuffer("K_club"),
													"Kelvin" -> ArrayBuffer("2_heart"), 
													"Jason" -> ArrayBuffer("Q_diamond"), 
													"Nick" -> ArrayBuffer("J_spade"), 
													"Jeff" -> ArrayBuffer("3_diamond"), 
													"Edwin" -> ArrayBuffer("6_heart"))
        
		initializeCard(cards)
		
		/*************************/





	}

	def drawCardAction() = {
		animation_getCard("Q_club")
	}
	
	def passAction() = {

	}

	def forfietAction() = {
		val result:Map[String,ArrayBuffer[String]] = Map("Eric" -> ArrayBuffer("win","blackjack"), 
							                        "John" -> ArrayBuffer("win","amounthigh"), 
							                        "Jack" -> ArrayBuffer("draw"),
													"Kelvin" -> ArrayBuffer("win","opponentbust"), 
													"Jason" -> ArrayBuffer("win","opponentbust"), 
													"Nick" -> ArrayBuffer("win","blackjack"), 
													"Jeff" -> ArrayBuffer("win","blackjack"), 
													"Edwin" -> ArrayBuffer("win","blackjack"))

		val cards:Map[String,ArrayBuffer[String]] = Map("Eric" -> ArrayBuffer("K_club","Q_club"), 
							                        "John" -> ArrayBuffer("A_diamond","A_spade"), 
							                        "Jack" -> ArrayBuffer("K_club","2_heart"),
													"Kelvin" -> ArrayBuffer("2_heart","6_diamond"), 
													"Jason" -> ArrayBuffer("Q_diamond","10_diamond"), 
													"Nick" -> ArrayBuffer("J_spade","Q_spade"), 
													"Jeff" -> ArrayBuffer("3_diamond","A_heart"), 
													"Edwin" -> ArrayBuffer("6_heart","1_diamond"))
		showResult(result,cards)
	}

	def leaveRoomAction() ={
		MainApp.goToMainPage()
	}
	
	/********************************************************************************************/


	/**********************Function For editing the UI**********************************************/
	/*
	List of Function

	-> addPlayer(name) 
	This function add a player to the UI

	-> initializeCard(cards)
	This function will give two card to all player one card is open and another card is closed
	The parameter cards is a Map ("Eric"=>("K_club")) which key is playername and value is card

	-> showResult(result,playercardlist)
	This function will show all the result of all player usually called when end games
	result is a map ("Eric"=>("win","blackjack"))where key is name and value is win and type of win
	The parameter cards is a Map ("Eric"=>("K_club")) which key is playername and value is card

	-> showActionButton(canForfiet)
	This function show the action button at bottom right corner
	If parameter true is passed in The Forfiet Button will be show

	-> hideActionButtonAndShowMessage(message)
	This function will hide all button at bottom right corner and add a Message Text at botoom right corner
	Hide the button when it is not the player turn like hideActionButtonAndShowMessage("Player 1 Turn!")

	->animation_sendHiddenCard(playername)
	This function will play the animation send card to the specified player and add the card to the player card list
	The playername must match the name that added using addPlayer(name)

	->animation_sendCardWithReveal(playername,card,WithBlurring)
	This function will play the animation send card to the specified player and flip the card for reveal then add card to player card list
	The playername must match the name that added using addPlayer(name)
	The WithBlurring is boolean while when true the card will have opacity. Usually true for client card where only hisself can see
	The card must match the name below

	->animation_getCard(card)
	This function will play the animation draw card from card deck and then reveal it
	The card will be send to player 0 and add to the card list
	The card must match the name below

	->showWinsLabel(playername,winType)
	This function will display a Title such as blackjack... which show the player has win
	The winType must be one of the following value: blackjack,amounthigh,opponentbust,draw


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

	def initializeCard(playerCards:Map[String,ArrayBuffer[String]]) = {

		//use new Thread because cannot sleep current thread because it will hangs the User Interface
		new Thread {
		    override def run {
		        //this sequence is clockwise
				var cardSequence = ListBuffer[Int]()
				cardSequence += (1,5,3,7,0,6,2,4)

		        //must put platform runlater if modify User Interface object in non javafx Thread
       			for(index <- cardSequence){
       				//if index is small the the length then only its occurs
       				if(index < playerLists.length){
	       				Platform.runLater(new Runnable() {
	       					override def run {
	       						
	       						animation_sendCardWithReveal(playerLists(index),playerCards(playerLists(index))(0),false)       						
	       					}
	       				});
	       				Thread.sleep(500)
       				}
       			}

       			for(index <- cardSequence){
       				if(index < playerLists.length){
	       				Platform.runLater(new Runnable() {
	       					override def run {
       							if(index == 0 ){
       								animation_sendCardWithReveal(playerLists(index),playerCards(playerLists(index))(1),true)   
       							}else{
       								animation_sendHiddenCard(playerLists(index))
       							}	
	       					}
	       				});
	       				Thread.sleep(500)
	       			}
       				
       				
       			}
		    }
		}.start()
	}

	var playerCardHolderList = new ListBuffer[AnchorPane]()
	playerCardHolderList += (player0CardHolder,player1CardHolder,player2CardHolder,player3CardHolder,player4CardHolder,player5CardHolder,player6CardHolder,player7CardHolder)

	def showResult(result: Map[String,ArrayBuffer[String]],playerCards:Map[String,ArrayBuffer[String]]) = {

		//use new Thread because cannot sleep current thread because it will hangs the User Interface
		new Thread {
		    override def run {
		        //this sequence is clockwise
				var cardSequence = ListBuffer[Int]()
				cardSequence += (5,3,7,0,6,2,4)

				//make dealer card holder background lightblue
				Platform.runLater(new Runnable() {
   					override def run {
						playerCardHolderList(1).style="-fx-border-width:3px;-fx-border-color:white;-fx-border-radius:10px;-fx-background-color: lightblue;"
   					}
   				});
				
				//********************show card of the dealer**************
				//remove the already revealed first card
				playerCards(playerLists(1)) -= cardListFlowPanes(1).getChildren()(0).asInstanceOf[javafx.scene.image.ImageView].userData.asInstanceOf[String]
				var currentCardIndex:Int = 1
				for(card <- playerCards(playerLists(1))){
					//if there are a hidden card replace the image
					if( currentCardIndex < cardListFlowPanes(1).getChildren().length){
						var hiddenCard:ImageView = cardListFlowPanes(1).getChildren()(currentCardIndex).asInstanceOf[javafx.scene.image.ImageView]
						Platform.runLater(new Runnable() {
		   					override def run {
								hiddenCard.image = new Image(getClass.getResourceAsStream("/Images/GamePage/Card/"+card+".png"))
		   					}
		   				});
						
						Thread.sleep(300)	
					}else{
						var newhiddencard: ImageView = new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/Card/"+card+".png")))

						Platform.runLater(new Runnable() {
		   					override def run {
								cardListFlowPanes(1).getChildren().addAll(newhiddencard)
		   					}
		   				});

						Thread.sleep(300)
					}
					currentCardIndex+=1
				}
				//************************************************

				for(index <- cardSequence){
					//if this player exist
					if(index < playerLists.length){
						//make card holder background light blue
						Platform.runLater(new Runnable() {
		   					override def run {
								playerCardHolderList(index).style="-fx-border-width:3px;-fx-border-color:white;-fx-border-radius:10px;-fx-background-color: lightblue;"
		   					}
		   				});
						Thread.sleep(300)

						//********************show card of the player**************
						//remove the already revealed first card
						playerCards(playerLists(index)) -= cardListFlowPanes(index).getChildren()(0).asInstanceOf[javafx.scene.image.ImageView].userData.asInstanceOf[String]
						var currentCardIndex:Int = 1
						for(card <- playerCards(playerLists(index))){
							//if there are a hidden card replace the image
							if( currentCardIndex < cardListFlowPanes(index).getChildren().length){
								var hiddenCard:ImageView = cardListFlowPanes(index).getChildren()(currentCardIndex).asInstanceOf[javafx.scene.image.ImageView]
								Platform.runLater(new Runnable() {
				   					override def run {
										hiddenCard.image = new Image(getClass.getResourceAsStream("/Images/GamePage/Card/"+card+".png"))
										hiddenCard.opacity = 1
				   					}
				   				});

								Thread.sleep(300)	
							}else{
								var newhiddencard: ImageView = new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/Card/"+card+".png")))
								Platform.runLater(new Runnable() {
				   					override def run {
										cardListFlowPanes(index).getChildren().addAll(newhiddencard)
				   					}
				   				});
								Thread.sleep(300)
							}
							currentCardIndex+=1
						}
						//************************************************

						//show win condition
						result(playerLists(index))(0) match {
							case "win" =>
								//player wins
								Platform.runLater(new Runnable() {
				   					override def run {
										showWinsLabel(playerLists(index),result(playerLists(index))(1))
				   					}
				   				});
								
								Thread.sleep(1250)

							case "lose" => 
								//dealer wins
								Platform.runLater(new Runnable() {
				   					override def run {
										showWinsLabel(playerLists(1),result(playerLists(index))(1))
				   					}
				   				});
								
								Thread.sleep(1250)

							case "draw" => 
								Platform.runLater(new Runnable() {
				   					override def run {
										showWinsLabel("","draw")
				   					}
				   				});
								
								Thread.sleep(1250)
						}
						//make card holder background none
						playerCardHolderList(index).style="-fx-border-width:3px;-fx-border-color:white;-fx-border-radius:10px;-fx-background-color: none;"
						var newLabel:Label = new Label(result(playerLists(index))(0))
						newLabel.layoutX = 20
						newLabel.layoutY = 20
						newLabel.textFill = Color.web("#FFFFFF")

						Platform.runLater(new Runnable() {
		   					override def run {
								playerCardHolderList(index).getChildren.addAll(newLabel)
		   					}
		   				});
						

						Thread.sleep(100)
					}
				}

				//show whether you win or lose
				result(playerLists(0))(0) match{
					case "win" =>
						//player wins
						Platform.runLater(new Runnable() {
		   					override def run {
		   						winTypeImageView.image = new Image(getClass.getResourceAsStream("/Images/GamePage/youwin.png"))
		   						winTypeImageView.rotate=0
								winTypeImageView.visible=true

								leaveRoomButton.visible = true
								hideActionButtonAndShowMessage("Game Ended!")
		   					}
		   				});

					case "lose" => 
						//dealer wins
						Platform.runLater(new Runnable() {
		   					override def run {
								winTypeImageView.image = new Image(getClass.getResourceAsStream("/Images/GamePage/youlose.png"))
								winTypeImageView.rotate=0
								winTypeImageView.visible=true

								leaveRoomButton.visible = true
								hideActionButtonAndShowMessage("Game Ended!")
		   					}
		   				});


					case "draw" => 
						Platform.runLater(new Runnable() {
		   					override def run {
								winTypeImageView.image = new Image(getClass.getResourceAsStream("/Images/GamePage/youdraw.png"))
								winTypeImageView.rotate=0
								winTypeImageView.visible=true

								leaveRoomButton.visible = true
								hideActionButtonAndShowMessage("Game Ended!")
		   					}
		   				});

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
			       			newhiddencard.userData = "unknown"
			       			cardListFlowPanes(playerIndex).getChildren().addAll(newhiddencard)

							
			       		}
			       	});
			        
			    }
			}.start()
			
		}


		mainAnchorPane.getChildren().addAll(cardback)
		combineAnimation.play()	
	}


	def animation_sendCardWithReveal(playerName:String,card:String,withBlurring: Boolean) = {

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
				       			newcard.userData = card
				       			if(withBlurring == true){
				       				newcard.opacity = 0.7
				       			}
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

			var hideBack:ScaleTransition = new ScaleTransition(new Duration(100), cardback)
			hideBack.setFromX(1)
			hideBack.setToX(0)
			var showFront:ScaleTransition = new ScaleTransition(new Duration(100), cardfront)
			showFront.setFromX(0)
			showFront.setToX(1)
			var showBack:ScaleTransition = new ScaleTransition(new Duration(100), cardback)
			showBack.setFromX(0)
			showBack.setToX(1)
			var hideFront:ScaleTransition = new ScaleTransition(new Duration(100), cardfront)
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
				if(playedTime < 1){
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
		       			newcard.opacity = 0.7
		       			newcard.userData = card
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

	def showWinsLabel(playername:String, winType: String) = {
		popUpTitle.visible = true
		popUpTitle.toFront()
		winTypeImageView.visible = true

		popUpTitle.text = playername

		winType match {
			case "blackjack" =>  
				winTypeImageView.image = new Image(getClass.getResourceAsStream("/Images/GamePage/blackjackwin.png"))
				winTypeImageView.fitWidth = 479
				winTypeImageView.fitHeight = 153
			case "amounthigh" =>
				winTypeImageView.image = new Image(getClass.getResourceAsStream("/Images/GamePage/amounthighwin.png"))
				winTypeImageView.fitWidth = 598
				winTypeImageView.fitHeight = 153
			case "opponentbust"=>
				winTypeImageView.image = new Image(getClass.getResourceAsStream("/Images/GamePage/opponentbustwin.png"))
				winTypeImageView.fitWidth = 673
				winTypeImageView.fitHeight = 147
			case "draw" =>
				winTypeImageView.image = new Image(getClass.getResourceAsStream("/Images/GamePage/draw.png"))
				winTypeImageView.fitWidth = 368
				winTypeImageView.fitHeight = 103
		}

		var shakeTitle:RotateTransition = new RotateTransition(new Duration(250), winTypeImageView)
		shakeTitle.fromAngle = -15
		shakeTitle.toAngle = 15
		shakeTitle.autoReverse=true
		shakeTitle.cycleCount = 5

		
		shakeTitle.onFinished = (event: ActionEvent) =>  {
			popUpTitle.visible = false
			winTypeImageView.visible = false


		}

		shakeTitle.play()

	}




	

	

	
	
	

}