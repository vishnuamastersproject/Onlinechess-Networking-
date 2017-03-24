import java.util.Random;

public class Board {
	private String[][] board = new String[8][8];
	private String firstPlayer = "";
	private String secondPlayer = "";
	private boolean tossACoin = false;
	private boolean whiteCastlingLeft = true;
	private boolean whiteCastlingRight = true;
	private boolean blackCastlingLeft = true;
	private boolean blackCastlingRight = true;
	private boolean doEnPassant = false;
	private int enPassantRow = -1;
	private int enPassantColumn = -1;
	private boolean pawnPromotion = false;
	private int promotionRow;
	private int promotioColumn;
	private boolean whiteCheck = false;
	private boolean blackCheck = false;
	
	private int threateningRow = -1;
	private int threateningColumn = -1;
	
	private int whiteKingRow = 7;
	private int whiteKingColumn = 4;
	private int blackKingRow = 0;
	private int blackKingColumn = 4;
	
	private int whiteCheckMate = -1;
	private int blackCheckMate = -1;
	
	private String whiteTime = "(00:00:00)";
	private String blackTime = "(00:00:00)";

	public Board(){
		initBoard();
	}
	
	public String getTime(boolean isBlack){
		if (!isBlack)
			return blackTime;
		else
			return whiteTime;
	}
	
	public void setTime(boolean isBlack, String time){
		if (!isBlack)
			whiteTime = time;
		else
			blackTime = time;
	}
	
	public boolean isPawnPromotion(){
		if (pawnPromotion){
			pawnPromotion = false;
			return true;
		}else
			return false;
	}

	public int win(boolean isBlack){
		if (!isBlack)
			return whiteCheckMate;
		else
			return blackCheckMate;
	}
	public void makePromotion(String piece, boolean isBlack){
		if (!isBlack ){
			if (piece.equalsIgnoreCase("queen")){
				board[promotionRow][promotioColumn] = "Q";
				checkQueen(promotionRow,promotioColumn,isBlack);
			}else if (piece.equalsIgnoreCase("rook")){
				board[promotionRow][promotioColumn] = "R";
				checkRook(promotionRow,promotioColumn,isBlack);
			}else if (piece.equalsIgnoreCase("bishop")){
				board[promotionRow][promotioColumn] = "B";
				checkBishop(promotionRow,promotioColumn,isBlack);
			}else if (piece.equalsIgnoreCase("knight")){
				board[promotionRow][promotioColumn] = "N";
				checkKnight(promotionRow,promotioColumn,isBlack);
			}
		}else{
			if (piece.equalsIgnoreCase("queen")){
				board[promotionRow][promotioColumn] = "q";
				checkQueen(promotionRow,promotioColumn,isBlack);
			}else if (piece.equalsIgnoreCase("rook")){
				board[promotionRow][promotioColumn] = "r";
				checkRook(promotionRow,promotioColumn,isBlack);
			}else if (piece.equalsIgnoreCase("bishop")){
				board[promotionRow][promotioColumn] = "b";
				checkBishop(promotionRow,promotioColumn,isBlack);
			}else if (piece.equalsIgnoreCase("knight")){
				board[promotionRow][promotioColumn] = "n";
				checkKnight(promotionRow,promotioColumn,isBlack);
			}
		}
	}
	
	public void addUser(String user){
		if (firstPlayer.equalsIgnoreCase("")){
			firstPlayer = user;
		}else{
			secondPlayer = user;
		}
	}
	
	public boolean isFirstPlayer(String user){
		return firstPlayer.equalsIgnoreCase(user);
	}
	
	public synchronized Boolean imWhite(String user){
		if (!tossACoin){
			Random rnd = new Random();
			rnd.setSeed(System.currentTimeMillis()); 
			Float coin = rnd.nextFloat();
			if (coin > 0.5){
				String aux = firstPlayer;
				firstPlayer = secondPlayer;
				secondPlayer = aux;
			}
			tossACoin = true;
		}
		return user.equalsIgnoreCase(firstPlayer);
	}
	
	public void setFirstPlayer(String user){
		firstPlayer = user;
	}
	
	public void setSecondPlayer(String user){
		secondPlayer = user;
	}
	
	private void initBoard(){
		/* peon */
		for (int j = 0 ; j < 8 ; j ++ ){
			board[1][j] = "p";
			board[6][j] = "P";
		}
		/* rook */
		board[0][0] = board[0][7] = "r";
		board[7][0] = board[7][7] = "R";
		
		/* Knight */
		board[0][1] = board[0][6] = "n";
		board[7][1] = board[7][6] = "N";
		
		/* bishop */
		board[0][2] = board[0][5] = "b";
		board[7][2] = board[7][5] = "B";
		
		/* queen */
		board[0][3] = "q";
		board[7][3] = "Q";
		
		/* king */
		board[0][4] = "k";
		board[7][4] = "K";
	}
	
	public String[][] getBoardWhite(){
		return board;
	}
	
	public String[][] getBoardBlack(){
		String[][] aux = new String[8][8];
		for (int i = 0 ; i < 8; i ++){
			for(int j = 0; j < 8; j++){
				aux[i][j] = board[8-i-1][8-j-1];
			}
		}
		
		return aux;
	}
	
	
	private boolean validRook(int r1, int c1, int r2, int c2){
		if ( r1 != r2 && c1 != c2){
			return false;
			
		}else if (r1 == r2 ){
			int min = Math.min(c1,c2);
			int max = Math.max(c1, c2);
			
			for(int j = min + 1 ; j < max ; j++){
				if ( board[r1][j] != null )
					return false;
			}
			
		}else if (c1 == c2){
			int min = Math.min(r1,r2);
			int max = Math.max(r1, r2);
			for(int i = min + 1; i < max ; i++){
				if ( board[i][c1] != null)
					return false;
			}
		}
		return true;
	}
	
