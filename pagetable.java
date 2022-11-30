
/*
 * Stores the pages in memory, and keeps tracks of filled pages
 * Containse setters and getters to store or fetch from a provided input page
 */

public class pagetable {
    public int[] pageTable;
    public int[] flagTable;
    int size = 0;

    pagetable(int size) {
        this.size = size;
        pageTable = new int[size];
        flagTable = new int[size];
    }

    public int getFrame(int page) {
        return pageTable[page];
    }

    public void setFrame(int page, int frame) {
        pageTable[page] = frame;
        flagTable[page] = 1;
    }

    public void deleteFrame(int page) {
        pageTable[page] = -1;
        flagTable[page] = 0;
    }

    public int getFlag(int page) {
        return flagTable[page];
    }

    public void printTable() {
        System.out.println("Page Table:");
        for (int i = 0; i < this.size; i++) {
            if(flagTable[i] == 1){
                System.out.println("Page " + i + " Frame " + pageTable[i]);
                System.out.println("Page " + i + " Flag " + flagTable[i]);
            }
        }
    }
}
