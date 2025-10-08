package com.quiz.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public final class IconUtil {
    private IconUtil() {}

    public static ImageIcon load(String resourcePath, int width, int height) {
        URL url = IconUtil.class.getResource(resourcePath);
        if (url == null) {
            return null;
        }
        ImageIcon icon = new ImageIcon(url);
        if (width > 0 && height > 0) {
            Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(image);
        }
        return icon;
    }
}


