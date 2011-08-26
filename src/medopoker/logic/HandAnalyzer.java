/*
 *
 * Copyright 2010 Nejc Saje
 * nejc.saje@gmail.com
 *
 */

package medopoker.logic;

import medopoker.logic.Util.Comparator;
import medopoker.logic.Util.HandComparator;
import medopoker.logic.Util.RankComparator;

/**
 *
 * @author Nejc Saje
 */
public class HandAnalyzer {
	Hand highest;
	Hand highest_pair=null;
    Hand highest_TOAK=null;
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

	public Hand analyze() {
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
                    newThreeOAK(current);
					i += 2;
					continue;
				}
				sub = Util.subList(cards, i, i+2);
				current = new Hand(1, sub, cards);
				newPair(current);
				i += 1;
                //TODO: checkFullHouse() - i think this is fixed, not sure though
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
		//System.out.println(cards.length);
		Card.sortByRank(cards);
		Card[] str = Card.singleRanks(cards);

		//System.out.println(str.length);
		for (int i=0; i<=(str.length-5); i++) {
			sub = (Card[])Util.subList(str, i, i+5);
			if (isStraight(sub)) {
				possibleHand(new Hand(4, sub, cards));
			}
		}
		// if last card is ace, check for hands if its in the beginning
		if (str[str.length-1].getRank() == 12) {
			// put ace in the beginning
			Card tmp = str[str.length-1];
			for (int i=str.length-1; i>0; i--) {
				str[i] = str[i-1];
			}
			str[0] = tmp;
			
			//check
			for (int i=0; i<=(str.length-5); i++) {
				sub = (Card[])Util.subList(str, i, i+5);
				if (isStraight(sub)) {
					Hand h = new Hand(4, sub, cards);
					h.overrideHighest(sub[sub.length-1]);
					possibleHand(h);
				}
			}
		}

        return highest;
	}
	
	public boolean isStraight(Card[] cards) {
		for (int i=1; i<cards.length; i++) {
			if (cards[i].getRank() != (cards[i-1].getRank()+1) % 13) {
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
            possibleHand(hand);
            checkFullHouse();
		} else {
			possibleHand(new Hand(2, Util.concat(hand.getCards(), highest_pair.getCards()), cards)); // two pairs
			if ((new HandComparator()).compare(hand, highest_pair) == 1) {
				highest_pair = hand;
                possibleHand(hand);
                checkFullHouse();
			}
		}
	}

    private void newThreeOAK(Hand hand) {
        if (highest_TOAK == null) {
            highest_TOAK = hand;
            possibleHand(hand);
            checkFullHouse();
        } else {        	
        	Hand toPair = hand;
        	Hand toTOAK = highest_TOAK;
        	if (hand.getHighest().getRank() > highest_TOAK.getHighest().getRank()) {
        		toPair = highest_TOAK;
        		toTOAK = hand;
        	}
        	Card[] ttp = new Card[]{toPair.getCards()[0], toPair.getCards()[1]}; // three to pair
        	Hand h = new Hand(6, Util.concat(toTOAK.getCards(), ttp), cards);
        	h.overrideHighest(toTOAK.getCards()[0]);
        	possibleHand(h);
        	
			if ((new HandComparator()).compare(hand, highest_TOAK) == 1) {
				highest_TOAK = hand;
                possibleHand(hand);
                checkFullHouse();
			}
        }
    }

	private void checkFullHouse() {
		if (highest_pair != null && highest_TOAK != null) {
			Hand h = new Hand(6, Util.concat(highest_TOAK.getCards(), highest_pair.getCards()), cards);
			h.overrideHighest(highest_TOAK.getCards()[0]);
			possibleHand(h);
		}
	}

	private void possibleHand(Hand hand) {
		Comparator cmp = new HandComparator();
		if (cmp.compare(hand, highest) == 1)
			highest = hand;
	}

}
