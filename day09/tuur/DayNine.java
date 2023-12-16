package day09.tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DayNine {
	
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayNine.class.getResource("input-day09.txt").toURI()));
		
		System.out.println("Part 1: " + lines.stream()
            .map(DayNine::parseLine)
            .mapToLong(DayNine::predict)
            .sum());
        
		System.out.println("Part 2: " + lines.stream()
            .map(DayNine::parseLine)
            .peek(Collections::reverse) // The magic sauce!    
            .mapToLong(DayNine::predict)
            .sum());
	}

    private static List<Long> parseLine(String line){
        List<Long> result = new ArrayList<>();
        for(String nb : line.split("\\s")) {
            result.add(Long.valueOf(nb));
        }
        return result;
    }

    private static long predict(List<Long> numbers) {

        List<Long> next = new ArrayList<>();
        boolean allZero = true;
        for (int i = 1; i < numbers.size(); i++) {
            Long first = numbers.get(i-1);
            Long second = numbers.get(i);
            Long dif = second - first;
            allZero &= dif == 0L;
            next.add(dif);
        }

        Long result = null;
        if (allZero) {
            result = numbers.get(numbers.size() - 1);
        } else {
            result = numbers.get(numbers.size() - 1) +  predict(next);
        }
        return result;
    }

}