import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Cycle - This object identifies a cycle through which each instruction goes through. Each instruction is fetched, decoded and executed.
 */

public class cycle 
{
    private int quantum;
    private int clockCycle;
    private short num;
    private int inst;
    private byte src,trg;
    //private String add;
    private short pc;
    private short value;
    private short add;
    private boolean Syntax = true;
    memory mainmemory = new memory();
    process p1;

    Queue<process> readyPriorityQueue = new PriorityQueue<>();
    roundRobinQueue readyRoundRobinQueue = new roundRobinQueue();

    cycle(int quantum) {
        this.quantum = quantum;
    }

    void load(String filename) throws Exception { //loads file
        p1 = new process();
        p1.loadProcess(filename,mainmemory);

        if (p1.PCB.getP_Priority() >= 0 && p1.PCB.getP_Priority() <= 15) {
            readyPriorityQueue.add(p1);
        }
        else if (p1.PCB.getP_Priority() > 15 && p1.PCB.getP_Priority() <= 31) {
            readyRoundRobinQueue.add(p1);
        }
        else {
            System.out.println("Priority is out of range");
        }
        // run(p1.PCB.reg, mainmemory);
        // mainmemory.printMemory();
    }

    public void run() {
        while (!readyPriorityQueue.isEmpty()) {
            p1 = readyPriorityQueue.poll();
            System.out.println(p1.toString());
            runPriorityProcess(p1.PCB.reg, p1.sharedMem);
        }

        while (!readyRoundRobinQueue.isEmpty()) {
            clockCycle = 0;

            p1 = readyRoundRobinQueue.peek();
            System.out.println(p1.toString());

            

            runRoundRobinProcess(p1.PCB.reg, p1.sharedMem);
        }
    }
    /**
     * @dev Run function works in loop with each iteration fetching, decoding and excuting a instruction.
     * if instruction = 243 which means instruction is 'END' identifying that the code has ended.
     * @param register = Register File
     * @param mem = Memory
     */
    public void runPriorityProcess(regFile register,memory mem)
    {
        pc = register.getReg((byte) 19);
        inst = Byte.toUnsignedInt(mem.getMemByte(pc));
        while(inst!=243) {
            fetchDecode(register, mem);
            if(Syntax==true && (trg<16 && trg>=0) && (src<16 && src>=0))          
                execute(register, mem);
            else 
                break;
            if(pc>register.getReg((byte)18)){ //PC going outside alloted code size
                System.out.println("Process terminated due to abnormal activity.");
                inst = 243;
                break;
            }
        }
        inst = 0;
        
    }

    public void runRoundRobinProcess(regFile register,memory mem)
    {
        pc = register.getReg((byte) 19);
        inst = Byte.toUnsignedInt(mem.getMemByte(pc));
        while(inst!=243) {
            fetchDecode(register, mem);
            System.out.println("Hello" + Syntax);
            if(Syntax==true && (trg<16 && trg>=0) && (src<16 && src>=0))          
                execute(register, mem);
            else 
                break;
            if(pc>register.getReg((byte)18)){ //PC going outside alloted code size
                System.out.println("Process terminated due to abnormal activity.");
                inst = 243;
                break;
            }
                
            clockCycle += 2;

            if (clockCycle >= quantum) {
                readyRoundRobinQueue.switchProcess();
                clockCycle = 0;
                break;
            }
        }
        
        if (inst == 243) {
            readyRoundRobinQueue.delete();
            inst = 0;
        }
        
    }
    /**
     * @dev First each instruction is check and next number of bytes are then read according to Instruction Format.
     * For register one byte is read, for immediate two bytes are read.
     * pc is used to read index of memory from code counter register (19th register).
     * add converts this address to string.
     * register.INC() is used to increment code counter.
     * @param register
     * @param mem
     */
    public void fetchDecode(regFile register,memory mem)
    {
        //System.out.println(register.getReg((byte)19));
        Syntax = true;
        pc = register.getReg((byte) 19);
        register.INC((byte) 19);
        inst = Byte.toUnsignedInt(mem.getMemByte(pc));
        System.out.println(inst);
        if(inst>=48 && inst<=54)
        {
            register.reFlag();
            pc = register.getReg((byte) 19);
            trg = mem.getMemByte(pc);
            register.INC((byte) 19);
            pc = register.getReg((byte) 19);
            num = mem.getMemShort(pc);
            register.INC((byte) 19);
            register.INC((byte) 19);
        }
        else if (inst>=55 && inst<=61)
        {
            pc = register.getReg((byte) 19);
            register.INC((byte) 19);
            pc = register.getReg((byte) 19);
            num = mem.getMemShort(pc);
            register.INC((byte) 19);
            register.INC((byte) 19);
        }
        else if(inst>=22 && inst<=28)
        {
            register.reFlag();
            pc = register.getReg((byte) 19);
            trg = mem.getMemByte(pc);
            register.INC((byte) 19);
            pc = register.getReg((byte) 19);
            src = mem.getMemByte(pc);
            register.INC((byte) 19);
            //System.out.println(trg + " " + src);
        }
        else if(inst>=113 && inst<=120)
        {
            register.reFlag();
            pc = register.getReg((byte) 19);
            trg = mem.getMemByte(pc);
            register.INC((byte) 19);
        }
        else if(inst == 81 || inst == 82)
        {
            register.reFlag();
            pc = register.getReg((byte) 19);
            trg = mem.getMemByte(pc);
            register.INC((byte) 19);
            pc = register.getReg((byte) 19);
            num = mem.getMemShort(pc);
            register.INC((byte) 19);
            register.INC((byte) 19);
        }
        else if(inst==242)
        {   
            register.reFlag();
            register.INC((byte) 19); //NOOP
        }
        else if(inst<=241 || inst>243)
        {
            System.out.println("Invalid Syntax");
            Syntax = false;
        }
    }

