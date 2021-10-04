# Monospace Table Builder
Utility to create monospace tables using kotlin.
This could help to create tables for terminal application.
Can also format then into other formats like MarkDown and Jira.


## Usage
```kotlin
val table = TableBuilder(FormatDefault.simple) // or FormatDefault.markdown 
    .columns {
        add("col1", ColumnDirection.left)
        add("col2", ColumnDirection.center)
        add("col3", ColumnDirection.right)
    }
    .newRow {
        add("r1")
        add("r2")
        add("r3")
    }
    .newRow {
        add("r1")
        add("r2")
        add("r3")
    }
    .build()
```

Simple Result
```
col1 | col2 | col3
---- | ---- | ----
r1   |  r2  |   r3
r1   |  r2  |   r3
```

MarkDown
```
| col1 |  col2 | col3 |
| :--- | :---: | ---: |
| r1   |   r2  |   r3 |
| r1   |   r2  |   r3 |
```
