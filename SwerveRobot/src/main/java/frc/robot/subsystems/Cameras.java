package frc.robot.subsystems;

import java.io.DataInputStream;
import java.io.IOException;

import frc.messenger.client.MessageDispatcher;
import frc.messenger.client.MessageHandler;
import frc.robot.util.ShuffleWood;

public class Cameras extends Subsystem {

  private boolean hub_good;
  private double hub_x;
  private double hub_y;
  
  private boolean ball_good;
  private double ball_x;
  private double ball_z;

  private boolean climber_good;
  private double climber_angle;

  public Cameras(MessageDispatcher dispatch) {
    super();

    MessageHandler handler = new MessageHandler()
        .setHandler(this::onMessage)
        .listen("Vision:Ball_Position")
        .listen("Vision:Climber_Angle")
        .listen("Vision:Hub_Measurements");
    
    dispatch.addMessageHandler(handler);
  }

  private void onMessage(String type, DataInputStream in) throws IOException {
    if (type.equals("Vision:Hub_Measurements")) {
      boolean good = in.readBoolean();
      if (good) {
        hub_x = in.readDouble();
        hub_y = in.readDouble();

        ShuffleWood.show("Hub XAngle", xAngle);
        ShuffleWood.show("Hub Distance", distance);
      } else {
        ShuffleWood.show("Hub XAngle", "bad");
        ShuffleWood.show("Hub Distance", "bad");
      }
    } else if (type.equals("Vision:Ball_Position")) {
      boolean good = in.readBoolean();
      ball_good = good;
      if (good) {
        ball_x = in.readDouble();
        ball_z = in.readDouble();

        ShuffleWood.show("Ball X", x);
        ShuffleWood.show("Ball Z", z);
      } else {
        ShuffleWood.show("Ball X", "bad");
        ShuffleWood.show("Ball Z", "bad");
      }
    } else if (type.equals("Vision:Climber_Angle")) {
      boolean good = in.readBoolean();
      climber_good = good;
      if (good) {
        double yAngle = in.readDouble();

        climber_angle = yAngle;

        ShuffleWood.show("Climber Angle", yAngle);
      } else {
        ShuffleWood.show("Climber Angle", "bad");
      }
    }
  }

  public double getHubAngle() {
    // Ryan do your stuff
    if (hub_good) {
      return hub_xAngle;
    }
    return 360.0;
  }

  public double getHubDistance() {
    if (hub_good) {
      return hub_distance;
    }
    return -1.0;
  }

  public double getBallX() {
    if (ball_good) {
      return ball_x;
    }
    return 50.0;
  }

  public double getBallZ() {
    if (ball_good) {
      return ball_z;
    }
    return 50.0;
  }

  public double getClimberAngle() {
    if (climber_good) {
      return climber_angle;
    }
    return 2147483647;
  }

  // TODO: Do the climber camera, not really sure what I'm trying to get out of it yet

  @Override
  public void robotPeriodic() {

  }
}
