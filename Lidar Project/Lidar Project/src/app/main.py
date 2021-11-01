from lidar_lib.rplidar import RPLidar
from weblog_lib.webServer import webServer
from web_lib.web_server import web_server
import time
import threading

class lidarProject:

    class Exception(Exception):
        '''Exception class for Lidar Project'''

    lidarSpec = {'serial':'/dev/ttyUSB0', 'firmware': (1, 29), 'hardware': 7, 'model': 24, 'health': 'Good'}


    def __init__(self):
        self.lidar = RPLidar('/dev/ttyUSB0')
        if (self.checkLidar()):
            self.webServer = webServer(8001)
            self.web_server = web_server(8002)
        else: 
            print("Exiting")
            exit()
        self.ListenForScans()

    def checkLidar(self):
        info = self.lidar.get_info()
        health = self.lidar.get_health()
        if (    info['firmware'] == self.lidarSpec['firmware'] and
                info['hardware'] == self.lidarSpec['hardware'] and
                info['model'] == self.lidarSpec['model'] and
                health[0] == self.lidarSpec['health']):
            print("Lidar is ready")
            return True
        else:
            raise Exception("Lidar not ready")
            print('Lidar Info: ' + info)
            print('Lidar Health: ' + health)
            return False

    def ListenForScans(self):
        def run():
            while True:
                try:
                    for scans in self.lidar.iter_scans(scan_type='express'):
                        self.recentScan = scans
                except Exception as e:
                    print(e)
                    pass
        threading.Thread(target=run).start()

    def getRecentScan(self):
        return self.recentScan

    def periodicUpdate(self):
        def run():
            while True:
                # webServer
                try:
                    self.webServer.clear()
                    self.webServer.log("Last Updated: " + str(time.time()))
                    for itr, i in enumerate(self.getRecentScan()):
                        self.webServer.log(str(itr) + ":" + str(i))
                except Exception as e:
                    print(e)
                    pass

                # web_server
                try:
                    self.web_server.setPointVars(self.getRecentScan())
                except Exception as e:
                    print(e)
                    pass
                time.sleep(2)
        threading.Thread(target=run).start()
        
lidarProject = lidarProject()
lidarProject.periodicUpdate()
