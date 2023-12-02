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
			.mapToLong(min -> min.red * min.blue * min.green)
			.sum());
	}
	
	private static final Pattern gameIdPattern = Pattern.compile("Game (?<id>\\d+):");
	private static final Pattern redPattern = Pattern.compile("(?<red>\\d+)\\sred");
	private static final Pattern bluePattern = Pattern.compile("(?<blue>\\d+)\\sblue");
	private static final Pattern greenPattern = Pattern.compile("(?<green>\\d+)\\sgreen");
	
	private static long parseGameId(String line) {
		Matcher matcher = gameIdPattern.matcher(line);
		if (matcher.find()) {
			return Long.parseLong(matcher.group("id"));
		}
		throw new IllegalArgumentException("Line does not contain an id : " + line);
	}
	
	private static boolean isValidGame(String line, int maxRed, int maxGreen, int maxBlue) {
		for(String draw : line.split(";"))  {
			Matcher redMatcher = redPattern.matcher(draw);
			if (redMatcher.find()) {
				int red = Integer.parseInt(redMatcher.group("red"));
				if (red > maxRed) {
					return false;
				}
			}
			Matcher blueMatcher = bluePattern.matcher(draw);
			if (blueMatcher.find()) {
				int blue = Integer.parseInt(blueMatcher.group("blue"));
				if (blue > maxBlue) {
					return false;
				}
			}
			Matcher greenMatcher = greenPattern.matcher(draw);
			if (greenMatcher.find()) {
				int green = Integer.parseInt(greenMatcher.group("green"));
				if (green > maxGreen) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private static Minimums determineMinimums(String line) {
		int minGreen = 0;
		int minBlue = 0;
		int minRed = 0;
		
		for(String draw : line.split(";"))  {
			Matcher redMatcher = redPattern.matcher(draw);
			if (redMatcher.find()) {
				int red = Integer.parseInt(redMatcher.group("red"));
				minRed = Math.max(minRed, red);
			}
			Matcher blueMatcher = bluePattern.matcher(draw);
			if (blueMatcher.find()) {
				int blue = Integer.parseInt(blueMatcher.group("blue"));
				minBlue = Math.max(minBlue, blue);
			}
			Matcher greenMatcher = greenPattern.matcher(draw);
			if (greenMatcher.find()) {
				int green = Integer.parseInt(greenMatcher.group("green"));
				minGreen = Math.max(minGreen, green);
			}
		}
		
		return new Minimums(minRed, minGreen, minBlue);
	}
	
	private static class Minimums {
		private final int red, green, blue;
		
		public Minimums(int red, int green, int blue) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		}
	}
}
