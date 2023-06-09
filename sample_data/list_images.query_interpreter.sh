#!../query_interpreter/build/install/query_interpreter/bin/query_interpreter
# This script is pointing into the installDist folder
# run `gradle :query_interpreter:installDist`
# to install. Otherwise compile the fatjar using
# `gradle :query_interpreter:fatjar` and copy it into the desired dir. Replace the interpreter by the following:
# `#!/usr/bin/env java -jar ./query_interpreter-1.0.0-all.jar`

workingDir("./images")
// Formats: simple, markdown, csv, jira
format(params.format ?: args[0] ?: markdown)

GQ {
  from image in (
    // We can even have nested queries, this is really great!!!
    from image in database()
    select image.key,
        image.extension,
        image.size.width as width,
        image.size.height as height,
        image.borderColors.mostCommon as commonColor,
        image.namedSwatches.vibrantSwatch as vibrantColor,
        image.swatchesColors.random() as randomColor
  )
  where image.extension == "jpg"
  orderby image.width, image.height
  select image
}
