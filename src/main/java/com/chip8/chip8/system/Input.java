package com.chip8.chip8.system;

import javafx.scene.input.KeyCode;

public class Input {
    private final boolean[] keys;

    public Input() {
        keys = new boolean[Chip8.KEY_COUNT];
    }

    public void pressKey(KeyCode key) {
        switch (key) {
            case Z -> keys[0] = true;
            case DIGIT1 -> keys[1] = true;
            case DIGIT2 -> keys[2] = true;
            case DIGIT3 -> keys[3] = true;
            case Q -> keys[4] = true;
            case W -> keys[5] = true;
            case E -> keys[6] = true;
            case A -> keys[7] = true;
            case S -> keys[8] = true;
            case D -> keys[9] = true;
            case LESS -> keys[10] = true;
            case X -> keys[11] = true;
            case DIGIT4 -> keys[12] = true;
            case R -> keys[13] = true;
            case F -> keys[14] = true;
            case C -> keys[15] = true;
        }

    }

    public void releaseKey(KeyCode key) {
        switch (key) {
            case Z -> keys[0] = false;
            case DIGIT1 -> keys[1] = false;
            case DIGIT2 -> keys[2] = false;
            case DIGIT3 -> keys[3] = false;
            case Q -> keys[4] = false;
            case W -> keys[5] = false;
            case E -> keys[6] = false;
            case A -> keys[7] = false;
            case S -> keys[8] = false;
            case D -> keys[9] = false;
            case LESS -> keys[10] = false;
            case X -> keys[11] = false;
            case DIGIT4 -> keys[12] = false;
            case R -> keys[13] = false;
            case F -> keys[14] = false;
            case C -> keys[15] = false;
        }

    }

    public boolean isPressed(int index) {
        return keys[index];
    }
}
