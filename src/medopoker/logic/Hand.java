/*
 *
 * Copyright 2010 Nejc Saje
 * nejc.saje@gmail.com
 *
 */

package medopoker.logic;

import medopoker.logic.Util.*;

/**
 *
 * @author Nejc Saje
 */
public class Hand {
	public static final String[] HANDS = {"High card", "Pair", "Two Pairs", "Three of a kind", "Straight", "Flush", "Full House", "Four of a kind", "Straight Flush" };
	private int hand_type;
	private Card[] cards;  // Cards that make up the hand type, e.g. AA (pair)
    private Card[] complement; // Cards that fill the hand to 5 cards, e.g. AA is the hand combination, KQJ is saved here as a complement.
	private Card highest;

	public Hand(int h, Card[] c, Card[] on_table) {
		hand_type = h;
		cards = c;
		complement = pickComplement(c, on_table);
		highest = (Card)Util.max(cards, new RankComparator());
	}

	private Card[] pickComplement(Card[] c, Card[] t) {
		Card[] r = Util.removeCards(t, c);
		int start = r.length-c.length-1;
		if (start < 0) start = 0;

		return Util.subList(r, start, r.length);
	}

	public int getRank() {
		return hand_type;
	}

	public Card getHighest() {
		return highest;
	}

	public Card[] getCards() {
		return cards;
	}

    public Card[] getComplement() {
        return complement;
    }

	public String toString() {
		return HANDS[hand_type];
	}

}
