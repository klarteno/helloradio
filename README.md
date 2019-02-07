# helloradio <br/>

# Run (also for protobuf generation) :

sbt compile <br/>

sbt runAll <br/>
browse localhost:9000 for JSON endpoints <br/>

sbt test

sbt clean <br/>
sbt cleanFiles <br/>
sbt cleanKeepFiles <br/>

curl -d '{"id":1000,"alias":"fghfghfgh","allowedLocations":["lisabona"]' -H "Content-Type: application/json" -X POST http://localhost:9000/toLowercase

curl -d '{"id":1000,"alias":"fghfghfgh","allowedLocations":["lisabona"]}' -H "Content-Type: "application/octet-stream"" -X POST http://localhost:9000/toLowercase
