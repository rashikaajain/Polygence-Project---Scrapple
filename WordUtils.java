/*
 * WordUtils.java reads words from a file and puts then into a list. The file contains
 * a list of random words separated by spaces. It finds a word in the list and returns 
 * the index.
 *
 * @author Rashika Jain
 * @since 11/7/25
 */
import java.util.Scanner;

public class WordUtils {

    // Array storing all words loaded from file
    private String[] wordBank;        

    // Name of the word list file
    private final String SOURCE_FILE = "wordList.txt";  

    // Constructor – loads words immediately
    public WordUtils() {
        this.loadWords();
    }

    /**
     * getLetters repeatedly prompts the user until they enter between 3 and 12 letters
     *
     * @param promptMessage: message displayed to user
     * @return a valid entered string of 3–12 letters
     */
    public String getLetters(String promptMessage) {
        String userEntry = "";

        // Loop until valid input
        while (true) {
            userEntry = Prompt.getString(promptMessage);

            // Check letters only + length requirement
            if (this.isLetters(userEntry)) {
                int size = userEntry.length();
                if (size >= 3 && size <= 12) {
                    break;   // exit loop once input is valid
                }
            }
        }
        return userEntry;
    }

    /**
     * isLetters checks whether a string contains only letters
     *
     * @param text: text input string
     * @return true if all characters are alphabetical
     */
    public boolean isLetters(String text) {
        // Checks each character
        for (int idx = 0; idx < text.length(); idx++) {
            char ch = text.charAt(idx);
            if (!Character.isLetter(ch)) {
                return false; // non-letter found
            }
        }
        return true; // no issues
    }

    /**
     * loadWords loads all words from the word list file into the array wordBank
     */
    private void loadWords() {
        Scanner fileInput = FileUtils.openToRead(SOURCE_FILE);
        String[] temp = new String[100000];  // temporary large array
        int count = 0;

        // Read all words from file
        while (fileInput.hasNext()) {
            temp[count] = fileInput.next(); 
            count++; 
        }

        // Copy exact number of words into final array
        this.wordBank = new String[count];
        for (int i = 0; i < count; i++) {
            this.wordBank[i] = temp[i];
        }
    }

    /**
     * Finds all words in wordBank that can be made from the given letters.
     *
     * @param letters: the pool of letters the user provides
     * @return array of all words that can be formed
     */
    public String[] findAllWords(String letters) {
        String[] matches = new String[1000];  // temporary storage
        int place = 0;

        // Loop over all words in dictionary
        for (int idx = 0; idx < this.wordBank.length; idx++) {
            String candidate = this.wordBank[idx];

            if (this.hasMatchLetters(candidate, letters)) {
                matches[place] = candidate; // store valid word
                place++;
            }
        }

        // Trim array to correct size
        String[] trimmed = new String[place];
        for (int i = 0; i < place; i++) {
            trimmed[i] = matches[i];
        }

        return trimmed;
    }

    /**
     * hasMatchLetters checks whether a word can be created from a pool of letters
     *
     * @param word: the dictionary word to check
     * @param pool: letters provided by the user
     * @return true if word can be formed from pool
     */
    private boolean hasMatchLetters(String word, String pool) {

        // Copy pool because we remove letters as matched
        String tempPool = pool;

        // Check each character in word
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            int pos = tempPool.indexOf(letter);

            if (pos == -1) {
                return false; // missing letter
            }

            // Remove matched letter
            tempPool = tempPool.substring(0, pos) +
                       (pos + 1 < tempPool.length() ? tempPool.substring(pos + 1) : "");
        }

        return true;
    }

    /**
     * printWords prints words in columns of five
     *
     * @param arr: array of words to print
     */
    public void printWords(String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.printf("%-15s", arr[i]); // formatted printing

            // newline every 5 words
            if ((i + 1) % 5 == 0) {
                System.out.println();
            }
        }
        System.out.println(); // final blank line
    }

    /**
     * bestWord returns the highest scoring word from an array
     *
     * @param arr: array of words
     * @param scores: score table for letters
     * @return best scoring word
     */
    public String bestWord(String[] arr, int[] scores) {
        String best = arr[0];
        int bestVal = this.getScore(best, scores);

        // Check all words
        for (int i = 1; i < arr.length; i++) {
            int current = this.getScore(arr[i], scores);

            // Choose the higher scoring word
            if (current > bestVal) {
                best = arr[i];
                bestVal = current;
            }
        }
        return best;
    }

    /**
     * getScore computes score for a single word
     *
     * @param word: the word to score
     * @param scoreArray: array of letter scores
     * @return total score
     */
    public int getScore(String word, int[] scoreArray) {
        int tally = 0;

        // Sum score based on letter positions
        for (int k = 0; k < word.length(); k++) {
            int idx = word.charAt(k) - 'a'; // maps letter → index
            tally += scoreArray[idx];
        }

        return tally;
    }

    /** Main entry point */
    public static void main(String[] args) {
        WordUtils app = new WordUtils();
        app.run();
    }

    /**
     * run handles main input/output loop
     */
    public void run() {
        String input = this.getLetters("Enter 3–12 letters (or Q to quit):");

        // Main loop until 'q'
        do {
            String[] possible = this.findAllWords(input);
            System.out.println();
            this.printWords(possible);

            int[] scoreSheet = new int[]{
                1,3,3,2,1,4,2,4,1,8,5,1,3,1,1,3,10,1,1,1,1,4,4,8,4,10
            };

            String best = this.bestWord(possible, scoreSheet);

            System.out.println("\nHighest scoring word: " + best);
            System.out.println("Score = " + this.getScore(best, scoreSheet) + "\n");

            input = this.getLetters("Enter 3–12 letters (or Q to quit):");

        } while (!input.equalsIgnoreCase("q")); // exit condition
    }
}
