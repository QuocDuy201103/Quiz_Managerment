package com.quiz.ui.panels;

import com.quiz.bus.ExamResultService;
import com.quiz.model.ExamAttemptStat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel thống kê: biểu đồ số lần làm theo đề thi
 */
public class StatisticsPanel extends JPanel {
    private ExamResultService examResultService;
    private JButton refreshButton;
    private JLabel titleLabel;
    private JScrollPane chartScrollPane;
    private BarChartPanel chartPanel;

    public StatisticsPanel() {
        this.examResultService = new ExamResultService();
        initializeComponents();
        setupLayout();
        loadDataAndRender();
    }

    private void initializeComponents() {
        titleLabel = new JLabel("Thống kê: Số lần làm theo đề thi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        refreshButton = new JButton("Làm mới", com.quiz.ui.IconUtil.load("/images/refresh.png", 16, 16));
        com.quiz.ui.StyleUtil.secondary(refreshButton);
        refreshButton.addActionListener(e -> loadDataAndRender());

        chartPanel = new BarChartPanel();
        chartScrollPane = new JScrollPane(chartPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chartScrollPane.setBorder(BorderFactory.createEmptyBorder());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(new EmptyBorder(0, 0, 10, 0));
        top.add(titleLabel, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.add(refreshButton);
        right.setOpaque(false);
        top.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(chartScrollPane, BorderLayout.CENTER);
    }

    private void loadDataAndRender() {
        List<ExamAttemptStat> stats = examResultService.getAttemptCountsPerExam();
        chartPanel.setData(stats);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private static class BarChartPanel extends JPanel {
        private List<ExamAttemptStat> data;

        public void setData(List<ExamAttemptStat> data) {
            this.data = data;
            updatePreferredSize();
        }

        private void updatePreferredSize() {
            int barWidth = 60;
            int gap = 24;
            int leftMargin = 80;
            int rightMargin = 40;
            int width = leftMargin + rightMargin;
            if (data != null) {
                width += data.size() * (barWidth + gap);
            }
            width = Math.max(width, 800);
            setPreferredSize(new Dimension(width, 420));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);

            int topMargin = 30;
            int bottomMargin = 90;
            int leftMargin = 80;
            int rightMargin = 40;

            // Axes
            g2.setColor(new Color(200, 200, 200));
            g2.drawLine(leftMargin, height - bottomMargin, width - rightMargin, height - bottomMargin); // X axis
            g2.drawLine(leftMargin, height - bottomMargin, leftMargin, topMargin); // Y axis

            if (data == null || data.isEmpty()) {
                g2.setColor(new Color(120, 120, 120));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                String msg = "Chưa có dữ liệu thống kê";
                int tw = g2.getFontMetrics().stringWidth(msg);
                g2.drawString(msg, (width - tw) / 2, height / 2);
                g2.dispose();
                return;
            }

            int max = 0;
            for (ExamAttemptStat s : data) {
                if (s.getAttempts() > max) max = s.getAttempts();
            }
            max = Math.max(max, 1);

            // Grid lines and Y labels (5 steps)
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(230, 230, 230));
            int steps = 5;
            for (int i = 0; i <= steps; i++) {
                int y = height - bottomMargin - (i * (height - bottomMargin - topMargin) / steps);
                g2.drawLine(leftMargin, y, width - rightMargin, y);
                g2.setColor(new Color(100, 100, 100));
                String label = String.valueOf((int) Math.round(max * (i / (double) steps)));
                int lw = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, leftMargin - lw - 8, y + 4);
                g2.setColor(new Color(230, 230, 230));
            }

            int barWidth = 60;
            int gap = 24;
            int x = leftMargin + gap;

            for (ExamAttemptStat s : data) {
                double ratio = s.getAttempts() / (double) max;
                int barHeight = (int) Math.round(ratio * (height - bottomMargin - topMargin));
                int y = height - bottomMargin - barHeight;

                // Bar
                g2.setColor(new Color(59, 130, 246));
                g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);
                g2.setColor(new Color(37, 99, 235));
                g2.drawRoundRect(x, y, barWidth, barHeight, 8, 8);

                // Value label above bar
                String value = String.valueOf(s.getAttempts());
                g2.setColor(new Color(30, 30, 30));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                int vw = g2.getFontMetrics().stringWidth(value);
                g2.drawString(value, x + (barWidth - vw) / 2, y - 6);

                // X label (exam title), wrap/truncate
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(new Color(60, 60, 60));
                String title = s.getExamTitle() != null ? s.getExamTitle() : ("ID " + s.getExamId());
                String wrapped = wrapTitle(title, barWidth + 10, g2);
                drawWrappedCentered(g2, wrapped, x + barWidth / 2, height - bottomMargin + 18, 14);

                x += barWidth + gap;
            }

            g2.dispose();
        }

        private String wrapTitle(String title, int maxWidth, Graphics2D g2) {
            if (g2.getFontMetrics().stringWidth(title) <= maxWidth) return title;
            String[] parts = title.split("\\s+");
            StringBuilder line = new StringBuilder();
            StringBuilder all = new StringBuilder();
            for (String p : parts) {
                String test = line.length() == 0 ? p : line + " " + p;
                if (g2.getFontMetrics().stringWidth(test) <= maxWidth) {
                    line = new StringBuilder(test);
                } else {
                    if (all.length() > 0) all.append("\n");
                    all.append(line);
                    line = new StringBuilder(p);
                }
            }
            if (line.length() > 0) {
                if (all.length() > 0) all.append("\n");
                all.append(line);
            }
            return all.toString();
        }

        private void drawWrappedCentered(Graphics2D g2, String text, int centerX, int startY, int lineSpacing) {
            String[] lines = text.split("\n");
            int y = startY;
            for (String line : lines) {
                int w = g2.getFontMetrics().stringWidth(line);
                g2.drawString(line, centerX - w / 2, y);
                y += lineSpacing;
            }
        }
    }
}


