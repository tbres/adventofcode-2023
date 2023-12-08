import re

INSTRUCTIONS_MAPPING = {
    'L': 0,
    'R': 1
}
DIRECTION_RE = re.compile(r'(?P<f>.+) = \((?P<l>.+), (?P<r>.+)\)')

def parse_input(path):
    directions = {}
    with open(path) as f:
        instructions = [INSTRUCTIONS_MAPPING.get(x) for x in f.readline() if INSTRUCTIONS_MAPPING.get(x) is not None]
        f.readline()
        for line in f.readlines():
            line = line.strip()
            match = DIRECTION_RE.match(line)
            if match:
                _from = match.group('f')
                left = match.group('l')
                right = match.group('r')
                directions[_from] = (left, right)
    return instructions, directions