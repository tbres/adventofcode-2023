import polars as pl
import re

NUMBER_RE = re.compile(r"(\d+)")
SYMBOL_RE = re.compile(r"[^0-9\.]{1}")


class PartNumber():

    def __init__(self, value: str, row: int, column: int):
        self.value = int(value)
        self.id = f"{row}-{column}"
        self.row = row
        self.min_column = column
        self.max_column = column + len(value) - 1

    def __repr__(self) -> str:
        return f"Part({self.value}, {self.row}, {self.min_column}-{self.max_column})"
    
    def _get_adjacent_df(self, df: pl.DataFrame) -> pl.DataFrame:
        row_range = [self.row - 1, self.row, self.row + 1]
        column_range = pl.arange(self.min_column - 1, self.max_column + 2, eager=True)
        adjacent_df = (
            df
            .filter(
                pl.col('row').is_in(row_range)
                & pl.col('column').is_in(column_range)
            )
        )
        return adjacent_df

    def next_to_symbol(self, symbol_df: pl.DataFrame) -> bool:
        adjacent_df = self._get_adjacent_df(symbol_df)
        return not adjacent_df.is_empty()
    
    def get_adjacent_gears(self, gear_df: pl.DataFrame) -> list:
        adjacent_df = self._get_adjacent_df(gear_df)
        return adjacent_df['gear_id'].to_list()


def parse_row(row):
    line: str = row[0]
    numbers = [(line.index(x), x) for x in NUMBER_RE.findall(line)]
    numbers = []
    is_number_range = False
    number_idx = None
    number = ''
    last_index = len(line) - 1
    for idx, x in enumerate(line):
        is_number = NUMBER_RE.match(x)
        if is_number and not is_number_range:
            number_idx = idx
            number += x
            is_number_range = True
        elif is_number and is_number_range:
            number += x
            if idx == last_index:
                numbers.append((number_idx, number))
        else:
            is_number_range = False
            if number:
                numbers.append((number_idx, number))
            number = ''

    symbols = [(idx, x) for idx, x in enumerate(line) if SYMBOL_RE.match(x)]
    return numbers, symbols


def parse_input(path):
    df = pl.read_csv(path, separator='\t', has_header=False, schema={'raw': pl.Utf8})
    numbers = []
    symbols = []
    for row_index, row in enumerate(df.iter_rows()):
        row_numbers, row_symbols = parse_row(row)
        for number in row_numbers:
            numbers.append(PartNumber(value=number[1], row=row_index, column=number[0]))
        for symbol in row_symbols:
            symbols.append({
                'symbol': symbol[1],
                'row': row_index,
                'column': symbol[0] 
            })
    symbol_df = pl.DataFrame(symbols)
    return numbers, symbol_df
