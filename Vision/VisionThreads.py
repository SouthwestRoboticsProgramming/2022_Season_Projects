import cv2
import threading
import struct
import time
import math
from Modules import StereoModule
from Modules import SingleModule
from Modules.Cameras import USBCamera
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

    connection = None

    enableHub = True
    enableBallDetect = False
    enableClimber = False

    # TEMPORARY

    print(USBCamera.checkAllCameras())

    def __init__(self,threadName):

        self.connection = True
        self.client = MessengerClient("10.21.29.3", 5805, "Vision-"+str(threadName), not Constants.EXPERIMENTAL)
        self.client.set_callback(lambda type, data: self._messageCallback(type))
        self.client.listen(Constants.MESSAGE_HUB_START)
        self.client.listen(Constants.MESSAGE_HUB_STOP)
        self.client.listen(Constants.MESSAGE_BALL_DETECT_START)
        self.client.listen(Constants.MESSAGE_BALL_DETECT_STOP)
        self.client.listen(Constants.MESSAGE_CLIMBER_START)
        self.client.listen(Constants.MESSAGE_CLIMBER_STOP)

    def _messageCallback(self,type):
        print("Got message:", type)

        if type == Constants.MESSAGE_HUB_START:
            self.enableHub = True
        elif type == Constants.MESSAGE_HUB_STOP:
            self.enableHub = False
        elif type == Constants.MESSAGE_BALL_DETECT_START:
            self.enableBallDetect = True
        elif type == Constants.MESSAGE_BALL_DETECT_STOP:
            self.enableBallDetect = False
        elif type == Constants.MESSAGE_CLIMBER_START:
            self.enableClimber = True
        elif type == Constants.MESSAGE_CLIMBER_STOP:
            self.enableClimber = False
        else:
            print("Unknown message", type)

    def _hubModule(self,camID,hubDiameter):
        settings = self.readValues("hubSettings")


        while not self.enableBallDetect:
            time.sleep(1/50.0)
            self.client.read()

        if Constants.EXPERIMENTAL:
            self._createTrackbars("Hub Module ID: " + str(camIDL) + ", " + str(camIDR))
        module = StereoModule(camIDL,camIDR,baseline,settings)

        while self.enableHub:

            time.sleep(1/50.0)

            if Constants.EXPERIMENTAL:
                settings = self._getTrackbars("Hub Module ID: " + str(camIDL) + ", " + str(camIDR))

            globalPose,localPose, outputFrame = module.getMeasurements(settings)

            if self.connection:
                data = None
                if not isinstance(globalPose, str):
                    data = struct.pack(">?dd",True,localPose[0],localPose[2])
                else:
                    data = struct.pack(">?",False)
                self.client.send_message("Vision:Hub_Measurements", data)
                self.client.read()

            if Constants.EXPERIMENTAL:
                cv2.imshow(str("Hub Module ID: " + str(camIDL) + ", " + str(camIDR)),outputFrame)
                cv2.waitKey(1)

                if cv2.waitKey(1) & 0xFF == ord('1'):
                    break
        
        module.release()

    def _climberModule(self,camID):

        while not self.enableClimber:
            time.sleep(1/50.0)
            self.client.read()

        settings = self.readValues("climberSettings")

        if Constants.EXPERIMENTAL:
            self._createTrackbars("Climber Camera ID: " + str(camID))
        module = SingleModule(camID,settings)
        while self.enableClimber:
            time.sleep(1/50.0)

            if Constants.EXPERIMENTAL:
                settings = self._getTrackbars("Climber Camera ID: " + str(camID))

            Xangle, Xangle2, Yangle, frame = module.getMeasurements(settings)

            if self.connection:
                data = None
                if not Xangle is False:

                    data = struct.pack(">?ddd", True, Xangle, Xangle2, Yangle)
                else:
                    data = struct.pack(">?", False)
                self.client.send_message("Vision:Climber_Angles", data)
                self.client.read()

            # TODO: Listen for stop message

            if Constants.EXPERIMENTAL:
                cv2.imshow(str("Climber Camera ID: " + str(camID)),frame)
                cv2.waitKey(1)

                if cv2.waitKey(1) & 0xFF == ord('1'):
                    break
        
        module.release()

    def _ballDetectionModule(self,camIDL,camIDR,baseline):
        settings = self.readValues("ballDetectionSettings")


        while not self.enableBallDetect:
            time.sleep(1/50.0)
            self.client.read()

        if Constants.EXPERIMENTAL:
            self._createTrackbars("Ball Detection Module ID: " + str(camIDL) + ", " + str(camIDR))
        module = StereoModule(camIDL,camIDR,baseline,settings)

        while self.enableBallDetect:

            time.sleep(1/50.0)

            print("ball")

            if Constants.EXPERIMENTAL:
                settings = self._getTrackbars("Ball Detection Module ID: " + str(camIDL) + ", " + str(camIDR))

            globalPose,localPose, outputFrame = module.getMeasurements(settings)

            if self.connection:
                data = None
                if not isinstance(globalPose, str):
                    data = struct.pack(">?ddd",True,localPose[0],localPose[1],localPose[2])
                else:
                    data = struct.pack(">?",False)
                self.client.send_message("Vision:Ball_Position", data)
                self.client.read()

            if Constants.EXPERIMENTAL:
                cv2.imshow(str("Ball Detection Module ID: " + str(camIDL) + ", " + str(camIDR)),outputFrame)
                cv2.waitKey(1)

                if cv2.waitKey(1) & 0xFF == ord('1'):
                    break
        
        module.release()

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
            cv2.waitKey(1)

    def _getTrackbars(self,instanceName):
            settings = [0,0,0,0,0,0,0,0,0]
            settings[0] = cv2.getTrackbarPos("Hue Min",str(instanceName) + " Track Bars")
            settings[1] = cv2.getTrackbarPos("Hue Max",str(instanceName) + " Track Bars")
            settings[2] = cv2.getTrackbarPos("Saturation Min",str(instanceName) + " Track Bars")
            settings[3] = cv2.getTrackbarPos("Saturation Max",str(instanceName) + " Track Bars")
            settings[4] = cv2.getTrackbarPos("Value Min",str(instanceName) + " Track Bars")
            settings[5] = cv2.getTrackbarPos("Value Max",str(instanceName) + " Track Bars")
            settings[6] = cv2.getTrackbarPos("Thresh Low", str(instanceName) + " Track Bars")
            settings[7] = cv2.getTrackbarPos("Exposure",str(instanceName) + " Track Bars")

            return(settings)

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
    thread = threading.Thread(target=_runClimberThread, args=(camID,))
    return(thread)

#   * I hade to make these functions outside of the class because threads can't use self *
def _runHubThread(camID,hubDiameter):
    module = VisionThreads("Hub-ID-"+str(camID))
    module._hubModule(camID,hubDiameter)

def _runBallDetectionThread(camIDL,camIDR,baseline):
    module = VisionThreads("Ball_Detection-ID-"+str(camIDL)+","+str(camIDR))
    module._ballDetectionModule(camIDL,camIDR,baseline)

def _runClimberThread(camID):
    module = VisionThreads("Climber-ID-"+str(camID))
    module._climberModule(camID)