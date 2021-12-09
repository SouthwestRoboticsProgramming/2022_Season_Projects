import cv2
import numpy as np
import math
import glob
import taskclient as tc
import struct

#client = tc.TaskMessenger("localhost", 8264, "Vision_Prosessing")

class Vision:

    experimental = True

    baseline = 7.38 # Distance between cameras (Units here affect distance units)
    alpha = 59.7 # Horizontal FOV in degrees
    beta = 31.5 # Vertical FOV in degrees

    targetWidth = 6.5

    boundingColor = (121, 82, 179)
    contourColor = (255, 193, 7)



    # Default values for object detection (Also used for locked mode)
    h_min = 20
    h_max = 35
    s_min = 104
    s_max = 255
    v_min = 41
    v_max = 240
    TLow = 0

    pixDistanceX = None
    pixDistanceY = None

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
            cv2.createTrackbar("Exposure","Track Bars", 5,10, self.empty)

    # Scans camera ports to find working ones
    def scanCameras(self):
        i = -10
        cams = []
        while i<=10:
            cap = cv2.VideoCapture(i)
            ret, frame = cap.read()

            if ret:
                print("Camera port " + str(i) + " works!")
                cams.append(i)
            i += 1
        return(cams)

    def setFrameShape(self,frame):
        self.pixDistanceX = (.5*frame.shape[1])/(math.tan(math.radians(.5*self.alpha)))
        self.pixDistanceY = (.5*frame.shape[0])/(math.tan(math.radians(.5*self.beta)))
        

        
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
            # Find the corners
            ret, corners = cv2.findChessboardCorners(gray,checkerboard,cv2.CALIB_CB_ADAPTIVE_THRESH + cv2.CALIB_CB_FAST_CHECK + cv2.CALIB_CB_NORMALIZE_IMAGE)
            # If criterion is met, refine the corners
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
    def objectDetection(self,frame,cameraNumber):
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


        # Mask off the object that we want to detect
        lower = np.array([h_min,s_min,v_min])
        upper = np.array([h_max,s_max,v_max])

        frameHSV = cv2.cvtColor(frame,cv2.COLOR_BGR2HSV)
        frameGray = cv2.cvtColor(frame,cv2.COLOR_BGR2GRAY)
        frameMask = cv2.inRange(frameHSV,lower,upper)
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


            if self.experimental:
                cv2.rectangle(frameResult,(x,y),( x + w,y + h ),self.boundingColor,3)
                cv2.imshow("Result " + str(cameraNumber),frameResult)
                cv2.imshow("Binary " + str(cameraNumber),binary)


        return(angleX,angleY,angle2X)


    
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
        
        return(x,y)

    def solveGlobal(self,a,b,centerAngle):
    
        c = math.sqrt(math.pow(a,2)+math.pow(b,2)-2*a*b*math.cos(math.radians(centerAngle)))
        x = (b**2- c**2-a**2) / (2*c)
        y = math.sqrt(abs(math.pow(a,2)-math.pow(x,2)))
    
        return(x,y,c)


        
    def visualizer(self, x,y,d):

        # Create a visualizer to see where it thinks the robot/ball is
        visualizer = np.zeros((500,500,3),np.uint8)
        globalVisualizer = np.zeros((500,500,3),np.uint8)

        scale = 10
        cam = ( int(visualizer.shape[1]*.5),int(visualizer.shape[0]-50) )
        obj = ( int(visualizer.shape[1]*.5+x*scale) , int(visualizer.shape[0]-50-y*scale) )

        print(obj)
        cv2.circle(visualizer,cam,5,self.contourColor,3)
        cv2.circle(visualizer,obj,5,self.contourColor,3)
        cv2.line(visualizer,cam,obj,self.boundingColor,3)
        cv2.putText(visualizer,("Distance: " + str(d)),(int((cam[0]+obj[0])/2),int((cam[1]+obj[1])/2)),cv2.FONT_HERSHEY_COMPLEX,.5,(255,255,255))
        cv2.imshow("Visualizer",visualizer)

        wh = 50
        cv2.line(globalVisualizer,(0,wh),(globalVisualizer.shape[1],wh))
        cv2.circle(globalVisualizer,(x,y),5,self.contourColor,3)



        cv2.waitKey(0)

    def run_single_camera(self,camID):
        cap = cv2.VideoCapture(camID)
        cap.set(cv2.CAP_PROP_AUTO_WB,0)
        cap.set(cv2.CAP_PROP_AUTOFOCUS,0)
        cap.set(cv2.CAP_PROP_AUTO_EXPOSURE,1)
        
        # Sets a calibration profile to calibrate the cameras on
        calProfile = self.calibrateCameraInit()

        ret, frame = cap.read()
        if ret:
            self.setFrameShape(frame)
        else:
            print("hmmmmmmmmmm")

        while True:
            if self.experimental: # Allows values to be changed using sliders, also allows windows to be shown.
                # Constantly set the exposure of the camera to
                cap.set(cv2.CAP_PROP_EXPOSURE, -cv2.getTrackbarPos("Exposure",  "Track Bars"))
            # Turn raw camera input into readable frames
            ret, frame = cap.read()

            # Calibrate every frame using the calibration profile
            sCam, sCam = self.calibrateCamera(frame,frame,calProfile[0],calProfile[1],calProfile[2],calProfile[3])
            
            # Use ball detection function to find the angle to center and right side of the object in both cameras
            Xangle, Yangle, Xangle2 = self.objectDetection(sCam,"")

            # Creating 'q' as the quit button for the webcam
            if cv2.waitKey(1) & 0xFF == ord('q'):
                cap.release()
                return()

    
    
    def run_stereo(self,camIDL,camIDR):
        
        capL = cv2.VideoCapture(camIDL)
        capR = cv2.VideoCapture(camIDR)

        # Set properties of cameras
        capL.set(cv2.CAP_PROP_AUTO_WB,0)
        capR.set(cv2.CAP_PROP_AUTO_WB,0)
        capL.set(cv2.CAP_PROP_AUTOFOCUS,0)
        capR.set(cv2.CAP_PROP_AUTOFOCUS,0)
        capL.set(cv2.CAP_PROP_AUTO_EXPOSURE,1)
        capR.set(cv2.CAP_PROP_AUTO_EXPOSURE,1)

        # Sets a calibration profile to calibrate the cameras on
        calProfile = self.calibrateCameraInit()

        ret, frame = capL.read()
        if ret:
            self.setFrameShape(frame)
        else:
            print("Left camera not found")

        while True:

            if self.experimental: # Allows values to be changed using sliders, also allows windows to be shown.
                # Constantly set the exposure of the camera to
                capL.set(cv2.CAP_PROP_EXPOSURE, -cv2.getTrackbarPos("Exposure",  "Track Bars"))
                capR.set(cv2.CAP_PROP_EXPOSURE, -cv2.getTrackbarPos("Exposure",  "Track Bars"))
            # Turn raw camera input into readable frames
            retL, frameL = capL.read()
            retR, frameR = capR.read()

            if retL and retR:

                # Calibrate every frame using the calibration profile
                sCamL, sCamR = self.calibrateCamera(frameL,frameR,calProfile[0],calProfile[1],calProfile[2],calProfile[3])
                
                # Use ball detection function to find the angle to center and right side of the object in both cameras
                XangleL, YangleL, XangleL2 = self.objectDetection(frameL,"Left")
                XangleR, YangleR, XangleR2 = self.objectDetection(frameR,"Right")


                x,z = self.stereoVision(XangleL,XangleR)
                x2,z2 = self.stereoVision(XangleL2,XangleR2)


                if YangleL is not None and YangleR is not None:
                    Yangle = (YangleL + YangleR)/2
                    y = math.tan(math.radians(Yangle)*z)
                else:
                    y = 0


                # Get the distance to the object in total using the distance formula
                #   Note: all of the sub 2's are 0 because for now we assume that the camera is not moving
                if x is not None:
                    d = math.sqrt(math.pow(0-x,2) + math.pow(0-z,2))
                    d2 = math.sqrt(math.pow(0-x2,2)+ math.pow(0-z2,2))
                else:
                    d = 0
                    d2 = 0


                #disatnceWithY = math.sqrt(math.pow(0-x,2)+math.pow(0-y,2)+math.pow(0-z,2))

                centerAngle = abs(XangleL - XangleL2)
                xReal, yReal,c = self.solveGlobal(d,d2,centerAngle)


                accuracy = -10*abs(c-self.targetWidth)+100


                # Temporary #
                print(accuracy)

                #if self.experimental:
                    #self.visualizer(xReal,yReal,d)
            elif retL:
                print("I'm gonna use the left camera only")
                capL.release()
                capR.release()
                self.run_single_camera(camIDL)
                return()
            elif retR:
                print("I'm gonna use the right camera only")
                capL.release
                capR.release()
                self.run_single_camera(camIDR)
                return
            else:
                print("No cameras found, listing open cameras...")
                cams = self.scanCameras()
                print(cams)
                return
                

            # Creating 'q' as the quit button for the webcam
            if cv2.waitKey(1) & 0xFF == ord('q'):
                capL.release()
                capR.release()
                return()







stereo_vision = Vision()
#
stereo_vision.run_stereo(2,4)
#stereo_vision.visualizer(20,40,80,80)

# Debugging
#x,y = stereo_vision.solveGlobal(11.18,12.80,12.09)
  

# close all windows
cv2.destroyAllWindows() 