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
| `Start` | No data | Tells the lidar to begin scanning. |
| `Stop` | No data | Tells the lidar to stop scanning. |

###### Messages Sent
| Message | Data format | Description |
| --- | --- | --- |
| `Ready` | No data | Indicates that the lidar is ready to begin scanning. |
| `ScanStart` | No data | Indicates that a new round of scanning has started. |
| `Scan` | `int quality`: Quality of the measurement from 0 to 15<br/>`double angle`: Angle in counterclockwise radians<br/>`double distance`: Distance from the lidar in millimeters<br/>A quality or distance of 0 indicates an invalid scan. | One measurement from the lidar sensor. |
