from Cameras.USBCamera import USBCamera

class SingleModule:

    camera = None

    def __init__(self,camID):
        self.camera = USBCamera(camID)

        # Turn off auto settings for greater control
        self.camera.turnOffAuto()

    def getMeasurements(self,exposure):
        self.camera.setExposure(exposure)
        frame = self.camera.getFrame()
        if frame != False:
            Xangle, Xangle2, Yangle, outputFrame = self.camera.objectDetection(frame)
        else:
            Xangle, Xangle2, Yangle, outputFrame = False
        return(Xangle, Xangle2, Yangle, outputFrame)
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