	private void checkRook(int r2, int c2, boolean isBlack){
		boolean isCheck = false;
		int i = r2 - 1;
		
		/* up*/
		while ( !isCheck && i >= 0 ){
			if (!isBlack && board[i][c2]!=null && board[i][c2].equals("k")){
				isCheck = true;
				whiteCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}if (isBlack  && board[i][c2]!=null && board[i][c2].equals("K")){
				isCheck = true;
				blackCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}else if (board[i][c2]!=null && !board[i][c2].equalsIgnoreCase("k"))
				break;
			
			i--;
		}
		
		/* down */
		i = r2 + 1;
		while ( !isCheck && i < 8 ){
			if (!isBlack && board[i][c2]!=null && board[i][c2].equals("k")){
				isCheck = true;
				whiteCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}if (isBlack  && board[i][c2]!=null && board[i][c2].equals("K")){
				isCheck = true;
				blackCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}else if (board[i][c2]!=null && !board[i][c2].equalsIgnoreCase("k"))
				break;
			i++;
		}
		
		/* left */
		i = c2 - 1;
		while ( !isCheck && i >= 0 ){
			if (!isBlack && board[r2][i]!=null && board[r2][i].equals("k")){
				isCheck = true;
				whiteCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}if (isBlack  && board[r2][i]!=null && board[r2][i].equals("K")){
				isCheck = true;
				blackCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}else if (board[r2][i]!=null && !board[r2][i].equalsIgnoreCase("k"))
				break;
			i--;
		}
		
		/* right */
		i = c2 + 1;
		while ( !isCheck && i < 8 ){
			if (!isBlack && board[r2][i]!=null && board[r2][i].equals("k")){
				isCheck = true;
				whiteCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}if (isBlack  && board[r2][i]!=null && board[r2][i].equals("K")){
				isCheck = true;
				blackCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}else if (board[r2][i]!=null && !board[r2][i].equalsIgnoreCase("k"))
					break;
			i--;
		}
	}
	
	private boolean validBishop(int r1, int c1, int r2, int c2){
		int d1 = Math.abs(r2 - r1);
		int d2 = Math.abs(c2 - c1);
		if ( d1 != d2 )
			return false;
		
		int x = r1;
		int y = c1;

		for ( int i = 0; i < d1 - 1 ; i++ ){
			if (r1 < r2 )
				x++;
			else
				x--;
			
			if ( c1 < c2 )
				y++;
			else
				y--;
			
			if (board[x][y] != null)
				return false;
		}
		return true;
	}
	
	private void checkBishop(int r2, int c2, boolean isBlack){
		int i = r2 - 1 ;
		int j = c2 - 1;
		boolean isCheck = false;
		
		/* diagonal up left */
		while (!isCheck && i >= 0 && j >= 0){
			if (!isBlack && board[i][j]!=null && board[i][j].equals("k")){
				isCheck = true;
				whiteCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}else if (isBlack && board[i][j]!=null && board[i][j].equals("K")){
				isCheck = true;
				blackCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}else if (board[i][j]!=null && board[i][j].equalsIgnoreCase("k"))
				break;
			i--;
			j--;
		}
		
		i = r2 - 1;
		j = c2 + 1;
		/* diagonal up right */
		while (!isCheck && i >= 0 && j < 8){
			if (!isBlack && board[i][j]!=null && board[i][j].equals("k")){
				isCheck = true;
				whiteCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}else if (isBlack && board[i][j]!=null && board[i][j].equals("K")){
				isCheck = true;
				blackCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}else if (board[i][j]!=null && board[i][j].equalsIgnoreCase("k"))
				break;
			i--;
			j++;
		}
		
		i = r2 + 1;
		j = c2 + 1;
		/* diagonal down right */
		while (!isCheck && i < 8 && j < 8){
			if (!isBlack && board[i][j]!=null && board[i][j].equals("k")){
				isCheck = true;
				whiteCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}else if (isBlack && board[i][j]!=null && board[i][j].equals("K")){
				isCheck = true;
				blackCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}else if (board[i][j]!=null && board[i][j].equalsIgnoreCase("k"))
				break;
			i++;
			j++;
		}
		
		i = r2 + 1;
		j = c2 - 1;
		/* diagonal down left */
		while (!isCheck && i < 8 && j >= 0){
			if (!isBlack && board[i][j]!=null && board[i][j].equals("k")){
				isCheck = true;
				whiteCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}else if (isBlack && board[i][j]!=null && board[i][j].equals("K")){
				isCheck = true;
				blackCheck = true;
				threateningRow = r2;
				threateningColumn = c2;
			}else if (board[i][j]!=null && board[i][j].equalsIgnoreCase("k"))
				break;
			i++;
			j--;
		}
		
	}
	
	private boolean validQueen(int r1, int c1, int r2, int c2){
		
		/* horizontal movement */
		if (r1 == r2 ){
			int min = Math.min(c1,c2);
			int max = Math.max(c1, c2);
			
			for(int j = min + 1 ; j < max ; j++){
				if ( board[r1][j] != null )
					return false;
			}
		/* vertical movement */	
		}else if (c1 == c2){
			int min = Math.min(r1,r2);
			int max = Math.max(r1, r2);
			for(int i = min + 1; i < max ; i++){
				if ( board[i][c1] != null)
					return false;
			}
			
		/* diagonal movement */
		}else{
			
			int d1 = Math.abs(r2 - r1);
			int d2 = Math.abs(c2 - c1);
			if ( d1 != d2 )
				return false;
			
			int x = r1;
			int y = c1;

			for ( int i = 0; i < d1 - 1 ; i++ ){
				if (r1 < r2 )
					x++;
				else
					x--;
				
				if ( c1 < c2 )
					y++;
				else
					y--;
				
				if (board[x][y] != null)
					return false;
			}
		}
		
		return true;
	}
	
