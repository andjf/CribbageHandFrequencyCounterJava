package src.main.java;

/**
 * @author andjf
 * @version December 20, 2021
 */
public class Main {

	/*
	*	I used a single byte to represent a card
	*
	*	The 4 low bits store the number of the card
	*	('A' = 0, '2' = 1, '3' = 2, ..., 'Q' = 11, 'K' = 12)
	*
	*	The 4 high bits store the suit of the card 
	*	('S' = 0, 'H' = 1, 'C' = 2, 'D' = 3)
	*
	*	For example, 0010 0111 would be broken up as follows:
	*		- 4 low bits (0111)
	*			0111 = 7 = '8'
	*		- 4 high bits (0010)
	*			0010 = 2 = 'C'
	*
	*	So the byte 0010 0111 would represent the 8 of clubs
	*/

	// Lists used to print cards
	final static char[] suits = new char[] {'S', 'H', 'C', 'D'};
	final static char[] numbers = new char[] {'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K'};

	/**
	 * Generates the String representation of a card
	 * @param card The card to get the String representation of
	 * @return The String representation of the given card
	 */
	public static String cardStr(byte card) {
		return new String(new char[] {numbers[card & 0xF], suits[card >> 4]});
	}

	/**
	 * Generates the String representation of a hand
	 * (an ordered collection of cards)
	 * @param hand The collection of cards that makes up the hand
	 * @return The String representation of the given hand
	 */
	public static String handStr(byte[] hand) {
		String toReturn = "";
		for(byte card : hand) {
			toReturn += cardStr(card) + " ";
		}
		return toReturn;
	}

	/**
	 * Calculates the value of a card given by cribbage rules. Face cards are 
	 * worth 10 and all other cards are worth their shown value where Ace is 1.
	 * @param card The card to get the value of
	 * @return The value of the card
	 */
	public static byte cardValue(byte card) {
		byte number = (byte)(card & 0xF);
		if(number < 10) {
			return (byte)(number + 1);
		}
		return 10;
	}

	/**
	 * Creation of a card from a number and suit
	 * @param number The number of the card (0 <= number <= 12)
	 * @param suit The suit of the card (0 <= suit <= 3)
	 * @return The byte representation of a card with given properties
	 */
	public static byte createCard(byte number, byte suit) {
		return (byte)((suit << 4) | number);
	}

