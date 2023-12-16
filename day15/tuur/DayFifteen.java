package day15.tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DayFifteen {

    public static void main(String[] args) throws Exception{
        List<String> lines = Files.readAllLines(Paths.get(DayFifteen.class.getResource("input-day15.txt").toURI()));

        System.out.println("Part 1: " + lines.stream()
            .map(line -> line.split(","))
            .flatMap(Arrays::stream)
            .mapToInt(DayFifteen::hash)
            .sum());

        
        Map<Integer, List<Lens>> boxes = new HashMap<>();
        lines.stream()
            .map(line -> line.split(","))
            .flatMap(Arrays::stream)
            .forEach(step -> applyStep(step, boxes));

        LongAccumulator accumulator = new LongAccumulator((a, b) -> a + b, 0l);
        boxes.forEach((number, box) -> {
            for (int i = 0; i < box.size(); i++) {
                Lens lens = box.get(i);
                int focusingPower = (number + 1) * (i + 1) * lens.focalLength;
                accumulator.accumulate(focusingPower);
            }
        });
        System.out.println("Part 2: " + accumulator.longValue());       
    }

    private static final Pattern labelMaker = Pattern.compile("(?<label>[a-z]+)(?<operation>[=-])(?<focal>[0-9]?)");

    private static void applyStep(String step, Map<Integer, List<Lens>> boxes) {
        Matcher matcher = labelMaker.matcher(step);
        if (matcher.matches()) {
            String label = matcher.group("label");
            String operation = matcher.group("operation");
            String focalLength = matcher.group("focal");

            int boxNumber = hash(label);
            
            List<Lens> box = boxes.computeIfAbsent(boxNumber, ArrayList::new);
            if ("=".equals(operation)) {
                Lens lens = new Lens(label, Integer.parseInt(focalLength));
                box.replaceAll(other -> {
                    if (lens.label.equals(other.label)) {
                        return lens;
                    } else {
                        return other;
                    }
                });
                if (!box.contains(lens)) {
                    box.add(lens);
                }
            } else { //if ("-".equals(operation)) {
                box.removeIf(lens -> label.equals(lens.label));
            }
        }
    }

    private static final class Lens {
        final String label;
        final int focalLength;

        public Lens(String label, int focalLength) {
            this.label = label;
            this.focalLength = focalLength;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Lens) {
                Lens other = (Lens) obj;
                return label.equals(other.label) && focalLength == other.focalLength;
            }
            return false;
        }

        @Override
        public String toString() {
            return "[" + label + " " + focalLength + "]";
        }
    }

    private static int hash(String input) {
        int result = 0;
        for (char c : input.toCharArray()) {
            int ascii = (int) c;
            result += ascii;
            result *= 17;
            result %= 256;
        }
        return result;
    }

}
