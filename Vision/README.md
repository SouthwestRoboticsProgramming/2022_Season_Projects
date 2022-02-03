# Vision Processing
### Adding a thread
Use either the getSingleCamThread() or the getStereoThread() function to assign the thread to a variable. Then all you have to do is call thatVariable.start(). I suggest setting up all of the cameras that you want to use in Constants.py. Turn experimental on if you want to control the slideers and see the camera feeds. DO NOT USE EXPERIMENTAL AT COMPETITION.

### Camera settings
The settings for the cameras is found in config.txt. The order for these settings is:
1. Hue Min
2. Hue Max
3. Saturation Min
4. Saturation Max
5. Value Min
6. Value Max
7. Threshold (Keep this at a low value)
8. Exposure (Varies greatly from camera to camera)
