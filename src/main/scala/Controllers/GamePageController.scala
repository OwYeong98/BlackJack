package Controllers

import MainSystem.MainApp
import ds.server.GameLogic._
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

import akka.actor.{Actor, ActorRef,ActorSelection, Props}

//Jenson
import ds.client.GamePageClientActor

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

	drawCardButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/drawcard.png"))));
	passButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/pass.png"))));
	forfietButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/forfiet.png"))));
	leaveRoomButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream("/Images/GamePage/leaveroom.png"))));


	//this variable store the player name of this client
	var playerName: String = null

	// Player name list with ordered sequence
	var playerTurnSequence:ArrayBuffer[String] = ArrayBuffer[String]() 
	//this list store player name of all player
	var playerLists:ArrayBuffer[String] = ArrayBuffer[String](null,null,null,null,null,null,null,null) 
	
	//this the the game page ActorRef
	var gamePageClientActorRef: ActorRef = null

	/**********************Function For Network**************************************************/

	//this function will be called when this page run
	def initializeData( playerName: String,clientActorRef:ActorRef) = {
		this.playerName = playerName
		//set userIcon
		userIcon.image = new Image(getClass.getResourceAsStream("/Images/GamePage/player.png"))
		setUserName(playerName)


		this.gamePageClientActorRef=clientActorRef
		

		//maybe ask ser initialize player name
	}

	def drawCardAction() = {
		//Tell server send client playerName back to server to perform deck.draw()
		gamePageClientActorRef ! GamePageClientActor.Draw(playerName)
		//server 
	}
	
	def passAction() = {
		gamePageClientActorRef ! "Passed"
		
	}

	//leave it first not sure if u guys want let user to forfiet the game if he got 16 or 17 point
	def forfietAction() = {
		
	}

	def leaveRoomAction() ={
		MainApp.goToMainPage()
	}

	var players = ArrayBuffer[Player]()
	def gameLogic() = {
		
		var pname = ArrayBuffer("Player4","Player1","Player3","Player2","Player5","Player6")
		var winner = ArrayBuffer[String]()

		//Instantiating Players
		pname.map((n: String) => {
			players.append(new Player(n))
		})
		println(playerLists)
		initializePlayer(pname)
		println(playerLists)

		 //Making the Deck
		val deck = new Deck()
		deck.makeDeck()
		//Can shuffle multiple times for more randomness?
		deck.shuffle()
		deck.shuffle()
		deck.shuffle()
		deck.shuffle()

		//Distributing the cards to the players
		for (c <- 0 to 1){
			players.map((p: Player) => {
			p.assignHandCard(deck.draw())
			})
		}

		val playerCards:Map[String,ArrayBuffer[Tuple3[String,Integer,String]]] = Map[String,ArrayBuffer[Tuple3[String,Integer,String]]]()

		for(player <- players){
			//make map ("Player1"->CardList)
			playerCards+=(player.playerName -> player.handCard)
		}

		//animation send card to each player
		initializeCard(playerCards)
		
		/*
		//get all player card
		val finalPlayerCards:Map[String,ArrayBuffer[Tuple3[String,Integer,String]]] = Map[String,ArrayBuffer[Tuple3[String,Integer,String]]]()

		for(player <- players){
			//make map ("Player1"->CardList)
			finalPlayerCards+=(player.playerName -> player.handCard)
		}
		
		//determine winner and show result
		showResult(ArrayBuffer[String]("Player1","Player2"),finalPlayerCards)
		*/
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
	
	*/
	def changePlayer(playerNameTurn:String) = {
		new Thread {
		    override def run {
				Platform.runLater(new Runnable() {
   					override def run {
						popUpTitle.visible = true
						popUpTitle.text = playerNameTurn +" Turn!"
   					}
   				});
				
				Thread.sleep(2000)
				Platform.runLater(new Runnable() {
   					override def run {
						popUpTitle.visible=false
						popUpTitle.text = ""
   					}
   				});
		    }
		}.start()
	
		//if it is this client turn show button
		if(playerName == playerNameTurn){
			showActionButton(false)
		}else{
			hideActionButtonAndShowMessage("It's "+playerNameTurn+" turn!")
			gamePageClientActorRef ! "Not My Turn"
		}	
	}

	def sendCard(name:String,card:String) = {
		//if the player is self
		if(playerName == name){
			animation_getCard(card)
		}else{
			animation_sendHiddenCard(name)
		}	
	}

	def removePlayer(name:String) ={
		playerTurnSequence -= name
		var index:Int = playerLists.indexOf(name)
		playerNameLabelList(index).text = ""
		playerVBoxList(index).visible = false
		cardListFlowPanes(index).getChildren().clear()
		playerLists.update(index,null)
	}

	def initializePlayer(playerList: ArrayBuffer[String] ) = {
		playerTurnSequence.clear();
		for(x <- playerNameLabelList){
			x.text = ""
		}
		for(x <- playerVBoxList){
			x.visible = false
		}
		for(x <- 0 to playerLists.length -1){
			playerLists(x) = null
		}
		for(x <- cardListFlowPanes){
			x.getChildren().clear()
		}

		playerTurnSequence++=playerList
		var clockwiseSequence = ListBuffer[Int]()
		clockwiseSequence += (0,6,2,4,1,5,3,7)

		//remove empty player in clockwise sequence
		for(x <- clockwiseSequence){
			if(x >= playerList.length ){
				clockwiseSequence -= x
			}
		}

		//playerList will show player in sequence which according to their turnn
		//for example if playerList = ("Jack","Nick","John","Martin") and player of this client is John
		//what i trying to do here is rearrage so that john will be the first
		//after rearrange will be playerList = ("John","Martin","Jack","Nick")
		
		//println(playerList)
		var beforePlayer = true
		var splitedArray:(ArrayBuffer[String],ArrayBuffer[String]) = playerList.partition((s:String)=>{
			if(s.toLowerCase == playerName.toLowerCase)
				beforePlayer=false

			beforePlayer
		})
		//splittedArray._1 will be player before this client which is ArrayBuffer("Jack","Nick")
		//splittedArray._2 will be player after this client which is ArrayBuffer("John","Martin")
		var rearrangedPlayerList = splitedArray._2++splitedArray._1

		for(index <- 0 to rearrangedPlayerList.length-1){
			playerLists.update(clockwiseSequence(index),rearrangedPlayerList(index))
			playerNameLabelList(clockwiseSequence(index)).text = rearrangedPlayerList(index)
			playerVBoxList(clockwiseSequence(index)).visible = true
		}
	}

	def initializeCard(playerCards:Map[String,ArrayBuffer[Tuple3[String,Integer,String]]]) = {


		//use new Thread because cannot sleep current thread because it will hangs the User Interface
		new Thread {
		    override def run {
		        //must put platform runlater if modify User Interface object in non javafx Thread
       			for(currPlayerName <- playerTurnSequence){
					if(currPlayerName != null){
						println(currPlayerName)
						Platform.runLater(new Runnable() {
							override def run {
								var cardName:String = playerCards(currPlayerName)(0)._3 +"_"+playerCards(currPlayerName)(0)._1 

								animation_sendCardWithReveal(currPlayerName,cardName,false)       						
							}
						});
						Thread.sleep(500)
					}
       				
       			}

       			for(currPlayerName <- playerTurnSequence){
					if(currPlayerName != null){
						Platform.runLater(new Runnable() {
							override def run {
								var cardName:String = playerCards(currPlayerName)(1)._3 +"_"+playerCards(currPlayerName)(1)._1 

								//if equal to this client reveal the card
								if(currPlayerName == playerName ){
									animation_sendCardWithReveal(currPlayerName,cardName,true)   
								}else{
									animation_sendHiddenCard(currPlayerName)
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

	def showResult(winnerList: ArrayBuffer[String],playerCardsTuple:Map[String,ArrayBuffer[Tuple3[String,Integer,String]]]) = {
		val result:Map[String,String] = Map()
		for(playerName <- playerTurnSequence){
			if(winnerList.contains(playerName)){
				//if only 1 winner then win else draw
				if(winnerList.length == 1){
					result+=(playerName->"win")
				}else{
					result+=(playerName->"draw")
				}
			}else{
				result+=(playerName->"lose")
			}
		}
		var playerCards = Map[String,ArrayBuffer[String]]()
		for((currPlayerName, cards) <- playerCardsTuple){
			var cardStringList:ArrayBuffer[String] = ArrayBuffer[String]()
			for(currCard <- cards ){
				var cardName:String = currCard._3 +"_"+currCard._1 
				cardStringList+=cardName
			}
			
			playerCards+=(currPlayerName->cardStringList)
		}
		


		//use new Thread because cannot sleep current thread because it will hangs the User Interface
		new Thread {
		    override def run {			
				

				for(name <- playerTurnSequence){
					//make card holder background light blue
					Platform.runLater(new Runnable() {
						override def run {
							playerCardHolderList(playerLists.indexOf(name)).style="-fx-border-width:3px;-fx-border-color:white;-fx-border-radius:10px;-fx-background-color: lightblue;"
						}
					});
					Thread.sleep(300)

					//********************show card of the player**************
					//remove the already revealed first card
					println(playerCards(name))
					playerCards(name) -= cardListFlowPanes(playerLists.indexOf(name)).getChildren()(0).asInstanceOf[javafx.scene.image.ImageView].userData.asInstanceOf[String]
					println(playerCards(name))
					var currentCardIndex:Int = 1
					for(card <- playerCards(name)){
						//if there are a hidden card replace the image
						if( currentCardIndex < cardListFlowPanes(playerLists.indexOf(name)).getChildren().length){
							var hiddenCard:ImageView = cardListFlowPanes(playerLists.indexOf(name)).getChildren()(currentCardIndex).asInstanceOf[javafx.scene.image.ImageView]
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
									cardListFlowPanes(playerLists.indexOf(name)).getChildren().addAll(newhiddencard)
								}
							});
							Thread.sleep(300)
						}
						currentCardIndex+=1
					}
					//************************************************

					//show win condition
					if(result(name) == "win"){
						Platform.runLater(new Runnable() {
							override def run {
								showWinsLabel(name,"win")
							}
						});
						Thread.sleep(1250)
					}else if(result(name) == "lose"){
						Platform.runLater(new Runnable() {
							override def run {
								showWinsLabel(name,"lose")
							}
						});
						Thread.sleep(1250)
					}else if(result(name) == "draw"){
						Platform.runLater(new Runnable() {
							override def run {
								showWinsLabel(name,"draw")
							}
						});
						Thread.sleep(1250)
					}
					
					//make card holder background none
					playerCardHolderList(playerLists.indexOf(name)).style="-fx-border-width:3px;-fx-border-color:white;-fx-border-radius:10px;-fx-background-color: none;"
					var newLabel:Label = new Label(result(name))
					newLabel.layoutX = 20
					newLabel.layoutY = 20
					newLabel.textFill = Color.web("#FFFFFF")

					Platform.runLater(new Runnable() {
						override def run {
							playerCardHolderList(playerLists.indexOf(name)).getChildren.addAll(newLabel)
						}
					});
					

					Thread.sleep(100)
					
				}

				//show whether you win or lose
				result(playerName) match{
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
			if(playerLists(index)!=null){
				if(playerLists(index).toLowerCase() == playerName.toLowerCase()){
					playerIndex = index
				}
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
			if(playerLists(index) != null){
				if(playerLists(index).toLowerCase() == playerName.toLowerCase()){
					playerIndex = index
				}
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
			case "win" =>  
				winTypeImageView.image = new Image(getClass.getResourceAsStream("/Images/GamePage/win.png"))
				winTypeImageView.fitWidth = 479
				winTypeImageView.fitHeight = 153
			case "lose" =>
				winTypeImageView.image = new Image(getClass.getResourceAsStream("/Images/GamePage/lose.png"))
				winTypeImageView.fitWidth = 598
				winTypeImageView.fitHeight = 153
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