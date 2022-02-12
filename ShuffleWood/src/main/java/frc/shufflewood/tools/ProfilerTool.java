package frc.shufflewood.tools;

import frc.shufflewood.gui.GuiContext;

public class ProfilerTool implements Tool {
    @Override
    public void draw(GuiContext gui) {
        gui.begin("Profiler");

        float[] values = {
                14, 7, 6, 5, 4, 3, 2
        };
        int[] colors = {
                0xff0000ff,
                0xff00ffff,
                0xff00ff00,
                0xffffff00,
                0xffff0000,
                0xffff00ff,
                0xffffffff
        };
        gui.pie(200, 200, values, colors);

        gui.end();
    }
}
