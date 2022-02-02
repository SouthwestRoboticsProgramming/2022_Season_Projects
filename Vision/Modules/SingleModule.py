from .Cameras import USBCamera

class SingleModule:

    camera = None

    def __init__(self,camID,settings):

        self.camera = USBCamera(camID,"new-checkerboard",settings)

        # Turn off auto settings for greater control
        self.camera.turnOffAuto()

    def getMeasurements(self,settings):
        self.camera.updateSettings(settings)
        self.camera.setExposure(settings[7])
        frame = self.camera.getFrame()
        if frame is not False:
            Xangle, Xangle2, Yangle, outputFrame = self.camera.objectDetection(frame)
        else:
            Xangle, Xangle2, Yangle = False, False, False
            outputFrame = frame

        return(Xangle, Xangle2, Yangle, outputFrame)

    def release(self):
        self.camera.release()
        return
'''
    def run_single_camera(self,camID):

        


            
            # Use ball detection function to find the angle to center and right side of the object in both cameras
            Xangle, Yangle, Xangle2 = self.objectDetection(frame,camID)

            if Xangle != "Obstructed" and self.isclient:
                data = struct.pack(">f", Xangle)
                self.client.send_message("Vision:Xangle", data)

            # Get fps of video feed with processing time
            new_frame_time = time.time()
            fps = 1/(new_frame_time-prev_frame_time)
            prev_frame_time = new_frame_time
            #print(fps)

            # Creating 'q' as the quit button for the webcam
            if cv2.waitKey(1) & 0xFF == ord('q'):
                cap.release()
                return()
'''