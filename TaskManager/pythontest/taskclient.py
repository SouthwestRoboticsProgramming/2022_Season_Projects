import socket as so
import select
import struct

class TaskMessenger:
    def __init__(self, messageServerHost, messageServerPort, name):
        socket = so.socket(so.AF_INET, so.SOCK_STREAM)
        socket.connect((messageServerHost, messageServerPort))
        
        self.socket = socket
        self.messageCallback = lambda mType, data: None

    def sendMessage(self, mType, data):
        typeLen = struct.pack(">h", len(mType))
        dataLen = struct.pack(">i", len(data))
        encoded = typeLen + mType.encode("utf-8") + dataLen + data
        encodedLen = len(encoded)

        totalSent = 0
        while totalSent < encodedLen:
            sent = self.socket.send(encoded[totalSent:])
            if sent == 0:
                raise RuntimeError("socket disconnected")
            totalSent += sent

    def read(self):
        readable = select.select([self.socket], [], [])
        for socket in readable:
            if socket == self.socket:
                self._handleRead()

    def disconnect(self):
        self.socket.close()

    def setMessageCallback(self, callback):
        self.messageCallback = callback

    def _read(self, count):
        data = ''
        while len(data) < count:
            data += self.socket.recv(count)
        return data

    def _handleRead(self):
        typeLen = struct.unpack(">h", self._read(2))[0]
        mType = self._read(typeLen).decode("utf-8")
        dataLen = struct.unpack(">i", self._read(4))[0]
        data = self._read(dataLen)
        self.messageCallback(mType, data)
