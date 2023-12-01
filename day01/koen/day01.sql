/*
 * 
 * Day 01
 * 
 * */

-- read raw input
create or replace table raw_input(raw varchar);
copy raw_input from 'C:\Repos\EXT\adventofcode-2023\day01\koen\data\input.txt' (auto_detect true);
select * from raw_input;

/*
 * Part 1
 * */

-- extract first and list number
create table input_value as
select
    raw,
    cast(regexp_extract(raw, '\d{1}') || regexp_extract(reverse(raw), '\d{1}') as integer) as value
from
    main.raw_input;

-- do sum
select
    sum(value)
from
    main.input_value;

/* Part 2
 * */

-- find first last
create or replace table input_firstlast as
select
    raw,
    regexp_extract(raw, '\d{1}|(one)|(two)|(three)|(four)|(five)|(six)|(seven)|(eight)|(nine)') || regexp_extract(reverse(raw), '\d{1}|(eno)|(owt)|(eerht)|(ruof)|(evif)|(xis)|(neves)|(thgie)|(enin)') as value
from
    main.raw_input;
select * from main.input_firstlast;

-- replace reverse numbers
create or replace table input_replaced_reverse as
select
    raw,
    regexp_replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(value, 'eno$', '1'), 'owt$', '2'), 'eerht$', '3'), 'ruof$', '4'), 'evif$', '5'), 'xis$', '6'), 'neves$', '7'), 'thgie$', '8'), 'enin$', '9') as value
from
    main.input_firstlast;
select * from main.input_replaced_reverse;
-- replace normal numbers
create or replace table input_replaced as
select
    raw,
    replace(replace(replace(replace(replace(replace(replace(replace(replace(value, 'one', '1'), 'two', '2'), 'three', '3'), 'four', '4'), 'five', '5'), 'six', '6'), 'seven', '7'), 'eight', '8'), 'nine', '9') as value
from
    main.input_replaced_reverse;
select * from main.input_replaced;


-- do sum
create or replace table input_replaced_value as
select
    raw,
    cast(value as integer) as value
from
    main.input_replaced;
select * from main.input_replaced_value;

select
    sum(value)
from
    main.input_replaced_value;


    