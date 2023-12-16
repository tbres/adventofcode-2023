package day14.tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;

public class DayFourteen {

    public static void main(String[] args) throws Exception{
        List<String> lines = Files.readAllLines(Paths.get(DayFourteen.class.getResource("input-day14.txt").toURI()));
        
        Map<Coord, Character> map = parseMap(lines);
        final int maxY = map.keySet().stream().mapToInt(Coord::getY).max().getAsInt();

        move(map, Direction.north);
        System.out.println("Part 1: " + load(map, maxY));

        /*****************************************/
        System.out.println();
        System.out.println("-----------------------------------------------------------");
        System.out.println(" Part 2");
        System.out.println("-----------------------------------------------------------");
        System.out.println();

        map = parseMap(lines);
        
        Direction[] directions = new Direction[] {Direction.north, Direction.west, Direction.south, Direction.east};
        
        
        
        
        Map<Map<Coord, Character>, Integer> stateByStep = new HashMap<>();
        stateByStep.put(new HashMap<>(map), 0);
        
        int count = 1;
        while (count < 1000) {
            for (Direction direction : directions) {
                move(map, direction);
            }

            Integer previousOccurence = stateByStep.get(map);
            if (previousOccurence != null) {
                System.out.println("Loop detected at iteration " + count);
                System.out.println("Previous occurence: " + previousOccurence);
                System.out.println("Interval: " + (count - previousOccurence));

                int resultState = previousOccurence + ((1000_000_000 - previousOccurence) % (count - previousOccurence));
                int score = stateByStep.entrySet().stream().filter(entry -> entry.getValue() == resultState).map(Entry::getKey).map(m -> load(m, maxY)).findAny().get();
                System.out.println(resultState + " -> " + score );
                break;
            } else {
                stateByStep.put(new HashMap<>(map), count);
            }
            count++;
        }

    }

    private static int load(Map<Coord, Character> map, int maxY) {
        return  map.entrySet().stream()
            .filter(entry -> 'O' == entry.getValue())
            .map(Entry::getKey)
            .mapToInt(coord -> (maxY + 1) - coord.y)
            .sum();
    }

    private static void move(Map<Coord, Character> input, Direction direction) {
        boolean change = true;
        while (change) {
            change = false;
            for (Entry<Coord, Character> entry: input.entrySet()) {
                if ('O' == entry.getValue()) {
                    Coord coord = entry.getKey();
                    Coord north = direction.mapping.apply(coord);
                    if ('.' == input.getOrDefault(north, '#')) {
                        input.put(coord, '.');
                        input.put(north, 'O');
                        change = true;
                    }
                }
            }
        }
    }

    private static enum Direction {

        north(c -> c.up()),
        south(c -> c.down()),
        west(c -> c.left()),
        east(c -> c.right());

        private final UnaryOperator<Coord> mapping;

        private Direction(UnaryOperator<Coord> mapping) {
            this.mapping = mapping;
        }
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
            System.out.println(sb.toString());
        }
        System.out.println();
    }

}
