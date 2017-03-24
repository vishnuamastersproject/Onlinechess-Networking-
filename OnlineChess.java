
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;

/*
* Compile:
	javac Board.java
	javac Locker.java
	javac OnlineChess.java

* Run server:
	java OnlineChess port

* Run client
	java OnlineChess ip port
*/

public class OnlineChess {
	private static int port;
	private static String serverIp = "";
	private static PrintWriter out;
	private static BufferedReader stdIn;
	private static long total_time = 0;

	private static void Server(){
		ServerSocket serverSocket;

		try {
			Locker lockWaitingUsers = new Locker(new Hashtable<String, Server>());
			serverSocket = new ServerSocket(port);

			while(true){
				Server server = new Server(serverSocket.accept(), lockWaitingUsers);
				server.start();
			}
			    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String chooseColor(){
		System.out.println("Choose a color: black, white or tac (toss a coin)");
		String answer = "";
		try {
			while ( (answer = stdIn.readLine()) != null ){
				if (answer.equalsIgnoreCase("white") || answer.equalsIgnoreCase("black") || 
					answer.equalsIgnoreCase("tac")){
					out.println(answer);
					break;
				}else{
					System.out.println("Invalid answer. Choose a color: black, white or tac (toss a coin)");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return answer;
	}
	
	private static void getMove(){
		String at = "";
    	String to = "";
    	long time_start, time_end;
    	time_start = System.currentTimeMillis();
    	System.out.print("Move piece at (row column) -> ");
    	try {
			if ( (at = stdIn.readLine()) != null ){
				System.out.print("\t to (row column) -> ");
				if ( (to = stdIn.readLine()) != null ){
					out.println(at);
					out.println(to);
					time_end = System.currentTimeMillis();
			    	total_time += time_end - time_start;
			    	out.println(time(total_time));
			    	System.out.println();
				}
			}
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void choosPiece(){
		String answer = "";
		try {
			while ( (answer = stdIn.readLine()) != null ){
				if (answer.equalsIgnoreCase("queen") || answer.equalsIgnoreCase("rook") || 
					answer.equalsIgnoreCase("bishop") || answer.equalsIgnoreCase("knight")){
					out.println(answer);
					break;
				}else{
					System.out.println("Invalid answer");
					System.out.println("Pawn Promotion. Do you want a queen, rook, bishop, or knight?");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static String time(long time){
		if (time == 0)
			return "(00:00:00)";
		
		long h =  (time / 3600000);
		long aux = (time % 3600000);
		
		long m =  (aux / 60000);
		aux = (aux % 60000);
		
		long s =  (aux / 1000);
	
		
		return "(" + h + ":" + m + ":" + s + ")";
		
	}
	
	private static void Client(){
		
		Socket echoSocket;
		try {
			echoSocket = new Socket(serverIp, port);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader( new InputStreamReader(echoSocket.getInputStream()));
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			String serverMessage, clientMessage;
			String opponent = "";
			String userName = "";
			String color = "";
			String otherColor = "";
			
			while ((serverMessage = in.readLine()) != null) {
				
				/* The server requests the username */
			    if (serverMessage.equals("username")){
			    	System.out.println("Welcome to online chess. What is your username? ");
			    	clientMessage = stdIn.readLine();
			    	userName = clientMessage;
			    	out.println(clientMessage);
			    	
			    /* Invalid username. The server requests the username */
			    }else if (serverMessage.equals("invalidUsername")){
			    	System.out.println("Invalid username. What is your username? ");
			    	clientMessage = stdIn.readLine();
			    	userName = clientMessage;
			    	out.println(clientMessage);
			    	
			    /* There is no other players */
			    } else if (serverMessage.equals("noPlayers")){
			    	System.out.println("There is no other players, you have to wait");
			    
			    /* The server send the list of players */
			    } else if (serverMessage.equals("players")){
			    	String listOfPlayers = "";
			    	String player = "";
			    	while ((player = in.readLine()) != null && !player.equals("&&")){
			    		listOfPlayers += player + "\n";
			    	}
			    	System.out.println("Choose one of the current available waiting player(s):");
			    	System.out.print(listOfPlayers);
			    	System.out.println("If you prefer to wait then send wait");
			    	clientMessage = stdIn.readLine();
			    	opponent = clientMessage;
			    	out.println(clientMessage);
			    	
			    /* The server respond that the player chose a invalid oponent */
			    }else if (serverMessage.equals("invalidPlayer")){
			    	System.out.println("Invalid answer.");
			    
			    /* the other player accepts the invitation */
			    }else if (serverMessage.equals("y")){
			    	System.out.println(opponent+" accept your invitation");
			    	
			    /* The other player rejects the invitation */
			    }else if (serverMessage.equals("n")){
			    	System.out.println(opponent+" rejects your invitation");
			    
			    /* Someone invites you to play */
			    }else if (serverMessage.equals("invitation")){
			    	
			    	if ((opponent = in.readLine()) != null){
			    		System.out.println(opponent + " want to play with you. Do you accept? (y/n): ");
			    		
			    		String answer = "";
			    		while ( (answer = stdIn.readLine()) != null ){
							if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("n")){
								out.println(answer);
								break;

							}else{
								System.out.println("Invalid answer. " + opponent + " want to play with you. Do you accept? (y/n): ");
							}
			    		}
			    	}
			    	
			    /* Opponent  doesn't response */
			    }else if (serverMessage.equals("noanswer")){
			    	System.out.println(opponent+" doesn't response");
			    
			    /* Choose color */
			    }else if (serverMessage.equals("color")){
			    	color = chooseColor();
			    	
			    /* Both chose the same color */
			    }else if (serverMessage.equals("samecolor")){
			    	System.out.println("Both chose the same color");
			    	color = chooseColor();
			    
			    }else if (serverMessage.equals("black") || serverMessage.equals("white")){
			    	System.out.println("Your color is "+serverMessage);
			    	color = serverMessage;
	
			    }else if (serverMessage.equals("init")){
			    	if (color.equalsIgnoreCase("white")){
			    		color = "WHITE";
			    		otherColor = "black";
			    	}else{
			    		color = "black";
			    		otherColor = "WHITE";
			    	}
			    	
			    	System.out.println("Start of game (White play first)");
			    	System.out.println("If you want do a Castling you must write: castling short");
			    	System.out.println("\t\t\t\t\t  or");
			    	System.out.println("\t\t\t\t\t  castling long");
			    	System.out.println("If you want do an En Passant you must write: enPassant row column\n");
			    
			    }else if (serverMessage.equals("board")){
			    	
			    	String aux = "";
			    	String board = ""; 
			    	String timeOther = "";
			    	for (int i = 0 ; i < 8; i ++){
						for(int j = 0; j < 8; j++){
							if ((aux = in.readLine()) != null){
								board += aux + " ";
							}
						}
						board += "\n";
					}
			    	if ((timeOther = in.readLine()) != null)
				    	System.out.println(otherColor+ " " + timeOther +"\n");
			    	else
			    		System.out.println(otherColor +"\n");
			    	System.out.println(board);
			    	System.out.println(color + " " + time(total_time)+"\n\n");
			    	
			    }else if (serverMessage.equals("move")){
			    	
			    	getMove();
			    	
			    }else if (serverMessage.equals("invalidMove")){
			    	System.out.println("Invalid move");
			    	getMove();
			    	
			    }else if (serverMessage.equals("pawnPromotion")){
			    	System.out.println("Pawn Promotion. Do you want a queen, rook, bishop, or knight? ");
			    	choosPiece();
			    	
			    	
			    }else if (serverMessage.equals("win")){
			    	System.out.println("GAME OVER\nCONGRATULATION! YOU WIN");
			    	break;
			    	
			    }else if (serverMessage.equals("lose")){
			    	System.out.println("GAME OVER\nYou lose");
			    	break;
			    }
			    
			}
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		
		/* Server Mode*/
		if (args.length == 1){
			port = Integer.parseInt(args[0]);
			
		/* Client Mode*/
		}else if (args.length == 2){
			serverIp = args[0];
			port = Integer.parseInt(args[1]);
			
		/* Error: Inavlid syntax */
		}else{
			System.out.println("Error: Inavlid syntax");
			System.out.println("Server mode: java OnlineChess port");
			System.out.println("Client mode: java OnlineChess serverIP serverPort");
			System.exit(0);
		}
		
		/* Error: Invalid port number */
		if (port < 1 || port > 65535){
			System.out.println("Error: Invalid port number");
			System.exit(0);
		}
		
		if ( serverIp.equals("") ){
			Server();
		}else{
			Client();
		}
		
	}
}
