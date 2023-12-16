package day10.tuur;

import java.nio.channels.Pipe;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class DayTen {
	
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayTen.class.getResource("test-input-day10.txt").toURI()));
        Map<Coord, Character> landscape = parseMap(lines);
        final Coord start = landscape.keySet().stream()
            .filter(c -> landscape.get(c) == 'S') 
            .findFirst().get();

        // System.out.println("Part 1: " + start.neighbours().stream()
        //    .map(neighbour -> follow(start, neighbour, landscape))
        //    .filter(l -> !l.isEmpty())
        //    .mapToInt(l -> l.size() / 2)
        //    .min().getAsInt());

        /****************************************************************/

        List<Coord> path = start.neighbours().stream()
            .map(neighbour -> follow(start, neighbour, landscape))
            .filter(l -> !l.isEmpty())
            .findFirst().get();

        Map<Coord, Character> partTwo = new HashMap<>();
        path.forEach(c -> partTwo.put(c, landscape.get(c)));
        
        print(partTwo);

        Map<Coord, Character> result = partTwo(partTwo);
        print(result);
        System.out.println("Part 2: " + result.values().stream().filter(IN::equals).count());
    }

    private static List<Coord> follow (final Coord start, Coord neighbour, Map<Coord, Character> landscape) {
        List<Coord> path = new ArrayList<>();
        path.add(start);

        Coord previous = start;
        Coord current = neighbour;
        while (!current.equals(start)) {
            Pipe pipe = Pipe.of(current, landscape.get(current));
            if (pipe.connects(previous)) {
                path.add(current);
                Coord next = pipe.follow(previous);
                previous = current;
                current = next;
            } else {
                return Collections.emptyList();
            }
        }
        return path;
    }

    private static final Character OUT = 'O';
    private static final Character IN = 'I';
    

    private static Map<Coord, Character> partTwo(Map<Coord, Character> landscape) {
        final int minX = landscape.keySet().stream().mapToInt(Coord::getX).min().getAsInt();
        final int maxX = landscape.keySet().stream().mapToInt(Coord::getX).max().getAsInt();
        final int minY = landscape.keySet().stream().mapToInt(Coord::getY).min().getAsInt();
        final int maxY = landscape.keySet().stream().mapToInt(Coord::getY).max().getAsInt();

        Set<Coord> toInvestigate = getEmptyCoords(landscape, minX, maxX, minY, maxY);

        while (!toInvestigate.isEmpty()) {
            Set<Coord> investigated = new HashSet<>();
            
            Set<Coord> current = new HashSet<>();
            current.add(toInvestigate.iterator().next());
            
            boolean escaped = false;
            
            while(!current.isEmpty()) {
                Set<Coord> next = new HashSet<>();
                for(Coord c : current) {
                    if (investigated.contains(c)) {
                        continue;
                    }
                    Character value = landscape.get(c);
                    if (value == null) {
                        investigated.add(c);
                        if (escaped(c, minX, maxX, minY, maxY)) {
                            escaped = true;
                        } else {
                            next.addAll(allNeigbours(c));
                        }
                    }
                }
                next.removeAll(investigated);
                current = next;
            }

            if (escaped) {
                investigated.forEach(c -> landscape.put(c, OUT));
            } else {
                investigated.forEach(c -> landscape.put(c, IN));
            }
            toInvestigate.removeAll(investigated);
        }
        return landscape;
    }

    public static boolean escaped(Coord c, int minX, int maxX, int minY, int maxY) {
        return c.x == minX || c.x == maxX
            || c.y == minY || c.y == maxY;
    }

    private static Set<Coord> getEmptyCoords (Map<Coord, Character> landscape,
        int minX, int maxX, int minY, int maxY) {


        Set<Coord> result = new HashSet<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Coord coord = new Coord(x, y);
                if (!landscape.containsKey(coord)) {
                    result.add(coord);
                }
            }
        }

        return result;
    }

    public static Set<Coord> allNeigbours(Coord c) {
        return allNeigbours(c.getX(), c.getY());
    }

    public static Set<Coord> allNeigbours(int x, int y) {
        Set<Coord> result = new HashSet<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i != x || j != y) {
                    result.add(new Coord(i, j));
                }
            } 
        }
        return result;
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

    public static final class Pipe {
        final Coord a, b;

        final Coord location;

        public Pipe(Coord location, Coord a, Coord b) {
            this.location = location;
            this.a = a;
            this.b = b;
        }

        public static Pipe of (Coord location, Character shape) {
            if (shape == null || shape == '.') {
                return new Pipe(location, null, null);
            } else if (shape == '|') {
                return new Pipe(location, location.up(), location.down());
            } else if (shape == '-') {
                return new Pipe(location, location.left(), location.right());
            } else if (shape == 'L') {
                return new Pipe(location, location.up(), location.right());
            } else if (shape == 'J') {
                return new Pipe(location, location.up(), location.left());
            } else if (shape == '7') {
                return new Pipe(location, location.left(), location.down());
            } else if (shape == 'F') {
                return new Pipe(location, location.right(), location.down());
            }
            return null;
        }

        public boolean connects(Coord other) {
            return other.equals(a) || other.equals(b);
        }

        public Coord follow(Coord start) {
            if (start.equals(a)) {
                return b;
            } else if (start.equals(b)) {
                return a;
            } 
            return null;
        }

        @Override
        public String toString() {
            return location + "[ " + a + " <-> " + b + " ]";
        }
    }


    private static final class Coord {
        final int x, y;
        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y +")";
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        public List<Coord> neighbours() {
            return Arrays.asList(
                new Coord(x, y - 1),
                new Coord(x, y + 1),
                new Coord(x - 1, y),
                new Coord(x + 1, y)
            );
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
        public boolean equals(Object obj) {
            if (obj instanceof Coord) {
                Coord other = (Coord) obj;
                return x == other.x && y == other.y;
            }
            return false;
        }
    }

    private static void print(Map<Coord, Character> landscape) {
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
                Character value = landscape.get(new Coord(x, y));
                if (value == IN) {
                    sb.append(IN);
                } else {
                    sb.append(".");
                }
            }

            System.out.println(sb.toString());
        }
        System.out.println();
    }
}