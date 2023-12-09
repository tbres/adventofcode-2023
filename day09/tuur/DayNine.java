package day09.tuur;

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

public class DayNine {
	
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayNine.class.getResource("input-day09.txt").toURI()));
        // System.out.println(lines);
		
		//System.out.println("Part 1: " + lines.stream().map(DayNine::parseLine).mapToLong(DayNine::predict).sum());
		System.out.println("Part 2: " + lines.stream().map(DayNine::parseLine).mapToLong(DayNine::predictBackwards).sum());



        //System.out.println(predict(Arrays.asList(0l, 3l, 6l, 9l, 12l, 15l)));
        //System.out.println(predict(Arrays.asList(1l,   3l,   6l, 10l, 15l, 21l)));
		//System.out.println(predictBackwards(Arrays.asList(10l,   13l,   16l, 21l, 30l, 45l)));
	}

    private static List<Long> parseLine(String line){
        List<Long> result = new ArrayList<>();
        System.out.println(line.split("\\s"));
        for(String nb : line.split("\\s")) {
            result.add(Long.valueOf(nb));
        }
        return result;
    }

    private static long predict(List<Long> numbers) {
        long difference = 0l;
        List<Long> next = new ArrayList<>();
        boolean allZero = true;
        for (int i = 1; i < numbers.size(); i++) {
            Long first = numbers.get(i-1);
            Long second = numbers.get(i);
            Long dif = second - first;
            allZero &= dif == 0L;
            next.add(dif);
        }

        System.out.println(next);
        Long result = null;
        if (allZero) {
            result = numbers.get(numbers.size() - 1);
        } else {
            result = numbers.get(numbers.size() - 1) +  predict(next);
        }
        return result;
    }

    private static long predictBackwards(List<Long> numbers) {
        List<Long> next = new ArrayList<>();
        boolean allZero = true;
        for (int i = 1; i < numbers.size(); i++) {
            Long first = numbers.get(i-1);
            Long second = numbers.get(i);
            Long dif = second - first;
            allZero &= dif == 0L;
            next.add(dif);
        }

        System.out.println(next);
        Long result = null;
        if (allZero) {
            result = numbers.get(0);
        } else {
            result = numbers.get(0) - predictBackwards(next);
        }
        System.out.println("-> " + result);
        return result;
    }
}