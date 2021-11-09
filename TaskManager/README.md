## Overview of the modules

### TaskManager-Server
The process that runs once on the coprocessor.
This process should always be running on the coprocessor (i.e. run as a service)

### TaskManager-Client
The library used from the main robot on the RoboRIO.
Allows the RoboRIO to control the tasks running on the coprocessor.
Only one client can connect to the server at a time, so the RoboRIO
can be connected, or the Controller, but not both at once.

### Task-Client
The library used in each task to communicate with the RoboRIO through the task manager.
This library is intentionally made very simple to allow easy porting to other
languages that tasks can be written in.
