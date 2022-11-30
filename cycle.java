import java.io.File;
import java.io.FileWriter;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.crypto.spec.RC2ParameterSpec;

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
    private short pc;
    private short value;
    private short add;
    private boolean Syntax = true;
    private regFile cpuRegFile = new regFile();
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

        System.out.println();
        // run(p1.PCB.reg, mainmemory);
        // mainmemory.printMemory();
    }

    public void run() {
        while (!readyPriorityQueue.isEmpty()) {
            clockCycle = 0;

            p1 = readyPriorityQueue.poll();
            //System.out.println(p1.toString());
            runPriorityProcess(p1);
        }

        while (!readyRoundRobinQueue.isEmpty()) {
            clockCycle = 0;

            p1 = readyRoundRobinQueue.peek();
            //System.out.println(p1.toString());

            runRoundRobinProcess(p1);
        }
    }
    /**
     * @dev Run function works in loop with each iteration fetching, decoding and excuting a instruction.
     * if instruction = 243 which means instruction is 'END' identifying that the code has ended.
     * @param register = Register File
     * @param mem = Memory
     */
    public void runPriorityProcess(process p)
    {
        copy(p.PCB.reg);
        
        pc = cpuRegFile.getReg((byte) 19);
        inst = Byte.toUnsignedInt(mainmemory.getMemByte(pc));
        try {
            File processFile = new File("output/" + p.PCB.getName() + ".txt");
            processFile.createNewFile();
            
            FileWriter processWriter = new FileWriter(processFile, processFile.exists());

            while(inst!=243) {
                fetchDecode(cpuRegFile, mainmemory);
                if(Syntax==true && (trg<16 && trg>=0) && (src<16 && src>=0)) {
                    execute(cpuRegFile, mainmemory);
                }        
                else 
                    break;
                if(pc>(cpuRegFile.getReg((byte)17)+cpuRegFile.getReg((byte)18))){ //PC going outside alloted code size
                    System.out.println("Process terminated due to abnormal activity.");
                    System.out.println(pc);
                    inst = 243;
                    break;
                }

                clockCycle += 2;
            }

            // codeBase = cpuRegFile.getReg((byte) 17);
            // dataBase = cpuRegFile.getReg((byte) 20);
            // stackBase = cpuRegFile.getReg((byte) 23);
            // mainmemory.memTable.deleteFrame(codeBase/128);
            // mainmemory.memTable.deleteFrame(dataBase/128);
            // mainmemory.memTable.deleteFrame(stackBase/128);

            save(p.PCB.reg);

            //p.PCB.reg.printGenReg();

            System.out.println(p.PCB.getName());
            p.PCB.codept.printTable();
            p.PCB.datapt.printTable();
            p.PCB.stackpt.printTable();

            p.PCB.setExecutionTime(clockCycle);
            System.out.println("Execution Time: " + p.PCB.getExecutionTime());
            // dump entire memory and execution time to file and close file

            p.PCB.printPcbToFile(processWriter);
            p.PCB.printProcessMemory(processWriter, mainmemory);

            processWriter.close();
            inst = 0;

            System.out.println();

        } catch (Exception e) {
            System.out.println("An error occurred: " + e);
        }
    }

    public void runRoundRobinProcess(process p)
    {
        copy(p.PCB.reg);
        
        pc = cpuRegFile.getReg((byte) 19);
        inst = Byte.toUnsignedInt(mainmemory.getMemByte(pc));
        try {
            File processFile = new File("output/" + p.PCB.getName() + ".txt");
            processFile.createNewFile();
            
            FileWriter processWriter = new FileWriter(processFile, processFile.exists());

            while(inst!=243) {
                fetchDecode(cpuRegFile, mainmemory);
                if(Syntax==true && (trg<16 && trg>=0) && (src<16 && src>=0))          
                    execute(cpuRegFile, mainmemory);
                    // add each instruction to file
                else 
                    break;
                if(pc>(cpuRegFile.getReg((byte)17)+cpuRegFile.getReg((byte)18))){ //PC going outside alloted code size
                    System.out.println("Process terminated due to abnormal activity.");
                    inst = 243;
                    break;
                }
                clockCycle += 2;
                readyRoundRobinQueue.incWait();

                if (clockCycle >= quantum) {

                    readyRoundRobinQueue.switchProcess();
                    p.PCB.setExecutionTime(clockCycle);

                    clockCycle = 0;
                    break;
                }
            }

            save(p.PCB.reg);
            
            if (inst == 243) {
                //p.PCB.reg.printGenReg();

                System.out.println(p.PCB.getName());
                p.PCB.codept.printTable();
                p.PCB.datapt.printTable();
                p.PCB.stackpt.printTable();

                //for(int i=0 ; i<)

                // codeBase = cpuRegFile.getReg((byte) 17);
                // dataBase = cpuRegFile.getReg((byte) 20);
                // stackBase = cpuRegFile.getReg((byte) 23);
                // mainmemory.memTable.deleteFrame(codeBase/128);
                // mainmemory.memTable.deleteFrame(dataBase/128);
                // mainmemory.memTable.deleteFrame(stackBase/128);
                
                p.PCB.setExecutionTime(2);
                System.out.println("Execution Time: " + p.PCB.getExecutionTime());
                System.out.println("Waiting Time: " + p.PCB.getWaitTime());

                p.PCB.printPcbToFile(processWriter);
                p.PCB.printProcessMemory(processWriter, mainmemory);
                
                processWriter.close();
                readyRoundRobinQueue.delete();
                inst = 0;

                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("An error occurred: " + e);
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
        //System.out.println(inst);
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
            System.out.println("Invalid Syntax Found");
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
                add = register.getReg((byte)23);
                value = mem.getMemShort(add+num);
                register.setReg(trg, value);
                break;   
            case "52":
                value = register.getReg(trg);
                add = register.getReg((byte)23);
                mem.setMem(add+num,value);
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
                if((register.getReg((byte) 22) > register.getReg((byte) 20)) && (register.getReg((byte) 22) <= (register.getReg((byte) 20) + register.getReg((byte) 21)))) {
                    add = register.getReg((byte) 22);
                    value = register.getReg(trg);
                    mem.setMem(add, value);
                    //System.out.println(trg + " " +add + " " + value + " " + mem.getMemShort(add));
                    register.INC((byte) 22);
                    register.INC((byte) 22);
                }    
                break;
            case "78":
                if((register.getReg((byte) 22) > register.getReg((byte) 20)) && (register.getReg((byte) 22) <= (register.getReg((byte) 20) + register.getReg((byte) 21)))) {
                    add = register.getReg((byte) 22);
                    value = mem.getMemShort(add);
                    register.setReg(trg,value);
                    register.DEC((byte) 22);
                    register.DEC((byte) 22);
                }
                break;
            case "f2":
                //NOOP
                break;    
            case "f3":
                break;    
            default:
                break;
        }
        //register.printGenReg();
    }

    void copy(regFile pFile){
        cpuRegFile.setReg((byte) 0, (short) pFile.getReg((byte)0));
        cpuRegFile.setReg((byte) 1, (short) pFile.getReg((byte)1));
        cpuRegFile.setReg((byte) 2, (short) pFile.getReg((byte)2));
        cpuRegFile.setReg((byte) 3, (short) pFile.getReg((byte)3));
        cpuRegFile.setReg((byte) 4, (short) pFile.getReg((byte)4));
        cpuRegFile.setReg((byte) 5, (short) pFile.getReg((byte)5));
        cpuRegFile.setReg((byte) 6, (short) pFile.getReg((byte)6));
        cpuRegFile.setReg((byte) 7, (short) pFile.getReg((byte)7));
        cpuRegFile.setReg((byte) 8, (short) pFile.getReg((byte)8));
        cpuRegFile.setReg((byte) 9, (short) pFile.getReg((byte)9));
        cpuRegFile.setReg((byte) 10, (short) pFile.getReg((byte)10));
        cpuRegFile.setReg((byte) 11, (short) pFile.getReg((byte)11));
        cpuRegFile.setReg((byte) 12, (short) pFile.getReg((byte)12));
        cpuRegFile.setReg((byte) 13, (short) pFile.getReg((byte)13));
        cpuRegFile.setReg((byte) 14, (short) pFile.getReg((byte)14));
        cpuRegFile.setReg((byte) 15, (short) pFile.getReg((byte)15));
        cpuRegFile.setReg((byte) 16, (short) pFile.getReg((byte)16));
        cpuRegFile.setReg((byte) 17, (short) pFile.getReg((byte)17));
        cpuRegFile.setReg((byte) 18, (short) pFile.getReg((byte)18));
        cpuRegFile.setReg((byte) 19, (short) pFile.getReg((byte)19));
        cpuRegFile.setReg((byte) 20, (short) pFile.getReg((byte)20));
        cpuRegFile.setReg((byte) 21, (short) pFile.getReg((byte)21));
        cpuRegFile.setReg((byte) 22, (short) pFile.getReg((byte)22));
        cpuRegFile.setReg((byte) 23, (short) pFile.getReg((byte)23));
        cpuRegFile.setReg((byte) 24, (short) pFile.getReg((byte)24));
        cpuRegFile.setReg((byte) 25, (short) pFile.getReg((byte)25));
        cpuRegFile.setReg((byte) 26, (short) pFile.getReg((byte)26));
        cpuRegFile.setReg((byte) 27, (short) pFile.getReg((byte)27));
        cpuRegFile.setReg((byte) 28, (short) pFile.getReg((byte)28));
        cpuRegFile.setReg((byte) 29, (short) pFile.getReg((byte)29));
        cpuRegFile.setReg((byte) 30, (short) pFile.getReg((byte)30));
        for (int i=0 ; i<16 ; i++) {
            cpuRegFile.setFlag(i,pFile.getFlag(i));
        }
    } 

    void save(regFile pFile)
    {
        pFile.setReg((byte) 0, (short) cpuRegFile.getReg((byte)0));
        pFile.setReg((byte) 1, (short) cpuRegFile.getReg((byte)1));
        pFile.setReg((byte) 2, (short) cpuRegFile.getReg((byte)2));
        pFile.setReg((byte) 3, (short) cpuRegFile.getReg((byte)3));
        pFile.setReg((byte) 4, (short) cpuRegFile.getReg((byte)4));
        pFile.setReg((byte) 5, (short) cpuRegFile.getReg((byte)5));
        pFile.setReg((byte) 6, (short) cpuRegFile.getReg((byte)6));
        pFile.setReg((byte) 7, (short) cpuRegFile.getReg((byte)7));
        pFile.setReg((byte) 8, (short) cpuRegFile.getReg((byte)8));
        pFile.setReg((byte) 9, (short) cpuRegFile.getReg((byte)9));
        pFile.setReg((byte) 10, (short) cpuRegFile.getReg((byte)10));
        pFile.setReg((byte) 11, (short) cpuRegFile.getReg((byte)11));
        pFile.setReg((byte) 12, (short) cpuRegFile.getReg((byte)12));
        pFile.setReg((byte) 13, (short) cpuRegFile.getReg((byte)13));
        pFile.setReg((byte) 14, (short) cpuRegFile.getReg((byte)14));
        pFile.setReg((byte) 15, (short) cpuRegFile.getReg((byte)15));
        pFile.setReg((byte) 16, (short) cpuRegFile.getReg((byte)16));
        pFile.setReg((byte) 17, (short) cpuRegFile.getReg((byte)17));
        pFile.setReg((byte) 18, (short) cpuRegFile.getReg((byte)18));
        pFile.setReg((byte) 19, (short) cpuRegFile.getReg((byte)19));
        pFile.setReg((byte) 20, (short) cpuRegFile.getReg((byte)20));
        pFile.setReg((byte) 21, (short) cpuRegFile.getReg((byte)21));
        pFile.setReg((byte) 22, (short) cpuRegFile.getReg((byte)22));
        pFile.setReg((byte) 23, (short) cpuRegFile.getReg((byte)23));
        pFile.setReg((byte) 24, (short) cpuRegFile.getReg((byte)24));
        pFile.setReg((byte) 25, (short) cpuRegFile.getReg((byte)25));
        pFile.setReg((byte) 26, (short) cpuRegFile.getReg((byte)26));
        pFile.setReg((byte) 27, (short) cpuRegFile.getReg((byte)27));
        pFile.setReg((byte) 28, (short) cpuRegFile.getReg((byte)28));
        pFile.setReg((byte) 29, (short) cpuRegFile.getReg((byte)29));
        pFile.setReg((byte) 30, (short) cpuRegFile.getReg((byte)30));
        for (int i=0 ; i<16 ; i++) {
            pFile.setFlag(i,cpuRegFile.getFlag(i));
        }
    } 

}