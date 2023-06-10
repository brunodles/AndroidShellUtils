#!../query_interpreter/build/install/query_interpreter/bin/query_interpreter
# This script is pointing into the installDist folder
# run `gradle :query_interpreter:installDist`
# to install. Otherwise compile the fatjar using
# `gradle :query_interpreter:fatjar` and copy it into the desired dir. Replace the interpreter by the following:
# `#!/usr/bin/env java -jar ./query_interpreter-1.0.0-all.jar`

// The script can accept arguments
// Use `args[n]` where the `n` is the index
// Use `params[name]` or `params.name` to access nameed arguments. The params should be passed as `name=value`.

workingDir("./boards")
// FormatsV1: simple, markdown, csv, jira
// FormatsV2: Csv, CsvWithFooterDivider, Tsv, Simple, Terminal, Jira, JiraSpaced, JiraSpaced2,
//      JiraSpacedWithoutFooterDivider, Markdown
format(params.format ?: args[0] ?: Terminal)

// Custom functions are supported
def splitTake(String content, String separator, Integer index) {
  return tryOrNull { content.split(separator)[index] }
}

# https://groovy-lang.org/using-ginq.html#_ginq_a_k_a_groovy_integrated_query
#GQ, i.e. abbreviation for GINQ
#|__ from
#|   |__ <data_source_alias> in <data_source>
#|__ [join/innerjoin/leftjoin/rightjoin/fulljoin/crossjoin]*
#|   |__ <data_source_alias> in <data_source>
#|   |__ on <condition> ((&& | ||) <condition>)* (NOTE: `crossjoin` does not need `on` clause)
#|__ [where]
#|   |__ <condition> ((&& | ||) <condition>)*
#|__ [groupby]
#|   |__ <expression> [as <alias>] (, <expression> [as <alias>])*
#|   |__ [having]
#|       |__ <condition> ((&& | ||) <condition>)*
#|__ [orderby]
#|   |__ <expression> [in (asc|desc)] (, <expression> [in (asc|desc)])*
#|__ [limit]
#|   |__ [<offset>,] <size>
#|__ select
#    |__ <expression> [as <alias>] (, <expression> [as <alias>])*
GQ {
  from board in (
    // We can even have nested queries, this is really great!!!
    from board in database()
    select
        board.name,
        board.imagesCount,
        splitTake(board.lastChange, " ", 0) as date,
        splitTake(board.lastChange, " ", 1) as time,
        board["__sourceFile"].name as sourceFile
  )
  where board.date in ["2022.01.06", "2022.05.05"]
  orderby board.name, board.date, board.time
  select board
}
