Overview of the modules:

TaskManager-Server:
  The process that runs once on the coprocessor.

TaskManager-Client:
  The library used from the main robot on the RoboRIO. Allows the RoboRIO
  to control the tasks running on the coprocessor.

Task-Client:
  The library used in each task to communicate with the RoboRIO through the
  task manager.