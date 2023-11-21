PS E:\AllAssignmentCode\javaIdea\NetworkProgramming\src>

javac ./ex3/Client/RMIClient.java
javac ./ex3/Server/RMIServer.java
java ex3.Server.RMIServer

java ex3.Client.RMIClient localhost 8000 register JYM 1234

add JYM2 2023-11-17-20:30 2023-11-17-21:30 ThisIsTestMeeting!
query 2023-11-17-20:30 2023-11-17-21:30
delete 0
clear
help
quit

java ex3.Client.RMIClient localhost 8000 delete JYM 1234 0
java ex3.Client.RMIClient localhost 8000 query JYM 1234 2023-11-16-20:30 2023-11-17-21:30
java ex3.Client.RMIClient localhost 8000 add JYM 1234 JYM2 2023-11-17-23:30 2023-11-17-23:50 TEST
java ex3.Client.RMIClient localhost 8000 clear JYM 1234