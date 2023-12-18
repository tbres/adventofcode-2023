package day18.tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DayEighteen {

    public static final String REGEX_STRING = "(?<direction>[RLUD])\\s(?<amount>\\d+)\\s\\(#(?<color>\\w+)\\)";

    public static final Pattern REGEX = Pattern.compile(REGEX_STRING);

    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(DayEighteen.class.getResource("input-day18.txt").toURI()));

        System.out.println("Part 1: " + shoelaceArea(dig(parseLinesPart1(lines))));
        System.out.println("Part 2: " + shoelaceArea(dig(parseLinesPart2(lines))));
    }

    private static long shoelaceArea(List<Coord> corners) {
        long circumference = 0l;
        long result = 0;

        for (int i = 1; i < corners.size(); i++) {
            Coord first = corners.get(i - 1);
            Coord second = corners.get(i);

            long area = first.xLong() * second.yLong() - second.xLong() * first.yLong();
            long length = Math.abs(first.x - second.x + first.y - second.y);

            circumference += length;
            result += area;
        }

        return Math.abs(result) / 2 + circumference / 2 + 1;
    }

    private static List<Coord> dig(List<Action> plan) {
        List<Coord> corners = new ArrayList<>();
        Coord currentPosition = new Coord(0,0);

        corners.add(currentPosition); //This will ease our life later for the shoelace algorithm.

        for (Action command : plan) {
            if (Direction.up.equals(command.direction)) {
                currentPosition = new Coord(currentPosition.x, currentPosition.y - command.amount);
            } else if (Direction.down.equals(command.direction)) {
                currentPosition = new Coord(currentPosition.x, currentPosition.y + command.amount);
            } else if (Direction.left.equals(command.direction)) {
                currentPosition = new Coord(currentPosition.x - command.amount, currentPosition.y);
            } else {
                currentPosition = new Coord(currentPosition.x + command.amount, currentPosition.y);
            }
            corners.add(currentPosition);   
        }

        return corners;
    }

    private static List<Action> parseLinesPart1(List<String> lines) {
        List<Action> result = new ArrayList<>();
        for (String line : lines) {

            Matcher matcher = REGEX.matcher(line);
            if (matcher.matches()) {
                String direction = matcher.group("direction");
                Integer amount = Integer.valueOf(matcher.group("amount"));
                String color = matcher.group("color");

                if ("U".equals(direction)) {
                    result.add(new Action(Direction.up, amount));
                } else if ("D".equals(direction)) {
                    result.add(new Action(Direction.down, amount));
                } else if ("R".equals(direction)) {
                    result.add(new Action(Direction.right, amount));
                } else {
                    result.add(new Action(Direction.left, amount));
                }
            }
        }

        return result;
    }

    private static List<Action> parseLinesPart2(List<String> lines) {
        List<Action> result = new ArrayList<>();
        for (String line : lines) {

            Matcher matcher = REGEX.matcher(line);
            if (matcher.matches()) {
                String color = matcher.group("color");

                String amountHex = color.substring(0, 5);
                String direction = color.substring(5); 

                int amount = Integer.valueOf(amountHex, 16);

                if ("0".equals(direction)) {
                    result.add(new Action(Direction.right, amount));
                } else if ("1".equals(direction)) {
                    result.add(new Action(Direction.down, amount));
                } else if ("2".equals(direction)) {
                    result.add(new Action(Direction.left, amount));
                } else {
                    result.add(new Action(Direction.up, amount));
                }
            }
        }

        return result;
    }

    private static final class Action {
        final Direction direction;
        final int amount;

        public Action(Direction direction, int amount) {
            this.direction = direction;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return direction + " " + amount;
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

        public long xLong() {
            return Integer.valueOf(x).longValue();
        }

        public long yLong() {
            return Integer.valueOf(y).longValue();
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