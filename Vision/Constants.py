class Constants:
    EXPERIMENTAL = True
    SHOW_VISUALIZERS = False

    HUB_CAMERA = 0 #FIXME
    HUB_TARGET_DIAMETER = 53.75 #FIXME

    BALL_DETECTION_CAMERA_LEFT = 0 #FIXME
    BALL_DETECTION_CAMERA_RIGHT = 2 #FIXME
    BALL_DETECTION_BASELINE = 20 #FIXME

    CLIMBER_CAMERA = 0 #FIXME

    BOUNDING_COLOR = (0,255,0)

    USBCAMERA_ALPHA = 55 # TODO: Find horizonatl FOV
    USBCAMERA_BETA = 41.2 # TODO: Find vertical FOV

    # Module 1
    MODULE_1_CAMID = 0

    # Messages
    MESSAGE_HUB_START = "Vision:HubStart"
    MESSAGE_BALL_DETECT_START = "Vision:BallDetectStart"
    MESSAGE_CLIMBER_START = "Vision:ClimberStart"
    MESSAGE_HUB_STOP = "Vision:HubStop"
    MESSAGE_BALL_DETECT_STOP = "Vision:BallDetectStop"
    MESSAGE_CLIMBER_STOP = "Vision:ClimberStop"