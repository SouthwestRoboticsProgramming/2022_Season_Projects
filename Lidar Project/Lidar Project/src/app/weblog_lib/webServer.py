#!/usr/bin/env python3
"""
Very simple HTTP server in python for logging requests
Usage::
    ./server.py [<port>]
"""
from http.server import BaseHTTPRequestHandler, HTTPServer
import logging
import threading
from sys import argv
import os

data = "NULL"

class webServer:

    def __init__(self, port=8080):
        # self.setData("NULL")
        def run(server_class=HTTPServer):
            server_address = ('', port)
            handler_class = self.HTTPRequestHandler
            httpd = server_class(server_address, handler_class)
            print('Starting httpd...\n')
            try:
                httpd.serve_forever()
            except KeyboardInterrupt:
                pass
            httpd.server_close()
            print('Stopping httpd...\n')    
        threading.Thread(target=run).start()

    def log(self, n_data):
        global data
        data += "<br>" + n_data.replace("\n", "<br>")
    
    def clear(self):
        global data
        data = ""

    class HTTPRequestHandler(BaseHTTPRequestHandler):
        def _set_response(self):
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()

        def do_GET(self):
            # print("do_GET")
            global data
            self._set_response()
            self.wfile.write(("<body><p>" + data + "</p>").encode())

        def do_POST(self):
            # print("do_POST")
            content_length = int(self.headers['Content-Length']) # <--- Gets the size of data
            post_data = self.rfile.read(content_length) # <--- Gets the data itself
            # print("POST request,\nPath: %s\nHeaders:\n%s\n\nBody:\n%s\n", str(self.path), str(self.headers), str(post_data)[2:len(str(post_data))-1])
            self._set_response()
            self.wfile.write("POST request for {}".format(self.path).encode('utf-8'))

# webServer = webServer()
# webServer.log("Sample A data \nSample B data")