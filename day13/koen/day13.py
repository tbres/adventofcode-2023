import numpy as np

MAPPING = {
    '.' : 0,  # ash
    '#': 1  # rocks
}


def parse_input(path):
    notes = []
    with open(path) as f:
        note = []
        for line in f.readlines():
            line = line.strip()
            if line == '':
                notes.append(np.vstack(note))
                note = []
                continue
            note.append(np.array([MAPPING.get(x) for x in line]))
        if note:
            notes.append(np.vstack(note))
    return notes


def get_inflection_point(note: np.ndarray, axis: int, logger):
    dim = note.shape[axis]
    max_dim = dim - 1
    for i in np.arange(1, dim):
        logger.debug(f"  checking {i}")
        width = min(i, max_dim - i + 1)
        s1 = i - width
        s2 = i + width
        slice1 = note[s1:i,:] if axis == 0 else note[:,s1:i]
        slice2 = note[i:s2,:] if axis == 0 else note[:,i:s2]
        slice2 = np.flip(slice2, axis=axis)
        logger.debug(f"    slice {s1}:{i}")
        logger.debug(f"{slice1}")
        logger.debug(f"    slice {i}:{s2}")
        logger.debug(f"{slice2}")
        if np.all(slice1 == slice2):
            return i
    return 0

def get_inflection_points(note: np.ndarray, logger):
    logger.debug("Checking rows")
    inflection_row = get_inflection_point(note, 0, logger)
    logger.debug("Checking columns")
    inflection_column = get_inflection_point(note, 1, logger)
    return inflection_row, inflection_column
