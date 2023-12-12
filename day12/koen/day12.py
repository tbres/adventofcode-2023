import re

OPERATIONAL = '.'
DAMAGED = '#'
UNKNOWN = '?'
GROUP_RE = re.compile(r'#+')

def parse_input(path):
    rows = []
    with open(path) as f:
        for line in f.readlines():
            springs, size_string = line.strip().split(' ')
            sizes = [int(x) for x in size_string.split(',')]
            rows.append([springs, sizes])
    return rows


def get_options(springs: str):
    if UNKNOWN in springs:
        unknown_index = springs.index(UNKNOWN)
        for option in [OPERATIONAL, DAMAGED]:
            springs_option = springs[:unknown_index] + option + springs[unknown_index + 1:]
            for next_option in get_options(springs_option):
                yield next_option
    else:
        yield springs


def count_valid_options(springs_sizes):
    valid_counts = []
    for springs, sizes in springs_sizes:
        valid_count = 0
        for option in get_options(springs):
            if [len(x) for x in GROUP_RE.findall(option)] == sizes:
                valid_count += 1
        valid_counts.append(valid_count)
    return sum(valid_counts)