/**
 * Cycle - This object identifies a cycle through which each instruction goes through. Each instruction is fetched, decoded and executed.
 */

public class cycle 
{
    private short num;
    private int inst;
    private byte src,trg;
    private String add;
    private short pc;
    private boolean Syntax;
    memory mainmemory = new memory();
    process p1;

    void load(String filename) throws Exception { //loads file
        p1 = new process();
        p1.loadProcess(filename,mainmemory);
        run(p1.PCB.reg, mainmemory);
        //mainmemory.printMemory();
    }
    /**
     * @dev Run function works in loop with each iteration fetching, decoding and excuting a instruction.
     * if instruction = 243 which means instruction is 'END' identifying that the code has ended.
     * @param register = Register File
     * @param mem = Memory
     */
    public void run(regFile register,memory mem)
    {
        while(inst!=243)
        {
            fetchDecode(register, mem);
            if(Syntax || ((trg<16 && trg>=0) && (src<16 && src>=0)))
                execute(register, mem);
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
            System.out.println(trg + " " + src);
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
        else if(inst<=241 && inst>243)
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
        String opcode = Integer.toHexString(inst);
        System.out.println(opcode);
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
            case "1A":
                register.DIV(trg, src);
                break;
            case "1B":
                register.AND(trg, src);
                break;
            case "1C":
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
                if(register.getFlag(1) == '0')
                    register.setReg((byte)19, num);
                break; 
            case "38":
                if(register.getFlag(1) == '1')
                    register.setReg((byte)19, num);
                break;
            case "39":
                if(register.getFlag(0) == '1')
                   register.setReg((byte)19, num);
                break; 
            case "3A":
                if(register.getFlag(2) == '1')
                    register.setReg((byte)19, num);
                break;
            case "3b":
                    register.setReg((byte)19, num);
                    System.out.println(num);
                break;     
            case "51":
                    short word = p1.getData(num);
                    register.setReg(trg, word);
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
                break;
            case "78":
                break;
            case "F2":
                break;    
            case "F3":
                break;    
            default:
                break;
        }
        register.printGenReg();
    }
}