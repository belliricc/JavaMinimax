package tris;

import java.util.Iterator;
import java.util.LinkedList;

public class GameConfig {
	

	/**
	 * This method returns an empty board with the specified playing player.
	 */
	public static GameConfig blankGame(int who) {
		int[][] a = {{0,0,0},{0,0,0},{0,0,0}};
		return new GameConfig(a, who);
	}
	
	// "config" contains a 3x3 matrix of integers representing the game status, kinda like a game snapshot.
	private int[][] config;
	
	// "whosPlaying" tells us who has to move now: (who has to write X or O but yet hasn't)
	// 1  -> X (player)
	// -1 -> O (computer)
	private int whosPlaying;
	
	/**
	 * The main constructor for this class.
	 */
	public GameConfig(int[][] conf, int who) {
		
		this.whosPlaying = who;
		
		this.config = new int[3][3];
		for(int i = 0; i < 3; i ++) {
			for(int j = 0; j < 3; j ++) {
				this.config[i][j] = conf[i][j];
			}
		}
	}
	
	/**
	 * This method changes the game's turn.
	 */
	public void changePlayer() {
		this.whosPlaying *= -1;
	}
	
	/**
	 * This method returns an updated game state. It does not change who's playing the game.
	 */
	public static GameConfig newMove(GameConfig actual, int row, int col) {
		
		int[][] newConfig = new int[3][3];
		
		for(int i = 0; i < 3; i ++) {
			for(int j = 0; j < 3; j ++) {
				newConfig[i][j] = actual.config[i][j];
			}
		}
		
		newConfig[row][col] = actual.whosPlaying;
		
		return new GameConfig(newConfig, actual.whosPlaying);
		
	}
	
	/**
	 * Returns a list of all the possible permutations of the game evolving from the current state.
	 * Returns null if the instance is a final state.
	 */
	public LinkedList<GameConfig> getChildren(){
		
		LinkedList<GameConfig> children = new LinkedList<GameConfig>();
		
		if(this.isFinalState()) { // final state: no children
			children = null;
		} else {
			int[][] newConf = new int[3][3];
			for(int i = 0; i < 3; i ++) {
				for(int j = 0; j < 3; j ++) {
					newConf[i][j] = this.config[i][j];
				}
			}
			
			for(int i = 0; i < 3; i ++) {
				for(int j = 0; j < 3; j ++) {
					if(newConf[i][j] == 0) {
						newConf[i][j] = whosPlaying;
						GameConfig newChild = new GameConfig(newConf, this.whosPlaying * -1);
						children.add(newChild);
						newConf[i][j] = 0;
					}
				}
			}
		}
		return children;
	}
	
	// Internal utility to convert integers to their relative text equivalent
	// int -> String
	// 0  -> "_"
	// 1  -> "X"
	// -1 -> "O"
	// sRep stands for String representation
	private String sRep(int a) {
		String s = "";
		
		if (a == 0)
			s = " ";
		
		if (a == 1)
			s = "X";
		
		if (a == -1)
			s = "O";
		
		return s;
	}
	
