package day23.tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;

public class DayTwentyThree {

    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(DayTwentyThree.class.getResource("input-day23.txt").toURI()));

        Plan plan = parseMap(lines);

        Map<Coord, Integer> distances =  walk(plan);

        System.out.println("Part 1: " + distances.get(plan.end));

        Map<Coord, Integer> distancesPart2 =  walkPart2(plan);

        System.out.println("Part 1: " + distancesPart2.get(plan.end));


    }

    private static final Character TRAIL = '.';
    private static final Character FOREST = '#';
    private static final Character SLOPE_LEFT = '<';
    private static final Character SLOPE_RIGHT = '>';
    private static final Character SLOPE_DOWN = 'v';
    private static final Character SLOPE_UP = '^';
    
    public static Map<Coord, Integer> walkPart2(Plan plan) {
        Map<Coord, Integer> longestPath = new HashMap<>();
        Path longest = null;

        List<Path> walking = new ArrayList<>();
        walking.add(new Path(plan.start, null));

        while (!walking.isEmpty()) {
            List<Path> keepWalking = new ArrayList<>();

            for (Path path : walking) {
                for (Direction direction : Direction.values()) {
                    Coord neighbour = path.coord.neighbour(direction);
                    
                    if (path.contains(neighbour)) {
                        // NO GOING BACK
                        continue;
                    }
                    Character tile = plan.get(neighbour);
                    if (tile == null || FOREST.equals(tile)) {
                        // DO NOTHING
                        continue;
                    } else {
                        Path newPath = new Path(neighbour, path);
                        if (newPath.length > longestPath.getOrDefault(neighbour, 0)) {
                            longestPath.put(neighbour, path.length + 1);
                            if (neighbour.equals(plan.end)) {
                                longest = new Path(neighbour, path);
                            } else {
                                keepWalking.add(newPath);
                            }
                        }
                    }
                }
            }
            walking = keepWalking;
        }

        System.out.println();
        print(plan, longest);
        System.out.println();

        return longestPath;
    }

    private static void print(Plan plan, Map<Coord, Integer> longestPath) {

        for (int y = 0; y <= plan.sizeY; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x <= plan.sizeX; x++) {
                Coord c = new Coord(x, y);

                Character terrain = plan.get(c);
                Integer longest = longestPath.get(c);

                if (FOREST.equals(terrain)) {
                    sb.append("    " + FOREST);
                } else if (longest != null) {
                    sb.append(" ");
                    if (longest < 1000) {
                        sb.append(" ");
                    }
                    if (longest < 100) {
                        sb.append(" ");
                    }
                    if (longest < 10) {
                        sb.append(" ");
                    }
                    sb.append(longest);
                } else {
                    sb.append("    " + terrain);
                }
            }
            System.out.println(sb.toString());
        }
    }

    private static void print(Plan plan, Path path) {
        Map<Coord, Character> pathCoords = new HashMap<>();
        Path current = path;
        while (current != null) {
            pathCoords.put(current.coord, 'O');
            current = current.previous;
        }

        for (int y = 0; y <= plan.sizeY; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x <= plan.sizeX; x++) {
                Coord c = new Coord(x, y);
                sb.append(pathCoords.getOrDefault(c, plan.get(c)));
            }
            System.out.println(sb.toString());
        }
    }

    public static Map<Coord, Integer> walk(Plan plan) {
        Map<Coord, Integer> longestPath = new HashMap<>();

        List<Path> walking = new ArrayList<>();
        walking.add(new Path(plan.start, null));
        while (!walking.isEmpty()) {
            List<Path> keepWalking = new ArrayList<>();

            for (Path path : walking) {
                Character last = plan.get(path.coord);
                
                if (Slope.isSlope(last)) {
                    Coord next = Slope.valueOf(last).follow(path.coord);

                    if (path.length + 1 > longestPath.getOrDefault(next, 0)) {
                        keepWalking.add(new Path(next, path));
                        longestPath.put(next, path.length + 1);
                    }
                } else {
          
                    for (Direction direction : Direction.values()) {
                        Coord neighbour = path.coord.neighbour(direction);
                        
                        if (path.contains(neighbour)) {
                            // NO GOING BACK
                            continue;
                        }
                        Character tile = plan.get(neighbour);
                        if (tile == null || FOREST.equals(tile)) {
                            // DO NOTHING
                            continue;
                        } else if (TRAIL.equals(tile)
                                || (SLOPE_UP.equals(tile) && !Direction.down.equals(direction)) 
                                || (SLOPE_DOWN.equals(tile) && !Direction.up.equals(direction))
                                || (SLOPE_LEFT.equals(tile) && !Direction.right.equals(direction))
                                || (SLOPE_RIGHT.equals(tile) && !Direction.left.equals(direction))) {
                            if (path.length + 1 > longestPath.getOrDefault(neighbour, 0)) {
                                keepWalking.add(new Path(neighbour, path));
                                longestPath.put(neighbour, path.length + 1);
                            }
                        }
                    }
                }

            }
            walking = keepWalking;
        }
        return longestPath;
    }

    private static enum Slope {
        up('^', Coord::up), 
        down('v', Coord::down), 
        left('<', Coord::left), 
        right('>', Coord::right);

        final char representation;
        final Function<Coord, Coord> action;

        Slope(char representation, Function<Coord, Coord> action) {
            this.representation = representation;
            this.action = action;
        }

        public Coord follow(Coord coord) {
            return action.apply(coord);
        }

        public static boolean isSlope(char c) {
            for (Slope s : Slope.values()) {
                if (c == s.representation) {
                    return true;
                }
            }
            return false;
        }

        public static Slope valueOf(char c) {
            for (Slope s : Slope.values()) {
                if (c == s.representation) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Not a slope: " + c);
        }
    }

    private static final class Path {
        final Coord coord;
        final Path previous;
        final int length;
        public Path(Coord coord, Path previous) {
            this.coord = coord;
            this.previous = previous;
            this.length = (previous == null) ? 0 : previous.length + 1;
        }

        public boolean contains(Coord c) {
            if (coord.equals(c)) {
                return true;
            } else {
                Path current = this.previous;
                while (current != null) {
                    if (current.coord.equals(c)) {
                        return true;
                    }
                    current = current.previous;
                }
            }
            return false;
        }
    }

    private static Plan parseMap(List<String> lines) {
        Map<Coord, Character> result = new HashMap<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                Character shape = line.charAt(x);
                result.put(new Coord(x, y), shape);
            }
        }
        return new Plan(result);
    }

    private static final class Plan {
        final Map<Coord, Character> map;
        final int sizeX, sizeY;
        final Coord start, end;

        public Plan( Map<Coord, Character> map) {
            this.map = map;

            this.sizeX = map.keySet().stream().mapToInt(c -> c.x).max().getAsInt();
            this.sizeY = map.keySet().stream().mapToInt(c -> c.y).max().getAsInt();

            this.start = map.entrySet().stream().filter(entry -> entry.getKey().y == 0).filter(entry -> entry.getValue() == '.').map(Entry::getKey).findFirst().get();
            this.end = map.entrySet().stream().filter(entry -> entry.getKey().y == sizeY).filter(entry -> entry.getValue() == '.').map(Entry::getKey).findFirst().get();
        }

        public Character get(Coord coord) {
            return map.get(coord);
        }

        public boolean contains(Coord coord) {
            return 0 <= coord.x && coord.x <= sizeX
                && 0 <= coord.y && coord.y <= sizeY;
        }
    }

    private static enum Direction {
        up, down, left, right;
    }

    private static final class Coord {
        final int x, y;
        final int hashCode;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
            this.hashCode = Objects.hash(x, y);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public long xLong() {
            return Integer.valueOf(x).longValue();
        }

        public long yLong() {
            return Integer.valueOf(y).longValue();
        }
        
        public List<Coord> neighbours() {
            return Arrays.asList(
                new Coord(x, y - 1),
                new Coord(x, y + 1),
                new Coord(x - 1, y),
                new Coord(x + 1, y)
            );
        }

        public Coord neighbour(Direction direction) {
            switch(direction) {
                case down: return down();
                case up: return up();
                case left: return left();
                case right: return right();
                default: throw new IllegalArgumentException();
            }
        }
        
        public Coord up() {
            return new Coord(x, y-1);
        }
        
        public Coord down() {
            return new Coord(x, y+1);
        }
        
        public Coord left() {
            return new Coord(x-1, y);
        }

        public Coord right() {
            return new Coord(x+1, y);
        }
        

        @Override
        public String toString() {
            return "(" + x + ", " + y +")";
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Coord) {
                Coord other = (Coord) obj;
                return x == other.x && y == other.y;
            }
            return false;
        }
    }
}