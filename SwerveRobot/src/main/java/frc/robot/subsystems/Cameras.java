package frc.robot.subsystems;

import java.io.DataInputStream;
import java.io.IOException;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.messenger.client.MessageDispatcher;
import frc.messenger.client.MessageHandler;
import frc.robot.util.ShuffleWood;

public class Cameras extends SubsystemBase {
  public Cameras(MessageDispatcher dispatch) {
    super();

    MessageHandler handler = new MessageHandler()
        .setHandler(this::onMessage)
        .listen("Vision:Ball_Position")
        .listen("Vision:Hub_Measurements");
    
    dispatch.addMessageHandler(handler);
  }

  private void onMessage(String type, DataInputStream in) throws IOException {
    if (type.equals("Vision:Hub_Measurements")) {
      boolean good = in.readBoolean();
      if (good) {
        double xAngle = in.readDouble();
        double distance = in.readDouble();

        ShuffleWood.show("Hub XAngle", xAngle);
        ShuffleWood.show("Hub Distance", distance);
      } else {
        ShuffleWood.show("Hub XAngle", "bad");
        ShuffleWood.show("Hub Distance", "bad");
      }
    } else if (type.equals("Vision:Ball_Position")) {
      boolean good = in.readBoolean();
      if (good) {
        double x = in.readDouble();
        double z = in.readDouble();

        ShuffleWood.show("Ball X", x);
        ShuffleWood.show("Ball Z", z);
      } else {
        ShuffleWood.show("Ball X", "bad");
        ShuffleWood.show("Ball Z", "bad");
      }
    }
  }

  public double getHubTargetAngle() {
    // Ryan do your stuff
    return 0.0;
  }

  public double getBallPosition() {
    // Ryan do stuff
    return 4.1;
  }

  public double getClimberAngle() {
    return 24.0;
  }

  // TODO: Do the climber camera, not really sure what I'm trying to get out of it yet

  @Override
  public void periodic() {

  }
}
