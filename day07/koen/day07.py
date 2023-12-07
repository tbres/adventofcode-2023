import polars as pl

OPTIONS = [
    'A', 'K', 'Q', 'J', 'T', 
    '9', '8', '7', '6', '5', '4', '3', '2'
]


def parse_input(path):
    df = pl.read_csv(path, separator=' ', has_header=False, schema={'hand': pl.Utf8, 'bid': pl.Int64})
    return df


def add_rank(df: pl.DataFrame) -> pl.DataFrame:
    option_counts = [pl.col('hand').str.count_matches(x, literal=True).alias(f'o_{x}') for x in OPTIONS]
    count_columns = [f'o_{x}' for x in OPTIONS]
    df = (
        df
        .with_columns(
            pl.col('hand')
            .str.replace_all('A', 'e', literal=True)
            .str.replace_all('K', 'd', literal=True)
            .str.replace_all('Q', 'c', literal=True)
            .str.replace_all('J', 'b', literal=True)
            .str.replace_all('T', 'a', literal=True)
            .str.parse_int(16)
            .alias('hand_hex'),
            *option_counts
        )
        .with_columns(
            pl.concat_list(*count_columns)
            .list.sort(descending=True)
            .alias('option_counts')
        )
        .drop(count_columns)
        .with_columns(
            pl.col('option_counts').list.get(0).alias('count1'),
            pl.col('option_counts').list.get(1).alias('count2')
        )
        .with_columns(
            pl.when(pl.col('count1').eq(5))
            .then(pl.lit(7))
            .when(pl.col('count1').eq(4))
            .then(pl.lit(6))
            .when(pl.col('count1').eq(3) & pl.col('count2').eq(2))
            .then(pl.lit(5))
            .when(pl.col('count1').eq(3))
            .then(pl.lit(4))
            .when(pl.col('count1').eq(2) & pl.col('count2').eq(2))
            .then(pl.lit(3))
            .when(pl.col('count1').eq(2))
            .then(pl.lit(2))
            .otherwise(pl.lit(1))
            .alias('prio')
        )
        .with_columns(
            pl.struct('prio', 'hand_hex').rank(method='ordinal', descending=False).alias('rank')
        )
    )
    return df
