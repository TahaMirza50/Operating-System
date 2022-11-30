import java.io.*;
import java.lang.Math;

public class process implements Comparable<process>
{
    memory sharedMem;
    int i = 0;
    pcb PCB;
    static int currPageNO = 0;  
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
        
        int x = 0;

        int codeBase = 128 * currPageNO;
        //currPageNO = 0;
        int codePages = (int) Math.ceil((double)PCB.getP_Size_C() / 128);
        while(codePages>0){
            byte[] pagePart = new byte[128];
            for(int i=0; (i +(128 * x)) < PCB.getP_Size_C() && i<128 ; i++){
                pagePart[i] = code[i +(128 * x)];
            }
            int frame = sharedMem.codeLoad(pagePart, 128);
            PCB.codept.setFrame(currPageNO,frame);
            x++;
            ++currPageNO;
            codePages--;
        }

        x = 0;
        int dataBase = 128 * currPageNO;
        //currPageNO = 0;
        int dataPages = (int) Math.ceil((double)PCB.getP_Size_D() / 128);
        while(dataPages>0){
            byte[] pagePart = new byte[128];
            for(int i=0; (i +(128 * x)) < PCB.getP_Size_D() && i<128 ; i++){
                pagePart[i] = data[i +(128 * x)];
            }
            int frame = sharedMem.codeLoad(pagePart, 128);
            PCB.datapt.setFrame(currPageNO,frame);
            x++;
            currPageNO++;
            dataPages--;
        }

        int dataCounter = dataBase;
        PCB.reg.setReg((byte) 23, (short) dataBase); // data base
        PCB.reg.setReg((byte) 24, (short) PCB.getP_Size_D()); // data limit
        PCB.reg.setReg((byte) 25, (short) dataCounter); // data counter
        
        int codeCounter = codeBase;
        PCB.reg.setReg((byte) 17, (short) codeBase); //code base
        PCB.reg.setReg((byte) 18, (short) PCB.getP_Size_C()); //code limit
        PCB.reg.setReg((byte) 19, (short) codeCounter); //code counter

        //currPageNO = 0;
        byte[] stack = new byte[128];
        int frame = sharedMem.codeLoad(stack, 128);
        PCB.stackpt.setFrame(currPageNO,frame);
        int stackBase = 128 * currPageNO;
        int stackCounter = stackBase;
        PCB.reg.setReg((byte) 20, (short) (stackBase)); //stack base
        PCB.reg.setReg((byte) 21, (short) (50)); //stack limit
        PCB.reg.setReg((byte) 22, (short) (stackCounter)); //stack coounter
        
        currPageNO++;
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
        f_is.close();
        //sharedMem.printMemory();
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
