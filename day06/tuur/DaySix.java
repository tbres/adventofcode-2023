package tuur;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class DaySix {
	
	public static void main(String[] args) throws Exception {

		long[][] input = new long[4][];
		input[0] = new long[] {54, 302};
		input[1] = new long[] {94, 1476};
		input[2] = new long[] {65, 1029};
		input[3] = new long[] {92, 1404};

		long marginOfError = 1l;
		for(long[] game : input) {
			long numberOfWaysToWin = numberOfWaysToWin(game[0], game[1]);
			marginOfError *= numberOfWaysToWin;
		}
		System.out.println("Part 1: " + marginOfError);
		
		long totalTime = Long.parseLong(Arrays.stream(input)
				.map(x -> Long.toString(x[0]))
				.collect(Collectors.joining()));
		long totalRecord = Long.parseLong(Arrays.stream(input)
				.map(x -> Long.toString(x[1]))
				.collect(Collectors.joining()));
		System.out.println("Part 2: " + numberOfWaysToWin(totalTime, totalRecord));

	}
	
	private static long numberOfWaysToWin(long timeLimit, long record) {
		return LongStream.range(0, timeLimit)
			.map(i -> i * (timeLimit - i))
			.filter(j -> j > record)
			.count();
	}
}
