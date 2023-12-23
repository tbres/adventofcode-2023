package day21.tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class DayTwentyOne {

    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(DayTwentyOne.class.getResource("input-day21.txt").toURI()));
        Map<Coord, Character> garden = parseMap(lines);

        Coord start = garden.entrySet().stream().filter(entry -> entry.getValue() == 'S').map(Entry::getKey).findFirst().get();
        Set<Coord> visited = new HashSet<>();
        visited.add(start);

        for(int i = 0; i < 64 ; i++) {
            visited = visit(garden, visited);
        }
        System.out.println("Part 1: " + visited.size());

        Garden garden2 = new Garden(garden);

        // System.out.println(garden2.sizeX +  " " + garden2.sizeY);
        // System.out.println(garden2.map(new Coord(-1, -1)));
        // System.out.println(garden2.map(new Coord(131, 131)));
        // System.out.println(garden2.map(new Coord(1000, 1000)));
        // System.out.println(garden2.map(new Coord(-1000, -1000)));

        System.out.println("Part 2: " + part2(garden2, start));
        
    }

    private static int part2(Garden garden, Coord start) {
        Set<Coord> state_a = new HashSet<>();
        Set<Coord> state_b = new HashSet<>();

        Set<Coord> latest = new HashSet<>();
        latest.add(start);
        
        for (int steps = 0; steps < 26501365 ; steps++) {
            if (steps % 1000 == 0) {
                System.out.println();
                System.out.println(steps);
                System.out.println("State a: " + state_a.size());
                System.out.println("State b: " + state_b.size());
            }

            Set<Coord> newest = visitPart2(garden, latest);
            if (steps % 2 == 0) {
                newest.removeIf(state_a::contains);
                state_a.addAll(newest);
            } else {
                newest.removeIf(state_b::contains);
                state_b.addAll(newest);
            }
            latest = newest;
        }


        System.out.println("State a: " + state_a.size());
        System.out.println("State b: " + state_b.size());
        
        return state_a.size() + state_b.size();
    }

    private static Set<Coord> visit(Map<Coord, Character> garden, Set<Coord> positions) {
        return positions.stream()
            .flatMap(coord -> coord.neighbours().stream())
            .filter(coord -> garden.getOrDefault(coord, '#') != '#')
            .collect(Collectors.toSet());
    }

    private static Set<Coord> visitPart2(Garden garden, Set<Coord> positions) {
        return positions.stream()
            .flatMap(coord -> coord.neighbours().stream())
            .distinct()
            .filter(coord -> garden.get(coord) != '#')
            .collect(Collectors.toSet());
    }

    private static Map<Coord, Character> parseMap(List<String> lines) {
        Map<Coord, Character> result = new HashMap<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                Character shape = line.charAt(x);
                result.put(new Coord(x, y), shape);
            }
        }
        return result;
    } 

    private static void print(Map<Coord, Character> landscape, Set<Coord> visited) {
        final int minX = 0; // landscape.keySet().stream().mapToInt(Coord::getX).min().getAsInt();
        final int maxX = landscape.keySet().stream().mapToInt(Coord::getX).max().getAsInt();
        final int minY = 0; // landscape.keySet().stream().mapToInt(Coord::getY).min().getAsInt();
        final int maxY = landscape.keySet().stream().mapToInt(Coord::getY).max().getAsInt();
        System.out.println();
        for(int y = minY; y <= maxY; y++) {
            StringBuilder sb = new StringBuilder();
            for(int x = minX ; x <= maxX; x++) {
                sb.append(landscape.getOrDefault(new Coord(x, y), '.'));
            }
            sb.append("    ");
            for(int x = minX ; x <= maxX; x++) {
                Coord c = new Coord(x, y);
                if (visited.contains(c)) {
                    sb.append('O');
                } else {
                    sb.append(landscape.getOrDefault(new Coord(x, y), '.'));
                }
            }

            System.out.println(sb.toString());
        }
        System.out.println();
    }

    private static final class Garden {
        final Map<Coord, Character> map;
        final int sizeX, sizeY;

        public Garden( Map<Coord, Character> map) {
            this.map = map;
            this.sizeX = map.keySet().stream().mapToInt(c -> c.x).max().getAsInt() + 1;
            this.sizeY = map.keySet().stream().mapToInt(c -> c.y).max().getAsInt() + 1;
        }

        public Character get(Coord coord) {
            return map.get(map(coord));
        }

        public Coord map(Coord coord) {
            int x = coord.x;
            while (x < 0) {
                x += sizeX;
            }
            x = x % sizeX;

            int y = coord.y;
            while (y < 0) {
                y += sizeY;
            }
            y = y % sizeY;

            return new Coord(x, y);
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