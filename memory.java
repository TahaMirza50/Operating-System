import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 *  Main Memory - a consecutive 64KB array of bytes
 * It is divided into 512 pages of 128 bytes each
 */

public class memory {
    public static byte[] mem = new byte[65536];

    public static pagetable memTable = new pagetable(512);
    public int pageSize = 128;


    // set the value in big endian format in memory at the provided offset
    public void setMem(int offset, short value) {
        byte firstByte = getFirstByte(value);
        byte secondByte = getSecondByte(value);

        mem[offset] = firstByte;
        mem[++offset] = secondByte;
    }


    // get the byte at the provided offset
    public byte getMemByte(int offset) {
        if (offset > 65535) {
            System.out.println("Memory out of bounds");
            return 0;
        }
        return mem[offset];
    }

    // get the short value by combining the two successive bytes at the provided offset
    public short getMemShort(int offset) {
        if (offset > 65535) {
            System.out.println("Memory out of bounds");
            return 0;
        }

        byte firstByte = mem[offset];
        byte secondByte = mem[++offset];

        return createShort2(firstByte, secondByte);
    }

    // fetches the next available empty frame in memory and stores the entire or part of the process in it
    public int codeLoad(byte[] carry, int codeSize)
    {
        int i = search();

        if(i==-1){
            System.out.println("Memory is full");
        } else {
            memTable.setFrame(i, i);
            //System.out.println(i);
            int j = 0;
            for ( j=0; j<codeSize ; j++){
                int memind = i*128+j;
                mem[ memind ] = carry[j];
            }
        }
        return i;
    }


    // searches the memory for an empty frame
    public int search() {
        int i = 0;
        for (i = 0; (i < memTable.size) && (memTable.getFlag(i) == 1) ; i++);
        if (memTable.getFlag(i) == 0) 
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

    String printMemory()
    {
        String output = "";
        for (int i=0 ; i<65536 ; i++)
        {
            System.out.print(Integer.toHexString((mem[i]&0xff)) + " ");
            output += Integer.toHexString(mem[i]) + " ";
        } 
        memTable.printTable();
        return output;
    }
}