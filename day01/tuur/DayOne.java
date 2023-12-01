package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DayOne {
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayOne.class.getResource("input-day01.txt").toURI()));

		System.out.println("Part 1: " +  lines.stream().mapToLong(DayOne::parseNumber).sum());

		System.out.println("Part 2: " +  lines.stream()
				.map(DayOne::mapAlphabeticNumbers)
				.mapToLong(DayOne::parseNumber)
				.sum());		
	}

	
	private static Map<String, String> replacements = new HashMap<>();
	static {
		replacements.put("one", "1");
		replacements.put("two", "2");
		replacements.put("three", "3");
		replacements.put("four", "4");
		replacements.put("five", "5");
		replacements.put("six", "6");
		replacements.put("seven", "7");
		replacements.put("eight", "8");
		replacements.put("nine", "9");
	}
	
	private static String mapAlphabeticNumbers(String calibrationValue) {
		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < calibrationValue.length(); i++) {
			String s = calibrationValue.substring(i);
			
			boolean match = false;
			for(Entry<String, String> replacement : replacements.entrySet()) {
				if (s.startsWith(replacement.getKey())) {
					result.append(replacement.getValue());
					match = true;
				}
			}
			
			if(!match) {
				result.append(calibrationValue.charAt(i));
			}
		}
		
		return result.toString();
	}
	
	private static long parseNumber(String calibrationValue) {
		Character first = null;
		Character last = null;
		
		for(char c : calibrationValue.toCharArray()) {
			if(Character.isDigit(c)) {
				if(first == null) {
					first = Character.valueOf(c);
				}
				last = Character.valueOf(c);
			}
		}
		
		return Long.valueOf("" + first + last);
	}
}
