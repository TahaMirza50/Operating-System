import java.io.*;
import java.lang.Math;

/**
 * The process class creates PCB for the process and load that process into the main memory according through paging.
 */

public class process implements Comparable<process>
{
    pcb PCB;
    static int currPageNO = 0;  

    process(){
        PCB = new pcb();
    }

    /**
     * @dev The function takes filename and load it into the memory pass to it by reference.
     * The file is readed byte by byte.
     * The loadProcess first reads the Name, Priority, ID, data size and calculate code size.
     * Then number of pages is calculated and data, code and stack are loaded according to it.
     * First code is loaded, then data, then stack. 
     * These pages are then saved in page table of code, data and stack in process PCB. 
     * @param filename
     * @param sharedMem
     * @throws Exception
     */
    void loadProcess(String filename, memory sharedMem) throws Exception {
        PCB.setP_Name(filename);

        File file = new File(filename);
        FileInputStream f_is = new FileInputStream(file);
        System.out.println("Reading File");
        
        PCB.setP_Priority((byte) f_is.read()); //Process Priority

        byte byte_first = (byte) f_is.read();
        byte byte_second = (byte) f_is.read();
        PCB.setP_ID(createShort(byte_first, byte_second)); //Process ID
        
        byte_first = (byte) f_is.read();
        byte_second = (byte) f_is.read();
        PCB.setP_Size_D(createShort(byte_first, byte_second)); //Process Data Size

        PCB.setP_Size_C((int)(file.length() - PCB.getP_Size_D() - 8)); //Process Code Size
        
        System.out.println("Process Name : " + PCB.getName());
        System.out.println("Process ID : " + PCB.getP_ID());
        System.out.println("Process Priority : " + PCB.getP_Priority());
        System.out.println("Code Size : " + PCB.getP_Size_C());
        System.out.println("Data Size : " + PCB.getP_Size_D());

        f_is.read();
        f_is.read(); //Skipping 3 bytes.
        f_is.read(); 

        byte[] code = new byte[PCB.getP_Size_C()];
        byte[] data = new byte[PCB.getP_Size_D()];

        for (int i = 0; i < PCB.getP_Size_D(); i++) { // loading data into temporary array.
            data[i + 1] = (byte) f_is.read();
            data[i] = (byte) f_is.read();
            i++;
        }
        for (int i = 0; i < PCB.getP_Size_C(); i++) { // loading code into temporary array.
            code[i] = (byte) f_is.read();
        }
        
        int x = 0;
        int codeBase = 128 * currPageNO;

        // loading code into main memory.
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

        
        // loading data into main memory.
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

        // setting register values.
        int dataCounter = dataBase;
        PCB.reg.setReg((byte) 23, (short) dataBase); // data base
        PCB.reg.setReg((byte) 24, (short) PCB.getP_Size_D()); // data limit
        PCB.reg.setReg((byte) 25, (short) dataCounter); // data counter
        
        int codeCounter = codeBase;
        PCB.reg.setReg((byte) 17, (short) codeBase); //code base
        PCB.reg.setReg((byte) 18, (short) PCB.getP_Size_C()); //code limit
        PCB.reg.setReg((byte) 19, (short) codeCounter); //code counter

        // loading stack into main memory.
        byte[] stack = new byte[128];
        int frame = sharedMem.codeLoad(stack, 128);
        PCB.stackpt.setFrame(currPageNO,frame);
        int stackBase = 128 * currPageNO;
        int stackCounter = stackBase;
        PCB.reg.setReg((byte) 20, (short) (stackBase)); //stack base
        PCB.reg.setReg((byte) 21, (short) (50)); //stack limit
        PCB.reg.setReg((byte) 22, (short) (stackCounter)); //stack coounter
        
        currPageNO++;

        // setting temporary array null
        code = null;
        data = null;
        System.out.println("File has been readed.");
        f_is.close();

    }
    
    /**
     * @dev Creates short of the bytes given.
     * @param FByte
     * @param SByte
     * @return
     */
    short createShort(byte FByte, byte SByte){
        short temp = (short) (FByte*256);
        temp = (short) (temp+SByte);
        return temp;
    }

    
    @Override
    /**
     * Compares process priority to the priority in process.
     * @param p1
     * @return
     */
    public int compareTo(process p1) {
        return p1.PCB.getP_Priority() > PCB.getP_Priority() ? 1 : -1;
    }

}
