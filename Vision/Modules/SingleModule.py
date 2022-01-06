

class SingleModule:

    usbCamera = None

    def __init__(self,camID):
        self.usbCamera = USBCamera(camID)

        # Turn off auto settings for greater control
        cap = VideoCapture(camID)
        cap.set(cv2.CAP_PROP_AUTO_WB,0)
        cap.set(cv2.CAP_PROP_AUTOFOCUS,0)
        cap.set(cv2.CAP_PROP_AUTO_EXPOSURE,0)

    def run_single_camera(self,camID):
        cap = cv2.VideoCapture(camID)
        cap.set(cv2.CAP_PROP_AUTO_WB,0)
        cap.set(cv2.CAP_PROP_AUTOFOCUS,0)
        cap.set(cv2.CAP_PROP_AUTO_EXPOSURE,1)
        
        # Sets a calibration profile to calibrate the cameras on
        #calProfile = self.calibrateCameraInit()

        ret, frame = cap.read()
        if ret:
            self.setFrameShape(frame)
        else:
            print("Camera ID not found")
        prev_frame_time = 0
        new_frame_time = 0
        count = 0

        while True:
            count += 1
            if self.isclient:
                global client
                self.client.read()

            if self.experimental: # Allows values to be changed using sliders, also allows windows to be shown.
                # Constantly set the exposure of the camera to
                cap.set(cv2.CAP_PROP_EXPOSURE, cv2.getTrackbarPos("Exposure",  "Track Bars " + str(self.instanceNumber)))
            # Turn raw camera input into readable frames
            ret, frame = cap.read()

            # Calibrate every frame using the calibration profile
            #sCam, sCam = self.calibrateCamera(frame,frame,calProfile[0],calProfile[1],calProfile[2],calProfile[3])
            
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
