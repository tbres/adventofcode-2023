import re

MAP_NAME_RE = re.compile(r"(?P<source>[a-z]+)-to-(?P<target>[a-z]+) map:")
NUMBER_RE = re.compile(r"\d+")


def extract_numbers(line):
    return [int(x) for x in NUMBER_RE.findall(line)]


def parse_input(path):
    mapping = {}
    with open(path) as f:
        seeds = extract_numbers(f.readline().strip())
        map_name = None
        map_ranges = []
        for line in f.readlines():
            line = line.strip()
            if not line:
                if map_name:
                    mapping[map_name] = map_ranges
                map_ranges = []
                continue
            if MAP_NAME_RE.match(line):
                map_match = MAP_NAME_RE.match(line)
                if map_match:
                    map_name = (map_match.group("source"), map_match.group("target"))
                continue
            map_ranges.append(extract_numbers(line))
    if map_name:
        mapping[map_name] = map_ranges
    return seeds, mapping



class MappingRange():

    def __init__(self, target_min: int, source_min: int, range_length: int):
        self.source_min = source_min
        self.source_max = source_min + range_length
        self.target_min = target_min
        self.target_max = target_min + range_length

    def in_range(self, source_value: int) -> bool:
        return source_value >= self.source_min and source_value < self.source_max

    def map(self, source_value: int) -> int:
        if self.in_range(source_value):
            return self.target_min + source_value - self.source_min
        return None
    

class Mapping():

    def __init__(self, source, target, mapping_ranges, logger):
        self.source = source
        self.target = target
        self.mapping_ranges: list[MappingRange] = [MappingRange(*x) for x in mapping_ranges]
        self.mapping_ranges.sort(key=lambda x: x.source_min)
        self.logger = logger

    def map(self, source_value) -> int:
        for mapping_range in self.mapping_ranges:
            target_value = mapping_range.map(source_value)
            if target_value is not None:
                return target_value
        return source_value
    
    def map_all(self, source_values):
        mapped_values = []
        for source_value in source_values:
            mapped_value = self.map(source_value)
            self.logger.debug(f"  {source_value} to {mapped_value}")
            mapped_values.append(mapped_value)
        return mapped_values
    
    def map_range(self, source_range):
        mapped_ranges = []
        source_min, source_max = source_range
        for mapping_range in self.mapping_ranges:
            target_min = None
            target_max = None
            reset_source_min = False
            if mapping_range.in_range(source_min) or mapping_range.in_range(source_max):
                target_min = mapping_range.map(source_min)
                if mapping_range.in_range(source_max):
                    target_max = mapping_range.map(source_max)
                else:
                    target_max = mapping_range.target_max - 1
                    reset_source_min = True
            if target_min is not None:
                mapped_ranges.append((target_min, target_max))
            if reset_source_min:
                source_min = mapping_range.source_max
        if not mapped_ranges:
            mapped_ranges.append(source_range)
        return mapped_ranges
    

    def map_all_ranges(self, source_ranges):
        mapped_values = []
        for source_range in source_ranges:
            mapped_ranges = self.map_range(source_range)
            self.logger.debug(f"  {source_range} to {mapped_ranges}")
            mapped_values.extend(mapped_ranges)
        mapped_values.sort(key=lambda x: x[0])
        return mapped_values

class MappingGraph():

    def __init__(self, mapping: dict, logger):
        self.mapping_graph = {k[0]: Mapping(*k, v, logger) for k, v in mapping.items()}
        self.logger = logger

    def do_mapping(self, target, source_values):
        mapping = self.mapping_graph.get(target)
        while mapping:
            self.logger.info(f"Mapping {mapping.source} to {mapping.target}")
            source_values = mapping.map_all(source_values)
            mapping = self.mapping_graph.get(mapping.target)
        return source_values
    
    def do_range_mapping(self, target, source_ranges):
        mapping = self.mapping_graph.get(target)
        while mapping:
            self.logger.info(f"Mapping {mapping.source} to {mapping.target}")
            source_ranges = mapping.map_all_ranges(source_ranges)
            mapping = self.mapping_graph.get(mapping.target)
        return source_ranges
