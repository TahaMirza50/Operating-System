class Node {
    process data;
    Node next;

    Node(process data) {
        this.data = data;
        this.next = null;
    }
}

public class roundRobinQueue {
    private Node head;
    private Node run;

    public roundRobinQueue() {
        this.head = null;
        this.run = null;
    }

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