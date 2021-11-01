from http.server import HTTPServer, BaseHTTPRequestHandler
import os
from math import sin, cos, radians, pi
import threading

# pointVars = "[]"
replacements = {'PYxyValues':'[]', 
                'PYxMin': -1,
                'PYxMax': 1,
                'PYyMin': -1,
                'PYyMax': 1}

abspath = os.path.dirname(os.path.abspath(__file__))

class web_server:

    def __init__(self, port=8080):
        def run(server_class=HTTPServer):
            server_address = ('', port)
            handler_class = self.Serv
            httpd = server_class(server_address, handler_class)
            print('Starting httpd...\n')
            try:
                httpd.serve_forever()
            except KeyboardInterrupt:
                pass
            httpd.server_close()
            print('Stopping httpd...\n')    
        threading.Thread(target=run).start()

        # samLis = [(None, 0.8095703125, 532), (None, 1.568359375, 531), (None, 1.9521484375, 530), (None, 2.9609375, 529), (None, 3.4697265625, 529), (None, 4.103515625, 528)]
        # self.setPointVars(samLis)
        # global pointVars
        # print(pointVars)

    def setPointVars(self, pointList):
        global replacements
        pointVars = "["
        ranges = {'min':0, 'max':0}
        for itr, i in enumerate(pointList):
            ang = i[1]
            dis = i[2]
            theta_rad = pi/2 - radians(ang)
            x = round(dis*cos(theta_rad))
            y = round(dis*sin(theta_rad))
            if (x<ranges['min']):
                ranges['min'] = x
            if (x>ranges['max']):
                ranges['max'] = x
            if (y<ranges['min']):
                ranges['min'] = y
            if (y>ranges['max']):
                ranges['max'] = y
            comma = ""
            if itr < len(pointList)-1:
                comma = ","
            pointVars += "{x:" + str(x) + ",y:" + str(y) + "}" + comma
        pointVars += "]"
        replacements['PYxyValues'] = pointVars
        margin = 1.05
        replacements['PYxMin'] = round(ranges['min'] * margin)
        replacements['PYxMax'] = round(ranges['max'] * margin)
        replacements['PYyMin'] = round(ranges['min'] * margin)
        replacements['PYyMax'] = round(ranges['max'] * margin)


    class Serv(BaseHTTPRequestHandler):
        global replacements, abspath
        def do_GET(self):
            indexName = 'index.html'
            path = abspath + "/" + indexName
            try:
                file_to_open = open(path).read()
                for i in replacements:
                    file_to_open = file_to_open.replace(i, str(replacements[i]))
                self.send_response(200)
            except Exception as e:
                file_to_open = "File not found"
                self.send_response(404)
            self.end_headers()
            self.wfile.write(bytes(file_to_open, 'utf-8'))

        def do_POST(self):
            pass

# web_server = web_server()
# samLis = [(None, 0.8095703125, 532), (None, 85.243324234, 531), (None, 173.94058345, 530), (None, 280.9609375, 529), (None, 3.4697265625, 529), (None, 4.103515625, 528)]
# web_server.setPointVars(samLis)
