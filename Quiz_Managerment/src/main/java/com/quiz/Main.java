package com.quiz;

import com.formdev.flatlaf.FlatLightLaf;
import com.quiz.ui.LoginFrame;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Main class để khởi chạy ứng dụng Quiz Management System
 */
public class Main {
    public static void main(String[] args) {
        // Thiết lập Look and Feel hiện đại
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            applyModernTheme();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Khởi chạy giao diện đăng nhập
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }

    private static void applyModernTheme() {
        // Bảng màu chủ đạo (xanh dương dịu mắt + bo góc hiện đại)
        Color accent = new Color(59, 130, 246);      // #3B82F6
        Color success = new Color(34, 197, 94);      // #22C55E
        Color warning = new Color(245, 158, 11);     // #F59E0B
        Color danger = new Color(239, 68, 68);       // #EF4444
        Color selectionBg = new Color(59, 130, 246); // nhạt của accent

        // Bo góc và trọng số focus
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Component.focusWidth", 1);

        // Màu nhấn và vùng chọn
        UIManager.put("Component.focusColor", new ColorUIResource(accent));
        UIManager.put("Button.default.background", new ColorUIResource(accent));
        UIManager.put("Button.default.foreground", new ColorUIResource(Color.BLACK));

        UIManager.put("Table.selectionBackground", new ColorUIResource(selectionBg));
        UIManager.put("List.selectionBackground", new ColorUIResource(selectionBg));
        UIManager.put("Tree.selectionBackground", new ColorUIResource(selectionBg));

        // Scrollbar mềm mại hơn
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.trackArc", 999);

        // Màu dùng chung (có thể dùng khi cần)
        UIManager.put("AppColors.success", new ColorUIResource(success));
        UIManager.put("AppColors.warning", new ColorUIResource(warning));
        UIManager.put("AppColors.danger", new ColorUIResource(danger));
        UIManager.put("AppColors.accent", new ColorUIResource(accent));
    }
}
