#!../query_interpreter/build/install/query_interpreter/bin/query_interpreter

workingDir("./boards")
format(csv)

GQ {
  from board in database()
  where board != null
  orderby board.name in desc
  limit 15
  select board.key,
         board.name,
         board.url,
         board.expectedPins,
         board.pinCount,
         board.imagesCount,
         board.enabled,
         board.lastChange
}
