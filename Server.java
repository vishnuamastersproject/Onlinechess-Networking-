import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Pattern;




public class Server extends Thread{
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private Locker lockWaitingUsers;
	private String userName = "";
	private Boolean negotiating = false;

	private Server otherPlayer = null;
	private String answer = "";
	private Boolean timeIsOver = false;
	private String myColor = "";
	private String opponentColor = "";
	private Board boardManager = null;
	private Boolean finish = false;
	private boolean myTurn = true;

	
	public Server(Socket socket, Locker lock){
		clientSocket = socket;
		lockWaitingUsers = lock;
		
		
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getUserName(){
		return this.userName;
	}
	
	
	public synchronized void timeIsOver(){
		timeIsOver = true;
		notify();
	}
	
	public Board getBoard(){
		return this.boardManager;
	}
	
	public synchronized Boolean waitForOtherPlayer(){
		this.timeIsOver = false;
		new Clock(this).start();
		
		while(otherPlayer == null && !timeIsOver){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return otherPlayer!=null;
	}
	
	public synchronized void stopToWait(Server server){

		if (otherPlayer == null){
			otherPlayer = server;
			notify();
		}
	}
	
	public synchronized void waitForAnswer(){
		timeIsOver = false;
		new Clock(this).start();
		while(answer.equals("") && !timeIsOver){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void reply(String answer){
		this.answer = answer;
		notify();
	}
	
	private void selectUserName(){
		try {
			String user;
			/* Read the username of the client */
			while ( (user = in.readLine()) != null ){
				if ( !user.equals("") && !Pattern.matches("\\s+", user) && 
						user.indexOf("&")<0 && 
						!user.equalsIgnoreCase("wait") &&
						lockWaitingUsers.addUser(user, this)){
					break;
				}else{
					out.println("invalidUsername");
				}
			}
			this.userName = user;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Boolean isNegotiating(){
		return this.negotiating;
	}
	
	private boolean sendReply(){
		negotiating = true;
		out.println("invitation");
		out.println(otherPlayer.getUserName());
		try {
			String answer;
			while ( (answer = in.readLine()) != null ){
				if (answer.equalsIgnoreCase("y")){
					otherPlayer.reply("y");
					return true;
				}else if (answer.equalsIgnoreCase("n")){
					otherPlayer.reply("n");
					otherPlayer = null;
					negotiating = false;
					return false;
				}
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	
	public synchronized String sendMyColor(String color){
		if (this.myColor.equals("")){
			boardManager = new Board();
			boardManager.addUser(this.userName);
		}
		while (this.myColor.equals("")){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.opponentColor = color;
		return myColor;
		
		
	}
	
	private synchronized void setMyColor(String color){
		this.myColor = color;
		notify();
	}

	private void sendBoard(){
		String[][] board;
		if (this.myColor.equalsIgnoreCase("white"))
			board = boardManager.getBoardWhite();
		else
			board = boardManager.getBoardBlack();
		out.println("board");
		for (int i = 0 ; i < 8; i ++){
			for(int j = 0; j < 8; j++){
				if ( board[i][j] == null)
					out.println(".");
				else
					out.println(board[i][j]);
			}
		}
		out.println(boardManager.getTime(this.myColor.equalsIgnoreCase("black")));
	}
	
	private synchronized void waitMyTurn(){
		while(!myTurn){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void nowYouCanPlay(){
		myTurn = true;
		notify();
	}
	
	public void run() {
		String message = "";
		out.println("username");

		try {
			/* 1st Select username */
			selectUserName();
			Boolean accept = false;
			
			/* 2nd Choose other player */
			while (!accept){
				/* There is no other players */
				if (lockWaitingUsers.size() == 1){
					out.println("noPlayers");
					if (waitForOtherPlayer())
						accept = sendReply();

				/* Choose a oponent*/
				}else{
					
					/* Someone invites you to play */
					if (otherPlayer != null){
						accept = sendReply();
					
					/* Invites another player */
					}else{
						
						/* Send the list of others players */
						out.println("players");
						for(Enumeration<String> e = lockWaitingUsers.keys(); e.hasMoreElements();){
							String next = e.nextElement();
							if ( !next.equals(this.userName))
								out.println(next);
						}
						out.println("&&");
						
						/* Read selection */
						String selectedPlayer = "";
						if ( (selectedPlayer = in.readLine()) != null ){
							if ( selectedPlayer.equalsIgnoreCase("wait")){
								if (waitForOtherPlayer())
									accept = sendReply();
								
							}else if ( !selectedPlayer.equals("") && !Pattern.matches("\\s+", selectedPlayer) && 
					    			lockWaitingUsers.contains(selectedPlayer) && !selectedPlayer.equals(this.userName)){
								
					    		otherPlayer = lockWaitingUsers.getUser(selectedPlayer);
							    if (otherPlayer != null && !otherPlayer.isNegotiating()){
							    	/* Send the invitation to the other player */
							    	otherPlayer.stopToWait(this);
							    	waitForAnswer();
							    	
							    	if (this.answer.equalsIgnoreCase("y")){
							    		out.println("y");
							    		lockWaitingUsers.removeUser(this.userName);
							    		lockWaitingUsers.removeUser(otherPlayer.getUserName());
								    	accept = true;
								    
							    	}else if (this.answer.equalsIgnoreCase("")){
							    		out.println("noanswer");
							    		otherPlayer = null;
							    		
							    	}else{
							    		out.println("n");
							    		otherPlayer = null;
							    		this.answer = "";
							    		
							    	}
		
							    }else {
							    	out.println("noanswer");
							    	otherPlayer = null;
							    }
					    	}else{
					    		out.println("invalidPlayer");
					    	}
						}
					}

				}
			}
			
			/* 3rd Choose color */
			out.println("color");
			String color;
			while ( (color = in.readLine()) != null ){
				setMyColor(color);
				opponentColor = otherPlayer.sendMyColor(this.myColor);
				if (this.boardManager == null){
					boardManager = otherPlayer.getBoard();
					boardManager.addUser(this.userName);
				}
				
				
				
				/* Both chose the same color */
				if (myColor.equalsIgnoreCase("white") && opponentColor.equalsIgnoreCase("white") ||
					myColor.equalsIgnoreCase("black") && opponentColor.equalsIgnoreCase("black")){
					out.println("samecolor");
					myColor = "";
					opponentColor = "";
					this.boardManager = null;
					
				/* Both chose different colors and start the game */
				}else if ((myColor.equalsIgnoreCase("white") && opponentColor.equalsIgnoreCase("black")) ||
						  (myColor.equalsIgnoreCase("black") && opponentColor.equalsIgnoreCase("white")) ||
						  (myColor.equalsIgnoreCase("white") && opponentColor.equalsIgnoreCase("tac")) || 
						  (myColor.equalsIgnoreCase("black") && opponentColor.equalsIgnoreCase("tac")) ){
					if (myColor.equalsIgnoreCase("white")){
						boardManager.setFirstPlayer(userName);
					}else{
						boardManager.setSecondPlayer(userName);
					}
					System.out.println("Start the game");
					break;
					
				/* I choose to toss a coin and the other player selects white */
				}else if (myColor.equalsIgnoreCase("tac") && opponentColor.equalsIgnoreCase("white")){
					this.myColor = "black";
					boardManager.setSecondPlayer(userName);
					out.println("black");
					break;
					
				/* I choose to toss a coin and the other player selects black */
				}else if (myColor.equalsIgnoreCase("tac") && opponentColor.equalsIgnoreCase("black")) {
					this.myColor = "white";
					boardManager.setFirstPlayer(userName);
					out.println("white");
					break;
				
				/* Both wants to toss a coin */
				}else if (myColor.equalsIgnoreCase("tac") && opponentColor.equalsIgnoreCase("tac")) {
					if (boardManager.imWhite(userName)){
						this.myColor = "white";
						out.println("white");
					}else{
						this.myColor = "black";
						out.println("black");
					}
					break;
				}
				
			}
			out.println("init");
			if (this.myColor.equalsIgnoreCase("black"))
				myTurn = false;

			sendBoard();
			
			/* Start the game */
			while (!finish){
				
				if (myTurn && boardManager.win(this.myColor.equalsIgnoreCase("black")) == -1){
					out.println("move");
					String at = "";
					String to = "";
					String time = "";
					while ((at = in.readLine()) != null && (to = in.readLine()) != null && (time = in.readLine()) != null){
						boardManager.setTime(this.myColor.equalsIgnoreCase("black"), time);
						if (boardManager.isValidMove(at, to, this.myColor.equalsIgnoreCase("black"))){
							if (boardManager.isPawnPromotion()){
								out.println("pawnPromotion");
								String piece;
								if((piece = in.readLine()) != null){
									boardManager.makePromotion(piece, this.myColor.equalsIgnoreCase("black"));
								}
							}
							sendBoard();
							
							myTurn = false;
							otherPlayer.nowYouCanPlay();
							
							if (boardManager.win(this.myColor.equalsIgnoreCase("black")) == 0){
								out.println("win");
								finish = true;
							}
							break;
						}else{
							out.println("invalidMove");
						}
					}

				}else if (myTurn && boardManager.win(this.myColor.equalsIgnoreCase("black")) == 1){
					out.println("lose");
					finish = true;
				}else{
					waitMyTurn();
					sendBoard();
					
					
				}
			}
		System.out.println("game over");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
}