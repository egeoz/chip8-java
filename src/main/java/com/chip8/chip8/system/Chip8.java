package com.chip8.chip8.system;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class Chip8 {
    private static final Logger log = Logger.getLogger(Chip8.class.getName());

    public static final short SCREEN_WIDTH = 64;
    public static final short SCREEN_HEIGHT = 32;
    public static final short MEMORY_SIZE = 4096;
    public static final short REGISTER_COUNT = 16;
    public static final short STACK_SIZE = 16;
    public static final short PC_START = 0x200;
    public static final short I_START = 0x0000;
    public static final short KEY_COUNT = 16;
    public static final int[] FONT = {
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x50, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
    };

    private static Chip8 chip8;
    private CPU cpu;
    private ROM rom;
    private Memory memory;
    private Input input;
    private final Random random;
    private Timer emulationTimer;
    private int emulationSpeed;

    public static synchronized Chip8 getInstance() {
        if (chip8 == null) {
            chip8 = new Chip8();
        }

        return chip8;
    }

    public Chip8() {
        this(null, null, null, null);
    }

    public Chip8(CPU cpu, ROM rom, Memory memory, Input input) {
        this.cpu = cpu;
        this.rom = rom;
        this.memory = memory;
        this.input = input;
        random = new Random();
        emulationSpeed = 5;
    }

    public ROM getRom() {
        return rom;
    }

    public Memory getMemory() {
        return memory;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    public void setRom(ROM rom) {
        this.rom = rom;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public int getEmulationSpeed() {
        return emulationSpeed;
    }

    public void setEmulationSpeed(int emulationSpeed) {
        this.emulationSpeed = emulationSpeed;
    }

    private int fetch(int location) {
        if (location > MEMORY_SIZE - 1 || location < 0) {
            log.severe("Invalid location: " + location);
        }

        return ((memory.getMemory(location) << 8) | (memory.getMemory(location + 1)));
    }

    private int fetch() {
        return fetch(cpu.getPC());
    }

    private void decodeAndExecute(int instruction) {
        switch (instruction) {
            case 0x00E0 -> {
                memory.getVideoMemory().clear();
                memory.getVideoMemory().setFullUpdateFlag(true);
                cpu.incrementProgramCounter();
            }
            case 0x00EE -> {
                if ((cpu.getSp() - 1) < 0) {
                    return;
                }
                cpu.setPC(cpu.getStack(cpu.getSp()));
                cpu.setSp((short) (cpu.getSp() - 1));
                cpu.incrementProgramCounter();
            }
            default -> {
                switch (instruction & 0xF000) {
                    case 0x1000 -> {
                        cpu.setPC((short) (instruction & 0x0FFF));
                    }
                    case 0x2000 -> {
                        if ((cpu.getSp() + 1) > 15) {
                            return;
                        }
                        cpu.setSp((short) (cpu.getSp() + 1));
                        cpu.setStack(cpu.getSp(), cpu.getPC());
                        cpu.setPC((short) (instruction & 0x0FFF));
                    }
                    case 0x3000 ->
                            cpu.setPC((short) (cpu.getV((instruction & 0x0F00) >>> 8) == (instruction & 0x00FF) ? cpu.getPC() + 4 : cpu.getPC() + 2));
                    case 0x4000 ->
                            cpu.setPC((short) (cpu.getV((instruction & 0x0F00) >>> 8) != (instruction & 0x00FF) ? cpu.getPC() + 4 : cpu.getPC() + 2));
                    case 0x5000 ->
                            cpu.setPC((short) (cpu.getV((instruction & 0x0F00) >>> 8) == cpu.getV(instruction & 0x00F0 >>> 4) ? cpu.getPC() + 4 : cpu.getPC() + 2));
                    case 0x6000 -> {
                        cpu.setV((instruction & 0x0F00) >>> 8, (byte) (instruction & 0x00FF));
                        cpu.incrementProgramCounter();
                    }
                    case 0x7000 -> {
                        int result = cpu.getV((instruction & 0x0F00) >>> 8) + (instruction & 0x00FF);

                        cpu.setV((instruction & 0x0F00) >>> 8, (byte) (result < 256 ? result : result - 256));
                        cpu.incrementProgramCounter();
                    }
                    case 0x8000 -> {
                        switch (instruction & 0x000F) {
                            case 0x0000 -> {
                                cpu.setV((instruction & 0x0F00) >>> 8, cpu.getV((instruction & 0x00F0) >>> 4));
                                cpu.incrementProgramCounter();
                            }
                            case 0x0001 -> {
                                cpu.setV((instruction & 0x0F00) >>> 8, (byte) (cpu.getV((instruction & 0x0F00) >>> 8) | cpu.getV((instruction & 0x00F0) >>> 4)));
                                cpu.incrementProgramCounter();
                            }
                            case 0x0002 -> {
                                cpu.setV((instruction & 0x0F00) >>> 8, (byte) (cpu.getV((instruction & 0x0F00) >>> 8) & cpu.getV((instruction & 0x00F0) >>> 4)));
                                cpu.incrementProgramCounter();
                            }
                            case 0x0003 -> {
                                cpu.setV((instruction & 0x0F00) >>> 8, (byte) (cpu.getV((instruction & 0x0F00) >>> 8) ^ cpu.getV((instruction & 0x00F0) >>> 4)));
                                cpu.incrementProgramCounter();
                            }
                            case 0x0004 -> {
                                int x = (instruction & 0x0F00) >>> 8;
                                int sum = cpu.getV(x) + cpu.getV((instruction & 0x00F0) >>> 4);
                                cpu.setV(0xF, (byte) (sum > 0xFF ? 1 : 0));
                                cpu.setV(x, (byte) (sum & 0xFF));
                                cpu.incrementProgramCounter();
                            }
                            case 0x0005 -> {
                                int x = (instruction & 0x0F00) >>> 8;
                                int y = (instruction & 0x00F0) >>> 4;
                                cpu.setV(0xF, (byte) (cpu.getV(x) > cpu.getV(y) ? 1 : 0));
                                cpu.setV(x, (byte) (cpu.getV(x) - cpu.getV(y)));
                                cpu.incrementProgramCounter();
                            }
                            case 0x0006 -> {
                                int x = (instruction & 0x0F00) >>> 8;
                                cpu.setV(0xF, (byte) ((cpu.getV(x) & 0x1) == 1 ? 1 : 0));
                                cpu.setV(x, (byte) (cpu.getV(x) >>> 1));
                                cpu.incrementProgramCounter();
                            }
                            case 0x0007 -> {
                                int x = (instruction & 0x0F00) >>> 8;
                                int y = (instruction & 0x00F0) >>> 4;
                                cpu.setV(0xF, (byte) (cpu.getV(y) > cpu.getV(x) ? 1 : 0));
                                cpu.setV(x, (byte) (cpu.getV(y) - cpu.getV(x)));
                                cpu.incrementProgramCounter();
                            }
                            case 0x000E -> {
                                int x = (instruction & 0x0F00) >>> 8;
                                cpu.setV(0xF, (byte) ((cpu.getV(x) >>> 7) == 1 ? 1 : 0));
                                cpu.setV(x, (byte) (cpu.getV(x) << 1));
                                cpu.incrementProgramCounter();
                            }
                        }
                    }

                    case 0x9000 ->
                            cpu.setPC((short) ((cpu.getV((instruction & 0x0F00) >>> 8) != cpu.getV((instruction & 0x00F0) >>> 4)) ? cpu.getPC() + 4 : cpu.getPC() + 2));

                    case 0xA000 -> {
                        cpu.setI((short) (instruction & 0x0FFF));
                        cpu.incrementProgramCounter();
                    }

                    case 0xB000 -> {
                        cpu.setPC((short) (cpu.getV(0) + instruction & 0x0FFF));
                    }

                    case 0xC000 -> {
                        cpu.setV((instruction & 0x0F00) >>> 8, (byte) (random.nextInt(255) & (instruction & 0x00FF)));
                        cpu.incrementProgramCounter();
                    }
                    case 0xD000 -> {
                        int vx = cpu.getV((instruction & 0x0F00) >> 8);
                        int vy = cpu.getV((instruction & 0x00F0) >> 4);
                        int height = instruction & 0x000F;
                        cpu.setV(0xF, (byte) 0);

                        for (int col = 0; col < height; col++) {
                            int pixel = memory.getMemory(cpu.getI() + col);

                            for (int row = 0; row < 8; row++) {
                                if ((pixel & (0x80 >> row)) != 0) {
                                    int posX = vx + row;
                                    int posY = vy + col;

                                    if (posX < Chip8.SCREEN_WIDTH && posY < Chip8.SCREEN_HEIGHT) {
                                        if (memory.getVideoMemory().getMemory(posX, posY)) {
                                            cpu.setV(0xF, (byte) 1);
                                        }
                                        memory.getVideoMemory().setMemory(posX, posY);
                                        memory.getVideoMemory().setUpdateFlag(posX, posY);
                                    }
                                }
                            }
                        }
                        cpu.incrementProgramCounter();
                    }

                    case 0xE000 -> {
                        switch (instruction & 0x00FF) {
                            case 0x009E -> {
                                if (input.isPressed(cpu.getV((instruction & 0x0F00) >> 8))) {
                                    cpu.incrementProgramCounter(2);
                                } else {
                                    cpu.incrementProgramCounter();
                                }
                            }
                            case 0x00A1 -> {
                                if (!input.isPressed(cpu.getV((instruction & 0x0F00) >> 8))) {
                                    cpu.incrementProgramCounter(2);
                                } else {
                                    cpu.incrementProgramCounter();
                                }
                            }
                        }
                    }
                    case 0xF000 -> {
                        switch (instruction & 0x00FF) {
                            case 0x0007 -> {
                                cpu.setV((instruction & 0x0F00) >>> 8, cpu.getDT());
                                cpu.incrementProgramCounter();
                            }
                            case 0x000A -> {
                                for (int i = 0; i < 0xF; i++) {
                                    if (input.isPressed(i)) {
                                        cpu.setV((instruction & 0x0F00) >>> 8, (byte) i);
                                        cpu.incrementProgramCounter();
                                        memory.getVideoMemory().setFullUpdateFlag(true);
                                    }
                                }
                            }
                            case 0x0015 -> {
                                cpu.setDT(cpu.getV((instruction & 0x0F00) >>> 8));
                                cpu.incrementProgramCounter();

                            }
                            case 0x0018 -> {
                                cpu.setST(cpu.getV((instruction & 0x0F00) >>> 8));
                                cpu.incrementProgramCounter();
                            }
                            case 0x001E -> {
                                int vx = cpu.getV((instruction & 0x0F00) >>> 8);

                                if (cpu.getI() + vx > 0xFFF) {
                                    cpu.setV(0xF, (byte) 1);
                                } else {
                                    cpu.setV(0xF, (byte) 0);
                                }

                                cpu.setI((short) (cpu.getI() + vx));
                                cpu.incrementProgramCounter();
                            }
                            case 0x0029 -> {
                                cpu.setI((short) (cpu.getV((instruction & 0x0F00) >> 8) * 5));
                                cpu.incrementProgramCounter();
                            }
                            case 0x0033 -> {
                                int vx = cpu.getV((instruction & 0x0F00) >>> 8);

                                memory.setMemory(cpu.getI(), vx / 100);
                                memory.setMemory(cpu.getI() + 1, (vx % 100) / 10);
                                memory.setMemory(cpu.getI() + 2, (vx % 100) % 10);
                                cpu.incrementProgramCounter();
                            }
                            case 0x0055 -> {
                                for (int i = 0; i <= (instruction & 0x0F00) >>> 8; i++) {
                                    memory.setMemory(cpu.getI() + i, cpu.getV(i));
                                }
                                cpu.incrementProgramCounter();
                            }
                            case 0x0065 -> {
                                for (int i = 0; i <= (instruction & 0x0F00) >>> 8; i++) {
                                    cpu.setV(i, (byte) memory.getMemory(cpu.getI() + i));
                                }
                                cpu.incrementProgramCounter();
                            }
                        }
                    }
                }
            }
        }
    }

    private void startTimer() {
        emulationTimer = new Timer();
        emulationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (cpu.getDT() > 0) {
                    cpu.setDT((byte) (cpu.getDT() - 1));
                }

                if (cpu.getST() > 0) {
                    if (cpu.getST() == 1) {
                    }
                    cpu.setST((byte) (cpu.getST() - 1));
                }

                decodeAndExecute(fetch());
            }
        }, emulationSpeed, emulationSpeed);
    }

    public void startEmulation(boolean reset) {
        if (reset) {
            reset();
        }

        startTimer();
    }

    public void pauseEmulation() {
        emulationTimer.cancel();
    }

    private void reset() {
        setMemory(new Memory());
        setCpu(new CPU());
        memory.loadRom(getRom().getRomData());
        setInput(new Input());
        emulationSpeed = 5;

        if (emulationTimer != null) {
            emulationTimer.cancel();
            emulationTimer.purge();
        }
    }
}
