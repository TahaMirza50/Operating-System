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
    pagetable datapt = new pagetable(20);
    pagetable codept = new pagetable(20);
    pagetable stackpt = new pagetable(20);
    private int currPC;
    private int instCount;

    public pcb() {
        reg = new regFile();
        currPC = 0;
        instCount = 0;
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


    int getinstcounter(){ 
        return instCount;
    }
   
    void incinstcounter(){
        instCount++;
    }

    int getcount(){
        return currPC;
    }
  
    void setcount(int count){
        currPC = count;
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
}