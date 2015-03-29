import java.awt.Color;
import java.util.ArrayList;

class Player120154649 extends GomokuPlayer {
	private static final int win = 10000000;
	private static final int lose = -10000000;
	private Color me;

	public Move chooseMove(Color[][] board, Color me) {
		this.me = me;
		Step best = negaMax(board, me, 3, lose * 100, win * 100, null);
		//System.out.println("Best:" + best.getMax());
		return best.getMove();
	}

	private Step negaMax(Color[][] board, Color player, int depth, int alpha, int beta, Move move){
		if(move != null){
			//is terminal node
			int winScore = isWin(board, player);
			if(winScore > 0){
				return new Step(move, winScore, board);
			}
			else if(isDraw(board))
				return new Step(move, 0, board);
			else if(depth <= 0)
				return new Step(move, eval(board, player), board);
		}
		ArrayList<Move> emptyFields = getEmptyFields(board);
		Step bestMove = null;
		for(Move possible_move : emptyFields){
			board[possible_move.row][possible_move.col] = player; // move player
			int val = -negaMax(board, not(player), depth-1, -beta, -alpha, possible_move).getMax();
			if(bestMove == null || val > bestMove.getMax())
				bestMove = new Step(possible_move, val, board);
			board[possible_move.row][possible_move.col] = null;   //undo move
			alpha = Math.max(alpha, val);
			if(alpha >= beta)
				break;
		}
		return bestMove;
	}

	private int isWin(Color[][] board, Color player){
		int limit = 5;
		int score = 0;
		for(int row=0; row<8; row++)
			for(int col=0; col<8; col++){
				if(getConsHoriz(board, player, row, col) >= limit)
					score++;
				if(getConsVert(board, player, row, col) >= limit)
					score++;
				if(getConsDiagRight(board, player, row, col) >= limit)
					score++;
				if(getConsDiagLeft(board, player, row, col) >= limit)
					score++;
			}
		return score * win;
	}

	private static int getConsHoriz(Color[][] board, Color player, int row, int col){
		if(col > 3)
			return 0;
		int consecutive = 0;
		while(col < 8 && board[row][col] == player){
			consecutive++;
			col++;
		}
		return consecutive;
	}

	private static int getConsVert(Color[][] board, Color player, int row, int col){
		if(row > 3)
			return 0;
		int consecutive = 0;
		while(row < 8 && board[row][col] == player){
			consecutive++;
			row++;
		}
		return consecutive;
	}

	private static int getConsDiagRight(Color[][] board, Color player, int row, int col){
		if(row > 3 || col > 3)
			return 0;
		int consecutive = 0;
		while(row < 8 && col < 8 && board[row][col] == player){
			consecutive++;
			col++;
			row++;
		}
		return consecutive;
	}

	private static int getConsDiagLeft(Color[][] board, Color player, int row, int col){
		if(row > 3 || col < 4)
			return 0;
		int consecutive = 0;
		while(row < 8 && col >= 0 && board[row][col] == player ){
			consecutive++;
			col--;
			row++;
		}
		return consecutive;
	}

	private boolean isDraw(Color[][] board){
		return getEmptyFields(board).size() == 0;
	}

	//EVALUATION OF POSSIBLE MOVES
	private int eval(Color[][] board, Color player){
		int value = 0;
		for(int row = 0; row < 8; row++)
			for(int col = 0; col < 8; col++){
				value += getHorizSeq(board, player, row, col);
				value += getVertSeq(board, player, row, col);
				value += getDiagRSeq(board, player, row, col);
				value += getDiagLSeq(board, player, row, col);
			}
		return value;
	}

	private static int consEval(int cons){
		switch(cons){
			case 1: return 1000;
			case 2:	return 15000;
			case 3: return 225000;
			case 4: return 3375000;
			default: return 0;
		}
	}

	private static int getHorizSeq(Color[][] board, Color player, int row, int col){
		if(col > 3)
			return 0;
		int score = 0;
		int gap_penelaty = 0;
		int cons = 0;
		while(col < 8){
			if(board[row][col] == player){
				cons++;
				score += consEval(cons);
				score -= gap_penelaty * 300;
				gap_penelaty = 0;
			}
			else if(board[row][col] != player && board[row][col] != null){
				if(cons > 3)
					return 0;
				cons = 0;
				score -= gap_penelaty * 300;
				gap_penelaty = 0;
			} else {
				if(cons > 0)
					gap_penelaty++;
				score += 100;
			}
			col++;
		}
		return score;
	}

