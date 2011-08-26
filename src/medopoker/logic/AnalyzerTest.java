package medopoker.logic;

import medopoker.logic.Util.Comparator;
import medopoker.logic.Util.HandComparator;
import com.SpecialK.SpecialKEval.SevenEval;

public class AnalyzerTest {
	public static void main(String[] args) {
		for (int i=0; i<1000; i++) {
			Card[] deck = Card.getDeck();
			Card[] player1 = new Card[2];
			Card[] player2 = new Card[2];
			Card[] on_table = new Card[5];
			player1[0] = deck[0];
			player2[0] = deck[1];
			player1[1] = deck[2];
			player2[1] = deck[3];
			on_table[0] = deck[4];
			on_table[1] = deck[5];
			on_table[2] = deck[6];
			on_table[3] = deck[7];
			on_table[4] = deck[8];
			
			HandAnalyzer ha1 = new HandAnalyzer(on_table, player1); Hand h1 = ha1.analyze();
			HandAnalyzer ha2 = new HandAnalyzer(on_table, player2); Hand h2 = ha2.analyze();
			
			Card[] cards1 = new Card[7];
			for (int j=0; j<5; j++)
				cards1[j] = on_table[j];
			for (int j=0; j<2; j++)
				cards1[j+5] = player1[j];
			Card[] cards2 = new Card[7];
			for (int j=0; j<5; j++)
				cards2[j] = on_table[j];
			for (int j=0; j<2; j++)
				cards2[j+5] = player2[j];
			
			int[] card1_int = new int[7];
			for (int j=0; j<7; j++) {
				//card1_int[j] = 12 - cards1[j].getRank() + (cards1[j].getSuit()*13);
				card1_int[j] = (12 - cards1[j].getRank()) * 4 + cards1[j].getSuit();
			}
			int[] card2_int = new int[7];
			for (int j=0; j<7; j++) {
				card2_int[j] = (12 - cards2[j].getRank()) * 4 + cards2[j].getSuit();
			}
			
			SevenEval seval = new SevenEval();
			Integer e1 = seval.getRankOf(card1_int[0],card1_int[1],card1_int[2],card1_int[3],card1_int[4],card1_int[5],card1_int[6]);
			Integer e2 = seval.getRankOf(card2_int[0],card2_int[1],card2_int[2],card2_int[3],card2_int[4],card2_int[5],card2_int[6]);
			
			Comparator cmp = new HandComparator();
			int mine = cmp.compare(h1, h2);
			int his = e1.compareTo(e2);			

			if (mine != his) {
				System.out.println("ON TABLE");
				for (Card c: on_table) {
					System.out.println(c);
				}
				System.out.println("\nPLAYER 1");
				for (Card c: player1) {
					System.out.println(c);
				}
				System.out.println("\nPLAYER 2:");
				for (Card c: player2) {
					System.out.println(c);
				}
				
				ha1.report();
				ha2.report();
				System.out.println(mine + " " + his);
				System.out.println("----------------------------------------");
				
			}
		}
	}
}