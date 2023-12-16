package day16.tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DaySixteen {


    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(DaySixteen.class.getResource("input-day16.txt").toURI()));

        Map<Coord, Character> map = parseMap(lines);
        final int maxX = map.keySet().stream().mapToInt(Coord::getX).max().getAsInt();
        final int maxY = map.keySet().stream().mapToInt(Coord::getY).max().getAsInt();


        long count = getEnergizedCount(new Step(new Coord(-1, 0), Direction.right), map, maxX, maxY);
        System.out.println("Part 1: " + count);

        List<Long> counts = new ArrayList<>();
        for (int x = 0; x <= maxX; x++) {
            counts.add(getEnergizedCount(new Step(new Coord(x, -1), Direction.down), map, maxX, maxY));
            counts.add(getEnergizedCount(new Step(new Coord(x, maxY + 1), Direction.up), map, maxX, maxY));
        }
        for (int y = 0; y <= maxY; y++) {
            counts.add(getEnergizedCount(new Step(new Coord(-1, y), Direction.right), map, maxX, maxY));
            counts.add(getEnergizedCount(new Step(new Coord(maxX + 1, y), Direction.left), map, maxX, maxY));
        }
        long max = counts.stream().mapToLong(i -> i).max().getAsLong();
        System.out.println("Part 2: " + max);
    }


    private static long getEnergizedCount(Step start, Map<Coord, Character> map, final int maxX, final int maxY) {
        Set<Step> visited = new HashSet<>();
        List<Step> steps = new ArrayList<>();
        steps.add(start);
        while (!steps.isEmpty()) {
            List<Step> nextSteps = new ArrayList<>();
            
            for (Step step : steps) {
                final Direction direction = step.direction;
                Coord next = step.coord.neighbour(direction);
                Character charzy = map.get(next);
                
                if (visited.contains(step)) {
                    // DO NOTHING, we're in a loop
                } else if (next.x < 0 || next.x > maxX || next.y < 0 || next.y > maxY) {
                    // DO NOTHING, out of bounds
                } else if ('.' == charzy) {
                    nextSteps.add(new Step(next, direction));
                } else if ('|' == charzy) {
                    if (Direction.left.equals(direction) || Direction.right.equals(direction)) {
                        nextSteps.add(new Step(next, Direction.up));
                        nextSteps.add(new Step(next, Direction.down));
                    } else {
                        nextSteps.add(new Step(next, direction));
                    }
                } else if ('-' == charzy) {
                    if (Direction.up.equals(direction) || Direction.down.equals(direction)) {
                        nextSteps.add(new Step(next, Direction.left));
                        nextSteps.add(new Step(next, Direction.right));
                    } else {
                        nextSteps.add(new Step(next, direction));
                    }
                } else if ('\\' == charzy) {
                    if (Direction.up.equals(direction)) {
                        nextSteps.add(new Step(next, Direction.left));
                    } else if (Direction.down.equals(direction)) {
                        nextSteps.add(new Step(next, Direction.right));
                    } else if (Direction.left.equals(direction)) {
                        nextSteps.add(new Step(next, Direction.up));
                    } else {// if (Direction.right.equals(direction)) {
                        nextSteps.add(new Step(next, Direction.down));
                    }
                } else if ('/' == charzy) {
                    if (Direction.up.equals(direction)) {
                        nextSteps.add(new Step(next, Direction.right));
                    } else if (Direction.down.equals(direction)) {
                        nextSteps.add(new Step(next, Direction.left));
                    } else if (Direction.left.equals(direction)) {
                        nextSteps.add(new Step(next, Direction.down));
                    } else {// if (Direction.right.equals(direction)) {
                        nextSteps.add(new Step(next, Direction.up));
                    }
                } else {
                    throw new RuntimeException("What happened here?");
                }

            }
            visited.addAll(steps);
            steps = nextSteps;
        }
        long count = visited.stream()
            .map(step -> step.coord)
            .filter(coord -> coord.x >= 0 && coord.x <= maxX && coord.y >= 0 && coord.y <= maxY)
            .distinct()
            .count();
        return count;
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

    private static final class Step {
        final Coord coord;
        final Direction direction;

        public Step(Coord c, Direction d) {
            coord = c;
            direction = d;
        }
        @Override
        public int hashCode() {
            return Objects.hash(coord, direction);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Step) {
                Step other = (Step) obj;
                return coord.equals(other.coord)
                    && direction.equals(other.direction);
            }
            return false;
        }

        @Override
        public String toString() {
            return coord.toString() + " : " + direction.name();
        }
    }

    private static enum Direction {
        up, down, left, right;
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
        public boolean equals(Object obj) {
            if (obj instanceof Coord) {
                Coord other = (Coord) obj;
                return x == other.x && y == other.y;
            }
            return false;
        }
    }


}
