rem Start 2 network nodes, then terminate them
start java DatabaseNode -tcpport 9000 -record 1:1 
timeout 2 > NUL
start java DatabaseNode -tcpport 9003 -connect localhost:9000 -record 2:2 
timeout 2 > NUL
java DatabaseClient -gateway localhost:9000 -operation terminate
java DatabaseClient -gateway localhost:9003 -operation terminate
