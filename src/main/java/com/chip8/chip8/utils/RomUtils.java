package com.chip8.chip8.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
public class RomUtils {
    private static final Logger log = Logger.getLogger(RomUtils.class.getName());

    public static byte[] readRomFromFile (String filePath) {
        try {
            return Files.readAllBytes(Path.of(filePath));
        } catch (IOException e) {
            log.severe("Unable to read file: " + e.getMessage());
            return null;
        }
    }
}