    /**
     * @dev fetches the opcode from instruction.
     * Executes the corresponding function to that opcode with appropriate inputs
     * Prints the register values after execution 
     * @param register - references the array of registers
     */
    public void execute(regFile register, memory mem)
    {
        //System.out.println(inst + " "  + num + " " + src + " " +  trg);
        //System.out.println(pc);
        String opcode = Integer.toHexString(inst);
        //System.out.println(opcode);
        switch (opcode) {
            case "16":
                register.MOV(trg, src);
                break;
            case "17":
                register.ADD(trg, src);
                break;
            case "18":
                register.SUB(trg, src);
                break;
            case "19":
                register.MUL(trg, src);
                break;
            case "1a":
                register.DIV(trg, src);
                break;
            case "1b":
                register.AND(trg, src);
                break;
            case "1c":
                register.OR(trg, src);
                break;
            case "30":
                register.MOVI(trg, num);
                break;
            case "31":
                register.ADDI(trg, num);
                break;
            case "32":
                register.SUBI(trg, num);
                break;
            case "33":
                register.MULI(trg, num);
                break;
            case "34":
                register.DIVI(trg, num);
                break;
            case "35":
                register.ANDI(trg, num);
                break;
            case "36":
                register.ORI(trg, num);
                break;
            case "37":
                if(register.getFlag(1) == '0'){
                    short base = register.getReg((byte)17);
                    register.setReg((byte)19, (short)(base + num));
                }
                break; 
            case "38":
                if(register.getFlag(1) == '1'){
                    short base = register.getReg((byte)17);
                    register.setReg((byte)19, (short)(base + num));
                }
                break;
            case "39":
                if(register.getFlag(0) == '1'){
                    short base = register.getReg((byte)17);
                    register.setReg((byte)19, (short)(base + num));
                }
                break; 
            case "3a":
                if(register.getFlag(2) == '1'){
                    short base = register.getReg((byte)17);
                    register.setReg((byte)19, (short)(base + num));
                }
                break;
            case "3b":
                short base = register.getReg((byte)17);
                //System.out.println(add);   
                register.setReg((byte)19, (short)(base + num));   
                //System.out.println(num);
                break;         
            case "51":
                    value = p1.getData(num);
                    register.setReg(trg, value);
                break;   
            case "52":
                    value = register.getReg(trg);
                    p1.setData(num, value);
                break;     
            case "71":
                register.SHL(trg);
                break;
            case "72":
                register.SHR(trg);
                break;
            case "73":
                register.RTL(trg);
                break;
            case "74":
                register.RTR(trg);
                break;
            case "75":
                register.INC(trg);
                break;
            case "76":
                register.DEC(trg);
                break;
            case "77":
                add = register.getReg((byte) 22);
                value = register.getReg(trg);
                mem.setMem(add, value);
                System.out.println(trg + " " +add + " " + value + " " + mem.getMemShort(add));
                register.INC((byte) 22);
                register.INC((byte) 22);
                break;
            case "78":
                add = register.getReg((byte) 22);
                value = register.getReg(trg);
                mem.getMemShort(add-1);
                register.DEC((byte) 22);
                register.DEC((byte) 22);
                break;
            case "f2":
                //NOOP
                break;    
            case "f3":
                break;    
            default:
                break;
        }
        register.printGenReg();
    }
}