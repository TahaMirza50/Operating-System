import java.io.*;

public class process
{
    memory sharedMem;
    int i = 0;
    pcb PCB;
    //static int p = 0;  
    cycle exe = new cycle();

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
        byte pagePart[] = new byte[1024];

        
        int codePages = PCB.getP_Size_C() / 1024;
        while(codePages>=0){
            for(int i=0; ((1024 * currPageNO) + i) < PCB.getP_Size_C() && i<1024 ; i++){
                pagePart[i] = code[i +(1024 * currPageNO)];
            }
            int frame = sharedMem.codeLoad(pagePart, 1024);
            PCB.codept.setFrame(currPageNO,frame);
            ++currPageNO;
            codePages--;
        }
        currPageNO = 0;
        pagePart = new byte[1024];
        int dataPages = PCB.getP_Size_D() / 1024;
        while(dataPages>=0){
            for(int i=0; ((1024 * currPageNO) + i) < PCB.getP_Size_D() && i<1024 ; i++){
                pagePart[i] = data[i +(1024 * currPageNO)];
            }
            int frame = sharedMem.codeLoad(pagePart, 1024);
            PCB.datapt.setFrame(currPageNO,frame);
            currPageNO++;
            dataPages--;
        }

        code = null;
        data = null;
        System.out.println("File has been readed.");
        sharedMem.printMemory();
    }

    byte getCode(short pc) {
        short pageno = (short) (pc / 1024);
        short d = (short) (pc % 1024);
        int frame = PCB.codept.getFrame(pageno);
        return  sharedMem.mem[(frame * 1024) + d];
    }

    short getData(short offset) {  
        short pageno = (short) (offset / 1024);
        short d = (short) (offset % 1024);
        int frame = PCB.datapt.getFrame(pageno);
        byte Fbyte = sharedMem.mem[(frame * 1024) + d];
        byte Sbyte = sharedMem.mem[(frame * 1024) + d+1];
        return sharedMem.createShort(Fbyte, Sbyte);
    }
    
    void setData(short offset,short value){ 
        short pageno = (short) (offset / 1024);
        short d = (short) (offset % 1024);
        int frame = PCB.datapt.getFrame(pageno);
        byte Fbyte = sharedMem.getFirstByte(value);
        byte Sbyte = sharedMem.getSecondByte(value);
        sharedMem.mem[(frame * 1024) + d] = Fbyte;
        sharedMem.mem[(frame * 1024) + d+1] = Sbyte;
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

    // void executeProcess(){   
    //     sharedMem.printMemory();
    //     exe.run(PCB.reg, sharedMem);
    // }
}
