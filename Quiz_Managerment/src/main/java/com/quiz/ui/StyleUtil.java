package com.quiz.ui;

import javax.swing.*;
import java.awt.*;

public final class StyleUtil {
    private static final Color COLOR_PRIMARY = new Color(59, 130, 246);   // blue
    private static final Color COLOR_SUCCESS = new Color(34, 197, 94);   // green
    private static final Color COLOR_WARNING = new Color(245, 158, 11);  // amber
    private static final Color COLOR_DANGER  = new Color(239, 68, 68);   // red
    private static final Color COLOR_SECOND  = new Color(108, 117, 125); // gray

    private StyleUtil() {}

    public static void makeRound(JButton button) {
        button.putClientProperty("JButton.buttonType", "roundRect");
    }

    public static void square(JButton button) {
        button.putClientProperty("JButton.buttonType", "square");
    }

    public static void primary(JButton button) {
        style(button, COLOR_PRIMARY, Color.WHITE);
    }

    public static void success(JButton button) {
        style(button, COLOR_SUCCESS, Color.WHITE);
    }

    public static void warning(JButton button) {
        style(button, COLOR_WARNING, Color.BLACK);
    }

    public static void danger(JButton button) {
        style(button, COLOR_DANGER, Color.WHITE);
    }

    public static void secondary(JButton button) {
        style(button, COLOR_SECOND, Color.WHITE);
    }

    private static void style(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        makeRound(button);
    }
}


