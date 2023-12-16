package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DaySeven {
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DaySeven.class.getResource("input-day07.txt").toURI()));

//		System.out.println(Hand.calculateKind("AAAAA".toCharArray()));
//		System.out.println(Hand.calculateKind("AA8AA".toCharArray()));
//		System.out.println(Hand.calculateKind("23332".toCharArray()));
//		System.out.println(Hand.calculateKind("TTT98".toCharArray()));
//		System.out.println(Hand.calculateKind("23432".toCharArray()));
//		System.out.println(Hand.calculateKind("A23A4".toCharArray()));
//		System.out.println(Hand.calculateKind("23456".toCharArray()));
		
//		System.out.println(new Hand("33332".toCharArray(), 1)
//				.compareTo(new Hand("2AAAA".toCharArray(), 1)));
//		System.out.println(new Hand("77888".toCharArray(), 1)
//				.compareTo(new Hand("77788".toCharArray(), 1)));

		List<Hand> sortedHands = lines.stream()
				.map(DaySeven::parse)
				.sorted()
				.peek(System.out::println)
				.collect(Collectors.toList());
		Long result = 0l;
		for(int rank = 1; rank <= sortedHands.size(); rank++) {
			result += rank * sortedHands.get(rank-1).bid;
		}
		
		System.out.println("Part 1: "+result);

		System.out.println("Part 2: ");

	}
	
	private static Hand parse(String line) {
		String[] split = line.split("\\s");
		return new Hand(split[0].toCharArray(), Long.parseLong(split[1]));
	}

	private static final class Hand implements Comparable<Hand> {
		private final char[] cards;
		
		private final long bid;
		
		private final Kind kind;
		
		public Hand(char[] cards, long bid) {
			super();
			this.cards = cards;
			this.bid = bid;
			this.kind = calculateKind(cards);
		}
		
		private static Kind calculateKind(char[] hand) {
			Map<Character, Integer> counts = new HashMap<>();
			for(char c : hand) {
				counts.merge(c, 1, (i,j) -> i + j);
			}
			
			if (counts.size() == 5) {
				return Kind.HIGH_CARD;
			} else if (counts.size() == 4) {
				return Kind.ONE_PAIR;
			} else if (counts.size() == 3) {
				for (int i : counts.values()) {
					if(i == 3) {
						return Kind.THREE_OF_A_KIND;
					}
				}
				return Kind.TWO_PAIRS;
			} else if (counts.size() == 2) {
				for (int i : counts.values()) {
					if(i == 4) {
						return Kind.FOUR_OF_A_KIND;
					}
				}
				return Kind.FULL_HOUSE;
			} else if (counts.size() == 1) {
				return Kind.FIVE_OF_A_KIND;
			} else {
				throw new IllegalArgumentException("" + hand);
				
			}
		}

		@Override
		public int compareTo(Hand other) {
			if (kind.equals(other.kind)) {
				for(int i = 0 ; i < cards.length; i++) {
					if (cards[i] != other.cards[i]) {
						return cardStrengths.get(cards[i])
								.compareTo(cardStrengths.get(other.cards[i]));
					}
				}
				return 0;
			} else {
				return kind.strength.compareTo(other.kind.strength);
			}
		}
		
		@Override
		public String toString() {
			return Arrays.toString(cards) + "  " + bid + " " + kind;
		}
	}
	
	private static enum Kind {
		FIVE_OF_A_KIND(6), 
		FOUR_OF_A_KIND(5), 
		FULL_HOUSE(4),
		THREE_OF_A_KIND(3), 
		TWO_PAIRS(2), 
		ONE_PAIR(1),
		HIGH_CARD(0);
		final Integer strength;
		private Kind(int value) {
			strength = value;
		}
	}
	
	private static final Map<Character, Long> cardStrengths = new HashMap<Character, Long>();
	static {
		cardStrengths.put('A', 13L);
		cardStrengths.put('K', 12L);
		cardStrengths.put('Q', 11L);
		cardStrengths.put('J', 10L);
		cardStrengths.put('T', 9L);
		cardStrengths.put('9', 8L);
		cardStrengths.put('8', 7L);
		cardStrengths.put('7', 6L);
		cardStrengths.put('6', 5L);
		cardStrengths.put('5', 4L);
		cardStrengths.put('4', 3L);
		cardStrengths.put('3', 2L);
		cardStrengths.put('2', 1L);
	}
}
