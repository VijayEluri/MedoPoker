/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.logic;

import medopoker.logic.Util.RankComparator;
import medopoker.logic.Util.SuitComparator;
import java.util.Random;

/**
 *
 * @author Nejc
 */
public class Card {

	public static final String[] RANKS = { "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King", "Ace" };
	public static final String[] SUITS = { "Hearts", "Diamonds", "Spades", "Clubs" };
    
    private final int rank;
    private final int suit;

    public Card(int s, int r) {
        suit = s;
        rank = r;
    }

	public int getRank() {
		return rank;
	}

	public int getSuit() {
		return suit;
	}
	
	public String toString() {
		return RANKS[rank] + " of " + SUITS[suit];
	}

	// Sorting methods
	public static void sortByRank(Card[] cards) {
		Util.sort(cards, new RankComparator());
	}

	public static void sortBySuit(Card[] cards) {
		Card.sortByRank(cards);
		Util.sort(cards, new SuitComparator());
	}

	public static Card[] singleRanks(Card[] cards) {
		int num_removed = 0;
		for (int i=0; i<cards.length; i++) {
			int search = Util.search(cards, cards[i], new RankComparator());
			if (search != -1 && search != i) {
				cards[i] = null;
				num_removed++;
			}
		}
		Card[] set = new Card[cards.length - num_removed];
		int j = 0;
		for (int i=0; i<cards.length; i++) {
			if (cards[i] == null)
				continue;
			set[j] = cards[i];
			j++;
		}

		return set;
	}

	public static Card[] cloneCards (Card[] cards) {
		Card[] clone = new Card[cards.length];
		for (int i=0; i<cards.length; i++)
			clone[i] = cards[i];
		return clone;
	}


	// Initialize deck
    public static Card[] getDeck() {
		Random rnd = new Random();
        Card[] deck = new Card[52];

        // Fill the deck
        for (int i=0; i<SUITS.length; i++) {
            for (int j=0; j<RANKS.length; j++) {
                deck[i*RANKS.length+j] = new Card(i, j);
            }
        }

        // Shuffle it
        for (int i=0; i<deck.length; i++) {
			int rand = rnd.nextInt(SUITS.length*RANKS.length - i) + i;
            Card temp = deck[i];
			deck[i] = deck[rand];
			deck[rand] = temp;
        }

		/*System.out.println("DECK");
		for (int i=0; i<deck.length; i++) {
			//System.out.println(deck[i]);
		}*/

		return deck;
    }

}
