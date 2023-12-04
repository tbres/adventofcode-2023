package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DayFour {
	
	private static final Long ONE = Long.valueOf(1l);

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayFour.class.getResource("input-day04.txt").toURI()));

		System.out.println("Part 1: " + lines.stream()
			.map(DayFour::winningNumbers)
			.mapToLong(DayFour::totalScore)
			.sum());
				
		System.out.println("Part 2: "  + part2(lines));
	}

	private static long part2(List<String> lines) {
	
		Map<Integer, Long> counts = new HashMap<>();
		for (int i = 0 ; i < lines.size(); i ++) {
			final Integer matches = winningNumbers(lines.get(i)).size();
			final Long count = counts.computeIfAbsent(i, t -> ONE);
			System.out.println("Card " + (i + 1) + " has " + matches + " matching numbers and " + count  + " occurences") ;
			
			for (int j = i + 1; j <= i + matches && j < lines.size(); j ++) {
				System.out.println(" - Add " + count  + " copies  of " + (j + 1));
				counts.put(j, (counts.computeIfAbsent(j, t -> ONE) + count));
			}
		}
		
		return counts.values().stream()
				.mapToLong(i -> i)
				.sum();
	}
	
	public static Long totalScore(Set<Long> winningNumbers) {
		long result = 0;
		for(Long number : winningNumbers) {
			if (result == 0) {
				result = 1;
			} else {
				result *= 2;
			}
		}
		return result;		
	}
	
	public static Set<Long> winningNumbers(String line) {
		String tmp = line.substring(line.indexOf(':') + 1).trim();
		String[] split = tmp.split("\\|");
		
		String[] winningNumbersString = split[0].trim().split("\\s");
		Set<Long> winningNumbers = new HashSet<>();
		for(String number : winningNumbersString) {
			if (! number.isBlank()) {
				winningNumbers.add(Long.valueOf(number));
			}
		}
		
		String[] numbersString = split[1].trim().split("\\s");
		Set<Long> myNumbers = new HashSet<>();
		for(String number : numbersString) {
			if (! number.isBlank()) {
				myNumbers.add(Long.valueOf(number));
			}
		}
		
		myNumbers.retainAll(winningNumbers);
		
		return myNumbers;
	}
}
