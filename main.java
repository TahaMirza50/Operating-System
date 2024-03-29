public class main
{
    /**
     * @dev Load all the process file one by one in the main memory and their respective queues.
     * Then execute all these files. 
     * At the end main memory and its pagetable is printed.
     */
    public static void main(String[] args)
    {
        cycle exe = new cycle(8);
        try{
            exe.load("p5");
            exe.load("sfull");
            exe.load("flags");
            exe.load("large0");
            exe.load("noop");
            exe.load("p5 - Copy");
            exe.load("power");

            exe.run();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e);
        }
        exe.mainmemory.printMemory();
    }
}

