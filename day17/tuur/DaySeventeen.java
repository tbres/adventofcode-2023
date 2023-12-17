package day17.tuur;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;

public class DaySeventeen {
    public static void main(String[] args) throws Exception{
        List<String> lines = Files.readAllLines(Paths.get(DaySeventeen.class.getResource("input-day17.txt").toURI()));

        Map<Coord, Integer> cityBlocks = parseMap(lines);
        final int minX = cityBlocks.keySet().stream().mapToInt(Coord::getX).min().getAsInt();
        final int maxX = cityBlocks.keySet().stream().mapToInt(Coord::getX).max().getAsInt();
        final int minY = cityBlocks.keySet().stream().mapToInt(Coord::getY).min().getAsInt();
        final int maxY = cityBlocks.keySet().stream().mapToInt(Coord::getY).max().getAsInt();

        final Coord start = new Coord(0,0);
        final Coord end = new Coord(maxX, maxY);

        History ROOT = new History(null, null, null, start);
        
        Map<History, Integer> lowestHeatLoss = new HashMap<>();
        Set<History> paths = new HashSet<>();
        paths.add(ROOT);
        lowestHeatLoss.put(ROOT, 0);

        while (!paths.isEmpty()) {
            Set<History> next = new HashSet<>();
            for (History path : paths) {
                // System.out.println();
                // System.out.println(path);
                for (Coord coord : path.four.neighbours()) {
                    if (coord.x < minX || coord.x > maxX 
                        || coord.y < minY || coord.y > maxY) {
                            //OUT OF BOUNDS
                        //    System.out.println(coord + " -> Out of bounds") ;
                    } else if (path.three != null && coord.equals(path.three)) {
                        // NO GOING BACK!
                        // System.out.println(coord + " -> Can't go back") ;
                    } else if (path.one != null
                        && ((coord.x == path.one.x
                                && coord.x == path.two.x
                                && coord.x == path.three.x
                                && coord.x == path.four.x)
                            || 
                            (coord.y == path.one.y
                                && coord.y == path.two.y
                                && coord.y == path.three.y
                                && coord.y == path.four.y))) {
                        // Can go straight for more than 3 blocks
                        // System.out.println(coord + " -> Can't go straight for more than 3 blocks") ;
                    } else {
                        // System.out.println(coord + " -> Loop detected");
                        History history = path.next(coord);
                        int heatLoss = lowestHeatLoss.get(path) + cityBlocks.get(coord);

                        int best = lowestHeatLoss.getOrDefault(history, Integer.MAX_VALUE);
                        
                        if (heatLoss < best) {
                            lowestHeatLoss.put(history, heatLoss);
                            next.add(history);
                        } else {
                            // System.out.println(coord + " -> Dropping path: " + p.heatLoss + "  >= " + best) ;
                        }
                    }
                }
            } 

            paths = next;   
        }

        System.out.println();
        System.out.println("Part 1: " + lowestHeatLoss.entrySet().stream()
            .filter(entry -> entry.getKey().four.equals(end))
            .mapToInt(Entry::getValue)
            .min().getAsInt());
            

    }

    public static final class History {
        final Coord one, two, three, four;

        public History(Coord one, Coord two, Coord three, Coord four) {
            this.one = one;
            this.two = two;
            this.three = three;
            this.four = four;
        }

        public History next(Coord coord) {
            return new History(two, three, four, coord);
        }

        @Override
        public String toString() {
            return one + " -> " + two + " -> " + three + " -> " + four ;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof History) {
                History other = (History) obj;
                return one.equals(other.one)
                    && two.equals(other.two)
                    && three.equals(other.three)
                    && four.equals(other.four);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(one, two, three, four);
        }
    }

    public static final class Step {
        final Coord from, to;

        public Step(Coord from, Coord to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return from + " -> " + to;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Step) {
                Step other = (Step) obj;
                return from.equals(other.from) && to.equals(other.to);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    public static final class Path {

        final Coord coord;

        final Path previous;

        final int hashCode;

        final int length;

        final int heatLoss;

        public Path(Path previous, Coord coord, int heatLoss) {
            this.previous = previous;
            this.coord = coord;
            this.hashCode = Objects.hash(previous, coord);
            this.length = (previous == null) ? 1 : previous.length + 1;
            this.heatLoss = (previous == null) ? 0 : previous.heatLoss + heatLoss;
        }

        public boolean containsStep(Coord from, Coord to) {
            Path current = this;
            while (current != null && current.previous != null) {
                if (current.coord.equals(to) && current.previous.coord.equals(from)) {
                    return true;
                }
                current = current.previous;
            }
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Path) {
                Path other = (Path) obj;
                return coord.equals(other.coord) && previous.equals(other.previous);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            Path current = this;
            while(current != null) {
                sb.insert(0, current.coord + " -> ");
                current = current.previous;
            }
            return sb.toString();
        }

    }

    private static Map<Coord, Integer> parseMap(List<String> lines) {
        Map<Coord, Integer> result = new HashMap<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                Character shape = line.charAt(x);
                result.put(new Coord(x, y), Integer.valueOf("" + shape));
                
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

    private static void print(Map<Coord, Integer> landscape) {
        final int minX = 0; // landscape.keySet().stream().mapToInt(Coord::getX).min().getAsInt();
        final int maxX = landscape.keySet().stream().mapToInt(Coord::getX).max().getAsInt();
        final int minY = 0; // landscape.keySet().stream().mapToInt(Coord::getY).min().getAsInt();
        final int maxY = landscape.keySet().stream().mapToInt(Coord::getY).max().getAsInt();
        System.out.println();
        for(int y = minY; y <= maxY; y++) {
            StringBuilder sb = new StringBuilder();
            for(int x = minX ; x <= maxX; x++) {
                int value = landscape.getOrDefault(new Coord(x, y), 0);
                if(value < 10) {
                    sb.append(" ");
                }
                if(value < 100) {
                    sb.append(" ");
                }
                sb.append(value);
                sb.append(" ");
            }
            System.out.println(sb.toString());
        }
        System.out.println();
    }

}
