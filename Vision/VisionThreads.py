import cv2
from Modules import SingleModule.SingleModule
from Constants import Constants

class VisionThreads:
    camID = None
    instanceName = None

    def __init__(self,instanceName):
        self.instanceName = instanceName

    def singleCamModule(self,camID):
        if Constants.EXPERIMENTAL:
            self.createTrackbars()

        
        module = SingleModule(camID)
        
        while True: # TODO: Find a better way to loop
            Xangle, Xangle2, Yangle, frame = module.getMeasurements()

            # TODO: Send these angles to messanger client

            if Constants.EXPERIMENTAL:
                cv2.imshow(str(self.instanceName) + " Camera",frame)
            # TODO: Calculate fps and add it to the frame
            # TODO: DO the obstructed thing (It'll be False if it is obstructed)



    def stereoModule(self,camIDL,camIDR,baseline,settings):
        if Constants.EXPERIMENTAL:
            self.createTrackbars()
        
        module = StereoModule(camIDL,camIDR,baseline)

        while True: # TODO: Find a better way to loop
            localPose,globalPose,outputFrame = module.getMeasurements(settings)

            # TODO: Send these angles to messenger client

            if Constants.EXPERIMENTAL:
                cv2.imshow(str(self.instanceName) + " Module")

            # TODO: Calculate fps and add it to the frame
            # TODO: DO the obstructed thing (It'll be False if it is obstructed)



    def createTrackbars(self):
            cv2.namedWindow(str(self.instanceName) + " Track Bars")
            cv2.resizeWindow(str(self.instanceName) + " Track Bars", 1000,500)
            cv2.createTrackbar("Hue Min",str(self.instanceName) + " Track Bars",self.h_min,179,self.empty)
            cv2.createTrackbar("Hue Max",str(self.instanceName) + " Track Bars",self.h_max,179,self.empty)
            cv2.createTrackbar("Saturation Min",str(self.instanceName) + " Track Bars",self.s_min,255,self.empty)
            cv2.createTrackbar("Saturation Max",str(self.instanceName) + " Track Bars",self.s_max,255,self.empty)
            cv2.createTrackbar("Value Min",str(self.instanceName) + " Track Bars",self.v_min,255,self.empty)
            cv2.createTrackbar("Value Max",str(self.instanceName) + " Track Bars",self.v_max,255,self.empty)
            cv2.createTrackbar("Thresh Low", str(self.instanceName) + " Track Bars", self.TLow , 255, self.empty)
            cv2.createTrackbar("Exposure",str(self.instanceName) + " Track Bars", self.exposure,200, self.empty)

    def empty(self,a):
        # This function doens't do anything but is required for creating trackbars
        pass