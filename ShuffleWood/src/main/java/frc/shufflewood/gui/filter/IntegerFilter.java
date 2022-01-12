package frc.shufflewood.gui.filter;

public class IntegerFilter implements TextFilter {
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