	private void checkQueen(int r2, int c2, boolean isBlack){
		checkRook(r2, c2, isBlack);
		if ( !whiteCheck && !blackCheck)
			checkBishop(r2, c2, isBlack);
	}
	private boolean isUnderAttack(int r2, int c2, boolean isBlack){
		int i;
		/* vertical up (rook and queen) */
		for ( i = r2 - 1 ; i >= 0 ; i --){
			if ((!isBlack && board[i][c2]!= null && ( board[i][c2].equals("q") || board[i][c2].equals("r")) ) ||
			     (isBlack && board[i][c2]!= null &&  ( board[i][c2].equals("Q") || board[i][c2].equals("R")) ))
				return true;
			else if ((!isBlack && board[i][c2]!=null && !board[i][c2].equals("q") && !board[i][c2].equals("r")) ||
					  (isBlack && board[i][c2]!=null && !board[i][c2].equals("Q") && !board[i][c2].equals("R"))   ){
				break;
			}
		}
		
		/* diagonal up right (bishop and queen)*/
		Boolean stop = false;
		i = r2 -1;
		int j = c2 + 1;
		while (i >= 0 && !stop && j < 8){
			if ((!isBlack && board[i][j]!= null && ( board[i][j].equals("q") || board[i][j].equals("b")) ) ||
				 (isBlack && board[i][j]!= null && ( board[i][j].equals("Q") || board[i][j].equals("B")) ))
					return true;
			else if ((!isBlack && board[i][j]!= null && !board[i][j].equals("q") && !board[i][j].equals("b")) ||
					 (isBlack && board[i][j]!= null && !board[i][j].equals("Q") && !board[i][j].equals("B")) )
				stop = true;
			j++;
			i--;
		}

		/* horizontal right (rook and queen) */
		for ( j = c2 + 1; j < 8 ; j++){
			if ((!isBlack && board[r2][j]!= null && ( board[r2][j].equals("q") || board[r2][j].equals("r")) ) ||
			     (isBlack && board[r2][j]!= null && ( board[r2][j].equals("Q") || board[r2][j].equals("R")) ))
				return true;
			else if ((!isBlack && board[r2][j]!= null && !board[r2][j].equals("q") && !board[r2][j].equals("r")) ||
					 ( isBlack && board[r2][j]!= null && !board[r2][j].equals("Q") && !board[r2][j].equals("R")))
				break;
		}

		/* diagonal down right (bishop and queen) */
		i = r2 +1;
		j = c2 + 1;
		stop = false;
		while (i<8 && !stop && j<8){
			
			if ((!isBlack && board[i][j]!= null && ( board[i][j].equals("q") || board[i][j].equals("b")) ) ||
				 (isBlack && board[i][j]!= null && ( board[i][j].equals("Q") || board[i][j].equals("B")) ))
					return true;
			else if ((!isBlack && board[i][j]!= null && !board[i][j].equals("q") && !board[i][j].equals("b")) || 
					 ( isBlack && board[i][j]!= null && !board[i][j].equals("Q") && !board[i][j].equals("B")) )
				stop = true;
			j++;
			i++;
		}
		
		/* vertical down */
		for ( i = r2 + 1 ; i < 8 ; i ++){
			if ((!isBlack && board[i][c2]!=null && (board[i][c2].equals("q") || board[i][c2].equals("r")) ) ||
			   (  isBlack && board[i][c2]!=null && (board[i][c2].equals("Q") || board[i][c2].equals("R")) ))
				return true;
			else if ( (!isBlack && board[i][c2]!=null && !board[i][c2].equals("q") && !board[i][c2].equals("r")) || 
					  ( isBlack && board[i][c2]!=null && !board[i][c2].equals("Q") && !board[i][c2].equals("R")) )
				break;
		}
		
		/* diagonal down left (bishop and queen)*/
		i = r2 + 1 ;
		j = c2 - 1;
		stop = false;
		while ( i < 8 && !stop && j >= 0){
		
			if ((!isBlack && board[i][j]!=null && ( board[i][j].equals("q") || board[i][j].equals("b")) ) ||
			     (isBlack && board[i][j]!=null && ( board[i][j].equals("Q") || board[i][j].equals("B")) ))
				return true;
			else if ((!isBlack && board[i][j]!=null && !board[i][j].equals("q") && !board[i][j].equals("b") ) ||
					  (isBlack && board[i][j]!=null && !board[i][j].equals("Q") && !board[i][j].equals("B")) ){
				stop = true;
			}
			j--;
			i++;
		}
		
		
		/* horizontal left (rook and queen) */
		for ( j = c2 - 1; j >= 0 ; j--){
			if ((!isBlack && board[r2][j]!= null && ( board[r2][j].equals("q") || board[r2][j].equals("r")) ) ||
			     (isBlack && board[r2][j]!= null && ( board[r2][j].equals("Q") || board[r2][j].equals("R")) ))
				return true;
			else if ( (!isBlack && board[r2][j]!= null && !board[r2][j].equals("q") && !board[r2][j].equals("r")) || 
					  ( isBlack && board[r2][j]!= null && !board[r2][j].equals("Q") && !board[r2][j].equals("R"))){
				break;
			}
		}
		
		/* diagonal up left (bishop and queen)*/
		i = r2 -1 ;
		j = c2 - 1;
		stop = false;
		while ( i >= 0 && !stop && j >= 0){
			
			if ((!isBlack && board[i][j]!=null && ( board[i][j].equals("q") || board[i][j].equals("b")) ) ||
			     (isBlack && board[i][j]!=null && ( board[i][j].equals("Q") || board[i][j].equals("B")) ))
				return true;
			else if ( (!isBlack && board[i][j]!=null && !board[i][j].equals("q") && !board[i][j].equals("b")) ||
					  ( isBlack && board[i][j]!=null && !board[i][j].equals("Q") && !board[i][j].equals("B")) ){
				stop = true;
			}
			j--;
			i--;
		}
		
		/* knight */
		if ( (r2-1) >= 0 && (c2-2) >= 0){
			String aux1 = board[r2-1][c2-2];
			if (( !isBlack && (aux1!=null && aux1.equals("n"))) || ( isBlack && (aux1!=null && aux1.equals("N"))))
				return true;
		}
		
		/* knight */
		if ((r2-2) >= 0 && (c2-1) >= 0){
			String aux2 = board[r2-2][c2-1];
			if (( !isBlack && (aux2!=null && aux2.equals("n"))) || ( isBlack && (aux2!=null && aux2.equals("N"))))
				return true;
		}
		
		/* knight */
		if ((r2-2) >= 0 && (c2+1) < 8){
			String aux3 = board[r2-2][c2+1];
			if (( !isBlack && (aux3!=null && aux3.equals("n"))) || ( isBlack && (aux3!=null && aux3.equals("N"))))
				return true;
		}
		/* knight */
		if ((r2-1) >= 0 && (c2+2) < 8){
			String aux4 = board[r2-1][c2+2];
			if (( !isBlack && (aux4!=null && aux4.equals("n"))) || ( isBlack && (aux4!=null && aux4.equals("N"))))
				return true;
		}
		
		/* knight */
		if ((r2+1) < 8 && (c2+2) < 8){
			String aux5 = board[r2+1][c2+2];
			if (( !isBlack && (aux5!=null && aux5.equals("n"))) || ( isBlack && (aux5!=null && aux5.equals("N"))))
				return true;
		}
		
		/* knight */
		if ((r2+2) < 8 && (c2+1) < 8 ) {
			String aux6 = board[r2+2][c2+1];
			if (( !isBlack && (aux6!=null && aux6.equals("n"))) || ( isBlack && (aux6!=null && aux6.equals("N"))))
				return true;
		}
		
		/* knight */
		if ((r2+2) < 8 && (c2-1) >= 0 ){
			String aux7 = board[r2+2][c2-1];
			if (( !isBlack && (aux7!=null && aux7.equals("n"))) || ( isBlack && (aux7!=null && aux7.equals("N"))))
				return true;
		}
		
		/* knight */
		if ((r2+1) < 8 && (c2-2) >= 0 ){
			String aux8 = board[r2+1][c2-2];
			if (( !isBlack && (aux8!=null && aux8.equals("n"))) || ( isBlack && (aux8!=null && aux8.equals("N"))))
				return true;
		}
		
		
		if ((r2-1) >= 0 && (c2-1) >= 0 && (c2+1) < 8){
			String aux9 = board[r2-1][c2-1];
			String aux10 = board[r2-1][c2+1];
			/* peon */
			if ( !isBlack  && ( (aux9!=null && aux9.equals("p")) || (aux10!=null && aux10.equals("p"))) )
				return true;
		}
		
		if ((r2+1) < 8 && (c2-1) >= 0 && (c2+1) < 8){
			String aux11 = board[r2+1][c2-1];
			String aux12 = board[r2+1][c2+1];
			/* peon */
			if ( isBlack  && ((aux11!=null && aux11.equals("P")) || (aux12!=null && aux12.equals("P"))) )
				return true;
		}
		
		return false;
	}
	
