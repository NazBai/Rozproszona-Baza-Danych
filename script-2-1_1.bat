rem Start 2 network nodes, find min/max and then terminate them
start java DatabaseNode -tcpport 9000 -record 1:1 
timeout 2 > NUL
start java DatabaseNode -tcpport 9006 -connect localhost:9000 -record 2:2 
timeout 1 > NUL

java DatabaseClient -gateway localhost:9000 -operation get-max
java DatabaseClient -gateway localhost:9000 -operation get-min
timeout 1 > NUL

java DatabaseClient -gateway localhost:9000 -operation terminate
java DatabaseClient -gateway localhost:9006 -operation terminate
