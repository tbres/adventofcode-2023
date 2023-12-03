package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DayThree {
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayThree.class.getResource("input-day03.txt").toURI()));
//		List<String> lines = Files.readAllLines(Paths.get(DayThree.class.getResource("test-input-day03.txt").toURI()));

		Schematic schematic = parseSchematic(lines);
		
		System.out.println("Part 1: " + findPartNumbers(schematic));

		System.out.println("Part 2: " + findGearRatios(schematic));

	}
	
	public static long findGearRatios(Schematic schematic) {
		Map<Coord, List<Long>> gearsAndParts = new HashMap<DayThree.Coord, List<Long>>();
		
		for(int y = 0; y <= schematic.sizeY; y++) {
			
			StringBuilder partNumber = null;
			Set<Coord> gears = new HashSet<>();
			
			for(int x = 0; x <= schematic.sizeX; x++) {
				final Coord coord = new Coord(x, y);
				final Character c = schematic.get(coord);

				if (Character.isDigit(c)) {
					if (partNumber == null) {
						partNumber = new StringBuilder();
					}
					partNumber.append(c);
					
					for(Coord neighbour : neighbours(coord)) {
						if (schematic.get(neighbour) == '*') {
							gears.add(neighbour);
						}
					}
				} 
				
				if (!Character.isDigit(c) || x == schematic.sizeX) {
					if (partNumber != null) {
						final Long number = Long.valueOf(partNumber.toString());
						for (Coord gear : gears) {
							gearsAndParts.computeIfAbsent(gear, k -> new ArrayList<Coord>());
							List<Long> list = gearsAndParts.get(gear);
							if (list == null) {
								list = new ArrayList<Long>();
								gearsAndParts.put(gear, list);
							}
							list.add(number);
						}
					}
					partNumber = null;
					gears = new HashSet<DayThree.Coord>();
				}
			}
		}
		long result = 0l;
		for (Coord gear : gearsAndParts.keySet()) {
			List<Long> list = gearsAndParts.get(gear);
			if (list.size() == 2) {
				System.out.println(gear +  " -> " + list);
				result += list.get(0) * list.get(1);
			}
		}
		return result;
	
	}
	
	public static long findPartNumbers(Schematic schematic) {
		long result = 0l;
		
		for(int y = 0; y <= schematic.sizeY; y++) {
			StringBuilder partNumber = null;
			boolean adjacent = false;
			
			for(int x = 0; x <= schematic.sizeX; x++) {
				final Coord coord = new Coord(x, y);
				final Character c = schematic.get(coord);

				if (Character.isDigit(c)) {
					if (partNumber == null) {
						partNumber = new StringBuilder();
					}
					partNumber.append(c);
					
					adjacent |= neighbours(coord).stream()
						.map(neighbour -> schematic.get(neighbour))
						.filter(DayThree::isSymbol)
						.findAny()
						.isPresent();
				} 
				if (!Character.isDigit(c) || x == schematic.sizeX) {
					if (partNumber != null) {
						System.out.println("    number " + partNumber + ", adjacent = " + adjacent);
						long number = Long.parseLong(partNumber.toString());
						if (adjacent) {
							result += number;							
						}
					}
					partNumber = null;
					adjacent = false;
				}
			}
		}
		
		return result;
	}
	
	private static boolean isSymbol(Character c) {
		return !DOT.equals(c) && !Character.isDigit(c);
	}
	
	private static List<Coord> neighbours(Coord coord) {
		List<Coord> result = new ArrayList<>();
		for(int i = coord.y-1; i <= coord.y+1 ; i++) {
			for (int j = coord.x-1; j <= coord.x+1; j++) {
				if (i == coord.y && j == coord.x) {
				} else {
					result.add(new Coord(j, i));
				}
			}
		}
		return result;
	}
	
	private static final Character DOT = Character.valueOf('.');
	
	public static Schematic parseSchematic(List<String> lines) {
		Schematic schematic = new Schematic();
		
		for (int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			for(int x = 0; x < line.length(); x++) {
				Character c = line.charAt(x);
				if(!DOT.equals(c)) {
					schematic.put(new Coord(x, y), c);					
				}
			}
		}
		
		return schematic;
	}
	
	private static class Schematic {
		private Map<Coord, Character> result = new HashMap<>();
		private int sizeX = 0;
		private int sizeY = 0;
		
		public Schematic() {
		}
		
		public void put(Coord coord, Character c) {
			result.put(coord, c);
			sizeX = Math.max(sizeX, coord.x);
			sizeY = Math.max(sizeY, coord.y);
		}
		
		public Character get(Coord coord) {
			return result.getOrDefault(coord, Character.valueOf('.'));
		}
	}
	
	private static class Coord {
		private final int x,y;
		
		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public String toString() {
			return "Coord [x=" + x + ", y=" + y + "]";
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof Coord))
				return false;
			Coord other = (Coord) obj;
			return x == other.x && y == other.y;
		}
	}
}
