# SimplePing
Source Code:
https://github.com/dcarao/SimplePing

Deliverable:
SimplePing-0.0.1-SNAPSHOT.jar

How to Run:
java -jar SimplePing-0.0.1-SNAPSHOT.jar

It will use the default property file: config.properties
************* Properties Configured: config.properties
# Host name
hosts=jasmin.com,google.com
# numbes of time to execute the Ping command
times=3
# Miliseconds how often the process run
scheduleDelay=60000
# Time out for the Ping (TCP)
timeout=5000
# URL where a JSON will be sent with the report information
url=https://httpbin.org/post
# Report file
logFile=output.txt
************************************

In case you want to use a custom property file, please run indicating the path and name of the file
  java -jar SimplePing-0.0.1-SNAPSHOT.jar <<path/filename.properties>>

For example:
  java -jar SimplePing-0.0.1-SNAPSHOT.jar config2.properties

Output:
A report is created as file defined in the logFile property


