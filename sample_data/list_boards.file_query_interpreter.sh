#!../file_query_interpreter_groovy/build/install/file_query_interpreter_groovy/bin/file_query_interpreter_groovy
# This script is pointing into the installDist folder
# run `gradle :file_query_interpreter_groovy:installDist`
# to install. Otherwise compile the fatjar using
# `gradle :file_query_interpreter_groovy:fatjar` and copy it into the desired dir. Replace the interpreter by the following:
# `#!/usr/bin/env java -jar ./file_query_interpreter_groovy-1.0.0-all.jar`

select {
  field("key")
  field("name")
  field("enabled")
  field("pinCount")
  field("imagesCount")
  field("lastChange")
}
from { file.parentFile.name == "boards" }
where {
    field("pinCount") eq 0
}