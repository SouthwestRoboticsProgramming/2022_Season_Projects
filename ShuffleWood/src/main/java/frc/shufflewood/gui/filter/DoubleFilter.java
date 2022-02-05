package frc.shufflewood.gui.filter;

public final class DoubleFilter implements TextFilter {
    @Override
    public boolean isAllowed(String str) {
        // Probably a better way to do this
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
