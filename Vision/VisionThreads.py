import cv2
import threading
import struct
import time
import math
from Modules import StereoModule
from Modules import SingleModule
from Constants import Constants
from messengerclient import MessengerClient

class VisionThreads:
    camID = None
    client = None
    
    h_min = 5
    h_max = 255
    s_min = 5
    s_max = 255
    v_min = 5
    v_max = 250
    TLow = 0
    exposure = 2

    def __init__(self):

        # TODO: DO a try catch or something for this
        self.client = MessengerClient("10.21.29.3", 8341, "Vision")
    """
    def _singleCamModule(self,camID):
        self.readValues()
        if Constants.EXPERIMENTAL:
            self._createTrackbars()

        
        module = SingleModule(camID)
        while True: # TODO: Find a better way to loop

            time.sleep(1000/50.0)

            settings = [self.h_min,self.h_max,self.s_min,self.s_max,self.v_min,self.v_max,self.TLow,self.exposure]
            Xangle, Xangle2, Yangle, frame = module.getMeasurements(settings)

            #print(Xangle, Xangle2, Yangle)

            data = None
            if not Xangle is False:
                data = struct.pack(">?ddd", True, Xangle, Xangle2, Yangle)
            else:
                data = struct.pack(">?", False)
            self.client.send_message("Vision:Angles", data)

            self.client.read()

            # TODO: Send these angles to messanger client
            if Constants.EXPERIMENTAL:
                self._getTrackbars()
                cv2.imshow(str(self.instanceName) + " Camera",frame)
                cv2.waitKey(1)
            # TODO: Calculate fps and add it to the frame
            # TODO: DO the obstructed thing (It'll be False if it is obstructed)

                if cv2.waitKey(1) & 0xFF == ord('q'):
                    module.release()
                    return()
    """

    def _hubModule(self,camID,hubDiameter):
        settings = self.readValues("hubSettings")

        if Constants.EXPERIMENTAL:
            self._createTrackbars("Hub Camera ID: " + str(camID))

        module = SingleModule(camID)
        while True:
            time.sleep(1000/50.0)

            if Constants.EXPERIMENTAL:
                settings = self._getTrackbars("Hub Camera ID: " + str(camID))

            Xangle, Xangle2, Yangle, frame = module.getMeasurements(settings)

            data = None
            if not Xangle is False:

                diffAngle = Xangle2 - Xangle
                distance = (.5 * hubDiameter) / math.tan(diffAngle)

                data = struct.pack(">?dd", True, Xangle, distance)
            else:
                data = struct.pack(">?", False)
            # TODO: Do a try catch to makc sure that we can connect
            self.client.send_message("Vision:Hub_Measurements", data)
            self.client.read()

            # TODO: Listen for stop message

            if Constants.EXPERIMENTAL:
                cv2.imshow(str("Hub Camera ID: " + str(camID)))
                cv2.waitKey(1)

                if cv2.waitKey(1) & 0xFF == ord('1'):
                    module.release()
                    return()

    def _climberModule(self,camID):
        settings = self.readValues("climberSettings")

        if Constants.EXPERIMENTAL:
            self._createTrackbars("Climber Camera ID: " + str(camID))

        module = SingleModule(camID)
        while True:
            time.sleep(1000/50.0)

            if Constants.EXPERIMENTAL:
                settings = self._getTrackbars("Climber Camera ID: " + str(camID))

            Xangle, Xangle2, Yangle, frame = module.getMeasurements(settings)

            data = None
            if not Xangle is False:

                data = struct.pack(">?d", True, Yangle)
            else:
                data = struct.pack(">?", False)
            # TODO: Do a try catch to makc sure that we can connect
            self.client.send_message("Vision:Climber_Angles", data)
            self.client.read()

            # TODO: Listen for stop message

            if Constants.EXPERIMENTAL:
                cv2.imshow(str("Climber Camera ID: " + str(camID)))
                cv2.waitKey(1)

                if cv2.waitKey(1) & 0xFF == ord('1'):
                    module.release()
                    return()

            


    """
    def _stereoModule(self,camIDL,camIDR,baseline,settings):
        if Constants.EXPERIMENTAL:
            self._createTrackbars()
        
        module = StereoModule(camIDL,camIDR,baseline)

        while True: # TODO: Find a better way to loop

            time.sleep(1000/50.0)

            globalPose,localPose,outputFrame = module.getMeasurements(settings)

            # TODO: Send these angles to messenger client

            data = None
            if not isinstance(globalPose, str):
                data = struct.pack(">?ddddd", True, globalPose[0], globalPose[1], localPose[0], localPose[1], localPose[2])
            else:
                data = struct.pack(">?", False)
            self.client.send_message("Vision:Stereo_Position", data)

            self.client.read()


            if Constants.EXPERIMENTAL:
                cv2.imshow(str(self.instanceName) + " Module")
                cv2.waitKey(1)

                # Creating 'q' as the quit button for the webcam
                if cv2.waitKey(1) & 0xFF == ord('q'):
                    module.release()
                    return()

            # TODO: Calculate fps and add it to the frame
            # TODO: DO the obstructed thing (It'll be False if it is obstructed)
    """

    def _ballDetectionModule(self,camIDL,camIDR,baseline):
        settings = self.readValues("ballDetectionSettings")

        if Constants.EXPERIMENTAL:
            self._createTrackbars("Ball Detection Module ID: " + str(camIDL) + ", " + str(camIDR))

        module = StereoModule(camIDL,camIDR,baseline)

        while True:

            time.sleep(1000/50.0)

            if Constants.EXPERIMENTAL:
                settings = self._getTrackbars("Ball Detection Module ID: " + str(camIDL) + ", " + str(camIDR))

            globalPose,localPose, outputFrame = module.getMeasurements(settings)

            data = None
            if not isinstance(globalPose, str):
                data = struct.pack(">?dd",True,localPose[0],localPose[2])
            else:
                data = struct.pack(">?",False)
            self.client.send_message("Vision:Ball_Position", data)
            self.client.read()

            if Constants.EXPERIMENTAL:
                cv2.imshow(str("Ball Detection Module ID: " + str(camIDL) + ", " + str(camIDR)))
                cv2.waitKey(1)

                if cv2.waitKey(1) & 0xFF == ord('1'):
                    module.release()
                    return()



    def _createTrackbars(self,instanceName):
            cv2.namedWindow(str(instanceName) + " Track Bars")
            cv2.resizeWindow(str(instanceName) + " Track Bars", 1000,500)
            cv2.createTrackbar("Hue Min",str(instanceName) + " Track Bars",self.h_min,179,self._empty)
            cv2.createTrackbar("Hue Max",str(instanceName) + " Track Bars",self.h_max,179,self._empty)
            cv2.createTrackbar("Saturation Min",str(instanceName) + " Track Bars",self.s_min,255,self._empty)
            cv2.createTrackbar("Saturation Max",str(instanceName) + " Track Bars",self.s_max,255,self._empty)
            cv2.createTrackbar("Value Min",str(instanceName) + " Track Bars",self.v_min,255,self._empty)
            cv2.createTrackbar("Value Max",str(instanceName) + " Track Bars",self.v_max,255,self._empty)
            cv2.createTrackbar("Thresh Low", str(instanceName) + " Track Bars", self.TLow , 255, self._empty)
            cv2.createTrackbar("Exposure",str(instanceName) + " Track Bars", self.exposure,1000, self._empty)

    def _getTrackbars(self,instanceName):
            settings = None
            settings[0] = cv2.getTrackbarPos("Hue Min",str(instanceName) + " Track Bars")
            settings[1] = cv2.getTrackbarPos("Hue Max",str(instanceName) + " Track Bars")
            settings[2] = cv2.getTrackbarPos("Saturation Min",str(instanceName) + " Track Bars")
            settings[3] = cv2.getTrackbarPos("Saturation Max",str(instanceName) + " Track Bars")
            settings[4] = cv2.getTrackbarPos("Value Min",str(instanceName) + " Track Bars")
            settings[5] = cv2.getTrackbarPos("Value Max",str(instanceName) + " Track Bars")
            settings[6] = cv2.getTrackbarPos("Thresh Low", str(instanceName) + " Track Bars")
            settings[7] = cv2.getTrackbarPos("Exposure",str(instanceName) + " Track Bars")

    def readValues(self,configFile):
        lines = open('./Settings/' + str(configFile) + '.txt','r')
        values = lines.readlines()
        i=0
        while i <= len(values)-1:
            values[i] = values[i].strip()
            i+=1
        settings = [int(i) for i in values]
        return(settings)

    def _empty(self,a):
        # This function doens't do anything but is required for creating trackbars
        pass

def getHubVisionThread(camID,hubDiameter):
    thread = threading.Thread(target=_runHubThread, args=(camID,hubDiameter))
    return(thread)

def getBallDetectionThread(camIDL,camIDR,baseline):
    thread = threading.Thread(target=_runBallDetectionThread, args=(camIDL,camIDR,baseline))
    return(thread)

def getClimberThread(camID):
    thread = threading.Thread(target=_runClimberThread, args=(camID))
    return(thread)



#   * I hade to make these functions outside of the class because threads can't use self *
def _runHubThread(camID,hubDiameter):
    module = VisionThreads()
    module._hubModule(camID,hubDiameter)

def _runBallDetectionThread(camIDL,camIDR,baseline):
    module = VisionThreads()
    module._ballDetectionModule(camIDL,camIDR,baseline)

def _runClimberThread(camID):
    module = VisionThreads()
    module._climberModule(camID)