	/**
	 * This method indicates who's winning; returns 10 if player A (X) is winning, -10 if
	 * player B (O) is winning, and 0 if no one has won. This doesn't mean that it's a draw!
	 *  0  -> no one; it's a draw or the board is not completed. 
	 *  10  -> player A won (he plays with X)
	 * -10  -> player B won (he plays with O)
	 */
	public int whoWon() {
		int res = 0;
		
		// checking horizontal/vertical positions.
		for(int i = 0; i < 3; i++) {
			// 0  -> "_"
			// 1  -> "X"
			// -1 -> "O"
			// So there is a winning configuration if the sum of a row/column is either 3 (A won) or -3 (B won); it's 0 if it's a draw.
			int rowSum = this.config[i][0] + this.config[i][1] + this.config[i][2];
			int colSum = this.config[0][i] + this.config[1][i] + this.config[2][i];
			// if |rowSum| = 3 OR |colSum| = 3
			if(Math.abs(rowSum) == 3) { 
				res = rowSum / 3;
				break;
			} else if (Math.abs(colSum) == 3) {
				res = colSum / 3;
				break;
			}
		}
		
		// Checking diagonals.
		int mainDiagonal = this.config[0][0] + this.config[1][1] + this.config[2][2];
		int antiDiagonal = this.config[2][0] + this.config[1][1] + this.config[0][2];
		if(Math.abs(mainDiagonal) == 3) { 
			res = mainDiagonal / 3;
		} else if (Math.abs(antiDiagonal) == 3) {
			res = antiDiagonal / 3;
		}
		
		
		return (res * 10);
	}
	
	
	/**
	 * How many moves were made to get to this configuration; aka how many pieces are there on the 3x3 grid.
	 * This is also the depth in the game tree (imagining to start from a blank start).
	 */
	public int movesNumber() {
		int count = 0;
		for(int i = 0; i < 3; i ++) {
			for(int j = 0; j < 3; j ++) {
				if(this.config[i][j] != 0)
					count++;
			}
		}
		return count;
	}
	
	/**
	 * Returns true if the board is in a final state (also meaning it's a leaf of the game tree).
	 * A state is final if someone won (whoWon = 1 or -1) or: whoWon = 0 AND no moves left.
	 */
	public boolean isFinalState() {
		return (Math.abs(this.whoWon()) == 10) || (this.whoWon() == 0 && this.movesNumber() == 9);
	}
	
	/**
	 * Visual text visualization of the state of the game.
	 */
	@Override
	public String toString() {
		String s = "";
		String sep = " | ";
		
		for(int row = 0; row < 3; row ++) {
			s += " " + sRep(config[row][0]) + sep + sRep(config[row][1]) + sep + sRep(config[row][2]);
			
			if(row < 2)
				s += "\n-----------\n";
		}
		return s;
	}
	

	/**
	 * Starting from "this" game configuration, get the next best move (next configuration).
	 * This is the method that the computer uses.
	 */
	public GameConfig getNextBestMove(){
		
		LinkedList<GameConfig> possibleNewMoves = this.getChildren();
		Iterator<GameConfig> iterator = possibleNewMoves.iterator();
		
		GameConfig newConfig = iterator.next();
		
		while(iterator.hasNext()) {
			GameConfig possibleNew = iterator.next();
			
			if(possibleNew.nodePoints() < newConfig.nodePoints()) {
				newConfig = possibleNew;
			}
		}
		return newConfig;
	}
	
	/**
	 * Main recursive method to calculate a node's points.
	 */
	private int minimax(GameConfig node, int whosPlaying) {
		
		LinkedList<GameConfig> childs = node.getChildren();
		
		int bestValue = 0;
		
		if(node.isFinalState()) {
			bestValue = node.whoWon();
			return bestValue;
		} else if(whosPlaying == 1) { // MAXIMIZE
			// find the max:
			Iterator<GameConfig> iterator = childs.iterator();
			bestValue = Integer.MIN_VALUE;
			
			while(iterator.hasNext()) {
				int score = minimax(iterator.next(), -1);
					if(score > bestValue) {
						bestValue = score;
					}
			}
			return bestValue;
		} else if(whosPlaying == -1) { // MINIMIZE
			
			// find the min:
			Iterator<GameConfig> iterator = childs.iterator();
			bestValue = Integer.MAX_VALUE;
			
			while(iterator.hasNext()) {
				int score = minimax(iterator.next(), 1);
				if(score < bestValue) {
					bestValue = score;
				}
			}
			return bestValue;
		}
		return bestValue;
	}
	
	/**
	 * Public method to start the recursion.
	 */
	public int nodePoints() {
		return minimax(this, this.whosPlaying);
	}
	
	/**
	 * Default getter.
	 */
	public int whosPlaying() {
		return this.whosPlaying;
	}
	
	/**
	 * Default getter.
	 */
	public int[][] getConfig(){
		return this.config;
	}
}
