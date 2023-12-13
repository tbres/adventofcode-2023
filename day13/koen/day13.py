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


def find_inflection_points(note):
    inflection_row = None
    inflection_column = None
    n_rows = note.shape[0]
    for i in np.arange(1, n_rows):
        width = min(i, n_rows - i)
        # TODO: slice indices using i and width
        # note[s1:s2,:]
        # note[s2:s3,:]
    return inflection_row, inflection_column