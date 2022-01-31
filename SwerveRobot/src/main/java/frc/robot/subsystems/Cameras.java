package frc.robot.subsystems;

import java.io.DataInputStream;
import java.io.IOException;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.messenger.client.MessageDispatcher;
import frc.messenger.client.MessageHandler;

public class Cameras extends SubsystemBase {
  public Cameras(MessageDispatcher dispatch) {
    super();

    MessageHandler handler = new MessageHandler()
        .setHandler(this::onMessage);
    //    .listen("some message type");
    
    dispatch.addMessageHandler(handler);
  }

  private void onMessage(String type, DataInputStream in) throws IOException {

  }

  public double getHubTargetAngle() {
    // Ryan do your stuff
    return 0.0;
  }

  public double getBallPosition() {
    // Mason fix stereo, Ryan do stuff
    return 4.1;
  }

  // TODO: Do the climber camera, not really sure what I'm trying to get out of it yet

  @Override
  public void periodic() {

  }
}
