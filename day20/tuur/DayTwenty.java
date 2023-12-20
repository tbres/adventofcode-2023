package day20.tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class DayTwenty {
    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(DayTwenty.class.getResource("input-day20.txt").toURI()));

        Map<String, Module> modules = parse(lines);
        
        PulseCounter counter = new PulseCounter();

        for(int i = 0; i < 1000; i++) {
            push(modules, counter);
        }
        System.out.println(counter);
        System.out.println("Part 1: " + (counter.high * counter.low));

        /**************************************************************/

        modules = parse(lines);
        counter = new PulseCounter();
        
        long pushes = 0l;

        Sink rx = (Sink) modules.get("rx");
        while (rx.off()) {
            pushes++;
            push(modules, counter);
        }
        System.out.println("Part 2: " + pushes);
    }

    private static void push(Map<String, Module> modules, PulseCounter counter) {
        List<Signal> signalsToProcess = new ArrayList<>();
        signalsToProcess.add(new Signal("button", "broadcaster", Pulse.low));

        while (!signalsToProcess.isEmpty()) {
            List<Signal> outputSignals = new ArrayList<>();

            for (Signal signal : signalsToProcess) {
                counter.count(signal.pulse);

                Module target = modules.get(signal.to);
                outputSignals.addAll(target.accept(signal));
            }

            signalsToProcess = outputSignals;
        }
    }

    private static Map<String, Module> parse (List<String> lines) {
        Map<String, Module> modules = new HashMap<>();

        List<Conjunction> conjunctions = new ArrayList<>();
        for (String line : lines) {
            Module module = parseLine(line);
            modules.put(module.name, module);

            if (module instanceof Conjunction) {
                Conjunction conjunction = (Conjunction) module;
                conjunctions.add(conjunction);
            }
        }

        for (Conjunction conjunction : conjunctions) {
            for (Module module : modules.values()) {
                if (module.destinations.contains(conjunction.name)) {
                    conjunction.inputStates.put(module.name, Pulse.low);
                }
            }
        }

        for (Module module : new ArrayList<Module>(modules.values())) {
            for (String dest : module.destinations) {
                if (!modules.containsKey(dest)) {
                    modules.put(dest, new Sink(dest));
                }
            }
        }

        return modules;
    }

    private static Module parseLine(String line) {
        String[] splitted = line.split("->");

        String name = splitted[0].trim();

        List<String> destinations =  Arrays.stream(splitted[1].split(","))
                                            .map(String::trim)
                                            .collect(Collectors.toList());

        if ("broadcaster".equals(name)) {
            return new Broadcaster(name, destinations);
        } else if (name.startsWith(PREFIX_CONJUNCTION)) {
            return new Conjunction(name.substring(1), destinations);
        } else if (name.startsWith(PREFIX_FLIPFLOP)) {
            return new FlipFlop(name.substring(1), destinations);
        } else {
            throw new IllegalArgumentException("Can't parse line: " + line);
        }
    }

    private static final class PulseCounter {
        long high = 0;
        long low = 0;

        public void count(Pulse pulse) {
            if (Pulse.high.equals(pulse)) {
                high++;
            } else {
                low++;
            }
        }
        
        @Override
        public String toString() {
            return "Counter: low = " + low + ", high = "  + high;
        }
    }

    private static final class Signal {
        final String from, to;
        final Pulse pulse;

        public Signal(String from, String to, Pulse pulse) {
            this.from = from;
            this.to = to;
            this.pulse = pulse;
        }

        @Override
        public String toString() {
            return from +  " -" + pulse  + "-> " + to;
         }
    }

    private static enum Pulse {
        low, high;
    }

    public static abstract class Module {

        final String name;

        final List<String> destinations = new ArrayList<>();

        public Module(String name, List<String> destinations) {
            this.name = name;
            this.destinations.addAll(destinations);
        }

        abstract List<Signal> accept(Signal signal);
    }

    public static final class Broadcaster extends Module {

        public Broadcaster(String name, List<String> destinations) {
            super(name, destinations);
        }
        
        List<Signal> accept(Signal signal) {
            return destinations.stream()
                    .map(dest -> new Signal(name, dest, signal.pulse))
                    .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return name  + " -> " + destinations;
        }
    }

    public static final String PREFIX_CONJUNCTION = "&";

    public static final class Conjunction extends Module {

        final Map<String, Pulse> inputStates = new HashMap<>();

        public Conjunction(String name, List<String> destinations) {
            super(name, destinations);
        }
        
        List<Signal> accept(Signal signal) {
            inputStates.put(signal.from, signal.pulse);

            Pulse output = determineOutput();
            return destinations.stream()
                    .map(dest -> new Signal(name, dest, output))
                    .collect(Collectors.toList());
        }

        private Pulse determineOutput() {
            for (Pulse value : inputStates.values()) {
                if (Pulse.low.equals(value)) {
                    return Pulse.high;
                }
            }
            return Pulse.low;
        }

        @Override
        public String toString() {
            return "Conjunction " + name + " (" + inputStates + ") -> " + destinations;
        }
    }

    public static final String PREFIX_FLIPFLOP = "%";

    public static final class FlipFlop extends Module {

        boolean state = false; //OFF

        public FlipFlop(String name, List<String> destinations) {
            super(name, destinations);
        }
        
        List<Signal> accept(Signal signal) {
            if (Pulse.low.equals(signal.pulse)) {

                Pulse output;
                if (state) {
                    state = false;
                    output = Pulse.low;
                } else {
                    state = true;
                    output =Pulse.high;
                }

                return destinations.stream()
                    .map(dest -> new Signal(name, dest, output))
                    .collect(Collectors.toList());
            }

            return Collections.emptyList();
        }

        @Override
        public String toString() {
            return "FlipFlop " + name + " (" + state + ") -> " + destinations;
        }
    }

    public static final class Sink extends Module {
        public boolean state = false; //OFF

        public Sink(String name) {
            super(name, Collections.emptyList());
        }

        @Override
        List<Signal> accept(Signal signal) {
            if (Pulse.low.equals(signal.pulse)) {
                state = true;
            }
            return Collections.emptyList();
        }

        public boolean state() {
            return state;
        }

        public boolean off() {
            return state == false;
        }

        @Override
        public String toString() {
            return "Sink " + name;
        }
    }
}