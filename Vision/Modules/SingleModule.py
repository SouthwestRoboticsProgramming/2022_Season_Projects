from .Cameras import USBCamera

class SingleModule:

    camera = None

    def __init__(self,camID):

        self.camera = USBCamera(camID,"WIN_20211116_16_05_41_Pro",self.readValues())

        # Turn off auto settings for greater control
        self.camera.turnOffAuto()

    def getMeasurements(self,settings):
        self.camera.updateSettings(settings)
        self.camera.setExposure(settings[7])
        frame = self.camera.getFrame()
        if frame is not False:
            Xangle, Xangle2, Yangle, outputFrame = self.camera.objectDetection(frame)
        else:
            Xangle, Xangle2, Yangle = False
            outputFrame = frame

        return(Xangle, Xangle2, Yangle, outputFrame)

    def readValues(self):
        settings = open('Vision/config.txt','r')
        values = settings.readlines()
        i=0
        while i <= len(values)-1:
            values[i] = values[i].strip()
            i+=1
        settings = [int(i) for i in values]
        return(settings)

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