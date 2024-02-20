/** COSC 121 - Lab 4
 * @author Andres Laverde 300340209
 *	This program is a Hangman game.
 *	It uses the contents of the the Official Scrabble Player's Dictionary, Second Edition.
 *		which contains roughly 120,000 words.
 *	We choose a random word from there that is at least 5 letters long.
 *	The player has 7 chances to guess the correct letters
 *	Each time the player gets a wrong guess the hangman figure 
 *		will continue to be drawn and updated.
 *	If the player guessed the word and wants to continue playing, 
 *		the game will loop until either the player wants to quit or gets out of guesses.
 *	At then end the program will ask the user for his name. If the player's score 
 *		is in the top 5 of all time, then it will be displayed.
 *	The program uses the Player class, defined at then end of the code.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Hangman {
	public static void main(String[] args) {

		/*Main boolean condition. This will be turned to true if the 
		   user runs out of guesses*/
		boolean isGameOver = false;
		int score = 0;
		File dictionary = new File("englishWords.txt");
		Scanner sc = new Scanner(System.in);

		System.out.println("Welcome to Java Hangman!\nYou have to guess"
				+ " the letters of a random word from the english dictionary."
				+ "\nYou have 7 guesses per word.\nGood Luck!\n");

		while(isGameOver == false) {
			int guessNum = 7;
			int turn = 1;
			String secretWord = getRandomWord(dictionary);
			ArrayList<Character> overAllCharsGuessed = new ArrayList<>();

			//This String will constantly be updated to match the state of the word being guessed
			String guessedLetters = "";

			// Using this int array we will keep track of the index of each of the letters in the secret word.
			// If its a correctly guessed letter, then that index is turned to one. Otherwise they will be zero.
			int[] secretChars = new int[secretWord.length()];

			char[] incorrectGuesses = new char[7];


			//This loop finishes either when you run out of guesses or you guessed the word correctly
			while(guessNum>0) {
				boolean isCorrectGuess = false;

				//A loop to display the game layout and dashes for each character in the word
				//If the number in the int array secretChars is 1 then it means it has been guessed and we will display it
				for(int i = 0; i<secretWord.length(); i++){
					if(secretChars[i] == 1) {
						guessedLetters += secretWord.charAt(i) + " ";
					}
					else {
						guessedLetters += "_ ";
					}
				}

				//We display the turn number
				System.out.println("Turn " + turn);
				//The string of guessed letters will be displayed the first time as just the under-scores for each character of the hidden word 
				System.out.println("Hidden Word: " + guessedLetters);

				//Then it gets voided so we can use it again with user input
				guessedLetters = "";

				// We display the letters already guessed so the user avoids repetition in the new guess
				displayIncorrectGuesses(incorrectGuesses);

				// We let the user know the number of guesses left
				System.out.println("Guesses left : " + guessNum);

				// 	We display the score so the user can keep track of it
				System.out.println("Score: " + score);

				// Prompt the user for a new guess
				System.out.println("Enter next guess: ");

				//we make sure its just a character that we are using for the input.
				//we use just the first character of the string inputed
				//we also turn the character or word into lower case because the dictionary is in lower case
				char userGuess = sc.next().toLowerCase().charAt(0);
				boolean alreadyGuessed = false;

				for(int i = 0; i<overAllCharsGuessed.size(); i++) {
					if(overAllCharsGuessed.get(i) == userGuess) {
						System.out.println("\n\nYou already guessed that\n\n");
						alreadyGuessed = true;
					}
				}
				if(alreadyGuessed) {
					continue;
				}
				else {
					overAllCharsGuessed.add(userGuess);
				}

				//We check the guess. If it is correct then we will update our int array to a 1, meaning that the character has been guessed.
				//Also, the boolean turns true.
				for(int i = 0; i<secretWord.length(); i++) {
					if(secretWord.charAt(i) == userGuess) {
						secretChars[i] = 1;
						score += 10;
						isCorrectGuess = true;
					}
				}

				//We want to avoid characters that are not letters, if the guess wasn't one, we penalize the user and ask for new input
				if(!(Character.isLetter(userGuess))) {
					System.out.println("You need to pick a letter of the english "
							+ "alphabet for this game to work\n");
					continue;
				}
				else {
					if(isCorrectGuess) {
						System.out.println("\nYour guess was correct\n");
						turn++;
					}
					else {
						System.out.println("\nSorry, there is no '" + userGuess + "' in the hidden word\n");
						turn++;
						guessNum--;
						for(int i = 0; i<incorrectGuesses.length; i++) {
							if(incorrectGuesses[i] == '\u0000') {
								incorrectGuesses[i] = userGuess;
								break;
							}
						}
					}
				}
				//We invoke this method to draw the hangman stick figure to the console. it updates each time the user guesses a wrong letter
				drawHangman(guessNum);

				//If the int array are all 1 then break and make a new game.
				// We make a boolean to serve as the condition of exiting the inner game loop and going to get a new word.
				boolean areAllGuessed = true;
				for(int i = 0; i<secretChars.length; i++) {

					//if any of the ints is a 0 then there are still letters to be guessed
					if(secretChars[i] == 0) {
						areAllGuessed = false;
					}
				}
				if(areAllGuessed == true) {
					System.out.println("\nYou guessed the word! Congratulations!\n");
					score += 100 + (guessNum *30);
					break;
				}
			}
			System.out.println("The word was: " + secretWord);
			if(guessNum<=0) {
				System.out.println("You are out of guesses, sorry.\n\n\nGAME OVER\n");
				isGameOver = true;
			}
			else {
				System.out.println("Do you want to continue playing? \n Y  OR  N");
				String keepPlaying = sc.next();
				if(keepPlaying.toLowerCase().equals("y")) {
					System.out.println("Alright!nNext word!!\n*******************************************\n\n");
				}
				else if(keepPlaying.toLowerCase().equals("n")) {
					System.out.println("Thanks for playing!\n*******************************************\n");
					isGameOver = true;
				}
			}
		}
		//We invoke the method necessary to take care of the player scores
		top5PlayerAdmin(score);
		sc.close();
	}

	/* We made a method that reads the entire dictionary and 
	 	adds to an ArrayList all the words that have five or more 
	 	letters and finally returns a random word */

	public static String getRandomWord(File f) {
		ArrayList<String> usableWords = new ArrayList<>();
		Scanner sc = null;

		//We first check the exception of not finding the file we want to use
		try {
			sc = new Scanner(f);			
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		while(sc.hasNext()) {
			String word = sc.nextLine();
			if(word.length()>= 5) {
				usableWords.add(word);
			}
		}
		sc.close();
		// We create a random number to be used as the index for the array list
		int randomIndex = (int)(Math.random()*usableWords.size());
		return usableWords.get(randomIndex);
	}

	/*	We made a method to display the incorrect guesses in alphabetical order */
	public static void displayIncorrectGuesses(char[] a) {
		Arrays.sort(a);
		String output = "";
		for(int i = 0; i<a.length; i++) {
			if(a[i] != '\u0000') {
				if(i == a.length-1)
					output += a[i];
				else	
					output += a[i] + ", ";
			}
		}
		output = output.toUpperCase();
		System.out.println("Incorrect Guesses: " + output);
	}


	//This method will allow us to enter players names and scores and keep their records.
	public static void top5PlayerAdmin(int s) {
		//We first create an array list of Strings
		ArrayList<String> top5Players = new ArrayList<>();

		// Create the file where we will store the data
		File top5 = new File("top5HangmanHighScorePlayers.txt");

		// we declare 2 Scanners: The first for the user input, the second one is a file reader.
		// We declare a Print Writer, to have a way to write into our file. 
		// We have the File reader scanner and print writer as null to catch any errors
		Scanner userInput = new Scanner(System.in);
		Scanner sc = null;
		PrintWriter writter = null;

		try {
			sc = new Scanner(top5);			
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}

		//Here we will add every line of the file as an element of our array list
		while(sc.hasNext()) {
			String HSPlayer = sc.nextLine();
			top5Players.add(HSPlayer);
		}
		//If the file is new then we don't bother doing nothing else and we add the player
		if(top5Players.size() == 0) {
			System.out.println("Please enter your name: ");
			String playerName = userInput.next();

			Player p1 = new Player(playerName, s);
			top5Players.add(p1.toString());
		}

		// Now, if there is more than one entry, we want to check the scores and place the new entry properly
		else {
			int otherScore = 0;
			boolean wasAdded = false;
			for(int i = 0; i<top5Players.size(); i++){
				//We get the value of the score(That should be after the last space in the string) and convert it to string
				otherScore= Integer.parseInt(top5Players.get(i).substring(top5Players.get(i).lastIndexOf(" ")+1));

				//If the new player score is greater than the previous entry, then we create a new player 
				//object and place it in the first instance of it being greater
				if(s >= otherScore) {
					System.out.println("Please enter your name: ");
					String playerName = userInput.next();

					Player p1 = new Player(playerName, s);
					top5Players.add(i, p1.toString());
					wasAdded = true;
					//We also break out so we don't add the new player everywhere
					break;
				}
			}
			//If the player wasn't added inside the loop then we add it here
			if(wasAdded == false) {
				System.out.println("Please enter your name: ");
				String playerName = userInput.next();

				Player p1 = new Player(playerName, s);
				top5Players.add(p1.toString());	
			}
		}		
		
		//After we have added the players to the Array list and sorted them, we write into the file
		try {
			writter = new PrintWriter(top5);
			for(int i = 0; i < top5Players.size(); i++) {
				writter.println(top5Players.get(i));
			}
			writter.close();
		} catch (IOException ioe) {

		}
		//And finally to display the players into the console:
		// if the list has less than 5, then  we just print as much as the list has
		if(top5Players.size()<5) {
			for(int i = 0; i < top5Players.size(); i++) {
				System.out.println(top5Players.get(i));
			}
		}
		//If it has more, then we just print the top 5
		else {
			for(int i = 0; i < 5; i++) {
				System.out.println(top5Players.get(i));
			}
		}
		sc.close();
		userInput.close();
	}
	
	//This method draws the stick Hangman.
	public static void drawHangman(int num) {
		
		if(num == 6) {
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("___|___");
			System.out.println();
		}
		else if (num == 5) {
			System.out.println("   ____________");
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("   | ");
			System.out.println("___|___");
			System.out.println();
		}
		else if(num == 4) {
			System.out.println("   ____________");
			System.out.println("   |          _|_");
			System.out.println("   |         /   \\");
			System.out.println("   |        |     |");
			System.out.println("   |         \\_ _/");
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("   |");
			System.out.println("___|___");
			System.out.println();
		}
		else if (num == 3) {
			System.out.println("   ____________");
			System.out.println("   |          _|_");
			System.out.println("   |         /   \\");
			System.out.println("   |        |     |");
			System.out.println("   |         \\_ _/");
			System.out.println("   |           |");
			System.out.println("   |           |");
			System.out.println("   |");
			System.out.println("___|___");
			System.out.println();
		}
		else if(num == 2) {
			System.out.println("   ____________");
			System.out.println("   |          _|_");
			System.out.println("   |         /   \\");
			System.out.println("   |        |     |");
			System.out.println("   |         \\_ _/");
			System.out.println("   |           |");
			System.out.println("   |           |");
			System.out.println("   |          / \\ ");
			System.out.println("___|___      /   \\");
			System.out.println();
		}
		else if (num == 1) {
			System.out.println("   ____________");
			System.out.println("   |          _|_");
			System.out.println("   |         /   \\");
			System.out.println("   |        |     |");
			System.out.println("   |         \\_ _/");
			System.out.println("   |          _|_");
			System.out.println("   |         / | \\");
			System.out.println("   |          / \\ ");
			System.out.println("___|___      /   \\");
			System.out.println();
		}
		else if (num == 0) {
			System.out.println("   ____________");
			System.out.println("   |          _|_  {YOU KILLED ME!!!! :( }");
			System.out.println("   |         /x x \\ /");
			System.out.println("   |        | ___ |");
			System.out.println("   |         \\_ _/");
			System.out.println("   |          _|_");
			System.out.println("   |         / | \\");
			System.out.println("   |          / \\ ");
			System.out.println("___|___      /   \\");
			System.out.println();
		}
	}
}
// We made a Player object so we can store an instance of each player in a Player list
class Player {
	private String name;
	private int score;

	public Player(String name, int score) {
		this.name = name;
		this.score = score;
	}

	// Getters
	public String getPlayerName() {
		return name;
	}
	public int getPlayerScore() {
		return score;
	}

	//Setters
	public void setPlayerName(String n) {
		name = n;
	}
	public void setPlayerScore(int s) {
		score = s;
	}

	//Overloaded toString method
	public String toString() {
		return name + " " + score;
	}
}
