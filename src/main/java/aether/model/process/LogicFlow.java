package aether.model.process;

import javax.swing.*;
import java.util.*;

public class LogicFlow {

    public static TaskNode performFCFS(Queue<TaskNode> readyQueue, Map<Integer, TaskNode> taskTable, JFrame frame) {
        if (readyQueue.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Stream Empty.");
            return null;
        }

        TaskNode node = readyQueue.poll();
        node.setState("Processing");
        JOptionPane.showMessageDialog(frame, "Active Node: " + node.getProcessID());
        node.setState("Terminated");
        taskTable.remove(node.getProcessID());
        return node;
    }

    public static TaskNode performSJF(Queue<TaskNode> readyQueue, Map<Integer, TaskNode> taskTable, JFrame frame) {
        if (readyQueue.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Stream Empty.");
            return null;
        }

        TaskNode shortest = readyQueue.stream()
                .min(Comparator.comparingInt(TaskNode::getBurstTime))
                .orElse(null);

        if (shortest != null) {
            readyQueue.remove(shortest);
            shortest.setState("Processing");
            JOptionPane.showMessageDialog(frame, "Active Node: " + shortest.getProcessID());
            shortest.setState("Terminated");
            taskTable.remove(shortest.getProcessID());
            return shortest;
        }
        return null;
    }
}
