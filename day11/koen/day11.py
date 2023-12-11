import numpy as np

MAPPING = {
    '.': 0,
    '#': 1
}


def parse_input(path):
    rows = []
    with open(path) as f:
        for line in f.readlines():
            rows.append(np.array([MAPPING.get(x) for x in line.strip()]))
    return np.vstack(rows)


def calculate_distance(a, b, empty_rows, empty_columns, empty_weight=2):
    min_row = min(a[0], b[0])
    max_row = max(a[0], b[0])
    min_column = min(a[1], b[1])
    max_column = max(a[1], b[1])
    row_distance = max_row - min_row
    empty_row_distance = sum([x in empty_rows for x in range(min_row, max_row)]) * (empty_weight - 1)
    column_distance = max_column - min_column
    empty_column_distance = sum([x in empty_columns for x in range(min_column, max_column)]) * (empty_weight - 1)
    return row_distance + empty_row_distance + column_distance + empty_column_distance
