from .Cameras import  USBCamera
import math
import numpy as np

class StereoModule:

    leftCamera = None
    rightCamera = None
    baseline = None # Distance between cameras. Whatever unit you measure this in will be the unit that everything is measured in

    def __init__(self,camIDL,camIDR,baseline,settings):
        self.leftCamera = USBCamera(camIDL,"new-checkerboard",settings)
        self.rightCamera = USBCamera(camIDR,"new-checkerboard",settings)
        self.baseline = baseline

        if self.leftCamera == False: return("Left")
        if self.rightCamera == False: return("Right")

        self.leftCamera.turnOffAuto()
        self.rightCamera.turnOffAuto()

    
    def getMeasurements(self,settings):
        self.leftCamera.setExposure(settings[7])
        self.leftCamera.updateSettings(settings)
        self.rightCamera.setExposure(settings[7])
        self.rightCamera.updateSettings(settings)

        frameL = self.leftCamera.getFrame()
        frameR = self.rightCamera.getFrame()

        if frameL is False: return("Left")
        if frameR is False: return("Right")

        XangleL, XangleL2, YangleL, stackedL = self.leftCamera.objectDetection(frameL)
        XangleR, XangleR2, YangleR, stackedR = self.rightCamera.objectDetection(frameR)

        # TODO: FIXME
        if XangleL == "Obstructed" and XangleR == "Obstructed": return("Both_Obstructed")
        if XangleL == "Obstructed": return("Left_Obstructed")
        if XangleR == "Obstructed": return("Right_Obstructed")

        x, z = self.solveStereo(XangleL,XangleR)
        x2, z2 = self.solveStereo(XangleL2,XangleR2)

        Yangle = (YangleL + YangleR)/2
        y = math.tan(math.radians(Yangle)*z)

        d1 = self.solveDistance(x, 0.0, z)
        d2 = self.solveDistance(x, 0.0, z)

        centerAngle = abs(XangleL - XangleL2)

        xGlobal, zGlobal, targetWidth = self.solveGlobal(d1,d2,centerAngle)

        localPose = (x,y,z)
        globalPose = (xGlobal,zGlobal)
        outputFrame = np.vstack((stackedL,stackedR))

        # TODO: Obstruction (If I want to)

        return(globalPose,localPose,outputFrame)

    
    def checkCameras(self):
        frameL = self.leftCamera.getFrame()
        frameR = self.rightCamera.getFrame()

        if frameL is False: return("Left")
        if frameR is False: return("Right")
        if frameL != False and frameR != False: return(True)



    def solveStereo(self,camAngleL,camAngleR):

        cam1 = math.tan(math.radians(-camAngleL+90))
        cam2 = math.tan(math.radians(-camAngleR+90))
        s = self.baseline

        x = -((cam2*s)/((cam1-cam2)*1.001+.0001))
        y = x * cam1
        
        return(x,y)

    def solveDistance(self,x,y,z):
        d = math.sqrt(math.pow(0-x,2) + math.pow(0-y,2) + math.pow(0-z,2))
        return(d)

    def solveGlobal(self,d1,d2,centerAngle):
    
        c = math.sqrt(math.pow(d1,2)+math.pow(d2,2)-2*d1*d2*math.cos(math.radians(centerAngle)))
        x = (d2**2- c**2-d1**2) / (2*c+.00001)
        y = math.sqrt(abs(math.pow(d1,2)-math.pow(x,2)))
    
        return(x,y,c)

    # TODO: Add accuracy with variable percentage

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
        self.leftCamera.release()
        self.rightCamera.release()