package nutritrack.utils;

import java.awt.Color;
import java.awt.Font;

/**
 * Common color palette, typography, and styling constants for Java Swing UI components.
 */
public class UIConstants {

    private UIConstants() { }

    // Color Palette
    public static final Color PRIMARY_COLOR = new Color(40, 67, 91);
    public static final Color PRIMARY_DARK = new Color(31, 53, 73);
    public static final Color ACCENT_COLOR = new Color(40, 67, 91);
    public static final Color DANGER_COLOR = new Color(40, 67, 91);
    public static final Color BACKGROUND_COLOR = new Color(242, 245, 248);
    public static final Color PANEL_BACKGROUND = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(39, 50, 61);
    public static final Color TEXT_MUTED = new Color(100, 112, 124);
    public static final Color BORDER_COLOR = new Color(215, 222, 229);

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_METRIC_VALUE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_METRIC_LABEL = new Font("Segoe UI", Font.PLAIN, 15);
}
