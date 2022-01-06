import cv2
import math
import numpy as np
import glob

class USBCamera:

    camID = None
    h_min = None
    h_max = None
    s_min = None
    s_max = None
    v_min = None
    v_max = None
    TLow = None

    def __init__(self,camID,settings):
        self.camID = camID
        self.h_min = settings[0]
        self.h_max = settings[1]
        self.s_min = settings[2]
        self.s_max = settings[3]
        self.v_min = settings[4]
        self.v_max = settings[5]
        self.TLow = settings[6]

    def calibrationProfile(self,calibrationImageName):
        checkerboard = (6,9) # Dimentions of checkerboard in boxes
        criteria = (cv2.TermCriteria_EPS + cv2.TermCriteria_MAX_ITER, 30, 0.001)

        # Create a vecotr to store vecots of 3D points for each checkerboard image
        objpoints = []
        # Vector to store 2D points
        imgpoints = []

        objp = np.zeros((1, checkerboard[0] * checkerboard[1], 3), np.float32)
        objp[0,:,:2] = np.mgrid[0:checkerboard[0],0:checkerboard[1]].T.reshape(-1,2)

        images = glob.glob('Vision/checkerboards/'+ str(calibrationImageName) +'.jpg')
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



    def calibrateCamera(self,frame,mtx,dist,newcameramtx,roi):
        undestortedFrame = cv2.undistort(frame,mtx,dist,None,newcameramtx)

        x,y,w,h = roi
        undestortedFrame = undestortedFrame[y:y+h,x:x+w]
        return(undestortedFrame)


    def objectDetection(self,frame,cameraName):
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

        binary3Channel = cv2.cvtColor(binary,cv2.COLOR_GRAY2BGR)
        stacked = np.hstack((binary3Channel,frameResult))


        return(angleX,angleY,angle2X,stacked)