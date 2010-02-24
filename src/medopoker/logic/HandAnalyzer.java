/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.logic;

import medopoker.logic.Util.HandComparator;
import medopoker.logic.Util.RankComparator;
import medopoker.logic.Util.Comparator;

/**
 *
 * @author Nejc
 */
public class HandAnalyzer {
	Hand highest;
	Hand highest_pair=null;
	Card[] cards;

	public HandAnalyzer(Card[] on_table, Card[] hole) {
		cards = new Card[7];
		for (int i=0; i<5; i++)
			cards[i] = on_table[i];
		for (int i=0; i<2; i++)
			cards[i+5] = hole[i];
	}

	public void report() {
		System.out.println("HIGHEST: " + highest.toString());
	}

	public void analyze() {
		Card[] sub;
		
		Card.sortByRank(cards);
		// High card
		Card[] hc = new Card[1];
		hc[0] = (Card)Util.max(cards, new RankComparator());
		highest = new Hand(0, hc, cards);
		Hand current;
		
		// Pair, ThreeOAK, FourOAK
		for (int i=0; i<6; i++) {
			if (cards[i].getRank() == cards[i+1].getRank()) {
				if (i<5)
				if (cards[i].getRank() == cards[i+2].getRank()) {
						if (i<4)
						if (cards[i].getRank() == cards[i+3].getRank()) {
							sub = Util.subList(cards, i, i+4);
							current = new Hand(7, sub, cards);
							possibleHand(current);
							i += 3;
							continue;
						}
					sub = Util.subList(cards, i, i+3);
					current = new Hand(3, sub, cards);
					checkFullHouse(current);
					possibleHand(current);
					i += 2;
					continue;
				}
				sub = Util.subList(cards, i, i+2);
				current = new Hand(1, sub, cards);
				newPair(current);
				possibleHand(current);
				i += 1;
			}
		}
		
		// Flush & Straight Flush
		Card.sortBySuit(cards);
		for(int i=0; i<3; i++) {
			sub = Util.subList(cards, i, i+5);
			if (isFlush(sub)) {
				possibleHand(new Hand(5, sub, cards));
				if (isStraight(sub)) {
					possibleHand(new Hand(8, sub, cards));
				}
			}
		}

		// Straight
		System.out.println(cards.length);
		Card[] str = Card.singleRanks(cards);
		Card.sortByRank(str);

		System.out.println(str.length);
		for (int i=0; i<=(str.length-5); i++) {
			sub = (Card[])Util.subList(str, i, i+5);
			if (isStraight(sub)) {
				possibleHand(new Hand(4, sub, cards));
			}
		}

	}
	
	public boolean isStraight(Card[] cards) {
		for (int i=1; i<cards.length; i++) {
			if (cards[i].getRank() != cards[i-1].getRank()+1) {
				return false;
			}
		}
		return true;
	}

	public boolean isFlush(Card[] cards) {
		for (int i=1; i<cards.length; i++) {
			if (cards[i].getSuit() != cards[i-1].getSuit()) {
				return false;
			}
		}
		return true;
	}
	
	private void newPair(Hand hand) {
		if (highest_pair == null) {
			highest_pair = hand;
		} else {
			possibleHand(new Hand(2, Util.concat(hand.getCards(), highest_pair.getCards()), cards));
			if ((new HandComparator()).compare(hand, highest_pair) == 1) {
				highest_pair = hand;
			}
		}
	}

	private void checkFullHouse(Hand hand) {
		if (highest_pair != null) {
			possibleHand(new Hand(6, Util.concat(hand.getCards(), highest_pair.getCards()), cards));
		}
	}

	private void possibleHand(Hand hand) {
		Comparator cmp = new HandComparator();
		if (cmp.compare(hand, highest) == 1)
			highest = hand;
	}

}
