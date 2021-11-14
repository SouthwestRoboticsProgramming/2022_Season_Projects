import time
import taskclient as tc

def messageCallback(mType, data):
    print(mType, data)

client = tc.TaskMessenger("localhost", 8264, "Python Task")
client.setMessageCallback(messageCallback)

count = 1
timer = 100

while True:
    timer = timer - 1
    if timer == 0:
        timer = 100
        client.sendMessage("Python! " + str(count), b'')
        print("sent message")
        count = count + 1
    time.sleep(0.01)
    
