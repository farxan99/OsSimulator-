package aether.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import aether.core.AetherKernel;

public class CellMonitor {

    private static final Color ACCENT_COLOR = new Color(189, 0, 255); // Neon Purple

    private int cellSize;
    private LinkedHashMap<Integer, Integer> cellRegistry;
    private JTextArea monitorOutput;
    private int frameLimit;
    private JPanel contentPanel;
    private Runnable backAction;

    public CellMonitor(AetherKernel kernel, Runnable backAction) {
        this.backAction = backAction;
        this.cellRegistry = new LinkedHashMap<>(16, 0.75f, true);
        this.cellSize = 4096;
        this.frameLimit = 5;

        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    // Update resource path for NetBeans compatibility
                    ImageIcon bg = new ImageIcon(getClass().getResource("/aether/resources/bg.jpg"));
                    g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);

                    // Add a dark overlay for readability
                    g.setColor(new Color(0, 0, 0, 180));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } catch (Exception e) {
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(0, 0, new Color(10, 14, 20), 0, getHeight(),
                            new Color(26, 31, 38));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        contentPanel.setLayout(new BorderLayout());

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(350);
        mainSplit.setDividerSize(0);
        mainSplit.setOpaque(false);
        mainSplit.setBorder(null);

        JPanel navPanel = createNavigationPanel(kernel);
        navPanel.setOpaque(false);

        monitorOutput = new JTextArea();
        monitorOutput.setEditable(false);
        monitorOutput.setFont(new Font("Consolas", Font.PLAIN, 20));
        monitorOutput.setOpaque(false);
        monitorOutput.setForeground(new Color(200, 200, 200));
        monitorOutput.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JScrollPane scrollArea = new JScrollPane(monitorOutput);
        scrollArea.setOpaque(false);
        scrollArea.getViewport().setOpaque(false);
        scrollArea.setBorder(null);

        mainSplit.setLeftComponent(navPanel);
        mainSplit.setRightComponent(scrollArea);

        contentPanel.add(mainSplit, BorderLayout.CENTER);

        updateMetrics();
    }

    public JPanel getPanel() {
        return contentPanel;
    }

    private JPanel createNavigationPanel(AetherKernel kernel) {
        JPanel nav = new JPanel(new GridLayout(12, 1, 10, 10));
        nav.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

        JLabel title = new JLabel("CELL MONITOR", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ACCENT_COLOR);
        nav.add(title);

        nav.add(createModernButton("Back to Shell", e -> {
            if (backAction != null) backAction.run();
        }));

        nav.add(createModernButton("Provision Cells", e -> provisionCells(kernel)));
        nav.add(createModernButton("Flux Cycle (LRU)", e -> fluxIteration()));

        return nav;
    }

    private JButton createModernButton(String label, ActionListener action) {
        JButton btn = new JButton(label.toUpperCase());
        btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(30, 35, 45));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_COLOR);
                btn.setForeground(Color.BLACK);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(30, 35, 45));
                btn.setForeground(Color.WHITE);
            }
        });
        return btn;
    }

    private void updateMetrics() {
        StringBuilder sb = new StringBuilder();
        sb.append(">> OPTIMUSPRIME CELL REGISTRY STATUS\n");
        sb.append("------------------------------------------------\n");

        if (cellRegistry.isEmpty()) {
            sb.append("\n[!] No cells currently active in the quantum field.\n");
        } else {
            for (Map.Entry<Integer, Integer> entry : cellRegistry.entrySet()) {
                sb.append(String.format("Block %04X -> Owner Node %04X\n", entry.getKey(), entry.getValue()));
            }
        }

        sb.append("\n------------------------------------------------\n");
        sb.append("Active Flux Channels: ").append(cellRegistry.size()).append(" / ").append(frameLimit);

        monitorOutput.setText(sb.toString());
    }

    private void provisionCells(AetherKernel kernel) {
        String input = JOptionPane.showInputDialog("Enter Allocation Magnitude:");
        if (input == null)
            return;

        try {
            int magnitude = Integer.parseInt(input);
            int cellsNeeded = (int) Math.ceil((double) magnitude / cellSize);
            int nodeID = new Random().nextInt(0xFFFF);

            for (int i = 0; i < cellsNeeded; i++) {
                cellRegistry.put(new Random().nextInt(0xFFFF), nodeID);
            }

            JOptionPane.showMessageDialog(null, "Provisioned " + cellsNeeded + " quantum cells.");
            updateMetrics();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Input Matrix Corrupted.");
        }
    }

    private void fluxIteration() {
        if (cellRegistry.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Flux vacuum detected.");
            return;
        }

        Iterator<Map.Entry<Integer, Integer>> it = cellRegistry.entrySet().iterator();
        Map.Entry<Integer, Integer> expelled = it.next();
        it.remove();

        JOptionPane.showMessageDialog(null, "Cell block " + expelled.getKey() + " returned to void (LRU).");
        updateMetrics();
    }
}
