package src.main.java;

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

	final static char[] suits = new char[] {'S', 'H', 'C', 'D'};
	final static char[] numbers = new char[] {'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K'};

	public static String cardStr(byte card) {
		return new String(new char[] {numbers[card & 0xF], suits[card >> 4]});
	}

	public static String handStr(byte[] hand) {
		String toReturn = "";
		for(byte card : hand) {
			toReturn += cardStr(card) + " ";
		}
		return toReturn;
	}

	public static byte cardValue(byte card) {
		byte number = (byte)(card & 0xF);
		if(number < 10) {
			return (byte)(number + 1);
		}
		return 10;
	}

	public static byte createCard(byte number, byte suit) {
		return (byte)((suit << 4) | number);
	}

	public static byte pointsFromKnobs(byte[] hand) {
		for(byte i = 0; i < 4; i++) {
			if((hand[i] & 0xF) == 10 && (hand[i] >> 4) == (hand[4] >> 4)) {
				return 1;
			}
		}
		return 0;
	}

	public static byte pointsFromFlush(byte[] hand) {
		int firstSuit = hand[0] >> 4;
		for(byte i = 1; i < 4; i++) {
			if((hand[i] >> 4) != firstSuit) {
				return 0;
			}
		}
		return (byte)(hand[4] >> 4 == firstSuit ? 5 : 4);
	}

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

	public static void printScoreReport(byte[] hand) {
		System.out.println("FROM KNOBS:    " + pointsFromKnobs(hand));
		System.out.println("FROM FLUSH:    " + pointsFromFlush(hand));
		System.out.println("FROM FIFTEENS: " + pointsFromFifteens(hand));
		System.out.println("FROM PAIRS:    " + pointsFromPairs(hand));
		System.out.println("FROM RUNS:     " + pointsFromRuns(hand));
	}

	public static byte score(byte[] hand) {
		byte total = 0;
		total += pointsFromKnobs(hand);
		total += pointsFromFlush(hand);
		total += pointsFromFifteens(hand);
		total += pointsFromPairs(hand);
		total += pointsFromRuns(hand);
		return total;
	}

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

	public static void main(String[] args) {
		int[] frequencies = getFrequencies(false);
		
		for(int i = 0; i < frequencies.length; i++) {
			System.out.println(String.format("Score %d:\t%d", i, frequencies[i]));
		}
	}
}
