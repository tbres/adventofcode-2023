package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DayTwelve {
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayTwelve.class.getResource("input-day12.txt").toURI()));

		System.out.println("Part 1: " + part1(lines));
		System.out.println("Part 2: ");
	}
	
	private static String duplicate(String input) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 5; i++) {
			sb.append(input);
		}
		return sb.toString();
	}

	private static List<Integer> duplicate(List<Integer> input) {
		List<Integer> result = new ArrayList<Integer>();
		for(int i = 0; i < 5; i++) {
			result.addAll(input);
		}
		return result;
	}
	
	private static long part1(List<String> lines) {
		long total = 0;
		for(String line : lines) {
			String[] split = line.split("\\s");
			String regex = createRegex(parse(split[1]));
			List<String> permutations = permutations(split[0]);
			total += permutations.stream()
				.filter(input -> input.matches(regex))
				.count();
		}
		return total;
	}
	
	public static List<Integer> parse(String s) {
		return Arrays.stream(s.trim().split(","))
			.map(Integer::valueOf)
			.collect(Collectors.toList());
	}		
	
	public static String createRegex(List<Integer> counts) {
		StringBuilder regex = new StringBuilder();
		
		regex.append("\\.*");
		for (int i = 0; i < counts.size(); i++) {
			if (i != 0) {
				regex.append("\\.+");				
			}
			regex.append("#{" + counts.get(i) + "}");
		}
		regex.append("\\.*");
		
		return regex.toString();
	}
	
	public static List<String> permutations(String input) {	
		List<String> permutations = new ArrayList<String>();
		recurse(input, permutations);
		return permutations;
	}
	
	public static void recurse(String input, List<String> result) {
		int index = input.indexOf('?');
		if (index >= 0) {
			String prefix = (index > 0) ? input.substring(0, index) : "";
			String postfix = (index + 1 < input.length()) ? input.substring(index +1) : "";
			recurse(prefix + "#" + postfix, result);
			recurse(prefix + "." + postfix, result);
		} else {
			result.add(input);
		}
	}
}
