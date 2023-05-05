package com.chip8.chip8.system;

import java.util.Arrays;

public class Memory {
    private final VideoMemory videoMemory;
    private final int[] memory;

    private void loadFont() {
        System.arraycopy(Chip8.FONT, 0, memory, 0, 80);
    }

    public Memory() {
        videoMemory = new VideoMemory();
        memory = new int[Chip8.MEMORY_SIZE];
        Arrays.fill(memory, 0);
        loadFont();
    }

    public VideoMemory getVideoMemory() {
        return videoMemory;
    }

    public int getMemory(int index) {
        if (index > Chip8.MEMORY_SIZE || index < 0) {
            throw new IllegalArgumentException(String.format("Invalid memory index : %d", index));
        }
        return memory[index];
    }

    public void setMemory(int index, int value) {
        if (index > Chip8.MEMORY_SIZE || index < 0) {
            throw new IllegalArgumentException(String.format("Invalid memory index : %d", index));
        }
        memory[index] = value;
    }

    public void loadRom(byte[] romData) {
        for (int i = 0; i < romData.length; i++) {
            setMemory(i + 512, romData[i] & 0xFF);
        }
    }
}
