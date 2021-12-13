## Overview of the modules

### TaskManager-Core
This process runs on each processor that requires task management.
It handles starting and stopping tasks, as well as deploying files to the processors.
The TaskManager connects to the Messenger server to allow other processes to
start and stop tasks.

### Controller
This is a graphical application that connects to the TaskManager through the Messenger
server. It allows easy access to uploading, deleting, and testing tasks. It can also view the
output of the tasks for debugging them.
