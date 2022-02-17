import socket
import select
import struct

class MessengerClient:
    """
    Allows interfacing with the robot-wide messaging service, Messenger.
    This allows multiple processes across different processors to communicate
    and share data with each other.

    This communication is done through messages. A message consists of a
    string indicating the type and a bytes object, which can contain any
    arbitrary data. A client can choose to listen to any type of message, and
    will only receive messages they have explicitly listened to.

    Author: rmheuer
    """

    def __init__(self, host, port, name, require):
        """
        Creates a new MessengerClient and attempts to connect to the Messenger
        server at the given address. A name is given to help identify the
        client in the server log.

        :param host: host of messenger server
        :param port: port of messenger server
        :param name: name of this client
        :param require: whether to crash if connection fails
        """

        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.settimeout(1)
        self.connected = True
        try:
            self.sock.connect((host, port))
        except socket.timeout:
            if require:
                print("Messenger connection required but failed!")
                quit()
            print("Messenger connection failed, switching to no-op mode")
            self.connected = False
        self.callback = lambda type, data: None

        if self.connected:
            self._send(self._encode_string(name))

    def send_message(self, type, data):
        """
        Sends a message to the Messenger server. The message will be dispatched
        to any other client that are listening to the message type.

        :param type: type of message
        :param data: message data
        """

        if not self.connected:
            return

        encoded_type = self._encode_string(type)
        encoded_data_len = struct.pack(">i", len(data))
        encoded = encoded_type + encoded_data_len + data

        packet_len = struct.pack(">i", len(encoded))
        packet = packet_len + encoded

        self._send(packet)

    def listen(self, type):
        """
        Indicates to the Messenger server that this client would like to listen
        to messages of the given type.

        :param type: message type to listen to
        """

        if not self.connected:
            return

        self.send_message("_Listen", self._encode_string(type))

    def unlisten(self, type):
        """
        Indicates to the Messenger server that this client would no longer like
        to listen to messages of the given type.

        :param type: message type to stop listening to
        """

        if not self.connected:
            return

        self.send_message("_Unlisten", self._encode_string(type))

    def set_callback(self, callback):
        """
        Sets a callback for when a message is received from the Messenger server.

        :param callback: message callback
        """

        self.callback = callback

    def read(self):
        """
        Reads in any available messages and indicates to the server that this client
        is still connected. If this method is not called for too long, the server will
        assume that the connection is dropped and disconnect.
        """

        if not self.connected:
            return

        self.send_message("_Heartbeat", b"")

        while self._available():
            self._read_message()

    def disconnect(self):
        """
        Disconnects from the Messenger server. If this method is not called, the server
        will still detect that the connection is lost, but it is good practice to call
        this method to end the connection safely.
        """

        if not self.connected:
            return

        self.sock.shutdown()
        self.sock.close()

    def _read(self, count):
        # Reads count bytes from the socket

        data = b""
        while len(data) < count:
            data += self.sock.recv(count - len(data))
        return data

    def _send(self, data):
        # Sends data to the socket

        total = 0
        while total < len(data):
            sent = self.sock.send(data[total:])
            if sent == 0:
                raise RuntimeError("socket disconnected")
            total += sent

    def _encode_string(self, str):
        # Encodes a string into length-prefixed UTF-8 encoding

        encoded_len = struct.pack(">h", len(str))
        return encoded_len + str.encode("utf-8")

    def _available(self):
        # Checks if there is data available on the socket

        readable = select.select([self.sock], [], [], 0)[0]

        for sock in readable:
            if sock == self.sock:
                return True

        return False

    def _read_message(self):
        # Reads in a message from the server

        length = struct.unpack(">i", self._read(4))[0]
        data = self._read(length)

        type_len = struct.unpack(">h", data[0:2])[0]
        type = data[2:(type_len + 2)].decode("utf-8")
        data_len = struct.unpack(">i", data[(type_len + 2):(type_len + 6)])[0]
        message_data = data[(type_len + 6):(type_len + data_len + 6)]

        self.callback(type, message_data)