	private boolean validKing(int r1, int c1, int r2, int c2, boolean isBlack){
		String piece = board[r1][c1];
		board[r1][c1]=null;
		if (Math.abs(r2 - r1)>1 || Math.abs(c2 - c1)>1 || isUnderAttack(r2, c2, isBlack)){
			
			board[r1][c1]=piece;
			return false;
		}
		board[r1][c1]=piece;
		
		return true;
	}
	
	private boolean validknight(int r1, int c1, int r2, int c2){
		int absR = Math.abs(r2 - r1);
		int absC = Math.abs(c2 - c1);
		
		if ((absR == 2 && absC == 1) || (absR == 1 && absC == 2))
			return true;
		
		return false;
	}
	
	private boolean isCheckKnight(int i, int j, boolean isBlack){
		if ( i >= 0 && i < 8 && j >= 0 && j < 8 && board[i][j] != null && board[i][j].equals("k") && !isBlack){
			whiteCheck = true;
			return true;
		}else if ( i >= 0 && i < 8 && j >= 0 && j < 8 && board[i][j] != null && board[i][j].equals("K") && isBlack){
			blackCheck = true;
			return true;
		}
		return false;
	}
	private void checkKnight(int r2, int c2, boolean isBlack){
		int i = r2 - 2;
		int j = c2 - 1;
		if (isCheckKnight(i,j,isBlack)){
			threateningRow = r2;
			threateningColumn = c2;
			return;
		}
		
		j = c2 + 1;
		if (isCheckKnight(i,j,isBlack)){
			threateningRow = r2;
			threateningColumn = c2;
			return;
		}
		
		i = r2 - 1;
		j = c2 - 2;
		if (isCheckKnight(i,j,isBlack)){
			threateningRow = r2;
			threateningColumn = c2;
			return;
		}
		
		j = c2 + 2;
		if (isCheckKnight(i,j,isBlack)){
			threateningRow = r2;
			threateningColumn = c2;
			return;
		}
		
		i = r2 + 2;
		j = c2 - 1;
		if (isCheckKnight(i,j,isBlack)){
			threateningRow = r2;
			threateningColumn = c2;
			return;
		}
		
		j = c2 + 1;
		if (isCheckKnight(i,j,isBlack)){
			threateningRow = r2;
			threateningColumn = c2;
			return;
		}
		
		i = r2 + 1;
		j = c2 - 2;
		if (isCheckKnight(i,j,isBlack)){
			threateningRow = r2;
			threateningColumn = c2;
			return;
		}
		
		j = c2 + 2;
		if (isCheckKnight(i,j,isBlack)){
			threateningRow = r2;
			threateningColumn = c2;
			return;
		}
	}
	
