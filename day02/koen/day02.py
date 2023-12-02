import polars as pl
import re

GAME_RE = re.compile(r"Game (\d+):")
BLUE_RE = re.compile(r"(\d+)(?= blue)")
RED_RE = re.compile(r"(\d+)(?= red)")
GREEN_RE = re.compile(r"(\d+)(?= green)")


def parse_row(row):
    return {
        'game': int(GAME_RE.findall(row[0])[0]),
        'blue': [int(x) for x in BLUE_RE.findall(row[0])],
        'red': [int(x) for x in RED_RE.findall(row[0])],
        'green': [int(x) for x in GREEN_RE.findall(row[0])],
    }


def parse_input(path):
    df = pl.read_csv(path, separator='|', has_header=False, schema={'raw': pl.Utf8})
    extacted_dicts = [parse_row(x) for x in df.iter_rows()]
    df = pl.DataFrame(extacted_dicts)
    return df
