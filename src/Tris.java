package tris;

import java.util.Scanner;

public class Tris {
	
	private static void spellText(long delay, String text) {
		for(int i = 0; i < text.length(); i++) {
			System.out.print(text.charAt(i));
			System.out.flush(); // Flushes the print buffer -> prints text immediatly.
			pauseFor(delay);
		}
	}
	
	private static void pauseFor(long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {}
	}
	
	public static void main(String[] argv) {
		Scanner scan = new Scanner(System.in);		
		
		spellText(40,"Welcome to my game, human.\n");
		pauseFor(200);
		
		boolean keepPlaying = true;
		do {
			GameConfig actualGame = null;
		
			spellText(40,"Do you think you can defeat me?\n");
			pauseFor(200);
			spellText(40,"Do you want to play first? (type Y or N): ");
			
			
			String yesorno = scan.next();
			
			if("Y".equalsIgnoreCase(yesorno)) {
				spellText(40,"Make your choice then.\n");
				spellText(40,"Your marker is an X. Where do you want to put it?\n");
				
				actualGame = GameConfig.blankGame(1);
				
			} else if("N".equalsIgnoreCase(yesorno)) {
				spellText(40,"Very well. Let me see....\n");
				
				actualGame = GameConfig.blankGame(-1);
				actualGame = actualGame.getNextBestMove();
			}
			
			spellText(20, actualGame.toString() + "\n");
			
			int row, column;
			
			while(!actualGame.isFinalState()) {
				do {
					spellText(40,"\nInsert row(1-3): ");
					row = scan.nextInt() - 1;
					
					spellText(40,"\nInsert column(1-3): ");
					column = scan.nextInt() - 1;
					
					if(actualGame.getConfig()[row][column] != 0) {
						spellText(40,"I cannot allow that, I'm sorry. Try again.\n");
					}
				} while(actualGame.getConfig()[row][column] != 0);
				
				actualGame = GameConfig.newMove(actualGame, row, column);
				
				if(actualGame.isFinalState() && actualGame.whoWon() == 10) {
					spellText(40,"This is impossible. No but for real: if you see this text there's a bug somewhere!\n");
				}
				actualGame.changePlayer();

				spellText(40,"Your move:\n");
				spellText(20, actualGame.toString() + "\n");
				if(!actualGame.isFinalState()) {
					spellText(40,"\nNow watch how it's done:\n");
					pauseFor(200);
					
					// now plays the CPU:
					actualGame = actualGame.getNextBestMove();
					spellText(20, actualGame.toString() + "\n");
				}
				if(actualGame.isFinalState() && actualGame.whoWon() == -10) {
					spellText(40,"\nI won. Obviously.\n");
				}
				
			}
			if(actualGame.isFinalState() && actualGame.whoWon() == 0) {
				spellText(40,"\nIt's a draw...\n");
				pauseFor(200);
				spellText(40,"You sure are very lucky...\n");
			}
			spellText(40,"\nDo you want to try again? (type Y or N): ");
			String input = scan.next();
			
			if("Y".equalsIgnoreCase(input)) {
				spellText(40,"\nLet me teach you another lesson then...\n");
			} else if("N".equalsIgnoreCase(input)) {
				spellText(40,"\nWise choice. Farewell.\n");
				keepPlaying = false;
			}
		} while (keepPlaying);
		scan.close();
	}
}