	/**
	 * Calculates the points earned from knobs
	 * @param hand The hand to score
	 * @return The number of points earned from knobs
	 */
	public static byte pointsFromKnobs(byte[] hand) {
		for(byte i = 0; i < 4; i++) {
			if((hand[i] & 0xF) == 10 && (hand[i] >> 4) == (hand[4] >> 4)) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * Calculates the points earned from flushes.
	 * @param hand The hand to score
	 * @return The number of points earned from flushes
	 */
	public static byte pointsFromFlush(byte[] hand) {
		int firstSuit = hand[0] >> 4;
		for(byte i = 1; i < 4; i++) {
			if((hand[i] >> 4) != firstSuit) {
				return 0;
			}
		}
		return (byte)(hand[4] >> 4 == firstSuit ? 5 : 4);
	}

	/**
	 * Calculates the points earned from fifteens.
	 * @param hand The hand to score
	 * @return The number of points earned from fifteens
	 */
	public static byte pointsFromFifteens(byte[] hand) {

		// there's room for improvement here.
		// you could skip entire iterations of the inner loops 
		// if you check the sum of the cards on the indices
		// to see if they're over 15 already
		byte total = 0;

		// Choose 2 cards
		for(byte first = 0; first < hand.length - 1; first++) {
			for(byte second = (byte)(first + 1); second < hand.length; second++) {
				byte[] indices = new byte[] {first, second};
				byte sum = 0;
				for(byte i : indices) {
					sum += cardValue(hand[i]);
				}
				if(sum == 15) { total += 2;}
			}
		}

		// Choose 3 cards
		for(byte first = 0; first < hand.length - 2; first++) {
			for(byte second = (byte)(first + 1); second < hand.length - 1; second++) {
				for(byte third = (byte)(second + 1); third < hand.length; third++) {
					byte[] indices = new byte[] {first, second, third};
					byte sum = 0;
					for(byte i : indices) {
						sum += cardValue(hand[i]);
					}
					if(sum == 15) { total += 2; }
				}
			}
		}

		// Choose 4 cards
		for(byte first = 0; first < hand.length - 3; first++) {
			for(byte second = (byte)(first + 1); second < hand.length - 2; second++) {
				for(byte third = (byte)(second + 1); third < hand.length - 1; third++) {
					for(byte fourth = (byte)(third + 1); fourth < hand.length; fourth++) {
						byte[] indices = new byte[] {first, second, third, fourth};
						byte sum = 0;
						for(byte i : indices) {
							sum += cardValue(hand[i]);
						}
						if(sum == 15) { total += 2; }
					}
				}
			}
		}

		// Choose 5 cards
		for(byte first = 0; first < hand.length - 4; first++) {
			for(byte second = (byte)(first + 1); second < hand.length - 3; second++) {
				for(byte third = (byte)(second + 1); third < hand.length - 2; third++) {
					for(byte fourth = (byte)(third + 1); fourth < hand.length - 1; fourth++) {
						for(byte fifth = (byte)(fourth + 1); fifth < hand.length; fifth++) {
							byte[] indices = new byte[] {first, second, third, fourth, fifth};
							byte sum = 0;
							for(byte i : indices) {
								sum += cardValue(hand[i]);
							}
							if(sum == 15) { total += 2; }
						}
					}
				}
			}
		}

		return total;
	}

	/**
	 * Calculates the points earned from pairs.
	 * @param hand The hand to score
	 * @return The number of points earned from pairs
	 */
	public static byte pointsFromPairs(byte[] hand) {
		byte total = 0;
		for(byte first = 0; first < hand.length - 1; first++) {
			for(byte second = (byte)(first + 1); second < hand.length; second++) {
				if((hand[first] & 0xF) == (hand[second] & 0xF)) {
					total += 2;
				}
			}
		}
		return total;
	}

	/**
	 * Calculates the points earned from runs.
	 * @param hand The hand to score
	 * @return The number of points earned from runs
	 */
	public static byte pointsFromRuns(byte[] hand) {
		byte[] frequencies = new byte[13];
		for (byte i = 0; i < 13; i++) {
			frequencies[i] = 0;
		}

		byte nUniqueNumbers = 0;
		for (byte i = 0; i < 5; i++) {
			byte currNumber = (byte)(hand[i] & 0xF);
			nUniqueNumbers += (frequencies[currNumber] == 0 ? 1 : 0);
			frequencies[currNumber]++;
		}

		byte[] numbers = new byte[nUniqueNumbers];
		byte currNumbersIndex = 0;
		for (byte i = 0; i < 13; i++) {
			if (frequencies[i] != 0) {
				numbers[currNumbersIndex++] = i;
			}
		}

		byte[] inRun = new byte[nUniqueNumbers];
		byte inRunSize = 0;
		byte[] currInRun = new byte[nUniqueNumbers];
		byte currInRunSize = 0;
		for (byte i = 0; i < nUniqueNumbers; i++) {
			inRun[i] = -1;
			currInRun[i] = -1;
		}

		byte maxRunLength = 1;
		byte currRunLength = 1;

		for (byte i = 0; i < nUniqueNumbers - 1; i++) {
			if (currInRunSize == 0) {
				currInRun[currInRunSize++] = numbers[i];
			}

			if (numbers[i] + 1 == numbers[i + 1]) {
				currInRun[currInRunSize++] = numbers[i + 1];
				currRunLength++;
				if (currRunLength > maxRunLength) {
					maxRunLength = currRunLength;
				}
			} else {
				if (currInRunSize >= 3) {
					for (byte c = 0; c < nUniqueNumbers; c++) {
						inRun[c] = currInRun[c];
					}
					inRunSize = currInRunSize;
					break;
				}
				for (byte c = 0; c < nUniqueNumbers; c++) {
					currInRun[c] = -1;
				}
				currInRunSize = 0;
				currRunLength = 1;
			}
		}
		if (currInRunSize >= 3 && inRunSize == 0) {
			for (byte c = 0; c < nUniqueNumbers; c++) {
				inRun[c] = currInRun[c];
			}
			inRunSize = currInRunSize;
		}

		if (inRun[0] == -1) { return 0; }

		byte product = 1;
		for (byte i = 0; i < inRunSize; i++) {
			product *= frequencies[inRun[i]];
		}

		return (byte)(product * inRunSize);
	}

	/**
	 * Prints the score breakdown of the given hand
	 * @param hand The hand to analyse
	 */
	public static void printScoreReport(byte[] hand) {
		System.out.println("FROM KNOBS:    " + pointsFromKnobs(hand));
		System.out.println("FROM FLUSH:    " + pointsFromFlush(hand));
		System.out.println("FROM FIFTEENS: " + pointsFromFifteens(hand));
		System.out.println("FROM PAIRS:    " + pointsFromPairs(hand));
		System.out.println("FROM RUNS:     " + pointsFromRuns(hand));
	}

	/**
	 * Calculates the value (score) of a hand. 
	 * The scoring is based on the "show" portion of the game.
	 * More info about scoring here https://www.mastersofgames.com/rules/cribbage-rules.htm
	 * @param hand The hand to score
	 * @return The value of the given hand
	 */
	public static byte score(byte[] hand) {
		byte total = 0;
		total += pointsFromKnobs(hand);
		total += pointsFromFlush(hand);
		total += pointsFromFifteens(hand);
		total += pointsFromPairs(hand);
		total += pointsFromRuns(hand);
		return total;
	}

	/**
	 * Generates the frequency distribution of cribbage scores
	 * and represents it as an array where the value at index i
	 * represents the number of cribbage hands that are worth i points
	 * @param verbose Method prints additional info if verbose flag is true
	 * @return The array representation of the frequency distribution
	 */
	public static int[] getFrequencies(boolean verbose) {
		
		byte[] deck = new byte[52];

		int[] frequencies = new int[30];

		for(byte suit = 0, i = 0; suit < 4; suit++) {
			for(byte number = 0; number < 13; number++, i++) {
				deck[i] = createCard(number, suit);
			}
		}

		for(byte drawIndex = 0; drawIndex < deck.length; drawIndex++) {
			if(verbose) {
				System.out.println((drawIndex + 1) + "/52");
			}
			for(byte first = 0; first < deck.length - 3; first++) {
				if(first == drawIndex) { continue; }
				for(byte second = (byte)(first + 1); second < deck.length - 2; second++) {
					if(second == drawIndex) { continue; }
					for(byte third = (byte)(second + 1); third < deck.length - 1; third++) {
						if(third == drawIndex) { continue; }
						for(byte fourth = (byte)(third + 1); fourth < deck.length; fourth++) {
							if(fourth == drawIndex) { continue; }
							byte[] hand = new byte[] {deck[first], deck[second], deck[third], deck[fourth], deck[drawIndex]};
							frequencies[score(hand)]++;
						}
					}
				}
			}
		}

		if(verbose) {
			for(int score = 0; score < frequencies.length; score++) {
				System.out.println(score + ": " + frequencies[score]);
			}
		}
		
		return frequencies;
	}

	/**
	 * Main method to drive the calculation
	 * @param args Unused command line arguments
	 */
	public static void main(String[] args) {
		int[] frequencies = getFrequencies(false);
		
		for(int i = 0; i < frequencies.length; i++) {
			System.out.println(String.format("Score %d:\t%d", i, frequencies[i]));
		}
	}
}
