
/*
 * Node class to be used for each element in queue
 */
class Node {
    process data;
    Node next;

    Node(process data) {
        this.data = data;
        this.next = null;
    }
}

/* 
 * circular Linked List that keeps tracks of current node being executed and switches process after quantum time
 */
public class roundRobinQueue {
    private Node head;
    private Node run;

    public roundRobinQueue() {
        this.head = null;
        this.run = null;
    }

    /**
     * Increments wait time of all processes in queue instead of the running process
     */
    public void incWait() {
        Node temp = head;
        while (temp.next != null) {
            if (temp != run)
                temp.data.PCB.incWaitTime();
            temp = temp.next;
        }
        if (temp != run)
            temp.data.PCB.incWaitTime();
    }

    /**
     * switches process after quantum time
     */
    public void switchProcess() {
        if (run.next != null) {
            run = run.next;
        }
        else {
            run = head;
        }
    }

    public boolean isEmpty() {
        return head == null;
    }

    public process peek() {
        return this.run.data;
    }

    public void add(process item) {
        Node node = new Node(item);
        if (isEmpty()) {
            head = node;
            run = head;
        } else {
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = node;
        }
    }

    /**
     * removes process from queue after its execution is complete or terminated
     */ 
    public void delete() {
        // delete the current completed run node and assign run with new process to execute
        Node temp = head;
        if (run.next != null) {
            // middle node
            if (run == head) {
                head = head.next;
                run = head;
            } else {
                while (temp.next != run) {
                    temp = temp.next;
                }
                temp.next = run.next;
                run = run.next;
            }
        }
        else {
            // last node
            if (run == head) {
                run = null;
                head = null;
            }
            else {
                while (temp.next != run) {
                    temp = temp.next;
                }
                temp.next = null;
                run = head;
            }
        }
    }
}