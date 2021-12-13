# 2022 Season Projects

This is where we will keep our projects that we work on this season.

# Current Projects

### RobotVisualizer
A graphical program that allows visualizing of values inside the robot.
These values can include things such as predicted position, obstacle detection, and pathfinding paths.

### TaskManager
A set of libraries that make working with coprocessors easier.
Allows starting, stopping, and deploying tasks to coprocessors.
See the readme in TaskManager for more details.

### Messenger
A service that allows processes throughout the robot across different processors to
communicate and send messages to each other. This service only needs to run on one processor,
and all of the other processes communicate through the one instance.

### Vision
Uses OpenCV to manipulate webcam inputs. This has been implemented to find vision targets and balls as well as using two cameras to find depth to any object.

### Lidar
A library and a task for interfacing with a RPLidar A1M8 lidar sensor. The API is fully asynchronous to make it easier to incorporate into tasks because it does not own the main loop.

### Pathfinding
A library that allows the robot to autonomously calculate paths through the environment. This pathfinding is based on a grid, so small openings might be missed.

### Robot
A simplified version of the mini robot code for testing things.
