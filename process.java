import java.io.*;

public class process implements Comparable<process>
{
    memory sharedMem;
    int i = 0;
    pcb PCB;
    //static int p = 0;  
    // cycle exe = new cycle();

    process(){
        PCB = new pcb();
        sharedMem = new memory();
    }

    void loadProcess(String filename, memory sharedMem) throws Exception {
        PCB.setP_Name(filename);

        File file = new File(filename);
        FileInputStream f_is = new FileInputStream(file);
        System.out.println("Reading File");
        //p++;
        
        PCB.setP_Priority((byte) f_is.read());

        byte byte_first = (byte) f_is.read();
        byte byte_second = (byte) f_is.read();
        PCB.setP_ID(createShort(byte_first, byte_second));
        
        byte_first = (byte) f_is.read();
        byte_second = (byte) f_is.read();
        PCB.setP_Size_D(createShort(byte_first, byte_second));

        System.out.println(PCB.getP_ID());
        PCB.setP_Size_C((int)(file.length() - PCB.getP_Size_D() - 8));
        
        System.out.println("Code Size : " + PCB.getP_Size_C());
        System.out.println("Data Size : " + PCB.getP_Size_D());

        f_is.read();
        f_is.read();
        f_is.read();

        byte[] code = new byte[PCB.getP_Size_C()];
        byte[] data = new byte[PCB.getP_Size_D()];

        for (int i = 0; i < PCB.getP_Size_D(); i++) {
            data[i + 1] = (byte) f_is.read();
            data[i] = (byte) f_is.read();
            i++;
        }
        for (int i = 0; i < PCB.getP_Size_C(); i++) {
            code[i] = (byte) f_is.read();
        }
        
        int currPageNO = 0;
        byte pagePart[] = new byte[128];

        
        int codePages = PCB.getP_Size_C() / 128;
        while(codePages>=0){
            for(int i=0; ((128 * currPageNO) + i) < PCB.getP_Size_C() && i<128 ; i++){
                pagePart[i] = code[i +(128 * currPageNO)];
            }
            int frame = sharedMem.codeLoad(pagePart, 128);
            PCB.codept.setFrame(currPageNO,frame);
            ++currPageNO;
            codePages--;
        }
        currPageNO = 0;
        pagePart = new byte[128];
        int dataPages = PCB.getP_Size_D() / 128;
        while(dataPages>=0){
            for(int i=0; ((128 * currPageNO) + i) < PCB.getP_Size_D() && i<128 ; i++){
                pagePart[i] = data[i +(128 * currPageNO)];
            }
            int frame = sharedMem.codeLoad(pagePart, 128);
            PCB.datapt.setFrame(currPageNO,frame);
            currPageNO++;
            dataPages--;
        }

        int dataBase = 128 * PCB.datapt.getFrame(0);
        int dataLimit = dataBase + PCB.getP_Size_D();
        int dataCounter = dataBase;
        PCB.reg.setReg((byte) 23, (short) dataBase); // data base
        PCB.reg.setReg((byte) 24, (short) dataLimit); // data limit
        PCB.reg.setReg((byte) 25, (short) dataCounter); // data counter
        
        int codeBase = 128 * PCB.codept.getFrame(0);
        int codeLimit = codeBase + PCB.getP_Size_C();
        int codeCounter = codeBase;
        PCB.reg.setReg((byte) 17, (short) codeBase); //code base
        PCB.reg.setReg((byte) 18, (short) codeLimit); //code limit
        PCB.reg.setReg((byte) 19, (short) codeCounter); //code counter

        currPageNO = 0;
        byte[] stack = new byte[128];
        int frame = sharedMem.codeLoad(stack, 128);
        PCB.stackpt.setFrame(currPageNO,frame);
        int stackBase = 128 * PCB.stackpt.getFrame(0);
        int stackimit = stackBase + 50;
        int stackCounter = stackBase;
        PCB.reg.setReg((byte) 20, (short) (stackBase)); //stack base
        PCB.reg.setReg((byte) 21, (short) (stackimit)); //stack limit
        PCB.reg.setReg((byte) 22, (short) (stackCounter)); //stack coounter
        
        //PCB.codept.printTable();
        // PCB.reg.setReg((byte) 17, (short) 0); //code base
        // PCB.reg.setReg((byte) 18, (short) 0); //code limit
        // PCB.reg.setReg((byte) 19, (short) 0); //code counter
        // PCB.reg.setReg((byte) 23, (short) 0); // data base
        // PCB.reg.setReg((byte) 24, (short) 0); // data limit
        // PCB.reg.setReg((byte) 25, (short) 0); // data counter
        //System.out.println(PCB.datapt.getFrame);
        code = null;
        data = null;
        System.out.println("File has been readed.");
        //sharedMem.printMemory();
    }

    byte getCode(short pc) {
        short pageno = (short) (pc / 128);
        short d = (short) (pc % 128);
        int frame = PCB.codept.getFrame(pageno);
        return  sharedMem.mem[(frame * 128) + d];
    }

    short getData(short offset) {  
        short pageno = (short) (offset / 128);
        short d = (short) (offset % 128);
        int frame = PCB.datapt.getFrame(pageno);
        byte Fbyte = sharedMem.mem[(frame * 128) + d];
        byte Sbyte = sharedMem.mem[(frame * 128) + d+1];
        return sharedMem.createShort(Fbyte, Sbyte);
    }
    
    void setData(short offset,short value){ 
        short pageno = (short) (offset / 128);
        short d = (short) (offset % 128);
        int frame = PCB.datapt.getFrame(pageno);
        byte Fbyte = sharedMem.getFirstByte(value);
        byte Sbyte = sharedMem.getSecondByte(value);
        sharedMem.mem[(frame * 128) + d] = Fbyte;
        sharedMem.mem[(frame * 128) + d+1] = Sbyte;
    }

    byte getFirstByte(short value) {
        byte temp = (byte) (value >> 8);
        return temp;
    }

    byte getSecondByte(short value){
        byte temp = (byte) (value);
        return temp;
    }
    
    short createShort(byte FByte, byte SByte){
        short temp = (short) (FByte*256);
        temp = (short) (temp+SByte);
        return temp;
    }

    @Override
    public int compareTo(process p1) {
        return p1.PCB.getP_Priority() < PCB.getP_Priority() ? 1 : -1;
    }
    // void executeProcess(){   
    //     sharedMem.printMemory();
    //     exe.run(PCB.reg, sharedMem);
    // }
}
