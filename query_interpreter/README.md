# Query Interpreter
This is a sql-like search on data files and present it on terminal using predefined formats ( simple, markdown, csv, jira). 

## Context
After implement a interpreter for the [file_query] using the groovy, I discovered that the language itself has a library
that does the same thing, and it is ready!
So I just decided to implement a way to grab the inputs, run user [GQ](https://groovy-lang.org/using-ginq.html) and 
present it using the [table_builder].

This is very similar to [file_query_interpreter_groovy], but is already better, because it is using the GQ 
implementation.

This module is a Groovy-only, it better for dealing with dynamic runtime and decoding JSONs, since the groovy works
really well with dynamic properties.

# Next steps
* [ ] Support other input formats
  * [ ] YAML
  * [ ] CSV
  * [ ] TSV
  * [ ] XML
* [ ] Pass the fatjar through proguard. _Not sure if it would relly work, based on runtime/reflection properties of Groovy_
* [ ] Create a way to "import" other script file. It will allow users to share the extra functions on a separate file.
* [ ] [maybe] Improve the `database` function to accept parameters
