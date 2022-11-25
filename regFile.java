/* 
*  Register File - It handles all the operations that are carried on register and its values. A single array was used for all register to avoid writing 
*  operations again. Character array with 1 and 0 was used for Flag Register.
*/

public class regFile 
{
    private short[] Reg = new short[31]; 
    private char[] flag = {'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0'}; 
    public regFile()
    {
        setReg((byte) 16, (short) 0); // first special purpose register (Zero Register)
        //setReg((byte) 17, (short) 0); // code base
        //setReg((byte) 18, (short) 0);  code limit
        //setReg((byte) 19, (short) 0); // code counter
        //setReg((byte) 20, (short) 65535); // stack base
        //setReg((byte) 21, (short) 50); // stack limit (50 bytes)
        //setReg((byte) 22, (short) 65535); // stack counter
        //setReg((byte) 23, (short) 0); // data base
        //setReg((byte) 24, (short) 0); // data limit
        //setReg((byte) 25, (short) 0); // data counter
    }
    public short getReg(byte reg)
    {
        return Reg[reg];
    }
    public void setReg(byte code, short value)
    {
        Reg[code] = value;
    }
    /**
     * @dev The function checks if answer is negative or zero and set sign or zero bit accordingly in flag register.
     * @param answer
     */
    public void setZero_Sign(short answer)
    {
        if (answer == 0)
            flag[1] = '1';
        else
            flag[1] = '0';
        if (answer < 0)
            flag[2] ='1';
        else
            flag[2] ='0';
    }
    /**
     * @dev The function checks if there would be carry in answer after applying shift or rotate operations.
     * If target value is less than 0 means when MSB is ON.  
     * @param trg
     */
    public void setCarry(short trg)
    {
        if(trg<0)
            flag[0] = '1';
        else
            flag[0] = '0';    
    }
    /**
     * @dev Checks for the overflow, Each case is checked differently depending on operations.
     * 1 = ADD, 2 = SUBTRACT, 3 = MULTIPLY, 5 = AND , 6 = OR.
     * @param trg = target register value before operation
     * @param src = source register value before operation 
     * @param x
     */
    public void setOverflow(short trg, short src, int x)
    {
        boolean bit = false;
        short result;
        int itrg = 0, isrc=0, iresult=0;
        switch (x)
        {
            case 1:
                result = (short) (trg + src);
                if (src<0 && trg<0 && result>0)
                    bit = true;
                else if (src>0 && trg>0 && result<0)
                    bit = true;
                break;
            case 2:
                result = (short) (trg - src);
                if (src<0 && trg>0 && result<0)
                    bit = true;
                else if (src>0 && trg<0 && result>0)
                    bit = true;
                break;
            case 3:
                result = (short) (trg * src);
                if (src == 0 || trg == 0)
                    bit = false;
                if (src == result / trg)
                    bit = false;
                else
                    bit = true;
                break;    
            case 5:       
                result = (short) (trg & src);
                itrg = (int) trg;
                isrc = (int) src;
                iresult = itrg & isrc;
                if(iresult != result)
                    bit = true;
                break;
            case 6:
                result = (short) (trg | src);
                itrg = (int) trg;
                isrc = (int) src;
                iresult = itrg | isrc;
                if(iresult != result)
                    bit = true;
                break;
        }
        if(!bit)
            flag[3] = '0';
        else
            flag[3] = '1';
    }
    /**
     * @dev Reinitialize flag register values to 0.
     */
    public void reFlag()
    {
        for(int i = flag.length-1 ; i >=0 ; i--){
            flag[i] = '0';
        }
    }
    /**
     * @dev It returns the value of bit at the index x in flag register.
     * @param x
     * @return
     */
    public char getFlag(int x)
    {
        return flag[x];
    }
    /**
     * @dev Following are the arithmetic, logical, rotate, shift operations on registers and immediate.
     * @param trg = target register
     * @param src = source register
     * @param num = immediate
     */
    public void MOV(byte trg, byte src)
    {
        Reg[trg] = Reg[src];
    }
    public void ADD(byte trg, byte src)
    {
        setOverflow(Reg[trg], Reg[src], 1);
        Reg[trg] += Reg[src];
        setZero_Sign(Reg[trg]);
    }
    public void SUB(byte trg, byte src)
    {
        setOverflow(Reg[trg], Reg[src], 2);
        Reg[trg] -= Reg[src];
        setZero_Sign(Reg[trg]);
    }
    public void MUL(byte trg, byte src)
    {
        setOverflow(Reg[trg], Reg[src], 3);
        Reg[trg] *= Reg[src];
        setZero_Sign(Reg[trg]);
    }
    public void DIV(byte trg, byte src)
    {
        if(Reg[trg] != 0)
        {
            Reg[trg] /= Reg[src];
            setZero_Sign(Reg[trg]);
        }
        else
            System.out.println("Not divisible by 0");
    }
    public void AND(byte trg, byte src)
    {
        setOverflow(Reg[trg], Reg[src], 5);
        Reg[trg] &= Reg[src];
        setZero_Sign(Reg[trg]);
    }
    public void OR(byte trg, byte src)
    {
        setOverflow(Reg[trg], Reg[src], 6);
        Reg[trg] |= Reg[src];
        setZero_Sign(Reg[trg]);
    }
    public void MOVI(byte trg, short num)
    {
        Reg[trg] = num;
    }
    public void ADDI(byte trg, short num)
    {
        setOverflow(Reg[trg], num, 1);
        Reg[trg] += num;
        setZero_Sign(Reg[trg]);
    }
    public void SUBI(byte trg, short num)
    {
        setOverflow(Reg[trg], num, 2);
        Reg[trg] -= num;
        setZero_Sign(Reg[trg]);
    }
    public void MULI(byte trg, short num)
    {
        setOverflow(Reg[trg], num, 3);
        Reg[trg] *= num;
        setZero_Sign(Reg[trg]);
    }
    public void DIVI(byte trg, short num)
    {
        if(Reg[trg] != 0)
        {
            Reg[trg] /= num;
            setZero_Sign(Reg[trg]);
        }
        else
            System.out.println("Not divisible by 0");
    }
    public void ANDI(byte trg, short num)
    {
        setOverflow(Reg[trg], num, 5);
        Reg[trg] &= num;
        setZero_Sign(Reg[trg]);
    }
    public void ORI(byte trg, short num)
    {
        setOverflow(Reg[trg], num, 6);
        Reg[trg] |= num;
        setZero_Sign(Reg[trg]);
    }
    public void SHL(byte trg)
    {
        setCarry(Reg[trg]);
        Reg[trg] <<= 1;
        setZero_Sign(Reg[trg]);
    }
    public void SHR(byte trg)
    {
        setCarry(Reg[trg]);
        Reg[trg] <<= 1;
        setZero_Sign(Reg[trg]);
    }
    public void RTL(byte trg)
    {
        setCarry(Reg[trg]);
        Reg[trg] = (short) Integer.rotateLeft(Reg[trg],1);
        setZero_Sign(Reg[trg]);
    }
    public void RTR(byte trg)
    {
        setCarry(Reg[trg]);
        Reg[trg] = (short) Integer.rotateLeft(Reg[trg],-1);
        setZero_Sign(Reg[trg]);
    }
    public void INC(byte trg)
    {
        Reg[trg]++;
    }
    public void DEC(byte trg)
    {
        Reg[trg]--;
    }
    /**
     * @dev General Register are printed first then Special Register.
     * For special register address is converted to int first to avoid negative addresses.
     * Flag register is printed in binary with MSB = flag[16] and LSB = flag[0].
     */
    public void printGenReg() {
        System.out.println("General Register values:");
        for (int i = 0; i < 16; i++) {
            System.out.println("Reg " + (i) + ": " + Reg[i]);
        }

        System.out.println("Special Register values:");
        for (int i = 16; i < Reg.length; i++) {
            System.out.println("Reg " + (i) + ": " + Short.toUnsignedInt(Reg[i]));

        }

        System.out.println("Flag Register");
        for(int i = flag.length-1 ; i >=0 ; i--){
            System.out.print(flag[i]);
        }
        System.out.println();
        System.out.println("------------------------------");
    }
}