	private boolean validPeon(int r1, int c1, int r2, int c2, Boolean isBlack){
		String end = board[r2][c2];
		if (!isBlack){
			/* first movement */
			if ( r1 == 6 && r2 == 4 && c1 == c2 && board[5][c1] == null && board[4][c1] == null)
				return true;
			
			/* any movement */
			else if ((r1-r2 == 1 )  && c1 == c2 && end == null){
				if ( r2==0 ){
					pawnPromotion = true;
					promotionRow = r2;
					promotioColumn = c2;
				}
				return true;
			
			/* capture */
			}else if ((r1-r2 == 1 ) && Math.abs(c2 - c1)==1 && end != null && !Character.isUpperCase(end.charAt(0))){
				if ( r2==0 ){
					pawnPromotion = true;
					promotionRow = r2;
					promotioColumn = c2;
				}
				return true;
			}
		}else{
			/* first movement */
			if ( r1 == 1 && r2 == 3 && c1 == c2 && board[2][c1] == null && board[3][c1] == null )
				return true;
			
			/* any movement */
			else if ((r2 - r1 == 1) && c1 == c2 && end == null){
				if ( r2==7 ){
					pawnPromotion = true;
					promotionRow = r2;
					promotioColumn = c2;
				}
				return true;
			
			/* capture */
			}else if ( (r2-r1 == 1) && Math.abs(c2 - c1)==1 && end != null && Character.isUpperCase(end.charAt(0))){
				if ( r2==7 ){
					pawnPromotion = true;
					promotionRow = r2;
					promotioColumn = c2;
				}
				return true;
			}
		}
		return false;
	}
	
	private void checkPeon(int r2, int c2, boolean isBlack){
		int i = r2 - 1;
		int j = c2 - 1;
		if (!isBlack && i >= 0 && j >= 0 && board[i][j]!=null && board[i][j].equals("k")){
			whiteCheck = true;
			threateningRow = r2;
			threateningColumn = c2;
			return;
		}
		
		j = c2 + 1;
		if (!isBlack && i >= 0 && j < 8 && board[i][j]!=null && board[i][j].equals("k")){
			whiteCheck = true;
			threateningRow = r2;
			threateningColumn = c2;
			return;
		}
		
		i = r2 + 1;
		j = c2 - 1;
		if (isBlack && i < 8 && j >= 0 && board[i][j]!=null && board[i][j].equals("K")){
			blackCheck = true;
			threateningRow = r2;
			threateningColumn = c2;
			return;
		}
		
		j = c2 + 1;
		if (isBlack && i < 8 && j < 8 && board[i][j]!=null && board[i][j].equals("K")){
			blackCheck = true;
			threateningRow = r2;
			threateningColumn = c2;
			return;
		}
	}
	
	private boolean castling(String type, boolean isBlack){
		int r1 = 0; int c1 = 0;
		int r2 = 0; int c2 = 0;
		int r3 = 0; int c3 = 0;
		boolean empty = false;
		boolean inPosition = false;
		
		if (!isBlack && type.equalsIgnoreCase("short")){
			r1 = 7; c1 = 4;
			r2 = 7; c2 = 5;
			r3 = 7; c3 = 6;
			if (board[7][5] == null && board[7][6] == null)
				empty = true;
			
			if (board[7][4] != null && board[7][4].equals("K") && board[7][7]!=null && board[7][7].equals("R"))
				inPosition = true;
			
			if (isUnderAttack(r1, c1, isBlack) || isUnderAttack(r2, c2, isBlack) || isUnderAttack(r3, c3, isBlack) || !empty || !inPosition)
				return false;
			
			board[7][4] = null; board[7][7] = null;
			board[7][6] = "K";  board[7][5] = "R";
			whiteKingRow = 7;
			whiteKingColumn = 6;
			checkRook(7, 5, isBlack);
			
		}else if (!isBlack && type.equalsIgnoreCase("long")){
			r1 = 7; c1 = 4;
			r2 = 7; c2 = 3;
			r3 = 7; c3 = 2;
			if (board[7][1] == null && board[7][2] == null && board[7][3] == null)
				empty = true;
			
			if (board[7][4]!=null && board[7][4].equals("K") && board[7][0]!=null && board[7][0].equals("R"))
				inPosition = true;
			
			if (isUnderAttack(r1, c1, isBlack) || isUnderAttack(r2, c2, isBlack) || isUnderAttack(r3, c3, isBlack) || !empty || !inPosition)
				return false;
			
			board[7][4] = null; board[7][0] = null;
			board[7][2] = "K";  board[7][3] = "R";
			whiteKingRow = 7;
			whiteKingColumn = 2;
			checkRook(7, 3, isBlack);
			
		}else if (isBlack && type.equalsIgnoreCase("short")){
			r1 = 0; c1 = 4;
			r2 = 0; c2 = 5;
			r3 = 0; c3 = 6;
			if (board[0][5] == null && board[0][6] == null)
				empty = true;
			
			if (board[0][4]!=null && board[0][4].equals("k") && board[0][7]!=null && board[0][7].equals("r"))
				inPosition = true;
			
			if (isUnderAttack(r1, c1, isBlack) || isUnderAttack(r2, c2, isBlack) || isUnderAttack(r3, c3, isBlack) || !empty || !inPosition)
				return false;
			
			board[0][4] = null; board[0][7] = null;
			board[0][6] = "k";  board[0][5] = "r";
			blackKingRow = 0;
			blackKingColumn = 6;
			checkRook(0, 5, isBlack);
			
		}else if (isBlack && type.equalsIgnoreCase("long")){
			r1 = 0; c1 = 4;
			r2 = 0; c2 = 3;
			r3 = 0; c3 = 2;
			if (board[0][1] == null && board[0][2] == null && board[0][3] == null)
				empty = true;
			
			if (board[0][4]!=null && board[0][4].equals("k") && board[0][0]!=null && board[0][0].equals("r"))
				inPosition = true;
			
			if (isUnderAttack(r1, c1, isBlack) || isUnderAttack(r2, c2, isBlack) || isUnderAttack(r3, c3, isBlack) || !empty || !inPosition)
				return false;
			
			board[0][4] = null; board[0][0] = null;
			board[0][2] = "k";  board[0][3] = "r";
			blackKingRow = 0;
			blackKingColumn = 6;
			checkRook(0, 3, isBlack);
		}
		
		doEnPassant = false;
		return true;
	}
	
