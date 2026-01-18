package aether.model.process;

public class TaskNode {
    private int taskID;
    private String state;
    private String owner;
    private int priority;
    private int burstTime;
    private int arrivalTime;

    public TaskNode(int taskID, String state, String owner, int priority, int burstTime, int arrivalTime) {
        this.taskID = taskID;
        this.state = state;
        this.owner = owner;
        this.priority = priority;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
    }

    public int getProcessID() {
        return taskID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOwner() {
        return owner;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public String toString() {
        return "Task ID: " + taskID +
                ", State: " + state +
                ", Owner: " + owner +
                ", Priority: " + priority +
                ", Burst Time: " + burstTime +
                ", Arrival Time: " + arrivalTime;
    }
}
