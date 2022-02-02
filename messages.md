# Message Data Documentation

### RoboRIO
Runs on the RoboRIO. This is the normal robot code.

###### Messages Read
No messages read

###### Messages Sent
| Message | Data format | Description |
| --- | --- | --- |
| `Location` | `double x`: X position in meters<br/>`double y`: Y position in meters<br/>`double angle`: Angle in counterclockwise radians<br/>All measurements are relative to the starting position. | The predicted location of the robot from the localization system. |

### Lidar
Runs on the Raspberry Pi.

###### Messages Read
| Message | Data format | Description |
| --- | --- | --- |
| `Lidar:Start` | No data | Tells the lidar to begin scanning. |
| `Lidar:Stop` | No data | Tells the lidar to stop scanning. |

###### Messages Sent
| Message | Data format | Description |
| --- | --- | --- |
| `Lidar:Ready` | No data | Indicates that the lidar is ready to begin scanning. |
| `Lidar:ScanStart` | No data | Indicates that a new round of scanning has started. |
| `Lidar:Scan` | `int quality`: Quality of the measurement from 0 to 15<br/>`double angle`: Angle in counterclockwise radians<br/>`double distance`: Distance from the lidar in millimeters<br/>A quality or distance of 0 indicates an invalid scan. | One measurement from the lidar sensor. |

### TaskManager
Runs on all processors that need task management. (Raspberry Pi and Jetson Nano)

The prefix for the Raspberry Pi is `RPi` and the prefix for the Jetson Nano is `Nano`.

###### Messages Read
| Message | Data format | Description |
| --- | --- | --- |
| `[prefix]:Start` | `string task`: The task to start | Starts the given task. |
| `[prefix]:Stop` | `string task`: The task to stop | Stops the given task. |
| `[prefix]:Delete` | `string task`: The task to delete | Deletes all files of the given task. |
| `[prefix]:Upload` | `string task`: The task to upload<br/>`int payloadLength`: The length of the payload in bytes<br/>`byte[] payload`: The task data payload as a ZIP archive | Uploads files as a new task. If the specified task already exists, it will be deleted and replaced with the new one. |
| `[prefix]:GetTasks` | No data | Gets the names of all the tasks on the TaskManager. The TaskManager will respond with a `[prefix]:Tasks` message. |
| `[prefix]:IsRunning` | `string task`: The task to check running status | Gets whether a task is running. The TaskManager will respond with a `[prefix]:Running` message. }

###### Messages Sent
| Message | Data format | Description |
| --- | --- | --- |
| `[prefix]:StdOut:[task]` | `string message`: The output line from the task. | A line from the standard output of a task. |
| `[prefix]:StdErr:[task]` | `string message`: The error output line from the task. | A line from the standard error of a task. |
| `[prefix]:Tasks ` | `int count`: The number of task names following<br/>`string[] name`: The names of each task | Response for a list of all task names on the TaskManager. |
| `[prefix]:Running ` | `string task`: The name of the task<br/>`boolean running`: Whether the task is running | Response for whether a task is running. |


### Vision

###### Messages Read
| Message | Data format | Description |
| --- | --- | --- |
| `Vision:HubStart` | No data | Tells the hub vision camera to start detecting. |
| `Vision:BallDetectStart` | No data | Tells the stereo ball detection module to start detecting. |
| `Vision:ClimberStart` | No data | Tells the climber vision camera to start detecting. |
| `Vision:HubStop` | No data | Tells the hub vision camera to stop detecting. |
| `Vision:BallDetectStop` | No data | Tells the stereo ball detection module to stop detecting. |
| `Vision:ClimberStop` | No data | Tells the climber vision camera to stop detecting. |

###### Messages Sent
| Message | Data format | Description |
| --- | --- | --- |
| `Vision:Hub_Measurements` | `bool obscured`: Indicates if an object has been found.<br/>`double Xangle`: X angle in degrees {-fov,fov} to center of target.<br/>`double distance`: Distance to the target, same units as target width.<br/>| The relative position of the robot compared to the vision target. |
| `Vision:Climber_Angle` | `bool obscured`: Indicates if an object has been found.<br/>`double Yangle`: Y angle in degrees {-fov,fov} to the bar.| The relative position of the robot compared to the vision target. |
| `Vision:Ball_Position` | `bool obscured`: Indicates if an object has been found.<br/>`double x`: X position of the robot to center of target.<br/>`double z`: Z position of the robot to the center of the target.<br/>|The relative position of a ball to the robot. |
