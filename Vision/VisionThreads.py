import cv2
from Modules import StereoModule
from Modules import SingleModule
from Constants import Constants

class VisionThreads:
    camID = None
    instanceName = None
    
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

    def _singleCamModule(self,camID):
        if Constants.EXPERIMENTAL:
            self._createTrackbars()

        
        module = SingleModule(camID)
        
        while True: # TODO: Find a better way to loop
            settings = [self.h_min,self.h_max,self.s_min,self.s_max,self.v_min,self.v_max,self.TLow,self.exposure]
            Xangle, Xangle2, Yangle, frame = module.getMeasurements(settings)

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
            localPose,globalPose,outputFrame = module.getMeasurements(settings)

            # TODO: Send these angles to messenger client

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
            cv2.createTrackbar("Exposure",str(self.instanceName) + " Track Bars", self.exposure,200, self._empty)

    def _getTrackbars(self):
            self.h_min = cv2.getTrackbarPos("Hue Min",str(self.instanceName) + " Track Bars")
            self.h_max = cv2.getTrackbarPos("Hue Max",str(self.instanceName) + " Track Bars")
            self.s_min = cv2.getTrackbarPos("Saturation Min",str(self.instanceName) + " Track Bars")
            self.s_max = cv2.getTrackbarPos("Saturation Max",str(self.instanceName) + " Track Bars")
            self.v_min = cv2.getTrackbarPos("Value Min",str(self.instanceName) + " Track Bars")
            self.v_max = cv2.getTrackbarPos("Value Max",str(self.instanceName) + " Track Bars")
            self.TLow = cv2.getTrackbarPos("Thresh Low", str(self.instanceName) + " Track Bars")
            self.exposure = cv2.getTrackbarPos("Exposure",str(self.instanceName) + " Track Bars")

    def _empty(self,a):
        # This function doens't do anything but is required for creating trackbars
        pass

def singleCamModule(instanceName,camID):
    module = VisionThreads(instanceName)
    module._singleCamModule(camID)
    return

def stereoModule(instanceName,camIDL,camIDR,baseline,settings):
    module = VisionThreads(instanceName)
    module._singleCamModule(camIDL,camIDR,baseline,settings)
    return