	private static int getVertSeq(Color[][] board, Color player, int row, int col){
		if(row > 3)
			return 0;
		int score = 0;
		int gap_penelaty = 0;
		int cons = 0;
		while(row < 8){
			if(board[row][col] == player){
				cons++;
				score += consEval(cons);
				score -= gap_penelaty * 300;
				gap_penelaty = 0;
			}
			else if(board[row][col] != player && board[row][col] != null){
				if(cons > 3)
					return 0;
				cons = 0;
				score -= gap_penelaty * 300;
				gap_penelaty = 0;
			} else {
				if(cons > 0)
					gap_penelaty++;
				score += 100;
			}
			row++;
		}
		return score;
	}

	private static int getDiagRSeq(Color[][] board, Color player, int row, int col){
		if(row > 3 || col > 3)
			return 0;
		int score = 0;
		int gap_penelaty = 0;
		int cons = 0;
		while(row < 8 && col < 8){
			if(board[row][col] == player){
				cons++;
				score += consEval(cons);
				score -= gap_penelaty * 300;
				gap_penelaty = 0;
			}
			else if(board[row][col] != player && board[row][col] != null){
				if(cons > 3)
					return 0;
				cons = 0;
				score -= gap_penelaty * 300;
				gap_penelaty = 0;
			} else {
				if(cons > 0)
					gap_penelaty++;
				score += 100;
			}
			row++;
			col++;
		}
		return score;
	}

	private static int getDiagLSeq(Color[][] board, Color player, int row, int col){
		if(row > 3 || col < 4)
			return 0;
		int score = 0;
		int gap_penelaty = 0;
		int cons = 0;
		while(row < 8 && col >= 0){
			if(board[row][col] == player){
				cons++;
				score += consEval(cons);
				score -= gap_penelaty * 300;
				gap_penelaty = 0;
			}
			else if(board[row][col] != player && board[row][col] != null){
				if(cons > 3)
				return 0;
				cons = 0;
				score -= gap_penelaty * 300;
				gap_penelaty = 0;
			} else {
				if(cons > 0)
					gap_penelaty++;
				score += 100;
			}
			row++;
			col--;
		}
		return score;
	}
	//END

	private Color not(Color player){
		if(player == Color.BLACK)
			return Color.WHITE;
		return Color.BLACK;
	}

	private ArrayList<Move> getEmptyFields(Color[][] board){
		ArrayList<Move> emptyFields = new ArrayList<Move>(64);
		for(int row = 0; row < 8; row++){
			for(int col = 0; col < 8; col++){
				if(board[row][col] == null){
					emptyFields.add(new Move(row, col));
				}
			}
		}
		return emptyFields;
	}


	class Step {
		private final Move move;
		private final int max;
		public Step(Move move, int max, Color[][] board){
			this.move = move;
			this.max = max;
		}
		public Move getMove(){ return move; }
		public int getMax(){ return max; }
	}


	//DEBUGING BELOW
	public static void main(String[] args) {
		Color play = Color.WHITE;
		Color enem = Color.BLACK;
		Color[][] board = new Color[][]{new Color[]{null, null, null, null, null, null, null, null},
																		new Color[]{null, null, play, null, null, null, null, null},
																		new Color[]{null, null, null, enem, null, null, null, null},
																		new Color[]{null, null, null, play, enem, null, null, null},
																		new Color[]{null, null, play, play, null, enem, null, null},
																		new Color[]{null, null, null, null, null, null, enem, null},
																		new Color[]{null, null, null, null, null, null, null, null},
																		new Color[]{null, null, null, null, null, null, null, null}};


		//for(int row=0; row<8; row++)
		//for(int col=0; col<8; col++){
		int col = 0;
		int row = 0;
		//System.out.println(eval(board, play));
		//System.out.println(getHorizSeq(board, enem, 0, 0));
	}
}
