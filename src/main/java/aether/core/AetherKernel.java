package aether.core;

import java.util.*;
import aether.model.process.TaskNode;
import aether.model.memory.CellStore;
import aether.model.memory.FluxCache;

public class AetherKernel {
    private Map<Integer, TaskNode> taskTable;
    private int taskIDCounter = 1;
    private Queue<TaskNode> readyQueue;
    private Queue<TaskNode> blockedQueue;
    private Queue<TaskNode> newQueue;

    private CellStore cellStore;
    private FluxCache fluxCache;

    public AetherKernel() {
        taskTable = new HashMap<>();
        readyQueue = new LinkedList<>();
        blockedQueue = new LinkedList<>();
        newQueue = new LinkedList<>();

        cellStore = new CellStore("config.txt");
        fluxCache = new FluxCache(3);
    }

    public void addNewTask(TaskNode task) {
        newQueue.add(task);
    }

    public void levelOneScheduling() {
        while (!newQueue.isEmpty()) {
            TaskNode task = newQueue.poll();
            task.setState("Ready");
            readyQueue.add(task);
        }
    }

    public TaskNode lowLevelScheduling(String algorithm) {
        if (readyQueue.isEmpty()) {
            return null;
        }

        TaskNode selectedTask = null;

        if (algorithm.equals("FCFS")) {
            selectedTask = readyQueue.poll();
        } else if (algorithm.equals("SJF")) {
            selectedTask = readyQueue.stream()
                    .min(Comparator.comparingInt(TaskNode::getBurstTime))
                    .orElse(null);

            if (selectedTask != null) {
                readyQueue.remove(selectedTask);
            }
        }

        return selectedTask;
    }

    public void createTask(int burstTime, int arrivalTime, int priority) {
        int taskID = taskIDCounter++;
        TaskNode task = new TaskNode(taskID, "Ready", "User", priority, burstTime, arrivalTime);
        taskTable.put(taskID, task);

        cellStore.allocateMemoryToProcess(taskID);

        readyQueue.add(task);
    }

    public void destroyTask(int taskID) {
        taskTable.remove(taskID);
        readyQueue.removeIf(p -> p.getProcessID() == taskID);
        blockedQueue.removeIf(p -> p.getProcessID() == taskID);
    }

    public void suspendTask(int taskID) {
        TaskNode task = taskTable.get(taskID);
        if (task != null && readyQueue.remove(task)) {
            task.setState("Suspended");
            blockedQueue.add(task);
        }
    }

    public void resumeTask(int taskID) {
        TaskNode task = taskTable.get(taskID);
        if (task != null && blockedQueue.remove(task)) {
            task.setState("Ready");
            readyQueue.add(task);
        }
    }

    public void blockTask(int taskID) {
        TaskNode task = taskTable.get(taskID);
        if (task != null && readyQueue.remove(task)) {
            task.setState("Blocked");
            blockedQueue.add(task);
        }
    }

    public void wakeupTask(int taskID) {
        TaskNode task = taskTable.get(taskID);
        if (task != null && blockedQueue.remove(task)) {
            task.setState("Ready");
            readyQueue.add(task);
        }
    }

    public void changePriority(int taskID, int newPriority) {
        TaskNode task = taskTable.get(taskID);
        if (task != null) {
            task.setPriority(newPriority);
        }
    }

    public Map<Integer, TaskNode> getTaskTable() {
        return taskTable;
    }

    public Queue<TaskNode> getReadyQueue() {
        return readyQueue;
    }

    public Queue<TaskNode> getBlockedQueue() {
        return blockedQueue;
    }

    public CellStore getCellStore() {
        return cellStore;
    }
}
