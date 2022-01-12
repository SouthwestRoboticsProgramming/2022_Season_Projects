package frc.shufflewood.gui.filter;

public class RangedIntegerFilter implements TextFilter {
    private final int min, max;

    public RangedIntegerFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isAllowed(String text) {
        // Probably a better way to do this
        try {
            int i = Integer.parseInt(text);
            return i >= min && i <= max;
        } catch (Exception e) {
            return false;
        }
    }
}
