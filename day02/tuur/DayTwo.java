package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DayTwo {
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayTwo.class.getResource("input-day02.txt").toURI()));

		System.out.println("Part 1: " + lines.stream()
			.filter(line -> isValidGame(line, 12, 13, 14))
			.mapToLong(DayTwo::parseGameId)
			.sum());

		System.out.println("Part 2: " + lines.stream()
			.map(DayTwo::determineMinimums)
			.mapToLong(Cubes::power)
			.sum());
	}
	
	private static final Pattern gameIdPattern = Pattern.compile("Game (?<id>\\d+):");
	
	private static long parseGameId(String line) {
		Matcher matcher = gameIdPattern.matcher(line);
		if (matcher.find()) {
			return Long.parseLong(matcher.group("id"));
		}
		throw new IllegalArgumentException("Line does not contain an id : " + line);
	}
	
	private static boolean isValidGame(String line, int maxRed, int maxGreen, int maxBlue) {
		for(String draw : line.split(";"))  {
			if (howManyBallsOfColor(draw, "red") > maxRed) return false;
			if (howManyBallsOfColor(draw, "green") > maxGreen) return false;
			if (howManyBallsOfColor(draw, "blue") > maxBlue) return false;
		}
		return true;
	}
	
	private static Cubes determineMinimums(String line) {
		int minGreen = 0;
		int minBlue = 0;
		int minRed = 0;
		
		for(String draw : line.split(";"))  {
			minRed = Math.max(minRed, howManyBallsOfColor(draw, "red"));
			minGreen = Math.max(minGreen, howManyBallsOfColor(draw, "green"));
			minBlue = Math.max(minBlue, howManyBallsOfColor(draw, "blue"));
		}
		
		return new Cubes(minRed, minGreen, minBlue);
	}
	
	private static int howManyBallsOfColor(String draw, String color) {
		Matcher colorMatcher = Pattern.compile("(?<amount>\\d+)\\s" + color).matcher(draw);
		if (colorMatcher.find()) {
			return Integer.parseInt(colorMatcher.group("amount"));
		}
		return 0;
	}
	
	private static class Cubes {
		private final int red, green, blue;
		
		public Cubes(int red, int green, int blue) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		}
		
		public int power() {
			return red * green * blue;
		}
	}
}
