/*
 *
 * Copyright 2010 Nejc Saje
 * nejc.saje@gmail.com
 *
 */

package medopoker.logic;

/**
 *
 * @author Nejc Saje
 */
public class Util {

	public static Card[] subList(Card[] c, int start, int end) {
		Card[] sub = new Card[end-start];
		for(int i=start, j=0; i<end; i++, j++) {
			sub[j] = c[i];
		}
		return sub;
	}

	public static Card[] removeCards(Card[] c, Card[] rm) {
		Card[] sub = new Card[c.length - rm.length];
		int j=0;
		for (int i=0; i<c.length; i++) {
			boolean remove = false;
			for (int k=0; k<rm.length; k++) {
				if (c[i] == rm[k]) remove = true;
			}
			if (!remove)
				sub[j++] = c[i];
		}
		return sub;
	}

	public static Card[] concat(Card[] c1, Card[] c2) {
		Card[] concat = new Card[c1.length + c2.length];
		for (int i=0; i<c1.length; i++) {
			concat[i] = c1[i];
		}
		for (int i=0; i<c2.length; i++) {
			concat[i + c1.length] = c2[i];
		}
		return concat;
	}

	public static Object max(Object[] c, Comparator cmp) {
		Object max = c[0];
		for (int i=1; i<c.length; i++) {
			if (cmp.compare(c[i], max) == 1)
				max = c[i];
		}
		return max;
	}

	public static void sort(Object[] c, Comparator cmp) {
		for (int i=1; i<c.length; i++) {
			for (int j=0; j<c.length-i; j++) {
				if (cmp.compare(c[j], c[j+1]) == 1) {
					Object temp = c[j];
					c[j] = c[j+1];
					c[j+1] = temp;
				}
			}
		}
	}

	public static int search(Object[] c, Object e, Comparator cmp) {
		int start = 0;
		int end = c.length-1;
		int mid;

		while (start<=end) {
			mid = (start+end)/2;
			//System.out.println("Search compare: "+c[mid]+" , "+e+",  mid: "+mid);
			if (cmp.compare(c[mid], e) < 0) {
				start = mid+1;
			} else if (cmp.compare(c[mid], e) > 0) {
				end = mid-1;
			} else {
				return mid;
			}
		}
		return -1;
	}


	/*
	 *
	 * Comparators
	 * 
	 * */
	public interface Comparator {
		public int compare(Object o1, Object o2);
	}
	
	public static class RankComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			if (o1==null) return -1;
			if (o2==null) return 1;
			return (((Card)o1).getRank() == ((Card)o2).getRank()) ? 0 :
				((((Card)o1).getRank() < ((Card)o2).getRank()) ? -1 : 1);
		}

	}

	public static class SuitComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			return (((Card)o1).getSuit() == ((Card)o2).getSuit()) ? 0 :
				((((Card)o1).getSuit() < ((Card)o2).getSuit()) ? -1 : 1);
		}

	}

	public static class HandComparator implements Comparator {

		public int compare(Object o1, Object o2) {

			Hand h1 = (Hand)o1;
			Hand h2 = (Hand)o2;

			if (h1.getRank() == h2.getRank()) {
				if (h1.getHighest().getRank() == h2.getHighest().getRank()) {
					// compare second pairs for two pairs handling
					if (h1.getRank() == 2) {
						int h1secondPairRank = h1.getCards()[2].getRank();
						int h2secondPairRank = h2.getCards()[2].getRank();
						if (h1secondPairRank < h2secondPairRank) {
							return -1;
						} else if (h1secondPairRank > h2secondPairRank) {
							return 1;
						}
					}
					
					// flush handling
					if (h1.getRank() == 5) {
						for (int i=h1.getCards().length-1; i>=0; i--) {
							if (h1.getCards()[i].getRank() < h2.getCards()[i].getRank()) {
								return -1;
							} else if (h1.getCards()[i].getRank() > h2.getCards()[i].getRank()) {
								return 1;
							}
						}
					}
					
					// full house handling
					if (h1.getRank() == 6) {
						int pairRank1 = h1.getCards()[3].getRank();
						int pairRank2 = h2.getCards()[3].getRank();
						if (pairRank1 < pairRank2) {
							return -1;
						} else if (pairRank1 > pairRank2) {
							return 1;
						}
					}
					
					Card[] c1 = h1.getComplement();
					Card[] c2 = h2.getComplement();
					/*for (int i=0; i<c1.length; i++) {
						if (c1[i].getRank() < c2[i].getRank())
							return -1;
						else if (c1[i].getRank() > c2[i].getRank())
							return 1;
					}*/
					if (c1.length == 0 || c2.length == 0) return 0;
                    for (int i=c1.length-1; i>=0; i--) {
						if (c1[i].getRank() < c2[i].getRank())
							return -1;
						else if (c1[i].getRank() > c2[i].getRank())
							return 1;
					}
					return 0;
				} else {
					if (h1.getHighest().getRank() < h2.getHighest().getRank()) {
						return -1;
					} else {
						return 1;
					}
				}
			} else {
				if (h1.getRank() < h2.getRank()) {
					return -1;
				} else {
					return 1;
				}
			}



			/*return (((Hand)o1).getRank() == ((Hand)o2).getRank()) ?
						(((Hand)o1).getHighest().getRank() == ((Hand)o2).getHighest().getRank()) ? 0 :
						((((Hand)o1).getHighest().getRank() < ((Hand)o2).getHighest().getRank()) ? -1 : 1)
				:((((Hand)o1).getRank() < ((Hand)o2).getRank()) ? -1 : 1);*/
		}

	}

	public static class LogList {
		
		LogElement first;

		public void append(String s) {
			first = new LogElement(s, first);
		}

		public LogElement getFirst() {
			return first;
		}

		public class LogElement {
			String s;
			LogElement next = null;
			public LogElement(String s, LogElement next) {
				this.s = s;
				this.next = next;
			}
			public String value() {
				return s;
			}
			public LogElement getNext() {
				return next;
			}
		}
	}
}
