# WSO2 Extension Siddhi IO WSO2Event

###Siddhi WSO2Event Source
WSO2Event source is used to receive events in the WSO2Event format via Thrift or binary protocols. By default it uses the following ports to retrieve events.

For Thrift:
 
 - TCP port:7611 
 - SSL port:7711

For Binary: 
 
 - TCP port:9611 
 - SSL port:9711

Use the tcp://<HOSTNAME>:<PORT>  and  ssl://<HOSTNAME>:<PORT> URLs to send events to the server as follows.

Use the following format for load-balancing: 
{tcp://HOSTNAME:PORT,tcp://hostname:PORT, ...}

Use the following format for failover:  
{tcp://HOSTNAME:PORT|tcp://hostname:PORT| ...}

Use the following format to send messages to more than one cluster of endpoints (cluster is defined using "{}") : 
{tcp://HOSTNAME:PORT|tcp://hostname:PORT| ...},{tcp://HOSTNAME:PORT} 

In the above format, the event is delivered to one endpoint on the first cluster of endpoints in a failover manner. Also, the same message is delivered to the endpoint defined in the second cluster.

###Siddhi WSO2Event Sink

WSO2Event source handles WSO2 events. It sends WSO2 events over Thrift using TCP, SSL/ TCP, HTTP, and HTTPS protocols to any external server, which can receive them.

## API Docs:

1. <a href="./api/4.0.1-SNAPSHOT">4.0.1-SNAPSHOT</a>
