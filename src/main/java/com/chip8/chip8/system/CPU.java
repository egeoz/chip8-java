package com.chip8.chip8.system;

public class CPU {

    private final byte[] V;
    private final short[] stack;
    private short PC;
    private short I;
    private short sp;
    private byte DT;
    private byte ST;

    public CPU() {
        V = new byte[Chip8.REGISTER_COUNT];
        stack = new short[Chip8.STACK_SIZE];
        PC = Chip8.PC_START;
        I = Chip8.I_START;
        sp = 0;
        DT = 0x00;
        ST = 0x00;
    }

    public byte getV(int index) {
        if (index >= Chip8.REGISTER_COUNT || index < 0) {
            throw new IllegalArgumentException(String.format("Invalid register index: %d", index));
        }
        return V[index];
    }

    public void setV(int index, byte value) {
        if (index >= Chip8.REGISTER_COUNT || index < 0) {
            throw new IllegalArgumentException(String.format("Invalid register index: %d", index));
        }
        V[index] = value;
    }

    public short getStack(int index) {
        if (index >= Chip8.STACK_SIZE || index < 0) {
            throw new IllegalArgumentException(String.format("Invalid stack index: %d", index));
        }
        return stack[index];
    }

    public void setStack(int index, short value) {
        if (index >= Chip8.STACK_SIZE || index < 0) {
            throw new IllegalArgumentException(String.format("Invalid stack index: %d", index));
        }
        stack[index] = value;
    }

    public short getPC() {
        return PC;
    }

    public void setPC(short PC) {
        this.PC = PC;
    }

    public short getI() {
        return I;
    }

    public void setI(short i) {
        I = i;
    }

    public short getSp() {
        return sp;
    }

    public void setSp(short sp) {
        this.sp = sp;
    }

    public byte getDT() {
        return DT;
    }

    public void setDT(byte DT) {
        this.DT = DT;
    }

    public byte getST() {
        return ST;
    }

    public void setST(byte ST) {
        this.ST = ST;
    }

    public void incrementProgramCounter(int amount) {
        setPC((short) (getPC() + (amount * 2)));
    }

    public void incrementProgramCounter() {
        incrementProgramCounter(1);
    }
}
