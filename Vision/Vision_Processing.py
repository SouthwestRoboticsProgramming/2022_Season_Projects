import cv2
import numpy as np
import math
import glob
import taskclient as tc
import struct

#client = tc.TaskMessenger("localhost", 8264, "Vision_Prosessing")

class Vision:

    experimental = True

    frame_rate = 120

    baseline = 3.2 #Distance between cameras (Units here affect distance units)
    alpha = 59.7 #Horizontal fov in degrees
    pixelsWidth = 1280

    boundingColor = (121, 82, 179)
    contourColor = (255, 193, 7)



    #Default values for object detection (Also used for locked mode)
    h_min = 20
    h_max = 35
    s_min = 104
    s_max = 255
    v_min = 41
    v_max = 240
    TLow = 0

    pixDistance = (.5*pixelsWidth)/(math.tan(math.radians(.5*alpha)))

    def empty(self,a):
        pass
    
    def __init__(self):

        if self.experimental:
            
            # Create sliders
            cv2.namedWindow("Track Bars")
            cv2.resizeWindow("Track Bars", 1000,500)
            cv2.createTrackbar("Hue Min","Track Bars",20,179,self.empty)
            cv2.createTrackbar("Hue Max","Track Bars",35,179,self.empty)
            cv2.createTrackbar("Saturation Min","Track Bars",104,255,self.empty)
            cv2.createTrackbar("Saturation Max","Track Bars",255,255,self.empty)
            cv2.createTrackbar("Value Min","Track Bars",41,255,self.empty)
            cv2.createTrackbar("Value Max","Track Bars",240,255,self.empty)
            cv2.createTrackbar("Thresh Low", "Track Bars", 0 , 255, self.empty)
            cv2.createTrackbar("Exposure","Track Bars", -10,10, self.empty)
        

        
    # Create a camera calibration profile
    def calibrateCameraInit(self):
        checkerboard = (6,9) # Dimentions of checkerboard in boxes
        criteria = (cv2.TermCriteria_EPS + cv2.TermCriteria_MAX_ITER, 30, 0.001)

        # Create a vecotr to store vecots of 3D points for each checkerboard image
        objpoints = []
        # Vector to store 2D points
        imgpoints = []

        objp = np.zeros((1, checkerboard[0] * checkerboard[1], 3), np.float32)
        objp[0,:,:2] = np.mgrid[0:checkerboard[0],0:checkerboard[1]].T.reshape(-1,2)

        images = glob.glob('Vision/checkerboards/*.jpg')
        for fname in images:
            img = cv2.imread(fname)
            gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
            #Find the corners
            ret, corners = cv2.findChessboardCorners(gray,checkerboard,cv2.CALIB_CB_ADAPTIVE_THRESH + cv2.CALIB_CB_FAST_CHECK + cv2.CALIB_CB_NORMALIZE_IMAGE)
            #If criterion is met, refine the corners
            if ret == True:
                objpoints.append(objp)
                corners2 = cv2.cornerSubPix(gray, corners, (11,11),(-1,-1),criteria)

                imgpoints.append(corners2)
                

                img = cv2.drawChessboardCorners(img, checkerboard,corners2,ret)

        h,w = img.shape[:2]

        ret, mtx, dist, rvecs, tvecs = cv2.calibrateCamera(objpoints, imgpoints, gray.shape[::-1], None, None)

        newcameramtx, roi = cv2.getOptimalNewCameraMatrix(mtx, dist, (w,h), 1, (w,h))

        dst = cv2.undistort(img,mtx,dist,None,newcameramtx)

        x,y,w,h = roi
        dst = dst[y:y+h,x:x+w]



        return(mtx,dist,newcameramtx,roi)


    # Use calibration profile to calibrate an image or frame
    def calibrateCamera(self,cam1,cam2,mtx,dist,newcameramtx,roi):
        undestoredCam1 = cv2.undistort(cam1,mtx,dist,None,newcameramtx)
        undestoredCam2 = cv2.undistort(cam2,mtx,dist,None,newcameramtx)

        x,y,w,h = roi
        undestoredCam1 = undestoredCam1[y:y+h,x:x+w]
        undestoredCam2 = undestoredCam2[y:y+h,x:x+w]
        return(undestoredCam1,undestoredCam2)


    # Find a ball using color and get it's properties
    def ballDetection(self,frame,cameraNumber):
        # Get posision of trackbars and assign them to variables
        if self.experimental == True: 
            h_min = cv2.getTrackbarPos("Hue Min","Track Bars")
            h_max = cv2.getTrackbarPos("Hue Max","Track Bars")
            s_min = cv2.getTrackbarPos("Saturation Min","Track Bars")
            s_max = cv2.getTrackbarPos("Saturation Max","Track Bars")
            v_min = cv2.getTrackbarPos("Value Min","Track Bars")
            v_max = cv2.getTrackbarPos("Value Max","Track Bars")
            TLow = cv2.getTrackbarPos("Thresh Low", "Track Bars")
            THigh = 255
        else:
            h_min = self.h_min
            h_max = self.h_max
            s_min = self.s_min
            s_max = self.s_max
            v_min = self.v_min
            v_max = self.v_max
            TLow = self.TLow
            THigh = 255


        
        lower = np.array([h_min,s_min,v_min])
        upper = np.array([h_max,s_max,v_max])

        frameHSV = cv2.cvtColor(frame,cv2.COLOR_BGR2HSV)
        frameGray = cv2.cvtColor(frame,cv2.COLOR_BGR2GRAY)
        frameMask = cv2.inRange(frameHSV,lower,upper)
        frameResult = cv2.bitwise_and(frame,frame,mask=frameMask)
        frameGrayMask = cv2.bitwise_and(frameGray,frameGray, mask=frameMask)
        colorMask = frameResult.copy()
        ret,binary = cv2.threshold(frameGrayMask,TLow,THigh,cv2.THRESH_BINARY)
        contours, hierarchy = cv2.findContours(binary,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
                
        
        pixDistance = self.pixDistance
        angle = None


        # Find a rectangle that fits around the ball (Thill will be used to find location)
        if len(contours)> 0:
            bestContour = max(contours, key = cv2.contourArea)
            
            x, y, w, h = cv2.boundingRect(bestContour)

            angle = math.degrees(math.atan(((x+.5*w) -(frame.shape[1]/2))/pixDistance))

            if self.experimental:
                cv2.rectangle(frameResult,(x,y),( x + w,y + h ),self.boundingColor,3)
                cv2.imshow("Result" + str(cameraNumber),frameResult)
                cv2.imshow("Binary" + str(cameraNumber),binary)


        return(angle)

    def visionTargetAngle(self,targetFrame):


        # Get posision of trackbars and assign them to variables
        h_min = cv2.getTrackbarPos("Hue Min","Track Bars")
        h_max = cv2.getTrackbarPos("Hue Max","Track Bars")
        s_min = cv2.getTrackbarPos("Saturation Min","Track Bars")
        s_max = cv2.getTrackbarPos("Saturation Max","Track Bars")
        v_min = cv2.getTrackbarPos("Value Min","Track Bars")
        v_max = cv2.getTrackbarPos("Value Max","Track Bars")
        TLow = cv2.getTrackbarPos("Thresh Low", "Track Bars")
        THigh = 255

        
        lower = np.array([h_min,s_min,v_min])
        upper = np.array([h_max,s_max,v_max])
        frameHSV = cv2.cvtColor(targetFrame,cv2.COLOR_BGR2HSV)
        frameGray = cv2.cvtColor(targetFrame,cv2.COLOR_BGR2GRAY)
        frameMask = cv2.inRange(frameHSV,lower,upper)
        frameResult = cv2.bitwise_and(targetFrame,targetFrame,mask=frameMask)
        frameGrayMask = cv2.bitwise_and(frameGray,frameGray, mask=frameMask)

        ret,binary = cv2.threshold(frameGrayMask,TLow,THigh,cv2.THRESH_BINARY)
        inverted = cv2.bitwise_not(binary)
        contours, hierarchy = cv2.findContours(binary,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
                
        #Draw a squigly line around the object
        cv2.drawContours(targetFrame,contours,-1,(0,255,255),3)
                
        distance = None
        localXAngle = None
        localYAngle = None

        #Find a rectangle that fits around the ball (Thill will be used to find location)
        if len(contours)> 0:
            bestContour = max(contours, key = cv2.contourArea)
            
            x, y, w, h = cv2.boundingRect(bestContour)
            cv2.rectangle(frameResult,(x,y),( x + w,y + h ),self.color,3)

            #Get distance to ball
            targetW = 39.25 #Inches
            targetH = 17 #Inches
            horFOV = self.alpha #Degrees
            verFOV = 24 #Degrees
            ########################

            frameInches = targetFrame.shape[0]/h*targetH

            distance = (.5 * frameInches)/math.tan(.5*verFOV)

            localYAngle = math.degrees(math.atan2(-(.5*targetFrame.shape[0]-(y+h)),distance))
            localXAngle = math.degrees(math.atan2(-(.5*targetFrame.shape[1]-(x+.5*w)),distance))


            # pointA = (x+int(.5*w),y)
            #pointB = (x+int(.5*w),y+h)
            #cv2.line(frameMask,pointA,pointB,(0,0,0),3)
            

        cv2.imshow("Result",frameResult)
        cv2.imshow("Binary",binary)
        cv2.imshow("Frame mask",frameMask)
        return(distance,localXAngle,localYAngle)
    
    def stereoVision(self,camAngleL,camAngleR):
        z = None
        y = None
        x = None
        if camAngleL is not None and camAngleR is not None:
            cam1 = math.tan(math.radians(-camAngleL+90))
            cam2 = math.tan(math.radians(-camAngleR+90))
            s = self.baseline

            x = -((cam2*s)/((cam1-cam2)*1.001+.0001))
            y = x * cam1

            z = math.sqrt(math.pow(x,2) + math.pow(y,2))
        
        return(x,y,z)
        
    def Visualizer(self, estPose):

        #Create a visualizer to see where it thinks the robot/ball is
        visualizer = np.zeros((500,500,3),np.uint8)
        cv2.imshow("Visualizer",visualizer)

    
    
    def run_stereo(self,camIDL,camIDR):
        
        capL = cv2.VideoCapture(camIDL)
        capR = cv2.VideoCapture(camIDR)

        # Set properties of cameras
        capL.set(cv2.CAP_PROP_AUTO_WB,0)
        capR.set(cv2.CAP_PROP_AUTO_WB,0)
        capL.set(cv2.CAP_PROP_AUTOFOCUS,0)
        capR.set(cv2.CAP_PROP_AUTOFOCUS,0)

        # Sets a calibration profile to calibrate the cameras on
        calProfile = self.calibrateCameraInit()

        while True:

            if self.experimental: # Allows values to be changed using sliders, also allows windows to be shown.
                # Constantly set the exposure of the camera to
                capL.set(cv2.CAP_PROP_EXPOSURE, -cv2.getTrackbarPos("Exposure",  "Track Bars"))
                capR.set(cv2.CAP_PROP_EXPOSURE, -cv2.getTrackbarPos("Exposure",  "Track Bars"))

            # Turn raw camera input into readable frames
            ret, frameL = capL.read()
            ret, frameR = capR.read()

            # Calibrate every frame using the calibration profile
            sCamL, sCamR = self.calibrateCamera(frameL,frameR,calProfile[0],calProfile[1],calProfile[2],calProfile[3])
            
            # Use ball detection function to find the angle to center of the object in both cameras
            angleL = self.ballDetection(sCamL,0)
            angleR = self.ballDetection(sCamR,1)

            x,y,z = self.stereoVision(angleL,angleR)


            # Temporary #
            print(z)

            # creating 'q' as the quit button for the video
            if cv2.waitKey(1) & 0xFF == ord('q'):
                capL.release()
                capR.release()
                return()








stereo_vision = Vision()
stereo_vision.run_stereo(0,1)
  

# close all windows
cv2.destroyAllWindows() 