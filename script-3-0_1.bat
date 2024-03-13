rem Start 3 network nodes, then terminate them
start java DatabaseNode -tcpport 9000 -record 1:1 
timeout 2 > NUL
start java DatabaseNode -tcpport 9007 -connect localhost:9000 -record 2:2 
timeout 2 > NUL
start java DatabaseNode -tcpport 9002 -connect localhost:9000 -connect localhost:9007 -record 3:3
timeout 2 > NUL
java DatabaseClient -gateway localhost:9000 -operation terminate
java DatabaseClient -gateway localhost:9007 -operation terminate
java DatabaseClient -gateway localhost:9002 -operation terminate

