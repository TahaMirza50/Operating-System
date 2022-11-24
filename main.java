public class main
{
    /**
     * @dev Initializes memory, registers, von-nueman cycle.
     * Stores instruction in file p0 to main memory
     * executes the instructions and outputs the register values
     */
    public static void main(String[] args)
    {
        // memory subMem = new memory();
        // regFile subRegister =  new regFile();
        // process Process = new process();
        cycle exe = new cycle(8);
        try{
            exe.load("p5");
            exe.load("sfull");

            System.out.println(exe.readyPriorityQueue.toString());

            exe.run();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e);
        }
        //Process.executeProcess();
        //memory.storeCode("src/p0.txt", subMem.mem);
        //exe.run(Process.PCB.reg, Process.sharedMem);
    }
}

