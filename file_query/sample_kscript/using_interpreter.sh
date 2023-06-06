#!./interpreter.sh test this file


database.newQuery {
    select(
        { field["key"] },
        { field["name"] },
        { field["imagesCount"] },
        { field["pinCount"] },
        { field["pins"].count() },
        { field["enabled"] },
        { field["lastChange"] },
        { file }
    )
    from { file.absolutePath.contains("boards") }
    where {
        field["name"] contains "card"
    }
}.present {
    add("key")
    add("name")
    add("imagesCount", right)
    add("pinCount", right)
    add("realPinCount", right)
    add("enabled")
    add("lastChange")
    add("file")
}.println()