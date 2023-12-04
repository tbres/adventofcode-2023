import polars as pl
import re

NUMBER_RE = re.compile(r"\d+")


def parse_row(row):
    card, numbers = row[0].split(':')
    winning, ours = numbers.split('|')
    return {
        'card': int(NUMBER_RE.findall(card)[0]),
        'winning': [int(x) for x in NUMBER_RE.findall(winning)],
        'ours': [int(x) for x in NUMBER_RE.findall(ours)],
    }


def parse_input(path):
    df = pl.read_csv(path, separator='\t', has_header=False, schema={'raw': pl.Utf8})
    extacted_dicts = [parse_row(x) for x in df.iter_rows()]
    df = pl.DataFrame(extacted_dicts)
    return df
