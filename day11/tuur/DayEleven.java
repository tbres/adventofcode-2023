package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class DayEleven {
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayEleven.class.getResource("input-day11.txt").toURI()));

		Map<Coord, Character> map = parse(lines);
		System.out.println("Part 1: " + part1(map, 2));
		System.out.println("Part 2: "+ part1(map, 1000000));
	}
	
	private static final Character GALAXY = Character.valueOf('#');
	private static final Character SPACE = Character.valueOf('.');
	
	private static long part1(Map<Coord, Character> map, Integer drift) {

		List<Coord> galaxies = new ArrayList<DayEleven.Coord>(); 
		Map<Integer, Integer> xWeights = new HashMap<Integer, Integer>();
		Map<Integer, Integer> yWeights = new HashMap<Integer, Integer>();
		map.entrySet().stream()
				.filter(entry -> GALAXY.equals(entry.getValue()))
				.map(Entry::getKey)
				.forEach(coord -> {
					galaxies.add(coord);
					xWeights.put(coord.x, 1);
					yWeights.put(coord.y, 1);
				});
		
		Map<Integer, Long> xRemapping = new HashMap<>();
		xRemapping.put(0, 0L);
		int maxX = map.keySet().stream().mapToInt(coord -> coord.x).max().getAsInt();
		long xCurrent = 0l;
		for (int x = 1; x <= maxX; x++) {
			xCurrent += xWeights.getOrDefault(x, drift).longValue(); 
			xRemapping.put(x, xCurrent);
		}

		Map<Integer, Long> yRemapping = new HashMap<>();
		yRemapping.put(0, 0L);
		int maxY = map.keySet().stream().mapToInt(coord -> coord.y).max().getAsInt();
		long yCurrent = 0l;
		for (int y = 1; y <= maxY; y++) {
			yCurrent += yWeights.getOrDefault(y, drift).longValue(); 
			yRemapping.put(y, yCurrent);
		}
		
		// FINALLY: 
		long totalDistance = 0l;		
		for (int i = 0; i < galaxies.size() - 1; i++) {
			Coord first = galaxies.get(i);
			
			for (int j = i + 1; j < galaxies.size(); j++) {
				Coord second = galaxies.get(j);

				totalDistance += Math.abs(xRemapping.get(first.x) - xRemapping.get(second.x)) 
								+ Math.abs(yRemapping.get(first.y) - yRemapping.get(second.y));
			}
		}
		return totalDistance;
	}
	
	private static Map<Coord, Character> parse(List<String> lines) {
		HashMap<Coord, Character> result = new HashMap<>();
		for(int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			for(int x = 0; x < line.length(); x++) {
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