	private boolean validEnPassant(int r1, int c1, boolean isBlack){
		if ( r1 < 0 || r1 > 7 || c1 < 0 || c1 > 7 )
			return false;
		
		if (isBlack){
			r1 = 8 - r1 - 1;
			c1 = 8 - c1 - 1;
		}
		
		if (!isBlack && r1 == enPassantRow && enPassantRow == 3 && 
				board[enPassantRow][enPassantColumn]!=null && board[enPassantRow][enPassantColumn].equals('p') &&
				board[r1][c1]!=null && board[r1][c1].equals("P") && 
				( c1 == enPassantColumn +1 || c1 == enPassantColumn - 1)){
			
				board[enPassantRow][enPassantColumn] = null;
				board[r1][c1] = null;
				board[enPassantRow-1][enPassantColumn] = "P";
				checkPeon(enPassantRow-1,enPassantColumn,isBlack);
				
				if (isUnderAttack(whiteKingRow, whiteKingColumn, isBlack)){
					board[enPassantRow][enPassantColumn] = "p";
					board[r1][c1] = "P";
					board[enPassantRow-1][enPassantColumn] = null;
					whiteCheck = false;
					return false;
					
				}	
	
		}else if (isBlack && r1 == enPassantRow && enPassantRow == 4 && 
				  board[enPassantRow][enPassantColumn]!=null && board[enPassantRow][enPassantColumn].equals('P') &&
				  board[r1][c1]!=null && board[r1][c1].equals("p") &&
				  ( c1 == enPassantColumn +1 || c1 == enPassantColumn - 1)){

				board[enPassantRow][enPassantColumn] = null;
				board[r1][c1] = null;
				board[enPassantRow+1][enPassantColumn] = "p";
				checkPeon(enPassantRow+1,enPassantColumn,isBlack);
				
				if (isUnderAttack(blackKingRow, blackKingColumn, isBlack)){
					board[enPassantRow][enPassantColumn] = "P";
					board[r1][c1] = "p";
					board[enPassantRow-1][enPassantColumn] = null;
					return false;
				}

		}else
			return false;
		
		doEnPassant = false;
		return true;
		
		
	}
	
	private boolean canMoveOneStep(int r2, int c2, boolean isBlack){
		
		if (!isBlack){
			if ( r2 >= 0 && r2 < 8 && c2 >= 0 && c2 < 8 &&
				(board[r2][c2]==null ||  Character.isUpperCase(board[r2][c2].charAt(0))) &&
				validKing(blackKingRow, blackKingColumn, r2, c2, !isBlack))
				return true;
		}else {
			if ( r2 >= 0 && r2 < 8 && c2 >= 0 && c2 < 8 &&
					(board[r2][c2]==null ||  !Character.isUpperCase(board[r2][c2].charAt(0))) &&
					validKing(whiteKingRow, whiteKingColumn, r2, c2, !isBlack))
					return true;
		}
		return false;
	}
	private boolean canMoveKing(boolean isBlack){

		if (!isBlack){
			/* king movements */
			if (canMoveOneStep(blackKingRow - 1, blackKingColumn, isBlack)) return true;
			if (canMoveOneStep(blackKingRow - 1, blackKingColumn + 1, isBlack)) return true;
			if (canMoveOneStep(blackKingRow, blackKingColumn + 1, isBlack)) return true;
			if (canMoveOneStep(blackKingRow + 1, blackKingColumn + 1, isBlack)) return true;
			if (canMoveOneStep(blackKingRow + 1, blackKingColumn , isBlack)) return true;
			if (canMoveOneStep(blackKingRow + 1, blackKingColumn - 1, isBlack)) return true;
			if (canMoveOneStep(blackKingRow, blackKingColumn - 1, isBlack)) return true;
			if (canMoveOneStep(blackKingRow - 1, blackKingColumn - 1, isBlack)) return true;
			
		}else{
			/* king movements */
			if (canMoveOneStep(whiteKingRow - 1, whiteKingColumn, isBlack)) return true;
			if (canMoveOneStep(whiteKingRow - 1, whiteKingColumn + 1, isBlack)) return true;
			if (canMoveOneStep(whiteKingRow, whiteKingColumn + 1, isBlack)) return true;
			if (canMoveOneStep(whiteKingRow + 1, whiteKingColumn + 1, isBlack)) return true;
			if (canMoveOneStep(whiteKingRow + 1, whiteKingColumn , isBlack)) return true;
			if (canMoveOneStep(whiteKingRow + 1, whiteKingColumn - 1, isBlack)) return true;
			if (canMoveOneStep(whiteKingRow, whiteKingColumn - 1, isBlack)) return true;
			if (canMoveOneStep(whiteKingRow - 1, whiteKingColumn - 1, isBlack)) return true;
		}
		
		return false;
	}
	
	private boolean canCapture(boolean isBlack){
		return isUnderAttack(threateningRow, threateningColumn, isBlack);
	}
	
