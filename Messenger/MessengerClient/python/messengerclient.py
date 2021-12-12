import socket
import select
import struct

class MessengerClient:
    def __init__(self, host, port, name):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((host, port))
        self.callback = lambda type, data: None

        self._send(self._encode_string(name))

    def send_message(self, type, data):
        encoded_type = self._encode_string(type)
        encoded_data_len = struct.pack(">i", len(data))
        encoded = encoded_type + encoded_data_len + data

        packet_len = struct.pack(">i", len(encoded))
        packet = packet_len + encoded

        self._send(packet)

    def listen(self, type):
        self.send_message("_Listen", self._encode_string(type))

    def unlisten(self, type):
        self.send_message("_Unlisten", self._encode_string(type))

    def set_callback(self, callback):
        self.callback = callback

    def read(self):
        self.send_message("_Heartbeat", b"")

        while self._available():
            self._read_message()

    def disconnect(self):
        self.sock.shutdown()
        self.sock.close()

    def _read(self, count):
        data = b""
        while len(data) < count:
            data += self.sock.recv(count - len(data))
        return data

    def _send(self, data):
        total = 0
        while total < len(data):
            sent = self.sock.send(data[total:])
            if sent == 0:
                raise RuntimeError("socket disconnected")
            total += sent

    def _encode_string(self, str):
        encoded_len = struct.pack(">h", len(str))
        return encoded_len + str.encode("utf-8")

    def _available(self):
        readable = select.select([self.sock], [], [])[0]

        for sock in readable:
            if sock == self.sock:
                return True

        return False

    def _read_message(self):
        length = struct.unpack(">i", self._read(4))[0]
        data = self._read(length)

        type_len = struct.unpack(">h", data[0:2])[0]
        type = data[2:(type_len + 2)].decode("utf-8")
        data_len = struct.unpack(">i", data[(type_len + 2):(type_len + 6)])[0]
        message_data = data[(type_len + 6):(type_len + data_len + 6)]

        self.callback(type, message_data)