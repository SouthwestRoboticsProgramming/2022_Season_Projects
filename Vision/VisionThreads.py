import cv2
import threading
import struct
import time
from Modules import StereoModule
from Modules import SingleModule
from Constants import Constants
from messengerclient import MessengerClient

class VisionThreads:
    camID = None
    instanceName = None
    client = None
    
    h_min = 5
    h_max = 255
    s_min = 5
    s_max = 255
    v_min = 5
    v_max = 250
    TLow = 0
    exposure = 2

    def __init__(self,instanceName):
        self.instanceName = instanceName
        self.client = MessengerClient("10.21.29.3", 8341, "Vision")

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



    def _createTrackbars(self):
            cv2.namedWindow(str(self.instanceName) + " Track Bars")
            cv2.resizeWindow(str(self.instanceName) + " Track Bars", 1000,500)
            cv2.createTrackbar("Hue Min",str(self.instanceName) + " Track Bars",self.h_min,179,self._empty)
            cv2.createTrackbar("Hue Max",str(self.instanceName) + " Track Bars",self.h_max,179,self._empty)
            cv2.createTrackbar("Saturation Min",str(self.instanceName) + " Track Bars",self.s_min,255,self._empty)
            cv2.createTrackbar("Saturation Max",str(self.instanceName) + " Track Bars",self.s_max,255,self._empty)
            cv2.createTrackbar("Value Min",str(self.instanceName) + " Track Bars",self.v_min,255,self._empty)
            cv2.createTrackbar("Value Max",str(self.instanceName) + " Track Bars",self.v_max,255,self._empty)
            cv2.createTrackbar("Thresh Low", str(self.instanceName) + " Track Bars", self.TLow , 255, self._empty)
            cv2.createTrackbar("Exposure",str(self.instanceName) + " Track Bars", self.exposure,1000, self._empty)

    def _getTrackbars(self):
            self.h_min = cv2.getTrackbarPos("Hue Min",str(self.instanceName) + " Track Bars")
            self.h_max = cv2.getTrackbarPos("Hue Max",str(self.instanceName) + " Track Bars")
            self.s_min = cv2.getTrackbarPos("Saturation Min",str(self.instanceName) + " Track Bars")
            self.s_max = cv2.getTrackbarPos("Saturation Max",str(self.instanceName) + " Track Bars")
            self.v_min = cv2.getTrackbarPos("Value Min",str(self.instanceName) + " Track Bars")
            self.v_max = cv2.getTrackbarPos("Value Max",str(self.instanceName) + " Track Bars")
            self.TLow = cv2.getTrackbarPos("Thresh Low", str(self.instanceName) + " Track Bars")
            self.exposure = cv2.getTrackbarPos("Exposure",str(self.instanceName) + " Track Bars")

    def readValues(self):
        lines = open('./config.txt','r')
        values = lines.readlines()
        i=0
        while i <= len(values)-1:
            values[i] = values[i].strip()
            i+=1
        self.h_min, self.h_max, self.s_min,self.s_max,self.v_min,self.v_max,self.TLow,self.exposure = [int(i) for i in values]

    def _empty(self,a):
        # This function doens't do anything but is required for creating trackbars
        pass

def getHubVisionThread(hubThreadNumber,camID):
    thread = threading.Thread(target=_runSingleCamModule,args=(("Hub Target Thread " + hubThreadNumber),camID))
    return(thread)

def getSingleCamThread(instanceName,camID):
    thread = threading.Thread(target=_runSingleCamModule,args=(instanceName,camID))
    return(thread)

def getStereoThread(instanceName,camIDL,camIDR,baseline,settings):
    thread = threading.Thread(target=_runStereoModule,args=(instanceName,camIDL,camIDR,baseline,settings))
    return(thread)

def _runSingleCamModule(instanceName,camID):
    module = VisionThreads(instanceName)
    module._singleCamModule(camID)
    return

def _runStereoModule(instanceName,camIDL,camIDR,baseline,settings):
    module = VisionThreads(instanceName)
    module._singleCamModule(camIDL,camIDR,baseline,settings)
    return