package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DayThirteen {
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayThirteen.class.getResource("input-day13.txt").toURI()));
		
		System.out.println("Part 1: " + solve(lines, 0));
		System.out.println("Part 2: " + solve(lines, 1));
	}
	
	private static long solve(List<String> lines, int allowedDifferences) {
		long total = 0l;
		
		List<String> current = new ArrayList<String>();
		for(String line : lines) {
			if (line.isBlank()) {
				Map<Coord, Character> map = parse(current);
				total  += findMirror(map, allowedDifferences);
				current = new ArrayList<String>();
			} else {
				current.add(line);
			}
		}
		if (!current.isEmpty()) {
			Map<Coord, Character> map = parse(current);
			total  += findMirror(map, allowedDifferences);
			current = new ArrayList<String>();
		}
		
		return total;
	}

	private static long findMirror(Map<Coord, Character> map, int allowedDifferences) {
		final int maxX = map.keySet().stream().mapToInt(coord -> coord.x).max().getAsInt();
		final int maxY = map.keySet().stream().mapToInt(coord -> coord.y).max().getAsInt();
		
		
		for (int y = 0; y < maxY; y++) {
			if (isHorizontalMirror(map, maxX, maxY, y) == allowedDifferences) {
				return (y + 1) * 100;
			}
		}
		for (int x = 0; x < maxX; x++) {
			if (isVerticalMirror(map, maxX, maxY, x) == allowedDifferences) {
				return x + 1;
			}
		}
		
		throw new IllegalArgumentException("No mirror in input");
	}

	private static int isVerticalMirror(Map<Coord, Character> map, final int maxX, final int maxY, final int x) {
		int differences = 0;
		for (int delta = 0; 0 <= x - delta && x + delta + 1 <= maxX ; delta++) {
			for (int y = 0; y <= maxY; y++) {			
				Character first = map.get(new Coord(x - delta, y));
				Character second = map.get(new Coord(x + delta + 1, y));
				if (!first.equals(second)) {
					differences++;
				}
			}			
		}
		return differences;
	}
	
	private static int isHorizontalMirror(Map<Coord, Character> map, final int maxX, final int maxY, final int y) {
		int differences = 0;
		for (int delta = 0; 0 <= y - delta && y + delta + 1 <= maxY ; delta++) {
			for (int x = 0; x <= maxX; x++) {
				Character first = map.get(new Coord(x, y - delta));
				Character second = map.get(new Coord(x, y + delta + 1));
				if (!first.equals(second)) {
					differences++;
				}
			}			
		}
		return differences;
	}
	
	private static Map<Coord, Character> parse(List<String> lines) {
		HashMap<Coord, Character> result = new HashMap<>();
		for (int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			for (int x = 0; x < line.length(); x++) {
				result.put(new Coord(x, y), line.charAt(x));
			}
		}
		return result;
	}
	
	private static class Coord {
		final int x, y;

		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Coord)) {
				return false;
			}
			Coord other = (Coord) obj;
			return x == other.x && y == other.y;
		}
	}
}
