package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class DayFive {
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayFive.class.getResource("input-day05.txt").toURI()));
		List<Long> seeds = parseSeedLocations(lines.get(0));
		List<List<Mapping>> mappings = parseMappings(lines);

		System.out.println("Part 1: " + seeds.stream()
				.mapToLong(l -> l)
				.map(number -> DayFive.map(number, mappings))
				.min().getAsLong());
		
		/**********************************************************/
		
		List<Range> seedRanges = parseSeedRanges(seeds);
		
		System.out.println("Part 2: " + seedRanges.stream()
			.flatMap(Range::stream)
			.parallel()
			.mapToLong(l -> l)
			.map(number -> DayFive.map(number, mappings))
			.min().getAsLong());
	}
	
	private static List<Long> parseSeedLocations(String seedString) {
		final List<Long> seeds = new ArrayList<>();
		for(String seed : seedString.substring(seedString.indexOf(':') + 1).trim().split("\\s")) {
			seeds.add(Long.valueOf(seed));
		}
		return seeds;
	}
	
	private  static List<Range> parseSeedRanges(List<Long> seeds) {
		List<Range> result = new ArrayList<>();
		for (int i = 0; i < seeds.size(); i += 2) {
			Long start = seeds.get(i);			
			Long range = seeds.get(i + 1 );
			
			result.add(new Range(start, start + range - 1));
		}
		result.sort(Comparator.comparing(Range::start));
		return result;
	}
	
	private static final String CATEGORY_REGEX = ".*-to-.*\\smap:";

	private static List<List<Mapping>> parseMappings(List<String> lines) {
		List<List<Mapping>> result = new ArrayList<>();

		List<Mapping> tmp = null;
		for (int i = 1 ; i < lines.size(); i++) {
			String line = lines.get(i);
			
			if (line.matches(CATEGORY_REGEX)) {
				if (tmp != null) {
					Collections.sort(tmp, Comparator.comparing(Mapping::sourceStart));
				}
				tmp = new ArrayList<>();
				result.add(tmp);
				
			} else if (!line.isBlank()){
				String[] split = line.split("\\s");
				tmp.add(new Mapping(
						Long.parseLong(split[1]), 
						Long.parseLong(split[0]), 
						Long.parseLong(split[2]))
					);
			}
		}
		
		if (tmp != null) {
			Collections.sort(tmp, Comparator.comparing(Mapping::sourceStart));
		}
		
		return result;
	}
	
	private static long map(long number, List<List<Mapping>> mappings) {
		long result = number;
		for (List<Mapping> mapperoni: mappings) {
			for (Mapping mapping : mapperoni) {
				if (mapping.canBeMapped(result)) {
					result = mapping.map(result);
					break;
				}
			}
		}
		return result;
	}
			
	private static final class Mapping {
		final long sourceStart, destinationStart, length;

		public Mapping(long sourceStart, long destinationStart, long length) {
			super();
			this.sourceStart = sourceStart;
			this.destinationStart = destinationStart;
			this.length = length;
		}
		
		public long sourceStart() {
			return sourceStart;
		}

		public long sourceEnd() {
			return sourceStart + length - 1;
		}
		
		public boolean canBeMapped(long source) {
			return sourceStart <= source && source <= sourceStart + length - 1;
		}
		
		public long map(long source) {
			return (source - sourceStart) + destinationStart;
		}
	
		@Override
		public String toString() {
			return "Mapping from [" + sourceStart + ", " + (sourceStart + length - 1) + "]"
					+ " to [" + destinationStart + ", " + (destinationStart + length - 1) + "]";
		}
		
	}
	
	private static final class Range{
		private final long start, end;
		
		public Range(long start, long end) {
			this.start = start;
			this.end = end;
		}
		
		public long start() {
			return start;
		}
		
		public long end() {
			return end;
		}
		
		public static Range ofLength(long start, long length) {
			return new Range(start, start + length - 1);
		}

		public boolean overlaps(Range other) {
			return !(other.end < start || end < other.start);
		}
		
		@Override
		public String toString() {
			return "[" + start + ", " + end + "]";
		}

		public Stream<Long> stream() {
			return LongStream.range(start, end + 1l).boxed();
		}
	}
}
