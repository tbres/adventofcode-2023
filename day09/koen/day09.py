
def parse_line(line):
    return [int(x) for x in line.split(' ')]


def parse_input(path):
    with open(path) as f:
        for line in f.readlines():
            yield parse_line(line)


def extrapolate(history, logger, reverse=False):
    if reverse:
        history.reverse()
    result = []
    logger.debug(f"Checking {history}")
    while not all([x == 0 for x in history]):
        result.append(history[-1])
        history = [history[i + 1] - history[i] for i in range(len(history) - 1)]
        logger.debug(f"    {history}")
    logger.debug(f"  {result}")
    return sum(result)
