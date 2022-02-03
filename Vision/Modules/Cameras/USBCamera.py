import cv2
import math
import numpy as np
import glob
import sys
import os

current = os.path.dirname(os.path.realpath(__file__))
parent = os.path.dirname(current)
parentOfParent = os.path.dirname(parent)
sys.path.append(parent)
sys.path.append(parentOfParent)
from Constants import Constants

class USBCamera:

    camID = None
    horizontalFOV = None
    verticalFOV = None

    cap = None
    calibration = None

    h_min = None
    h_max = None
    s_min = None
    s_max = None
    v_min = None
    v_max = None
    TLow = None
    exposure = None

    def __init__(self,camID,cameraType,settings):
        self.camID = camID
        self.h_min = settings[0]
        self.h_max = settings[1]
        self.s_min = settings[2]
        self.s_max = settings[3]
        self.v_min = settings[4]
        self.v_max = settings[5]
        self.TLow = settings[6]
        self.cap = cv2.VideoCapture(camID)
        self.calibration = self.calibrationProfile(cameraType)
        self.horizontalFOV = Constants.USBCAMERA_ALPHA
        self.verticalFOV = Constants.USBCAMERA_BETA
        self.Exposure = 0

        # Test the camera and set up some one-time values
        ret, frame = self.cap.read()

        if ret:
            self.setPixelDistance(frame)
        else:
            if Constants.EXPERIMENTAL:
                print("Camera ID " + str(camID) + " not found")


    def getFrame(self):
        # TODO: Use global client (Choosing to send data to client is in Constants)
        calibrationProfile = self.calibration

        ret, uncalibratedFrame = self.cap.read()
        
        if ret:
            frame = self.calibrateCamera(uncalibratedFrame,calibrationProfile)
        else:
            frame = ret


        return(frame)

    def setExposure(self,exposure):
        self.cap.set(cv2.CAP_PROP_EXPOSURE,exposure)
    
    def turnOffAuto(self):
        self.cap.set(cv2.CAP_PROP_AUTO_WB,1)
        self.cap.set(cv2.CAP_PROP_AUTOFOCUS,0)
        self.cap.set(cv2.CAP_PROP_AUTO_EXPOSURE,1)



    def setPixelDistance(self,frame):
        self.pixDistanceX = (.5*frame.shape[1])/(math.tan(math.radians(.5*self.horizontalFOV)))
        self.pixDistanceY = (.5*frame.shape[0])/(math.tan(math.radians(.5*self.verticalFOV)))
        


    def calibrationProfile(self,calibrationImageName):
        checkerboard = (6,9) # Dimentions of checkerboard in boxes
        criteria = (cv2.TermCriteria_EPS + cv2.TermCriteria_MAX_ITER, 30, 0.001)

        # Create a vecotr to store vecots of 3D points for each checkerboard image
        objpoints = []
        # Vector to store 2D points
        imgpoints = []

        objp = np.zeros((1, checkerboard[0] * checkerboard[1], 3), np.float32)
        objp[0,:,:2] = np.mgrid[0:checkerboard[0],0:checkerboard[1]].T.reshape(-1,2)

        images = glob.glob('checkerboards/'+ str(calibrationImageName) +'.jpg')
        for fname in images:
            img = cv2.imread(fname)
            if img is not None:
                gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
                # Find the corners
                ret, corners = cv2.findChessboardCorners(gray,checkerboard,cv2.CALIB_CB_ADAPTIVE_THRESH + cv2.CALIB_CB_FAST_CHECK + cv2.CALIB_CB_NORMALIZE_IMAGE)
                # If criterion is met, refine the corners
                if ret:
                    objpoints.append(objp)
                    corners2 = cv2.cornerSubPix(gray, corners, (11,11),(-1,-1),criteria)

                    imgpoints.append(corners2)
                    

                    img = cv2.drawChessboardCorners(img, checkerboard,corners2,ret)
                else:
                    print("No corners found")
                    # TODO: Figure out what to do if there are no corners in the image
            else:
                print("No image found")
                # TODO: Figure out what to do if there isn't an image to calibrate on.
                

        h,w = img.shape[:2]

        ret, mtx, dist, rvecs, tvecs = cv2.calibrateCamera(objpoints, imgpoints, gray.shape[::-1], None, None)

        newcameramtx, roi = cv2.getOptimalNewCameraMatrix(mtx, dist, (w,h), 1, (w,h))

        dst = cv2.undistort(img,mtx,dist,None,newcameramtx)

        x,y,w,h = roi
        dst = dst[y:y+h,x:x+w]



        return(mtx,dist,newcameramtx,roi)






    def calibrateCamera(self,frame,calibrationProfile):

        mtx = calibrationProfile[0]
        dist = calibrationProfile[1]
        newcameramtx = calibrationProfile[2]
        roi = calibrationProfile[3]

        undestortedFrame = cv2.undistort(frame,mtx,dist,None,newcameramtx)

        x,y,w,h = roi
        undestortedFrame = undestortedFrame[y:y+h,x:x+w]
        return(undestortedFrame)



    def objectDetection(self,frame):
        # Get posision of trackbars and assign them to variables
        h_min = self.h_min
        h_max = self.h_max
        s_min = self.s_min
        s_max = self.s_max
        v_min = self.v_min
        v_max = self.v_max
        TLow = self.TLow
        THigh = 255

        # Mask off the object that we want to detect
        lower = np.array([h_min,s_min,v_min])
        upper = np.array([h_max,s_max,v_max])

        if (h_min>h_max):
            lower = np.array([h_min,s_min,v_min])
            upper = np.array([255,s_max,v_max])

            lower2 = np.array([0,s_min,v_min])
            upper2 = np.array([h_max,s_max,v_max])
        else: lower2 = False

        frameBlur = cv2.GaussianBlur(frame,(5,5),0)
        frameHSV = cv2.cvtColor(frameBlur,cv2.COLOR_BGR2HSV)
        frameGray = cv2.cvtColor(frameBlur,cv2.COLOR_BGR2GRAY)
        frameMask = cv2.inRange(frameHSV,lower,upper)
        if lower2 is not False:
            frameMask2 = cv2.inRange(frameHSV,lower2,upper2)
            # add masks
            result = 255*(frameMask,frameMask2)
            frameMask = result.clip(0, 255).astype("uint8")
        frameResult = cv2.bitwise_and(frame,frame,mask=frameMask)
        frameGrayMask = cv2.bitwise_and(frameGray,frameGray, mask=frameMask)
        ret,binary = cv2.threshold(frameGrayMask,TLow,THigh,cv2.THRESH_BINARY)
        contours, hierarchy = cv2.findContours(binary,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
                
        
        pixDistanceX = self.pixDistanceX
        pixDistanceY = self.pixDistanceY
        angleX = None
        angleY = None
        angle2X = None


        # Find a rectangle that fits around the ball (This will be used to find location)
        if len(contours)> 0:
            bestContour = max(contours, key = cv2.contourArea)
            
            x, y, w, h = cv2.boundingRect(bestContour)

            angleX = math.degrees(math.atan(((x+.5*w) - (frame.shape[1]/2))/pixDistanceX))
            angleY = math.degrees(math.atan(((y+.5*h) - (frame.shape[0]/2))/pixDistanceY))
            angle2X = math.degrees(math.atan(((x) - (frame.shape[1]/2))/pixDistanceX))
            cv2.rectangle(frameResult,(x,y),( x + w,y + h ),Constants.BOUNDING_COLOR,3)
        else:
            angleX = False
            angleY = False
            angle2X = False

        binary3Channel = cv2.cvtColor(binary,cv2.COLOR_GRAY2BGR)
        stacked = np.hstack((binary3Channel,frameResult))


        return(angleX,angle2X,angleY,stacked)


    def circleDetection(self,frame):
        # Get posision of trackbars and assign them to variables
        h_min = self.h_min
        h_max = self.h_max
        s_min = self.s_min
        s_max = self.s_max
        v_min = self.v_min
        v_max = self.v_max
        TLow = self.TLow
        THigh = 255

        # Mask off the object that we want to detect
        lower = np.array([h_min,s_min,v_min])
        upper = np.array([h_max,s_max,v_max])

        frameBlur = cv2.GaussianBlur(frame,(5,5),0)
        frameHSV = cv2.cvtColor(frameBlur,cv2.COLOR_BGR2HSV)
        frameGray = cv2.cvtColor(frameBlur,cv2.COLOR_BGR2GRAY)
        frameMask = cv2.inRange(frameHSV,lower,upper)
        frameResult = cv2.bitwise_and(frame,frame,mask=frameMask)
        frameGrayMask = cv2.bitwise_and(frameGray,frameGray, mask=frameMask)
        ret,binary = cv2.threshold(frameGrayMask,TLow,THigh,cv2.THRESH_BINARY)
        frameEdges = cv2.Canny(binary,50,50)

        # Find circles
        params = cv2.SimpleBlobDetector_Params()

        params.filterByArea = False
        params.minArea = 1000

        params.filterByCircularity = True
        params.minCircularity = 0.1

        params.filterByConvexity = False
        params.minConvexity = 0.1

        params.filterByInertia = False
        params.minInertiaRatio = 0.001

        detector = cv2.SimpleBlobDetector_create(params)

        keypoints = detector.detect(frameEdges)

        blank = np.zeros((1,1))

        blobs = cv2.drawKeypoints(frame,keypoints,0,(255,0,0),cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS)

        contours, hierarchy = cv2.findContours(binary,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
                
        
        pixDistanceX = self.pixDistanceX
        pixDistanceY = self.pixDistanceY
        angleX = None
        angleY = None
        angle2X = None


        # Find a rectangle that fits around the ball (This will be used to find location)
        if len(contours)> 0:
            bestContour = max(contours, key = cv2.contourArea)
            
            x, y, w, h = cv2.boundingRect(bestContour)

            angleX = math.degrees(math.atan(((x+.5*w) - (frame.shape[1]/2))/pixDistanceX))
            angleY = math.degrees(math.atan(((y+.5*h) - (frame.shape[0]/2))/pixDistanceY))
            angle2X = math.degrees(math.atan(((x) - (frame.shape[1]/2))/pixDistanceX))
            cv2.rectangle(frameResult,(x,y),( x + w,y + h ),Constants.BOUNDING_COLOR,3)
        else:
            angleX = False
            angleY = False
            angle2X = False

        binary3Channel = cv2.cvtColor(binary,cv2.COLOR_GRAY2BGR)
        stacked = np.hstack((binary3Channel,frameResult))

        cv2.imshow("Edges",frameEdges)
        cv2.imshow("Blobs",blobs)
        cv2.waitKey(1)


        return(angleX,angle2X,angleY,stacked)


    def updateSettings(self,settings):
        self.h_min = settings[0]
        self.h_max = settings[1]
        self.s_min = settings[2]
        self.s_max = settings[3]
        self.v_min = settings[4]
        self.v_max = settings[5]
        self.TLow = settings[6]
        self.Exposure = settings[7]

    def release(self):
        self.cap.release()


    def checkAllCameras():
        i = -10
        cams = []
        while i<=10:
            cap = cv2.VideoCapture(i)
            ret, frame = cap.read()
            cap.release()

            if ret:
                print("Camera port " + str(i) + " works!")
                cams.append(i)
            i += 1
        return(cams)

