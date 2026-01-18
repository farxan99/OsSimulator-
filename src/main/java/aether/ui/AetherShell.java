package aether.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.table.TableCellRenderer;
import java.util.*;

import aether.core.AetherKernel;
import aether.model.process.TaskNode;

public class AetherShell extends JFrame {
    private AetherKernel kernel;
    private JTable taskTable;
    private DefaultTableModel taskModel;
    private JFrame rootFrame;

    // Aether Aesthetic Palette
    private static final Color BG_DARK = new Color(10, 14, 20);
    private static final Color SURFACE = new Color(26, 31, 38);
    private static final Color ACCENT_CYAN = new Color(0, 255, 209);
    private static final Color ACCENT_PURPLE = new Color(189, 0, 255);
    private static final Color TEXT_MAIN = new Color(240, 240, 240);

    public AetherShell(AetherKernel kernel) {
        this.kernel = kernel;
        initInterface();
    }

    private void initInterface() {
        rootFrame = new JFrame("AetherOS | Next-Gen Kernel Shell");
        rootFrame.setUndecorated(true);
        rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rootFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainLayer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background Gradient
                GradientPaint bg = new GradientPaint(0, 0, BG_DARK, getWidth(), getHeight(), new Color(20, 10, 30));
                g2.setPaint(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle grid pattern
                g2.setColor(new Color(255, 255, 255, 10));
                for (int i = 0; i < getWidth(); i += 40)
                    g2.drawLine(i, 0, i, getHeight());
                for (int i = 0; i < getHeight(); i += 40)
                    g2.drawLine(0, i, getWidth(), i);
            }
        };

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50));

        JLabel brand = new JLabel("AETHER OS");
        brand.setFont(new Font("Orbitron", Font.BOLD, 42));
        brand.setForeground(ACCENT_CYAN);
        header.add(brand, BorderLayout.WEST);

        JLabel status = new JLabel("KERNEL v4.0.2 // STABLE", JLabel.RIGHT);
        status.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        status.setForeground(new Color(255, 255, 255, 150));
        header.add(status, BorderLayout.EAST);

        mainLayer.add(header, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(6, 1, 15, 15));
        sidebar.setOpaque(false);
        sidebar.setBorder(BorderFactory.createEmptyBorder(50, 50, 100, 20));

        sidebar.add(createAetherButton("VECTOR CORE", e -> showProcessManager()));
        sidebar.add(createAetherButton("MEMORY HUB", e -> openMemoryMonitor()));
        sidebar.add(createAetherButton("SYNC LINK", e -> openSyncLink()));
        sidebar.add(createAetherButton("SHUTDOWN", e -> System.exit(0)));

        mainLayer.add(sidebar, BorderLayout.WEST);

        // Welcome Hero
        JPanel hero = new JPanel(new GridBagLayout());
        hero.setOpaque(false);
        JLabel welcomeMsg = new JLabel("SYSTEMS NOMINAL. READY FOR OPERATION.");
        welcomeMsg.setFont(new Font("Segoe UI Light", Font.PLAIN, 24));
        welcomeMsg.setForeground(TEXT_MAIN);
        hero.add(welcomeMsg);

        mainLayer.add(hero, BorderLayout.CENTER);

        rootFrame.add(mainLayer);
        rootFrame.setVisible(true);
    }

    private void showProcessManager() {
        rootFrame.getContentPane().removeAll();

        JPanel pmPanel = new JPanel(new BorderLayout());
        pmPanel.setBackground(BG_DARK);

        // Header for PM
        JPanel pmHead = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pmHead.setOpaque(false);
        JButton backBtn = createAetherButton("BACK", e -> initInterface());
        pmHead.add(backBtn);
        pmPanel.add(pmHead, BorderLayout.NORTH);

        // Split Layout
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(300);
        split.setDividerSize(1);
        split.setOpaque(false);
        split.setBorder(null);

        // Tool Sidebar
        JPanel tools = new JPanel(new GridLayout(10, 1, 10, 10));
        tools.setOpaque(false);
        tools.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tools.add(createAetherButton("ADD VECTOR", e -> createTask()));
        tools.add(createAetherButton("KILL VECTOR", e -> destroyTask()));
        tools.add(createAetherButton("SUSPEND", e -> suspendTask()));
        tools.add(createAetherButton("RESUME", e -> resumeTask()));
        tools.add(createAetherButton("DISPATCH", e -> dispatchBatch()));

        // Data Table
        String[] cols = { "ID", "STATE", "PRIORITY", "BURST", "ARRIVAL", "OWNER" };
        taskModel = new DefaultTableModel(cols, 0);
        taskTable = new JTable(taskModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? BG_DARK : SURFACE);
                } else {
                    c.setBackground(ACCENT_PURPLE);
                }
                return c;
            }
        };
        taskTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskTable.setForeground(Color.WHITE);
        taskTable.setRowHeight(35);
        taskTable.setShowGrid(false);
        taskTable.getTableHeader().setBackground(SURFACE);
        taskTable.getTableHeader().setForeground(ACCENT_CYAN);
        taskTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(taskTable);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(null);

        split.setLeftComponent(tools);
        split.setRightComponent(scroll);
        pmPanel.add(split, BorderLayout.CENTER);

        rootFrame.add(pmPanel);
        rootFrame.revalidate();
        rootFrame.repaint();
        refreshTaskList();
    }

    private void openMemoryMonitor() {
        rootFrame.dispose();
        new CellMonitor(kernel);
    }

    private void openSyncLink() {
        SwingUtilities.invokeLater(() -> {
            SyncLink sl = new SyncLink();
            sl.setVisible(true);
        });
    }

    private JButton createAetherButton(String text, ActionListener action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(ACCENT_PURPLE.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT_PURPLE);
                } else {
                    g2.setColor(SURFACE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(ACCENT_CYAN);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        return btn;
    }

    private void createTask() {
        String burst = JOptionPane.showInputDialog("Burst Cycle:");
        String priority = JOptionPane.showInputDialog("Priority Level:");
        if (burst == null || priority == null)
            return;

        try {
            kernel.createTask(Integer.parseInt(burst), 0, Integer.parseInt(priority));
            refreshTaskList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Matrix Mismatch: Invalid Input");
        }
    }

    private void destroyTask() {
        String idStr = JOptionPane.showInputDialog("Target Vector ID:");
        if (idStr == null)
            return;
        kernel.destroyTask(Integer.parseInt(idStr));
        refreshTaskList();
    }

    private void suspendTask() {
        String idStr = JOptionPane.showInputDialog("Suspend Vector ID:");
        if (idStr == null)
            return;
        kernel.suspendTask(Integer.parseInt(idStr));
        refreshTaskList();
    }

    private void resumeTask() {
        String idStr = JOptionPane.showInputDialog("Resume Vector ID:");
        if (idStr == null)
            return;
        kernel.resumeTask(Integer.parseInt(idStr));
        refreshTaskList();
    }

    private void dispatchBatch() {
        String[] options = { "SJF", "FCFS" };
        int choice = JOptionPane.showOptionDialog(null, "Select Logic Gateway", "Dispatch", 0, 0, null, options,
                options[0]);
        if (choice == -1)
            return;

        while (!kernel.getReadyQueue().isEmpty()) {
            TaskNode node = kernel.lowLevelScheduling(options[choice]);
            if (node != null) {
                node.setState("PROCESSING");
                refreshTaskList();
                // Simulation of work
                node.setState("TERMINATED");
                kernel.getTaskTable().remove(node.getProcessID());
            }
        }
        refreshTaskList();
        JOptionPane.showMessageDialog(null, "Batch Processing Complete.");
    }

    private void refreshTaskList() {
        if (taskModel == null)
            return;
        taskModel.setRowCount(0);
        for (TaskNode node : kernel.getTaskTable().values()) {
            taskModel.addRow(new Object[] {
                    node.getProcessID(),
                    node.getState(),
                    node.getPriority(),
                    node.getBurstTime(),
                    node.getArrivalTime(),
                    node.getOwner()
            });
        }
    }

    public static void main(String[] args) {
        AetherKernel kernel = new AetherKernel();
        SwingUtilities.invokeLater(() -> new AetherShell(kernel));
    }
}
