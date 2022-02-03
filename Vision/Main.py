import VisionThreads
from Constants import Constants
import threading

def main():
    hubThread = VisionThreads.getHubVisionThread(Constants.HUB_CAMERA,Constants.HUB_TARGET_DIAMETER)
    #ballDetectionThread = VisionThreads.getBallDetectionThread(Constants.BALL_DETECTION_CAMERA_LEFT,Constants.BALL_DETECTION_CAMERA_RIGHT,Constants.BALL_DETECTION_BASELINE)
    #climberThread = VisionThreads.getClimberThread(Constants.CLIMBER_CAMERA)

    hubThread.start()
    #ballDetectionThread.start()
    #climberThread.start()

    hubThread.join()
    #ballDetectionThread.join()
    # climberThread.join()


if __name__ == "__main__":
    main()


# For reference

"""

import cv2
import numpy as np
import math
import glob 
import struct
import threading
import time
import sched
from messengerclient import MessengerClient



class Vision:

    experimental = True
    isclient = False
    saveVideo = False

    instanceNumber = None
    savedVideo = None

    baseline = 7.38 # Distance between cameras (Units here affect distance units)
    alpha = 59.7 # Horizontal FOV in degrees
    beta = 31.5 # Vertical FOV in degrees

    targetWidth = 6.5 # Real life measurement for target object (Used for accuracy measurement, optional)

    boundingColor = (121, 82, 179)
    contourColor = (255, 193, 7)



    # Default values for object detection (Also used for locked mode)
    h_min = 0
    h_max = 255
    s_min = 0
    s_max = 255
    v_min = 0
    v_max = 255
    TLow = 0
    exposure = 0

    pixDistanceX = None
    pixDistanceY = None

    if isclient:
        client = MessengerClient("localhost", 8341, "Vision")

    def empty(self,a):
        pass
    
    def __init__(self,instanceName):
        self.readValues()
        self.instanceNumber = instanceName

        if self.experimental:
            
            # Create sliders
            cv2.namedWindow("Track Bars " + str(self.instanceNumber))
            cv2.resizeWindow("Track Bars " + str(self.instanceNumber), 1000,500)
            cv2.createTrackbar("Hue Min","Track Bars " + str(self.instanceNumber),self.h_min,179,self.empty)
            cv2.createTrackbar("Hue Max","Track Bars " + str(self.instanceNumber),self.h_max,179,self.empty)
            cv2.createTrackbar("Saturation Min","Track Bars " + str(self.instanceNumber),self.s_min,255,self.empty)
            cv2.createTrackbar("Saturation Max","Track Bars " + str(self.instanceNumber),self.s_max,255,self.empty)
            cv2.createTrackbar("Value Min","Track Bars " + str(self.instanceNumber),self.v_min,255,self.empty)
            cv2.createTrackbar("Value Max","Track Bars " + str(self.instanceNumber),self.v_max,255,self.empty)
            cv2.createTrackbar("Thresh Low", "Track Bars " + str(self.instanceNumber), self.TLow , 255, self.empty)
            cv2.createTrackbar("Exposure","Track Bars " + str(self.instanceNumber), self.exposure,200, self.empty)
        
        if self.saveValues:
            fourcc = cv2.VideoWriter_fourcc(*'XVID')
            self.savedVideo = cv2.VideoWriter("/Vision/SavedGames/" + str(instanceName)+"_"+str(time.time), fourcc, 30,(320,240))

    def saveValues(self):
        settings = open('Vision/config.txt','w')
        values = [str(self.h_min)+"\n",str(self.h_max)+"\n",str(self.s_min)+"\n",str(self.s_max)+"\n",str(self.v_min)+"\n",str(self.v_max)+"\n",str(self.TLow)+"\n",str(self.exposure)]
        settings.writelines(values)
        settings.close()
    def readValues(self):
        settings = open('Vision/config.txt','r')
        values = settings.readlines()
        i=0
        while i <= len(values)-1:
            values[i] = values[i].strip()
            i+=1
        self.h_min, self.h_max, self.s_min,self.s_max,self.v_min,self.v_max,self.TLow,self.exposure = [int(i) for i in values]



    # Scans camera ports to find working ones
    def scanCameras(self):
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

    # Math setup
    def setFrameShape(self,frame):
        self.pixDistanceX = (.5*frame.shape[1])/(math.tan(math.radians(.5*self.alpha)))
        self.pixDistanceY = (.5*frame.shape[0])/(math.tan(math.radians(.5*self.beta)))
        
    # Locks values to what is currently on the sliders
    def lockExperimental(self):
            self.h_min = cv2.getTrackbarPos("Hue Min","Track Bars " + str(self.instanceNumber))
            self.h_max = cv2.getTrackbarPos("Hue Max","Track Bars " + str(self.instanceNumber))
            self.s_min = cv2.getTrackbarPos("Saturation Min","Track Bars " + str(self.instanceNumber))
            self.s_max = cv2.getTrackbarPos("Saturation Max","Track Bars " + str(self.instanceNumber))
            self.v_min = cv2.getTrackbarPos("Value Min","Track Bars " + str(self.instanceNumber))
            self.v_max = cv2.getTrackbarPos("Value Max","Track Bars " + str(self.instanceNumber))
            self.TLow = cv2.getTrackbarPos("Thresh Low", "Track Bars " + str(self.instanceNumber))
            self.exposure = 0

        
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


    # Find an object using color and get it's properties
    def objectDetection(self,frame,cameraNumber):
        # Get posision of trackbars and assign them to variables
        if self.experimental: 
            h_min = cv2.getTrackbarPos("Hue Min","Track Bars " + str(self.instanceNumber))
            h_max = cv2.getTrackbarPos("Hue Max","Track Bars " + str(self.instanceNumber))
            s_min = cv2.getTrackbarPos("Saturation Min","Track Bars " + str(self.instanceNumber))
            s_max = cv2.getTrackbarPos("Saturation Max","Track Bars " + str(self.instanceNumber))
            v_min = cv2.getTrackbarPos("Value Min","Track Bars " + str(self.instanceNumber))
            v_max = cv2.getTrackbarPos("Value Max","Track Bars " + str(self.instanceNumber))
            TLow = cv2.getTrackbarPos("Thresh Low", "Track Bars " + str(self.instanceNumber))
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

        frameBlur = cv2.GaussianBlur(frame,(5,5),0)
        frameHSV = cv2.cvtColor(frameBlur,cv2.COLOR_BGR2HSV)
        frameGray = cv2.cvtColor(frameBlur,cv2.COLOR_BGR2GRAY)
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
            cv2.rectangle(frameResult,(x,y),( x + w,y + h ),self.boundingColor,3)
        else:
            angleX = "Obstructed"
            angleY = "Obstructed"
            angle2X = "Obstructed"

        binary3 = cv2.cvtColor(binary,cv2.COLOR_GRAY2BGR)
        stacked = np.hstack((binary3,frameResult))

        if self.experimental:
            # cv2.imshow("Result " + str(cameraNumber) + " Instance " + str(self.instanceNumber),frameResult)
            # cv2.imshow("Binary " + str(cameraNumber) + " Instance " + str(self.instanceNumber),binary)
            cv2.imshow("Camera " + str(cameraNumber) + " Instance " + str(self.instanceNumber),stacked)
        if self.saveVideo:
            self.savedVideo.write(stacked)


        return(angleX,angleY,angle2X)


    # Gets distance to points using two cameras
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

    
    # Solves for the real posisiton of the camera
    def solveGlobal(self,a,b,centerAngle):
    
        c = math.sqrt(math.pow(a,2)+math.pow(b,2)-2*a*b*math.cos(math.radians(centerAngle)))
        x = (b**2- c**2-a**2) / (2*c+.00001)
        y = math.sqrt(abs(math.pow(a,2)-math.pow(x,2)))
    
        return(x,y,c)


    # Shows a graphic representation of the camera and the object
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
        globCam = (int(globalVisualizer.shape[1]*.5+x*scale),int(wh+y*scale))
        cv2.line(globalVisualizer,(0,wh),(globalVisualizer.shape[1],wh),self.contourColor)
        cv2.circle(globalVisualizer,globCam,5,self.contourColor,3)
        cv2.line(globalVisualizer,globCam,(globCam[0],globCam[1]-y*scale),self.boundingColor)
        cv2.line(globalVisualizer,(int(globalVisualizer.shape[1]*.5),wh),(globCam[0],wh),self.boundingColor)
        cv2.line(globalVisualizer,globCam,(int(globalVisualizer.shape[1]*.5),wh),self.boundingColor)

        cv2.putText(globalVisualizer,"X: " + str(x),(int(globCam[0]-.5*x*scale),wh-10),cv2.FONT_HERSHEY_COMPLEX,.5,(255,255,255))
        cv2.putText(globalVisualizer,"Y: " + str(y),(int(globCam[0]+10),int(globCam[1]-.5*y*scale)),cv2.FONT_HERSHEY_COMPLEX,.5,(255,255,255))
        cv2.putText(globalVisualizer,"Distance: " + str(d),(int((globCam[0]+.5*globalVisualizer.shape[1])/2),int((globCam[1]+wh)/2)-30),cv2.FONT_HERSHEY_COMPLEX,.5,(255,255,255))
        print(globCam)
        cv2.imshow("Global Visualizer",globalVisualizer)



        cv2.waitKey(0)

    # Used only one camera for object detection, used for non-stero, and when a camera breaks.
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

    
    # Main method just for stero
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

        ret, frameL = capL.read()
        if ret:
            #sCamL, sCamL = self.calibrateCamera(frameL,frameL,calProfile[0],calProfile[1],calProfile[2],calProfile[3])
            self.setFrameShape(frameL)
        else:
            print("Left camera not found")
            ret, frameR = capR.read()
            if ret:
                #sCamR, sCamR = self.calibrateCamera(frameR,frameR,calProfile[0],calProfile[1],calProfile[2],calProfile[3])
                self.setFrameShape(frameR)
            else:
                print("Right camera not found")
        counterStart = 1000
        leftObstruct = counterStart
        rightObstruct = counterStart
                

        while True:

            if self.experimental: # Allows values to be changed using sliders, also allows windows to be shown.
                # Constantly set the exposure of the camera to
                capL.set(cv2.CAP_PROP_EXPOSURE, -cv2.getTrackbarPos("Exposure",  "Track Bars " + str(self.instanceNumber)))
                capR.set(cv2.CAP_PROP_EXPOSURE, -cv2.getTrackbarPos("Exposure",  "Track Bars " + str(self.instanceNumber)))
            else:
                capL.set(cv2.CAP_PROP_EXPOSURE, -self.exposure)
                capR.set(cv2.CAP_PROP_EXPOSURE, -self.exposure)
            # Turn raw camera input into readable frames
            retL, frameL = capL.read()
            retR, frameR = capR.read()

            if retL and retR:

                # Calibrate every frame using the calibration profile
                #sCamL, sCamR = self.calibrateCamera(frameL,frameR,calProfile[0],calProfile[1],calProfile[2],calProfile[3])
                
                # Use ball detection function to find the angle to center and right side of the object in both cameras
                XangleL, YangleL, XangleL2 = self.objectDetection(frameL,"Left")
                XangleR, YangleR, XangleR2 = self.objectDetection(frameR,"Right")

                if XangleL == "Obstructed":
                    leftObstruct -=1
                else: leftObstruct = counterStart
                if XangleR == "Obstructed":
                    rightObstruct -=1
                else: leftObstruct = counterStart

                new_frame_time = 0
                prev_frame_time = 0






                # Get the distance to the object in total using the distance formula
                #   Note: all of the sub 2's are 0 because for now we assume that the camera is not moving


                #disatnceWithY = math.sqrt(math.pow(0-x,2)+math.pow(0-y,2)+math.pow(0-z,2))
                if XangleL != "Obstructed" and XangleL != None:
                    x,z = self.stereoVision(XangleL,XangleR)
                    x2,z2 = self.stereoVision(XangleL2,XangleR2)

                    if x is not None:
                        d = math.sqrt(math.pow(0-x,2) + math.pow(0-z,2))
                        d2 = math.sqrt(math.pow(0-x2,2)+ math.pow(0-z2,2))
                    else:
                        d = 0
                        d2 = 0

                    centerAngle = abs(XangleL - XangleL2)
                    xReal, yReal,c = self.solveGlobal(d,d2,centerAngle)

                    Yangle = (YangleL + YangleR)/2
                    y = math.tan(math.radians(Yangle)*z)

                    accuracy = -10*abs(c-self.targetWidth)+100
                    if c == 0 or accuracy < 1:
                        accuracy = 0


                    # Temporary #
                    #print(accuracy)
                    
                    new_frame_time = time.time()
                    fps = 1/(new_frame_time-prev_frame_time)
                    prev_frame_time = new_frame_time



                if leftObstruct < 1:
                    print("Left camera obstructed")
                    capL.release()
                    capR.release()
                    self.run_single_camera(camIDR)
                    return

                if leftObstruct < 1:
                    print("Right camera obstructed")
                    capL.release()
                    capR.release()
                    self.run_single_camera(camIDL)
                    return



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

def stereoThread(instanceName, leftCamera, rightCamera):
    vision = Vision(instanceName)
    vision.run_stereo(leftCamera,rightCamera)

def singleCamThread(instanceName, camera):
    vision = Vision(instanceName)
    vision.run_single_camera(camera)
                




# Multithreading

# vision1 = Vision(1)
# vision2 = Vision(2)

t2 = threading.Thread(target=singleCamThread, args=("Laptop camera",-2,))
t2.start()

#vision1 = Vision(1)
# cams = vision1.scanCameras()
# print(cams)
# vision1.run_single_camera(-1)
#vision1.run_single_camera(-1)


#stereo_vision = Vision()

#stereo_vision.run_stereo(2,4)
  

# close all windows
cv2.destroyAllWindows() 


"""