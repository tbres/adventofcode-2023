package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import tuur.DaySeven.Kind;

public class DaySevenPartTwo {

    private static final String JOKER = "J";

    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(DaySeven.class.getResource("input-day07.txt").toURI()));

        List<Hand> sortedHands = lines.stream()
            .map(DaySevenPartTwo::parse)
            .sorted(DaySevenPartTwo::compareHands)
            .peek(System.out::println)
            .collect(Collectors.toList());

        Long result = 0l;
        for(int rank = 0; rank < sortedHands.size(); rank++) {
            result += (rank + 1) * sortedHands.get(rank).bid;
        }
        
        System.out.println("Part 2: " + result);
    }

    private static Hand parse(String line) {
		String[] split = line.split("\\s");
		String cards = split[0];
        long bid = Long.parseLong(split[1]);

        String replacement = cards;
        if (cards.contains(JOKER)) {
            Map<Character, Integer> occurences = new HashMap<>();
            for (char card : cards.toCharArray()) {
                occurences.merge(card, 1, Integer::sum);
            }

            Character bestChar = occurences.entrySet().stream()
                .filter(entry -> 'J' != entry.getKey())
                .sorted(DaySevenPartTwo::compareCards)
                .map(Entry::getKey)
                .findFirst().orElse('A');

            replacement = cards.replace('J', bestChar);
        }

        return new Hand(cards, replacement, DaySeven.calculateKind(replacement.toCharArray()), bid);
	}

    private static final class Hand {
        final String cards;
        final String replacement;
        final Kind kind;
        final long bid;

        public Hand (String cards, String replacement, Kind kind, long bid) {
            this.cards = cards;
            this.replacement = replacement;
            this.kind = kind;
            this.bid = bid;
        }

        @Override
        public String toString() {
            return cards + " -> " + replacement + " (" + kind + ") = " + bid; 
        }
    }

    private static int compareCards(Entry<Character, Integer> first, Entry<Character, Integer> second) {
        if (first.getValue() == second.getValue()) {
            return cardStrengths.get(second.getKey()).compareTo(cardStrengths.get(first.getKey())); // compare card strength
        }
        return Integer.compare(second.getValue(), first.getValue()); //compare occurences
    } 

    private static int compareHands(Hand first, Hand second) {
        if (first.kind.equals(second.kind)) {
            for(int i = 0 ; i < first.cards.length(); i++) {
                char a = first.cards.charAt(i);
                char b = second.cards.charAt(i);
                if (a != b) {
                    return cardStrengths.get(a).compareTo(cardStrengths.get(b));
                }
            }
            return 0;
        } 
        return first.kind.strength.compareTo(second.kind.strength);
    }

    static final Map<Character, Long> cardStrengths = new HashMap<Character, Long>();
	static {
		cardStrengths.put('A', 13L);
		cardStrengths.put('K', 12L);
		cardStrengths.put('Q', 11L);
        // value 10 is unused
		cardStrengths.put('T', 9L);
		cardStrengths.put('9', 8L);
		cardStrengths.put('8', 7L);
		cardStrengths.put('7', 6L);
		cardStrengths.put('6', 5L);
		cardStrengths.put('5', 4L);
		cardStrengths.put('4', 3L);
		cardStrengths.put('3', 2L);
		cardStrengths.put('2', 1L);

		cardStrengths.put('J', 0L); // Joker is lowest card
	}

}
