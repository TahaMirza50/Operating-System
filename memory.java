import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 *  Main Memory - a consecutive 64KB array of bytes
 */

public class memory {
    public static byte[] mem = new byte[65536];

    public static pagetable memTable = new pagetable(64);
    public int pageSize = 1024;



    public void setMem(int offset, short value) {
        byte firstByte = getFirstByte(value);
        byte secondByte = getSecondByte(value);

        mem[offset] = firstByte;
        mem[++offset] = secondByte;
    }



    public byte getMemByte(int offset) {
        if (offset > 65535) {
            System.out.println("Memory out of bounds");
            return 0;
        }
        return mem[offset];
    }

    public short getMemShort(int offset) {
        if (offset > 65535) {
            System.out.println("Memory out of bounds");
            return 0;
        }

        byte firstByte = mem[offset];
        byte secondByte = mem[++offset];

        return createShort(firstByte, secondByte);
    }

    public int codeLoad(byte[] carry, int codeSize)
    {
        int i = search();

        if(i==-1){
            System.out.println("Memory is full");
        } else {
            memTable.setFrame(i, 1);
            int j = 0;
            for ( j=0; j<codeSize ; j++){
                int memind = i*1024+j;
                mem[ memind ] = carry[j];
            }
        }
        return i;
    }

    //TODO
    /**
     * It reads the provided instruction file and stores them in main memory to be executed later by the CPU.
     * @param fileName - file that contains the instructions
     * @param mem - reference to the CPU's main memory
     */
    /* 
    public static void storeCode(String fileName, byte[] mem) {
        try {
            File codeObj = new File(fileName);
            Scanner reader = new Scanner(codeObj);
            while (reader.hasNextLine()) {
              String line = reader.nextLine();

              String[]  data = line.split(" ");

              for (int i = 0; i < data.length; i++) {
                mem[i] = (byte) Integer.parseInt(data[i], 16);
                //short value = (short) Integer.parseInt(data[i], 16);
                //mem.setMem(i, value);
              }
            }
            reader.close();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred: " + e);
          }
    }
*/
    public int search() {
        int i = 0;
        for (i = 0; (i < memTable.size) && (memTable.getFrame(i) == 1); i++);
        if (memTable.getFrame(i) == 0) 
            return i;
        else 
            return -1;
    }
    
    /**
     * @dev returns the 8 LSB bits from the given short value.
     * @param value
     * @return byte
     */
    byte getFirstByte(short value) { 
        byte tmp = (byte)(value >> 8);
        return tmp;
    }

     /**
     * @dev returns the 8 MSB bits from the given short value.
     * @param value
     * @return byte
     */
    byte getSecondByte(short value) { 
        byte tmp = (byte) value;
        return tmp;
    }

    /**
     * @dev combines the two bytes to form a short val in Big endian format
     * @param Firstbyte
     * @param Secondbyte
     * @return short
     */
    short createShort(byte FByte, byte SByte){
        short temp = (short) (FByte*256);
        temp = (short) (temp+SByte);
        return temp;
    }
    short createShort2(byte Firstbyte,byte Secondbyte) { 
        String hex = Integer.toHexString(Firstbyte & 0xFF) + Integer.toHexString(Secondbyte & 0xFF);
        short tmp = (short) Integer.parseInt(hex,16);
        return tmp;
    }
    void printMemory()
    {
        for (int i=0 ; i<65536 ; i++)
        {
            System.out.print(Integer.toHexString(mem[i]) + " ");
        } 
    }
}