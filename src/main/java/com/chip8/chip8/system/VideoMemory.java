package com.chip8.chip8.system;

public class VideoMemory {
    private boolean[][] memory;
    private boolean[][] updateFlag;
    private boolean fullUpdateFlag;

    public VideoMemory() {
        memory = new boolean[Chip8.SCREEN_WIDTH][Chip8.SCREEN_HEIGHT];
        updateFlag = new boolean[Chip8.SCREEN_WIDTH][Chip8.SCREEN_HEIGHT];
        fullUpdateFlag = false;
    }

    public void clear() {
        memory = new boolean[Chip8.SCREEN_WIDTH][Chip8.SCREEN_HEIGHT];
        updateFlag = new boolean[Chip8.SCREEN_WIDTH][Chip8.SCREEN_HEIGHT];
        fullUpdateFlag = false;
    }

    public boolean getMemory(int x, int y) {
        if ((x > Chip8.SCREEN_WIDTH || y > Chip8.SCREEN_HEIGHT) || (x < 0 || y < 0)) {
            return false;
        }
        return memory[x][y];
    }

    public boolean getUpdateFlag(int x, int y) {
        if ((x >= Chip8.SCREEN_WIDTH || y >= Chip8.SCREEN_HEIGHT) || (x < 0 || y < 0)) {
            return false;
        }
        return updateFlag[x][y];
    }

    public void setMemory(int x, int y) {
        if ((x >= Chip8.SCREEN_WIDTH || y >= Chip8.SCREEN_HEIGHT) || (x < 0 || y < 0)) {
            return;
        }
        memory[x][y] ^= true;
    }

    public void setUpdateFlag(int x, int y) {
        if ((x >= Chip8.SCREEN_WIDTH || y >= Chip8.SCREEN_HEIGHT) || (x < 0 || y < 0)) {
            return;
        }
        updateFlag[x][y] ^= true;
    }

    public boolean isFullUpdateFlag() {
        return fullUpdateFlag;
    }

    public void setFullUpdateFlag(boolean fullUpdateFlag) {
        this.fullUpdateFlag = fullUpdateFlag;
    }
}
