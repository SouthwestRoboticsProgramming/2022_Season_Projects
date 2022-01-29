package frc.shufflewood.tools;

import frc.shufflewood.gui.GuiContext;

public class StartingStateMenu implements Tool {
    private enum StartingLocation {
        PLAYER_SIDE,
        CLIMB_SIDE
    }

    private StartingLocation startingLocation;

    @Override
    public void draw(GuiContext gui) {
        gui.begin("Starting State");

        boolean changed = false;

        boolean[] b = new boolean[1];
        b[0] = startingLocation == StartingLocation.PLAYER_SIDE;
        gui.text("Starting tarmac:");
        gui.checkbox(b); gui.sameLine(); gui.text("Player side");
        if (b[0]) startingLocation = StartingLocation.PLAYER_SIDE;
        b[0] = startingLocation == StartingLocation.CLIMB_SIDE;
        gui.checkbox(b); gui.sameLine(); gui.text("Climb side");
        if (b[0]) startingLocation = StartingLocation.CLIMB_SIDE;

        gui.end();
    }
}
