import polars as pl
import re

NUMBER_RE = re.compile(r"\d+")

def extract_numbers(line):
    return [int(x) for x in NUMBER_RE.findall(line)]


def parse_input(path):
    with open(path) as f:
        times = extract_numbers(f.readline().strip())
        distances = extract_numbers(f.readline().strip())
    return pl.DataFrame([times, distances], schema=['max_time', 'distance_record'], orient='col')


def parse_input2(path):
    with open(path) as f:
        time = int(''.join([x for x in NUMBER_RE.findall(f.readline().strip())]))
        distance = int(''.join([x for x in NUMBER_RE.findall(f.readline().strip())]))
    return time, distance
