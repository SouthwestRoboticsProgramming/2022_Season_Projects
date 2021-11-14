import socket as so
import select
import struct

class TaskMessenger:
    def __init__(self, messageServerHost, messageServerPort, name):
        socket = so.socket(so.AF_INET, so.SOCK_STREAM)
        socket.connect((messageServerHost, messageServerPort))
        
        self.socket = socket
        self.messageCallback = lambda mType, data: None
        self.heartbeat = struct.pack(">h", len("_Heartbeat")) + "_Heartbeat" + struct.pack(">i", 0)

        # Identify ourselves to the message server
        self._write(struct.pack(">h", len(name)) + name.encode("utf-8"))

    def sendMessage(self, mType, data):
        typeLen = struct.pack(">h", len(mType))
        dataLen = struct.pack(">i", len(data))
        encoded = typeLen + mType.encode("utf-8") + dataLen + data
        self._write(encoded)

    def read(self):
        self._write(self.heartbeat)

        readable = select.select([self.socket], [], [], 0)
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

    def _write(self, data):
        dataLen = len(data)
        totalSent = 0
        while totalSent < dataLen:
            sent = self.socket.send(data[totalSent:])
            if sent == 0:
                raise RuntimeError("socket disconnected")
            totalSent += sent

    def _handleRead(self):
        typeLen = struct.unpack(">h", self._read(2))[0]
        mType = self._read(typeLen).decode("utf-8")
        dataLen = struct.unpack(">i", self._read(4))[0]
        data = self._read(dataLen)
        self.messageCallback(mType, data)
