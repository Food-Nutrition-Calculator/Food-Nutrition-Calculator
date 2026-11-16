package nutritrack.utils;

import java.awt.Color;
import java.awt.Font;

/**
 * Common color palette, typography, and styling constants for Java Swing UI components.
 */
public class UIConstants {

    private UIConstants() { }

    // Color Palette
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);     // Clean Blue
    public static final Color PRIMARY_DARK = new Color(31, 97, 141);
    public static final Color ACCENT_COLOR = new Color(46, 204, 113);      // Emerald Green
    public static final Color DANGER_COLOR = new Color(231, 76, 60);       // Soft Red
    public static final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Light Gray Background
    public static final Color PANEL_BACKGROUND = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);        // Dark Charcoal
    public static final Color TEXT_MUTED = new Color(127, 140, 141);

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_METRIC_VALUE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_METRIC_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
}
