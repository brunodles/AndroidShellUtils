#!./build/install/file_query_interpreter_groovy/bin/file_query_interpreter_groovy
# This script is pointing into the installDist folder
# run `gradle :file_query_interpreter_groovy:installDist`
# to install it

select {
    field("key")
    field("name")
    field("imagesCount")
    field("pinCount")
    field("pins") { it.count() }
    field("enabled")
    field("lastChange")
#    file()
}
from { file.absolutePath.contains("boards") }
where {
    field["name"].contains("card")
}
tablePresentation {
    add("key")
    add("name")
    add("imagesCount", right)
    add("pinCount", right)
    add("realPinCount", right)
    add("enabled")
    add("lastChange")
#    add("file")
}