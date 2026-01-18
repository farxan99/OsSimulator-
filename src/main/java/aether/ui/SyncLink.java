package aether.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.concurrent.Semaphore;

interface NexusChannel extends Remote {
    void broadcast(String signal) throws RemoteException;

    String pulse() throws RemoteException;
}

class NexusChannelImpl extends UnicastRemoteObject implements NexusChannel {
    private String signal;

    protected NexusChannelImpl() throws RemoteException {
        super();
        signal = "";
    }

    @Override
    public synchronized void broadcast(String signal) throws RemoteException {
        this.signal = signal;
    }

    @Override
    public synchronized String pulse() throws RemoteException {
        return signal;
    }
}

public class SyncLink extends JFrame {
    private static final Color NEON_CYAN = new Color(0, 255, 209);
    private static final Color NEON_PURPLE = new Color(189, 0, 255);

    private JTextArea logTerminal;
    private int nodeCount;
    private Semaphore quantumLock;
    private NexusChannel nexus;

    public SyncLink() {
        quantumLock = new Semaphore(1);
        nodeCount = 0;

        setTitle("AetherOS | SyncLink");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon bg = new ImageIcon(getClass().getResource("/aether/resources/bg.jpg"));
                    g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(10, 14, 20, 230));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } catch (Exception e) {
                    g.setColor(new Color(15, 15, 20));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(350);
        split.setDividerSize(1);
        split.setOpaque(false);
        split.setBorder(null);

        JPanel sidebar = createSidebar();

        logTerminal = new JTextArea();
        logTerminal.setEditable(false);
        logTerminal.setFont(new Font("Consolas", Font.PLAIN, 18));
        logTerminal.setBackground(new Color(0, 0, 0, 0));
        logTerminal.setForeground(NEON_CYAN);
        logTerminal.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JScrollPane scroll = new JScrollPane(logTerminal);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        split.setLeftComponent(sidebar);
        split.setRightComponent(scroll);

        mainPanel.add(split, BorderLayout.CENTER);
        add(mainPanel);

        initializeNexus();
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel(new GridLayout(10, 1, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));

        JLabel title = new JLabel("SYNC LINK", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(NEON_CYAN);
        panel.add(title);

        panel.add(createNavButton("TERMINATE LINK", e -> dispose()));
        panel.add(createNavButton("SPAWN NODE", e -> spawnNode()));
        panel.add(createNavButton("INIT SOCKET", e -> initSocketLink()));
        panel.add(createNavButton("INIT RMI", e -> initRMILink()));

        return panel;
    }

    private JButton createNavButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        btn.setBackground(new Color(25, 30, 40));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(NEON_PURPLE, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(NEON_PURPLE);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(25, 30, 40));
            }
        });
        return btn;
    }

    private void initializeNexus() {
        try {
            nexus = new NexusChannelImpl();
            // Using a slightly different port for local testing to avoid conflicts
            Registry reg = LocateRegistry.createRegistry(1101);
            reg.rebind("NexusChannel", nexus);
            log("Nexus Core Online | Port 1101");
        } catch (Exception e) {
            log("Nexus Core Initialization Failed: " + e.getMessage());
        }
    }

    private void spawnNode() {
        nodeCount++;
        String nodeID = "NODE_" + String.format("%03d", nodeCount);
        new Thread(() -> {
            try {
                log(nodeID + " requesting quantum lock...");
                quantumLock.acquire();
                log(nodeID + " locked critical frequency.");
                Thread.sleep(1500);
                log(nodeID + " releasing frequency.");
                nexus.broadcast(nodeID + " signal pulsed.");
            } catch (Exception e) {
                log("Node Critical Error: " + e.getMessage());
            } finally {
                quantumLock.release();
            }
        }).start();
    }

    private void initSocketLink() {
        new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(9091)) {
                log("Socket Link listening on 9091");
                while (true) {
                    try (Socket s = ss.accept()) {
                        log("Inbound signal detected: " + s.getInetAddress());
                    }
                }
            } catch (IOException e) {
                log("Socket Link Fault: " + e.getMessage());
            }
        }).start();
    }

    private void initRMILink() {
        log("Attempting RMI bridge to master node...");
        // Placeholder for real logic
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logTerminal.append("[SYSTEM] " + msg + "\n");
        });
    }
}
