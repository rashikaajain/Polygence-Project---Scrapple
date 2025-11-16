/**
 *  A simple version of the Scrabble game where the user plays against the computer.
 *  The player and computer both draw tiles and alternately form words until the pool
 *  of tiles is empty or an invalid word is entered.
 *
 *  The player inputs a word made from their tiles, and the computer generates a random word.
 *
 *  @author	Rashika Jain
 *  @since	11/7/25
 */

public class Scrapple {
	
	// Point values for each letter A–Z (Scrabble-style)
	public int [] scores = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10,
					 		1, 1, 1, 1, 4, 4, 8, 4, 10};

	// Pool of all remaining tiles available to draw from
	private String tilesRemaining = 
					"AAAAAAAAAABBCCDDDDEEEEEEEEEEEEEFFGGGHHIIIIIIIII" +
					"JKLLLLMMNNNNNNOOOOOOOOPPQRRRRRRSSSSTTTTTTUUUUVVWWXYYZ";
	
	private final int NUMTILES = 8;			// the number of tiles per player
	private final int MIN_WORD_LENGTH = 4;	// minimum of 4 characters

	//Current tiles in player and computer hands
	private String playerTiles = "";
	private String computerTiles = "";

	// Total scores for the player and the computer
	private int playerScore = 0;
	private int computerScore = 0;
	
	
	public Scrapple() {
	}
	
	public static void main(String [] args) {
		Scrapple sjr = new Scrapple();
		sjr.run();
	}
	
	/**
	 * Main game loop which handles alternating turns for player and computer
	 * Continues until tiles run out or an invalid word is entered
	 */
	public void run() {
		printIntroduction(); // Display the game title and rules

		playerTiles = drawTiles(NUMTILES);    // Draw player’s initial tiles
		computerTiles = drawTiles(NUMTILES);  // Draw computer’s initial tiles

		// Continue the game as long as tiles remain
		while (tilesRemaining.length() > 0) {
			printTilesRemaining(); // Show current pool of tiles
			System.out.println("");
			System.out.println("Player Score: " + playerScore + "\n");
			System.out.println("Computer Score: " + computerScore + "\n");
			System.out.println("THE TILES IN YOUR HAND ARE: " + formatTiles(playerTiles) + "\n");
			System.out.println("THE TILES IN THE COMPUTER HAND ARE: " + formatTiles(computerTiles) + "\n");
			
			// Prompt player for a word
			String word = Prompt.getString("Please enter a word created from your current set of tiles -> ").toUpperCase();
			
			// Validate player word
			if (!isValidWord(word, playerTiles)) {
				System.out.println("Invalid word! Game over.");
				break;
			}

			// Add score for player's word
			int wordScore = calculateScore(word);
			playerScore += wordScore;

			// Remove used letters and draw replacements
			playerTiles = refillTiles(playerTiles, word);

			// Show updated tiles and scores
			printTilesRemaining();
			System.out.println("Player Score: " + playerScore);
			System.out.println("Computer Score: " + computerScore);
			System.out.println("THE TILES IN YOUR HAND ARE: " + formatTiles(playerTiles));
			System.out.println("THE TILES IN THE COMPUTER HAND ARE: " + formatTiles(computerTiles));
			
			// Wait for ENTER to let computer play
			Prompt.getString("It's the computer's turn. Hit ENTER on the keyboard to continue -> ");
			
			// Computer plays a random word
			String computerWord = makeComputerWord(computerTiles);
			System.out.println("The computer chose: " + computerWord);
			int computerWordScore = calculateScore(computerWord);
			computerScore += computerWordScore;

			// Computer replaces used tiles
			computerTiles = refillTiles(computerTiles, computerWord);

			// Stop game if no tiles remain
			if (playerTiles.length() == 0 || tilesRemaining.length() == 0) break;
		}

		// Print final results
		System.out.println("\nFinal Scores:");
		System.out.println("Player: " + playerScore);
		System.out.println("Computer: " + computerScore);
		if (playerScore > computerScore) System.out.println("You win!");
		else if (computerScore > playerScore) System.out.println("Computer wins!");
		else System.out.println("It’s a tie!");
	}
	
	
	/**
	 *  printIntroduction prints the introduction screen for Scrapple
	 *  it displays the logo, tile list, and scoring explanation
	 */
	public void printIntroduction() {
		System.out.print(" _______     _______     ______     ______    ");
		System.out.println(" ______    ______   __          _______");
		System.out.print("/\\   ___\\   /\\  ____\\   /\\  == \\   /\\  __ \\   ");
		System.out.println("/\\  == \\  /\\  == \\ /\\ \\        /\\  ____\\");
		System.out.print("\\ \\___   \\  \\ \\ \\____   \\ \\  __<   \\ \\  __ \\  ");
		System.out.println("\\ \\  _-/  \\ \\  _-/ \\ \\ \\_____  \\ \\  __\\");
		System.out.print(" \\/\\______\\  \\ \\______\\  \\ \\_\\ \\_\\  \\ \\_\\ \\_\\ ");
		System.out.println(" \\ \\_\\     \\ \\_\\    \\ \\______\\  \\ \\______\\");
		System.out.print("  \\/______/   \\/______/   \\/_/ /_/   \\/_/\\/_/ ");
		System.out.println("  \\/_/      \\/_/     \\/______/   \\/______/ TM");
		System.out.println();
		System.out.print("This game is a modified version of Scrabble. ");
		System.out.println("The game starts with a pool of letter tiles, with");
		System.out.println("the following group of 100 tiles:\n");
		
		// Print the initial tile pool
		for (int i = 0; i < tilesRemaining.length(); i ++) {
			System.out.printf("%c ", tilesRemaining.charAt(i));
			if (i == 49) System.out.println();
		}
		System.out.println("\n");
		System.out.printf("The game starts with %d tiles being chosen at ran", NUMTILES);
		System.out.println("dom to fill the player's hand. The player must");
		System.out.printf("then create a valid word, with a length from 4 to %d ", NUMTILES);
		System.out.println("letters, from the tiles in his/her hand. The");
		System.out.print("\"word\" entered by the player is then checked. It is first ");
		System.out.println("checked for length, then checked to make ");
		System.out.print("sure it is made up of letters from the letters in the ");
		System.out.println("current hand, and then it is checked against");
		System.out.print("the word text file. If any of these tests fail, the game");
		System.out.println(" terminates. If the word is valid, points");
		System.out.print("are added to the player's score according to the following table ");
		System.out.println("(These scores are taken from the");
		System.out.println("game of Scrabble):");
		
		// Print letter score chart
		char c = 'A';
		for (int i = 0; i < 26; i++) {
			System.out.printf("%3c", c);
			c = (char)(c + 1);
		}
		System.out.println();
		for (int i = 0; i < scores.length; i++) System.out.printf("%3d", scores[i]);
		System.out.println("\n");
		
		System.out.print("The score is doubled (BONUS) if the word has consecutive double ");
		System.out.println("letters (e.g. ball).\n");
		
		System.out.print("Once the player's score has been updated, more tiles are ");
		System.out.println("chosen at random from the remaining pool");
		System.out.printf("of letters, to fill the player's hand to %d letters. ", NUMTILES);
		System.out.println("The player again creates a word, and the");
		System.out.print("process continues. The game ends when the player enters an ");
		System.out.println("invalid word, or the letters in the");
		System.out.println("pool and player's hand run out. Ready? Let's play!\n");
		
		Prompt.getString("HIT ENTER on the keyboard to continue:");
	}

	/**
	 * drawTiles draws random tiles from the remaining pool
	 * @param num the number of tiles to draw
	 * @return a string containing the drawn tiles
	 */
	public String drawTiles(int num) {
		String hand = "";
		for (int i = 0; i < num && tilesRemaining.length() > 0; i++) {
			int index = (int)(Math.random() * tilesRemaining.length());
			hand += tilesRemaining.charAt(index);
			// Remove the drawn tile from the pool
			tilesRemaining = tilesRemaining.substring(0, index) + tilesRemaining.substring(index + 1);
		}
		return hand;
	}

	/**
	 * isValidWord checks if the given word can be made from the available tiles
	 * @param word the word entered by the player
	 * @param hand the tiles in the player's possession
	 * @return true if valid, false otherwise
	 */
	public boolean isValidWord(String word, String hand) {
		if (word.length() < MIN_WORD_LENGTH || word.length() > NUMTILES) return false;
		String copy = hand;
		for (int i = 0; i < word.length(); i++) {
			char ch = word.charAt(i);
			int idx = copy.indexOf(ch);
			if (idx == -1) return false; // tile not found
			copy = copy.substring(0, idx) + copy.substring(idx + 1);
		}
		return true;
	}

	/**
	 * calculateScore calculates the Scrabble-style score of a given word
	 * adds up letter values and doubles score if a double letter appears
	 * @param word the word to score
	 * @return total word score
	 */
	public int calculateScore(String word) {
		int score = 0;
		for (int i = 0; i < word.length(); i++) {
			char ch = word.charAt(i);
			if (Character.isLetter(ch)) {
				score += scores[ch - 'A'];
			}
		}
		// Bonus: double score if consecutive letters match
		for (int i = 0; i < word.length() - 1; i++) {
			if (word.charAt(i) == word.charAt(i + 1)) {
				score *= 2;
				break;
			}
		}
		return score;
	}

	/**
	 * makeComputerWord generates a simple random 4–6 letter word for the computer using its tiles
	 * @param hand the computer’s current tiles
	 * @return a pseudo-random word
	 */
	public String makeComputerWord(String hand) {
		int length = 4 + (int)(Math.random() * 3); // between 4–6 letters
		if (length > hand.length()) length = hand.length();
		String word = "";
		for (int i = 0; i < length; i++) {
			int index = (int)(Math.random() * hand.length());
			word += hand.charAt(index);
			hand = hand.substring(0, index) + hand.substring(index + 1);
		}
		return word;
	}

	/**
	 * refillTiles refills tiles after playing a word
	 * removes used letters and draws replacements from pool
	 * @param oldHand the current tiles before playing
	 * @param usedWord the word played
	 * @return updated string of tiles
	 */
	public String refillTiles(String oldHand, String usedWord) {
		for (int i = 0; i < usedWord.length(); i++) {
			char ch = usedWord.charAt(i);
			int idx = oldHand.indexOf(ch);
			if (idx != -1) oldHand = oldHand.substring(0, idx) + oldHand.substring(idx + 1);
		}
		int tilesNeeded = NUMTILES - oldHand.length();
		oldHand += drawTiles(tilesNeeded);
		return oldHand;
	}

	/**
	 * printTilesRemaining prints the remaining tile pool neatly formatted
	 */
	public void printTilesRemaining() {
		System.out.println("\nHere are the tiles remaining in the pool of letters:");
		for (int i = 0; i < tilesRemaining.length(); i++) {
			System.out.print(tilesRemaining.charAt(i) + " ");
			if ((i + 1) % 20 == 0) System.out.println();
		}
		System.out.println();
	}

	/**
	 * formatTiles adds spaces between letters for display purposes
	 * @param tiles the string of letters to format
	 * @return formatted string with spaces
	 */
	public String formatTiles(String tiles) {
		String formatted = "";
		for (int i = 0; i < tiles.length(); i++) {
			formatted += tiles.charAt(i);
			if (i < tiles.length() - 1) formatted += " ";
		}
		return formatted;
	}
}
