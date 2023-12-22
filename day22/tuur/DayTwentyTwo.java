package day22.tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DayTwentyTwo {

    private static final Pattern REGEX = Pattern.compile("(?<x1>\\d+),(?<y1>\\d+),(?<z1>\\d+)~(?<x2>\\d+),(?<y2>\\d+),(?<z2>\\d+)");

    public static void main(String[] args) throws Exception {
        List<String> input = Files.readAllLines(Paths.get(DayTwentyTwo.class.getResource("input-day22.txt").toURI()));

        List<Line> lines = input.stream()
            .map(DayTwentyTwo::parseLine)
            .peek(System.out::println)
            .collect(Collectors.toList());
        
        Map<Line, Set<Line>> supporting = drop(lines); // brick 'key' is supporting brick 'values'

        System.out.println("Part 1: " + part1(supporting));
        
        System.out.println("Part 2: " + part2(supporting));
    }

    private static int part2(Map<Line, Set<Line>> supporting) {
        Map<Line, Set<Line>> supportedBy = new HashMap<>();
        for (Entry<Line, Set<Line>> entry : supporting.entrySet()) {
            for (Line line : entry.getValue()) {
                supportedBy.computeIfAbsent(line, l -> new HashSet<Line>()).add(entry.getKey());
            }
        }

        int chainReaction = 0;
        for (Line line : supporting.keySet()) {
            HashSet<Line> alreadyDisintegrated = new HashSet<>();
            alreadyDisintegrated.add(line);

            chainReaction += howManyOtherBricksWouldFall(line, supporting, supportedBy, alreadyDisintegrated);
        }

        return chainReaction;
    }

    private static int howManyOtherBricksWouldFall(Line line, Map<Line, Set<Line>> supporting, Map<Line, Set<Line>> supportedBy, Set<Line> alreadyDisintegrated) {
        Set<Line> supportedLines = supporting.get(line);
        if (supportedLines.isEmpty()) {
            return 0;

        } else {
            int count = 0; // Other bricks that would fall, this brick excluded!
            for (Line supported : supporting.get(line)) {
                Set<Line> supports = new HashSet<>(supportedBy.get(supported));
                supports.removeIf(alreadyDisintegrated::contains);

                if (supports.isEmpty()) {
                    alreadyDisintegrated.add(supported);
                    int tmp = howManyOtherBricksWouldFall(supported, supporting, supportedBy, alreadyDisintegrated);
                    count += tmp + 1;
                }
            }
            return count; 
        }
    }

    private static int part1(Map<Line, Set<Line>> supporting) {
        Map<Line, Integer> supports = supporting.values().stream()
            .flatMap(Set::stream)
            .collect(Collectors.toMap(line -> line, line -> 1, Integer::sum));

        int blocksToDisintegrate = 0;
        for (Entry<Line, Set<Line>> entry : supporting.entrySet()) {

            boolean safeToDisintegrate = true;
            for (Line supported : entry.getValue()) {
                if (supports.get(supported) == 1) {
                    safeToDisintegrate = false;
                }
            }
            if (safeToDisintegrate) {
                blocksToDisintegrate++;
            }
        }
        return blocksToDisintegrate;
    }

    private static Map<Line, Set<Line>> drop(List<Line> lines) {
        Map<Coord3D, Integer> heights = new HashMap<>(); //2D grid mapping (x, y) to the highest z value;

        Map<Coord3D, Line> supports = new HashMap<>(); //2D grid mapping (x, y) to the line corresponding with the highest z value;

        
        Map<Line, Set<Line>> resting = new HashMap<>();

        List<Line> falling = new ArrayList<>(lines);
        falling.sort(Comparator.comparing(Line::lowestZ));
        char count = 'A';
        for (Line line : falling) {
            System.out.println(count + ": " + line);
            count++;
            if (line.lowestZ() == 1) {
                resting.put(line, new HashSet<>());
                if (line.isHorizontal()) {
                    for(Coord3D point : line.asCoords()) {
                        Coord3D point2d = new Coord3D(point.x, point.y, 0);
                        heights.put(point2d, point.z);
                        supports.put(point2d, line);
                    }
                } else {
                    Coord3D point2d = new Coord3D(line.start.x, line.start.y, 0);
                    heights.put(point2d, line.highestZ());
                    supports.put(point2d, line);
                }
                System.out.println(" -> is already at it's final location");
            } else {
                int heighestZ = line.asCoords().stream()
                                    .mapToInt(p -> heights.getOrDefault(new Coord3D(p.x, p.y, 0), 0))
                                    .max().getAsInt();

                Set<Line> supporting = line.asCoords().stream()
                                    .map(p -> supports.get(new Coord3D(p.x, p.y, 0)))
                                    .filter(Objects::nonNull)
                                    .filter(l -> l.highestZ() == heighestZ)
                                    .collect(Collectors.toSet());

                Line finalPosition;
                if (heighestZ + 1 == line.lowestZ()) { //Optimalisation
                    finalPosition = line;
                } else if (line.isHorizontal()) {
                    finalPosition = new Line(
                                        new Coord3D(line.start.x, line.start.y, heighestZ + 1),
                                        new Coord3D(line.end.x, line.end.y, heighestZ + 1)
                                    );
                } else {
                    if (line.start.z > line.end.z) {
                        finalPosition = new Line(
                                        new Coord3D(line.start.x, line.start.y, heighestZ + 1 + line.start.z - line.end.z),
                                        new Coord3D(line.end.x, line.end.y, heighestZ + 1)
                                    );
                    } else {
                        finalPosition = new Line(
                                        new Coord3D(line.start.x, line.start.y, heighestZ + 1),
                                        new Coord3D(line.end.x, line.end.y, heighestZ + 1 + line.end.z - line.start.z)
                                    );
                    }
                }

                resting.put(finalPosition, new HashSet<>());
                supporting.forEach(support -> resting.get(support).add(finalPosition));
                for(Coord3D point : finalPosition.asCoords()) {
                    heights.put(new Coord3D(point.x, point.y, 0), point.z);
                }
                if (finalPosition.isHorizontal()) {
                    for(Coord3D point : finalPosition.asCoords()) {
                        Coord3D point2d = new Coord3D(point.x, point.y, 0);
                        heights.put(point2d, point.z);
                        supports.put(point2d, finalPosition);
                    }
                } else {
                    Coord3D point2d = new Coord3D(finalPosition.start.x, finalPosition.start.y, 0);
                    heights.put(point2d, finalPosition.highestZ());
                    supports.put(point2d, finalPosition);
                }

                System.out.println(" -> drops to " + finalPosition);
                System.out.println(" -> supported by " + supporting);
            }
        } 

        return resting;
    } 

    private static Line parseLine(String input) {
        Matcher matcher = REGEX.matcher(input);
        if (matcher.matches()) {
            return new Line(
                new Coord3D(
                    Integer.parseInt(matcher.group("x1")),
                    Integer.parseInt(matcher.group("y1")),
                    Integer.parseInt(matcher.group("z1"))
                ), 
                new Coord3D(
                    Integer.parseInt(matcher.group("x2")), 
                    Integer.parseInt(matcher.group("y2")),
                    Integer.parseInt(matcher.group("z2"))
                )
            );
        }
        throw new IllegalArgumentException("Can't parse line: " + input);
    }

    private static final class Line {
        final Coord3D start, end;

        public Line(Coord3D start, Coord3D end) {
            this.start = start;
            this.end = end;
        }

        public int lowestZ() {
            return Integer.min(start.z, end.z);
        }

        public int highestZ() {
            return Integer.max(start.z, end.z);
        }

        public List<Coord3D> asCoords() {
            List<Coord3D> result = new ArrayList<>();
            for (int xi = Integer.min(start.x, end.x); xi <= Integer.max(start.x, end.x); xi++) {
                for (int yi = Integer.min(start.y, end.y); yi <= Integer.max(start.y, end.y); yi++) {
                    for (int zi = Integer.min(start.z, end.z); zi <= Integer.max(start.z, end.z); zi++) {
                        result.add(new Coord3D(xi, yi, zi));
                    }
                }
            }
            return result;
        }

        public Line drop(int amount) {
            return new Line(
                new Coord3D(start.x, start.y, start.z - amount),
                new Coord3D(end.x, end.y, end.z - amount)
            );
        }

        public boolean isHorizontal() {
            return start.z == end.z;
        }

        @Override
        public String toString() {
            return start + " ~ " + end; 
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Line) {
                Line other = (Line) obj;
                return start.equals(other.start) && end.equals(other.end);
            }
            return false;
        }
    }

    private static final class Coord3D {

        final int x, y, z;

        public Coord3D(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }
        
        public int z() {
            return z;
        }

        @Override
        public String toString() {
            return String.format("(%s, %s, %s)", x, y, z);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Coord3D) {
                Coord3D other = (Coord3D) obj;
                return x == other.x && y == other.y && z == other.z;
            }
            return false;
        }
    } 
}