	private boolean canBlockRook(boolean isBlack){
		
		if (!isBlack){
			if (threateningRow != blackKingRow && threateningColumn == blackKingColumn){
				int min = Math.min(threateningRow, blackKingRow);
				int max = Math.max(threateningRow, blackKingRow);
				for (int i = min + 1 ; i < max ; i++){
					if (isUnderAttack(i,blackKingColumn, isBlack))
						return true;
				}
				
			}else if (threateningColumn != blackKingColumn && threateningRow == blackKingRow){
				int min = Math.min(threateningColumn,blackKingColumn);
				int max = Math.max(threateningColumn,blackKingColumn);
				for (int j = min + 1 ; j < max ; j++){
					if (isUnderAttack(blackKingRow,j, isBlack))
						return true;
				}
			}
		}else{
			if (threateningRow != whiteKingRow && threateningColumn == whiteKingColumn){
				int min = Math.min(threateningRow, whiteKingRow);
				int max = Math.max(threateningRow, whiteKingRow);
				for (int i = min + 1 ; i < max ; i++){
					if (isUnderAttack(i,whiteKingColumn, isBlack))
						return true;
				}
				
			}else if (threateningColumn != whiteKingColumn && threateningRow == whiteKingRow){
				int min = Math.min(threateningColumn,whiteKingColumn);
				int max = Math.max(threateningColumn,whiteKingColumn);
				for (int j = min + 1 ; j < max ; j++){
					if (isUnderAttack(whiteKingRow,j, isBlack))
						return true;
				}
			}
		}
		return false;
	}
	
	private boolean canBlockBishop(boolean isBlack){
		if (!isBlack){
			if (threateningRow < blackKingRow && threateningColumn > blackKingColumn){
				int i = blackKingRow - 1;
				int j = blackKingColumn + 1 ;
				while ( i > threateningRow && j < threateningColumn ){
					if (isUnderAttack(i,j,isBlack)){
						return true;
					}
					i--;
					j++;
				}
			}else if (threateningRow > blackKingRow && threateningColumn > blackKingColumn){
				int i = blackKingRow + 1;
				int j = blackKingColumn + 1;
				while ( i < threateningRow && j < threateningColumn){
					if (isUnderAttack(i,j,isBlack)){
						return true;
					}
					i++;
					j++;
				}
			}else if (threateningRow > blackKingRow && threateningColumn < blackKingColumn){
				int i = blackKingRow + 1;
				int j = blackKingColumn - 1;
				while ( i < threateningRow && j > threateningColumn){
					if (isUnderAttack(i,j,isBlack)){
						return true;
					}
					i++;
					j--;
				}
			}else if (threateningRow < blackKingRow && threateningColumn < blackKingColumn){
				int i = blackKingRow - 1;
				int j = blackKingColumn - 1;
				while(i > threateningRow && j > threateningColumn){
					if (isUnderAttack(i,j,isBlack)){
						return true;
					}
					i--;
					j--;
				}
			}
		}else{
			if (threateningRow < whiteKingRow && threateningColumn > whiteKingColumn){
				int i = whiteKingRow - 1;
				int j = whiteKingColumn + 1 ;
				while ( i > threateningRow && j < threateningColumn ){
					if (isUnderAttack(i,j,isBlack)){
						return true;
					}
					i--;
					j++;
				}
			}else if (threateningRow > whiteKingRow && threateningColumn > whiteKingColumn){
				int i = whiteKingRow + 1;
				int j = whiteKingColumn + 1;
				while ( i < threateningRow && j < threateningColumn){
					if (isUnderAttack(i,j,isBlack)){
						return true;
					}
					i++;
					j++;
				}
			}else if (threateningRow > whiteKingRow && threateningColumn < whiteKingColumn){
				int i = whiteKingRow + 1;
				int j = whiteKingColumn - 1;
				while ( i < threateningRow && j > threateningColumn){
					if (isUnderAttack(i,j,isBlack)){
						return true;
					}
					i++;
					j--;
				}
			}else if (threateningRow < whiteKingRow && threateningColumn < whiteKingColumn){
				int i = whiteKingRow - 1;
				int j = whiteKingColumn - 1;
				while(i > threateningRow && j > threateningColumn){
					if (isUnderAttack(i,j,isBlack)){
						return true;
					}
					i--;
					j--;
				}
			}
		}
		return false;
		
	}
	
	private boolean canBlockQueen(boolean isBlack){
		if (canBlockRook(isBlack))
			return true;
		else
			return canBlockBishop(isBlack);
		
		
	}
	
	private boolean canBlock(boolean isBlack){
		String threatening = board[threateningRow][threateningColumn];
		if (threatening != null){
			if (threatening.equalsIgnoreCase("n") || threatening.equalsIgnoreCase("p"))
				return false;
			
			if (threatening.equalsIgnoreCase("r"))
				return canBlockRook(isBlack);
		
		
			if (threatening.equalsIgnoreCase("b"))
				canBlockBishop(isBlack);
			
			if (threatening.equalsIgnoreCase("q"))
				return canBlockQueen(isBlack);
			
		}
		return false;
	}
	private void isCheckMate(boolean isBlack){
		boolean a = canMoveKing(isBlack);
		boolean b = canCapture(isBlack);
		boolean c = canBlock(isBlack);
		if (a)
			System.out.println("canMoveKing ");
		
		if (b)
			System.out.println("canCapture ");
		
		if (c)
			System.out.println("canBlock ");
		
		if ( !a && !b && !c){
			if (!isBlack){
				whiteCheckMate = 0; /* winner */
				blackCheckMate = 1;
			}else{
				blackCheckMate = 0; /* winner */
				whiteCheckMate = 1;
			}
		}
			
		
					
	}

