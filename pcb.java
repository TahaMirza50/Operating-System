import java.io.FileWriter;

public class pcb
{
    private int p_ID;
    private byte p_Priority;
    private int p_Size_C;
    private int p_Size_D;
    private int p_exectionTime;
    private int p_waitingTime;
    private String p_Name;
    regFile reg;
    pagetable datapt = new pagetable(512);
    pagetable codept = new pagetable(512);
    pagetable stackpt = new pagetable(512);

    public pcb() {
        reg = new regFile();
    }

    public pcb(byte priority, byte id, String filename, byte sized, byte sizec){
        p_Priority = priority;
        p_ID = id;
        p_Name = filename;
        p_Size_D = sized;
        p_Size_C = sizec;
    }

    void incWaitTime() {
        p_waitingTime += 2;
    }

    int getWaitTime() {
        return p_waitingTime;
    }

    void setExecutionTime(int time) {
        p_exectionTime += time;
    }

    int getExecutionTime() {
        return p_exectionTime;
    }

    int getP_ID(){
        return p_ID;
    }

    byte getP_Priority() {
        return p_Priority;
    }

    void setP_ID(int id) {
        p_ID = id;
    }

    void setP_Priority(byte priority) {
        p_Priority = priority;
    }

    void setP_Size_C(int size) {
        p_Size_C = size;
    }

    void setP_Size_D(int size) {
        p_Size_D = size;
    }

    String getName() {
        return p_Name;
    }

    void setP_Name(String file) {
        p_Name = file;
    }

    int getP_Size_C() {
        return p_Size_C;
    }
    
    int getP_Size_D() {
        return p_Size_D;
    }

    void set_cpt(int frameno, int pageno){ 
        codept.setFrame(frameno,pageno);
    }

    void set_dpt(int frameno, int pageno){ 
        datapt.setFrame(frameno,pageno);
    }

    void set_spt(int frameno, int pageno){ 
        stackpt.setFrame(frameno,pageno);
    }

    int get_cpt(int pageno){ 
        return codept.getFrame(pageno);
    }

    int get_dpt(int pageno){ 
        return datapt.getFrame(pageno);
    }

    int get_spt(int pageno){ 
        return stackpt.getFrame(pageno);
    }

    void printPcbToFile(FileWriter fw) {
        try {
            fw.write("Process ID: " + p_ID + "\n");
            fw.write("Process Name: " + p_Name + "\n");
            fw.write("Process Priority: " + p_Priority + "\n");
            fw.write("Process Size Code: " + p_Size_C + "\n");
            fw.write("Process Size Data: " + p_Size_D + "\n");
            fw.write("Process Execution Time: " + p_exectionTime + "\n");
            fw.write("Process Waiting Time: " + p_waitingTime + "\n");
            fw.write("Process Register Values: " + reg.printGenReg() + "\n");
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    void printProcessMemory(FileWriter fw, memory mem) {
        try {
            
            short dataBase = reg.getReg((byte) 23); // data base
            short dataLimt = reg.getReg((byte) 24); // data limit
            fw.write("Data" + "\n");
            for (int i = dataBase; i <= dataBase+dataLimt; i++) {
                fw.write(Integer.toHexString(mem.getMemByte(i)) + " ");
            }
            fw.write("\n");
            fw.write("----------------" + "\n");

            short codeBase = reg.getReg((byte) 17); // code base
            short codeLimt = reg.getReg((byte) 18); // code limit

            fw.write("Code" + "\n");
            for (int i = codeBase; i <= codeBase+codeLimt; i++) {
                fw.write(Integer.toHexString(mem.getMemByte(i)) + " ");
            }
            fw.write("\n");
            fw.write("----------------" + "\n");

            short stackBase = reg.getReg((byte) 20); // stack base
            short stackLimt = reg.getReg((byte) 21); // stack limit

            fw.write("Stack" + "\n");
            for (int i = stackBase; i <= stackBase+stackLimt; i++) {
                fw.write(Integer.toHexString(mem.getMemByte(i)) + " ");
            }
            fw.write("\n");
            fw.write("----------------" + "\n");
            
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}