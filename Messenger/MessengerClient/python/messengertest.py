from messengerclient import MessengerClient
import time

client = MessengerClient("localhost", 8341, "Tester")

def message_callback(type, data):
    print("Got " + type + " with data:")
    print(data.decode("utf-8"))
client.set_callback(message_callback)
client.listen("Test")

client.send_message("Test", "Hello, Messenger!".encode("utf-8"))

while True:
    client.read()
    time.sleep(0.025)