	public Boolean isValidMove(String at, String to, Boolean isBlack){
		if (!isBlack)
			blackCheck = false;
		else
			whiteCheck = false;
			
		String[] parts1 = at.split(" ");
		String[] parts2 = to.split(" ");
		
		/* Castling */
		if (parts1.length == 2 && parts1[0].equalsIgnoreCase("castling") && 
		   (parts1[1].equalsIgnoreCase("short") || parts1[1].equalsIgnoreCase("long"))){
			
			if ((!isBlack && parts1[1].equalsIgnoreCase("short") && !whiteCastlingRight) || 
				(!isBlack && parts1[1].equalsIgnoreCase("long") && !whiteCastlingLeft) ||
				( isBlack && parts1[1].equalsIgnoreCase("short") && !whiteCastlingRight) ||
				( isBlack && parts1[1].equalsIgnoreCase("long") && !whiteCastlingLeft)){
				return false;
				
			}else{
				return castling(parts1[1], isBlack);
			}
		}
		/* En passant */
		if(parts1.length == 3 && parts1[0].equalsIgnoreCase("enPassant")){
			if (!doEnPassant)
				return false;
			int r1 = Integer.parseInt(parts1[1])-1;
			int c1 = Integer.parseInt(parts1[2])-1;
			
			return validEnPassant(r1, c1, isBlack);
				
		}
		
		if (parts1.length != 2 || parts2.length != 2)
			return false;
					
		int r1 = Integer.parseInt(parts1[0]);
		int c1 = Integer.parseInt(parts1[1]);
		int r2 = Integer.parseInt(parts2[0]);
		int c2 = Integer.parseInt(parts2[1]);
		
		if ( r1 < 1 || r1 > 8 || c1 < 1 || c1 > 8 || r2 < 1 || r2 > 8 || c2 < 1 || c2 > 8)
			return false;
		
		r1 --; c1--; r2--; c2--;
		
		if (isBlack){
			r1 = 8 - r1 - 1;
			c1 = 8 - c1 - 1;
			r2 = 8 - r2 - 1;
			c2 = 8 - c2 - 1;
		}
		
		String piece = board[r1][c1];
		String end = board[r2][c2];
		
		if (( r1==r2 && c1==c2) || (piece == null))
			return false;
		
		if (!isBlack && !Character.isUpperCase(piece.charAt(0)))
			return false;
		
		if (isBlack && Character.isUpperCase(piece.charAt(0)))
			return false;
			
		if (!isBlack && end != null && Character.isUpperCase(end.charAt(0)))
			return false;
		
		if (isBlack && end != null && !Character.isUpperCase(end.charAt(0)))
			return false;
		
		
		/* rook movement */
		
		if (piece.equalsIgnoreCase("r")){
			if (!validRook(r1, c1, r2, c2))
				return false;
			else
				checkRook(r2, c2, isBlack);
		}
		
		/* bishop movement */
		if (piece.equalsIgnoreCase("b")){
			if (!validBishop(r1, c1, r2, c2))
				return false;
			else 
				checkBishop(r2, c2, isBlack);
		}
		
		/* queen movement */
		if (piece.equalsIgnoreCase("q")){
			if (!validQueen(r1, c1, r2, c2))
				return false;
			else
				checkQueen(r2, c2, isBlack);
		}
		
		/* king movement */
		if (piece.equalsIgnoreCase("k")){
			if (!validKing(r1, c1, r2, c2, isBlack))
				return false;
			else{
				if (!isBlack){
					whiteKingRow = r2;
					whiteKingColumn = c2;
				
				}else{
					blackKingRow = r2;
					blackKingColumn = c2;
				}
			}
		}
		
		/* knight movement */
		if (piece.equalsIgnoreCase("n")){
			if (!validknight(r1, c1, r2, c2))
				return false;
			else 
				checkKnight(r2, c2, isBlack);
		}
		
		/* peon movement */
		if (piece.equalsIgnoreCase("p")){
			if (!validPeon(r1, c1, r2, c2, isBlack))
				return false;
			else
				checkPeon(r2, c2, isBlack);
		}
		
		/* CASTLING */
		if (!isBlack){
			if (whiteCastlingLeft && r1==7 && c1==0 ){
				whiteCastlingLeft = false;
				System.out.println("no whiteCastlingLeft");
				
			}else if (whiteCastlingRight && r1==7 && c1==7){
				whiteCastlingRight = false;
				System.out.println("no whiteCastlingRight");
				
			}else if((whiteCastlingLeft || whiteCastlingRight) && r1==7 && c1==4){
				whiteCastlingLeft = false;
				whiteCastlingRight = false;
				System.out.println("no whiteCastling");
			}
		}else{
			if (blackCastlingLeft && r1==0 && c1==0){
				blackCastlingLeft = false;
				System.out.println("no blackCastlingLeft");
			
			}else if (blackCastlingRight && r1==0 && c1==7){
				blackCastlingRight = false;
				System.out.println("no blackCastlingRight");
				
			}else if ((blackCastlingLeft || blackCastlingRight) && r1==0 && c1==4){
				blackCastlingLeft = false;
				blackCastlingRight = false;
				System.out.println("no blackCastling");
			}
		}
		
		
		if (piece.equalsIgnoreCase("p") &&
			((r1 == 6 && r2 == 4 && c1 == c2 && board[5][c1] == null && board[4][c1] == null) || 
		     (r1 == 1 && r2 == 3 && c1 == c2 && board[2][c1] == null && board[3][c1] == null))){
			doEnPassant = true;
			enPassantRow = r2;
			enPassantColumn = c2;
		}else
			doEnPassant = false;
		
		
		
		
		/* move piece */
		System.out.println(r1 + " "+c1+" "+r2+" "+c2 );
		board[r1][c1] = null;
		board[r2][c2] = piece;
		
		if (!isBlack){
			if (isUnderAttack(whiteKingRow, whiteKingColumn, isBlack)){
				System.out.println("is Under Attack white king");
				board[r1][c1] = piece;
				board[r2][c2] = end;
				whiteCheck = false;
				return false;
				
			}	
		}else{
			if (isUnderAttack(blackKingRow, blackKingColumn, isBlack)){
				System.out.println("is Under Attack black king");
				board[r1][c1] = piece;
				board[r2][c2] = end;
				blackCheck = false;
				return false;
			}
		}
		
		if (whiteCheck){
			System.out.println("White Check");
			isCheckMate(isBlack);
				
		}else if (blackCheck){
			System.out.println("Black Check");
			isCheckMate(isBlack);
		}
		return true;
		
	}

}