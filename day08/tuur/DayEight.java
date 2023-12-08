package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DayEight {
	
	private static final String REGEX = 
			"(?<from>[0-9A-Z]{3})\\s=\\s\\((?<left>[0-9A-Z]{3}),\\s(?<right>[0-9A-Z]{3})\\)";

	private static final Pattern PATTERN = Pattern.compile(REGEX);
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayEight.class.getResource("input-day08.txt").toURI()));
		
		System.out.println("Part 1: " + followInstructions(lines.get(0), parseNodes(lines)));
		System.out.println("Part 2: " + followInstructionsPart2(lines.get(0), parseNodes(lines)));
		
	}
	
	private static int followInstructions(String instructions, Map<String, Node> nodes) {
		Node current = nodes.get("AAA");
		
		for(int step = 0; true ; step++) {
			char c = instructions.charAt(step % instructions.length());
			System.out.println(step + " -> " + c);
			if (c == 'L') {
				current = nodes.get(current.left);
			} else {
				current = nodes.get(current.right);
			}
			
			if (current.name.equals("ZZZ")) {
				System.out.println("GOAL " + (step+1));
				return step+1;
			}
		}
	}
	
	private static long followInstructionsPart2(String instructions, Map<String, Node> nodes) {
		final List<Node> startNodes = nodes.values().stream()
				.filter(node -> node.name.endsWith("A"))
				.collect(Collectors.toList());

		List<Node> current = startNodes;
		List<Node> next = new ArrayList<DayEight.Node>();
		Map<String, Integer> goals = new HashMap<String, Integer>();
		for(int step = 0; true ; step++) {
			char instruction = instructions.charAt(step % instructions.length());
			
			for(int i = 0; i < current.size(); i++) {			
				Node node = current.get(i);
				Node n = null;
				if (instruction == 'L') {
					n = nodes.get(node.left);
				} else {
					n = nodes.get(node.right);
				}
				if (n.name.endsWith("Z")) {
					String key = startNodes.get(i) + " -> " + n;
					final int nbr = step + 1;
					goals.computeIfAbsent(key, k -> nbr);
				}
				next.add(n);
			}
			if (goals.size() == startNodes.size()) {
				return lowestCommonMultiple(goals.values());
			} else {
				current = next;
				next = new ArrayList<DayEight.Node>();
			}
		}
	}
	
	public static long lowestCommonMultiple(Collection<Integer> numbers) {
		Set<Integer> primeFactors = new HashSet<Integer>();
		for(Integer nb : numbers) {
			primeFactors.addAll(primeFactors(nb));
		}
		
		long result = 1l;
		for(Integer factor : primeFactors) {
			result *= factor.longValue();
		}
		return result;
	}
	
	public static Set<Integer> primeFactors(int number) {
		Set<Integer> result = new HashSet<Integer>();
		for (int i = 2; i < number; i++) {
			while(number % i == 0 ) {
				result.add(i);
				number = number/i;
			}
		}
		if(number > 2) {
			result.add(number);
		}
		return result;		
	}
	
	private static Map<String, Node> parseNodes(List<String> lines) {
		Map<String, Node> nodes = new HashMap<String, DayEight.Node>();
		
		for(String line : lines) {
			Matcher matcher = PATTERN.matcher(line);
			if (matcher.matches()) {
				String name = matcher.group("from");
				String left = matcher.group("left");
				String right = matcher.group("right");
				nodes.put(name, new Node(name, left, right));
			}
		}
		
		return nodes;
	}
	
	private static class Node {
		final String name;
		final String left;
		final String right;
		public Node(String name, String left, String right) {
			super();
			this.name = name;
			this.left = left;
			this.right = right;
		}
		@Override
		public String toString() {
			return name + " = (" + left + ", " + right + ")";
		}
	}
}
