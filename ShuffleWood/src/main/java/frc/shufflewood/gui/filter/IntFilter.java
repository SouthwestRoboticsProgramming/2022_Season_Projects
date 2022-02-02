package frc.shufflewood.gui.filter;

import frc.shufflewood.gui.filter.TextFilter;

public final class IntFilter implements TextFilter {
    @Override
    public boolean isAllowed(String text) {
        // Probably a better way to do this
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
