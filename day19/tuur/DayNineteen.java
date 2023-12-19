package day19.tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DayNineteen {

    public static final String RE_FIRST = "^[a-zA-Z]+\\{.*\\}$";

    public static final String RE_PART = "^\\{x=(?<x>\\d+),m=(?<m>\\d+),a=(?<a>\\d+),s=(?<s>\\d+)\\}$";

    public static final Pattern PATTERN_PART = Pattern.compile(RE_PART);

    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(DayNineteen.class.getResource("input-day19.txt").toURI()));

        Map<String, Workflow> workflows = lines.stream()
            .filter(s -> s.matches(RE_FIRST))
            .map(DayNineteen::parseWorkflow)
            .collect(Collectors.toMap(Workflow::name, i -> i));

        List<Part> parts = lines.stream()
            .filter(s -> s.matches(RE_PART))
            .map(DayNineteen::parsePart)
            .collect(Collectors.toList());

        List<Part> accepted = new ArrayList<>();
        List<Part> rejected = new ArrayList<>();

        parts.forEach(part -> process(part, workflows, accepted, rejected));

        System.out.println("Part 1: " + accepted.stream().mapToLong(Part::rating).sum());
    }

    private static void process(Part part, Map<String, Workflow> workflows, List<Part> accepted, List<Part> rejected) {
        Workflow wf = workflows.get("in");

        while (wf != null) {
            String result = wf.apply(part);
            if ("A".equals(result)) {
                accepted.add(part);
                return;
            } else if ("R".equals(result)) {
                rejected.add(part);
                return;
            } else {
                wf = workflows.get(result);
            }
        }
    }

    // {x=787,m=2655,a=1222,s=2876}
    private static Part parsePart(String line) {
        Matcher m = PATTERN_PART.matcher(line);
        if (m.matches()) {
            return new Part(
                Integer.parseInt(m.group("x")), 
                Integer.parseInt(m.group("m")),
                Integer.parseInt(m.group("a")),
                Integer.parseInt(m.group("s"))
            );
        }
        throw new IllegalArgumentException("Bad part: " + line);
    }

    // ex{x>10:one,m<20:two,a>30:R,A}
    private static Workflow parseWorkflow(String line) {
        String workflowName = line.substring(0, line.indexOf('{'));
        String impl = line.substring(line.indexOf('{') + 1, line.length() - 1);
        String[] split = impl.split(",");

        List<Condition> conditions = new ArrayList<>();
        for (int i = 0; i < split.length - 1 ; i++) {
            String s = split[i];

            int opIdx = s.indexOf(">");
            if (opIdx == -1) {
                opIdx = s.indexOf("<");
            }

            String cat = s.substring(0, opIdx);
            char op = s.charAt(opIdx);
            String val = s.substring(opIdx + 1, s.indexOf(":"));
            String res = s.substring(s.indexOf(":") + 1);
            conditions.add(new Condition(
                    Category.valueOf(cat), 
                    Operation.valueOf(op), 
                    Integer.parseInt(val), 
                    res
                )
            );
        }

        String def = split[split.length - 1];
        return new Workflow(workflowName, conditions, def);
    }


    private static enum Category {
        x, m, a, s;
    }

    private static enum Operation {
        lt('<'), 
        gt('>');

        final char representation;

        Operation(char representation) {
            this.representation = representation;
        }

        public static Operation valueOf(char c) {
            for (Operation op : Operation.values()) {
                if (op.representation == c) {
                    return op;
                }
            }
            throw new IllegalArgumentException("Unknown operation: " + c);
        }
    }

    public static final class Part {
        final int x, m, a, s;

        public Part(int x, int m, int a, int s) {
            this.x = x;
            this.m = m;
            this.a = a;
            this.s = s;
        }

        public int get(Category category) {
            switch (category) {
                case x: return x;
                case m: return m;
                case a: return a;
                case s: return s;
                default:
                    throw new RuntimeException();
            }
        }

        public long rating() {
            return x + m + a + s;
        }

        @Override
        public String toString() {
            return "{x=" + x + ", m=" + m + ", a=" + a + ", s=" + s + "}";
        }
    }

    public static final class Workflow {
        final String name;
        final List<Condition> conditions;
        final String defaultResult;

        public Workflow(String name, List<Condition> conditions, String defaultResult) {
            this.name = name;
            this.conditions = new ArrayList<>(conditions);
            this.defaultResult = defaultResult;
        }

        public String apply(Part part) {
            for (Condition condition : conditions) {
                if (condition.matches(part)) {
                    return condition.result;
                }
            }
            return defaultResult;
        }

        public String name() {
            return name;
        }

        @Override
        public String toString() {
            return name + "{" + conditions + "," + defaultResult + "}";
        }
    }

    public static final class Condition {
        final Category category;
        final Operation operation;
        final int value;
        final String result;

        public Condition(Category category, Operation operation, int value, String result) {
            this.category = category;
            this.operation = operation;
            this.value = value;
            this.result = result;
        }

        public boolean matches(Part part) {
            int partValue = part.get(category);
            if (Operation.gt.equals(operation)) {
                return partValue > value;
            } else {
                return partValue < value;
            }
        }

        @Override
        public String toString() {
            return category + "" +  operation.representation + "" + value + ":" + result;   
        }
    
    }

}