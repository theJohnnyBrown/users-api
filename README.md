# users-api

A small example homework application

## Usage

### CLI Example

    lein run -m users-api.cli --pipes-file a.psv \
      --commas-file a.csv \
      --pipes-file b.psv \
      --spaces-file a.ssv \
      --sort birthdate

This will read in the given files, parse them appropriately, and print the
output sorted by date of birth

### Web API example

To start the API, simply do

    lein ring server

The location of a seed data file can be configured in `resources/config.edn`

To use the API, you can do

    curl -H 'content-type: text/pipe-separated' -X POST  http://localhost:3000/records -d 'Brown|Jonathan |M| Green | 1988-01-18'

to insert a pipe-separated record. Other supported content types are `text/csv`
and `text/space-separated`.

To query records, the same API supports `/records/name`, `/records/birthdate`,
and `/records/gender`. These endpoints will return JSON.

## License

Copyright © 2019 Jonathan Brown

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
