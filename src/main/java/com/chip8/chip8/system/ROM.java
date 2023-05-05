package com.chip8.chip8.system;

import com.chip8.chip8.utils.RomUtils;

public class ROM {
    private final byte[] romData;

    public ROM(String filePath) {
        romData = RomUtils.readRomFromFile(filePath);
    }

    public byte[] getRomData() {
        return romData;
    }

}
