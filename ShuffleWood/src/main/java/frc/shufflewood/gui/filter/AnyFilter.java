package frc.shufflewood.gui.filter;

public class AnyFilter implements TextFilter {
    @Override
    public boolean isAllowed(String text) {
        return true;
    }
}
