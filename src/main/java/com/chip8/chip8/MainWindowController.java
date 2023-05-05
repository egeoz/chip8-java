package com.chip8.chip8;

import com.chip8.chip8.system.Chip8;
import com.chip8.chip8.system.ROM;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.logging.Logger;

public class MainWindowController {
    private static final Logger log = Logger.getLogger(MainWindowController.class.getName());
    private static final int SCALE = 15;
    @FXML
    private Canvas canvas;
    @FXML
    private MenuItem menuEmulationContinue;
    @FXML
    private MenuItem menuEmulationPause;

    private GraphicsContext graphicsContext;
    private AnimationTimer screenTimer;

    @FXML
    private void initialize() {
        graphicsContext = canvas.getGraphicsContext2D();
    }

    @FXML
    private void onMenuEmulationContinue() {
        Chip8.getInstance().startEmulation(false);
        screenTimer.start();
        menuEmulationPause.setDisable(false);
        menuEmulationContinue.setDisable(true);
    }

    @FXML
    private void onMenuEmulationPause() {
        Chip8.getInstance().pauseEmulation();
        screenTimer.stop();
        menuEmulationPause.setDisable(true);
        menuEmulationContinue.setDisable(false);
    }

    @FXML
    private void onMenuEmulationSpeedSlow() {
        Chip8.getInstance().setEmulationSpeed(10);
        Chip8.getInstance().pauseEmulation();
        Chip8.getInstance().startEmulation(false);
    }

    @FXML
    private void onMenuEmulationSpeedNormal() {
        Chip8.getInstance().setEmulationSpeed(5);
        Chip8.getInstance().pauseEmulation();
        Chip8.getInstance().startEmulation(false);
    }

    @FXML
    private void onMenuEmulationSpeedFast() {
        Chip8.getInstance().setEmulationSpeed(2);
        Chip8.getInstance().pauseEmulation();
        Chip8.getInstance().startEmulation(false);
    }

    @FXML
    private void onMenuFileOpenClick() {
        Scene scene = canvas.getScene();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Chip-8 ROM");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Chip-8 ROMS", "*.ch8"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(scene.getWindow());

        clearScreen();

        if (file != null) {
            log.info("Loading " + file);
            Chip8.getInstance().setRom(new ROM(file.getPath()));
            Chip8.getInstance().startEmulation(true);

            screenTimer = getAnimationTimer();
            screenTimer.start();

            menuEmulationPause.setDisable(false);
            menuEmulationContinue.setDisable(true);

            scene.setOnKeyPressed(e -> Chip8.getInstance().getInput().pressKey(e.getCode()));
            scene.setOnKeyReleased(e -> Chip8.getInstance().getInput().releaseKey(e.getCode()));
        }
    }

    private AnimationTimer getAnimationTimer() {
        return new AnimationTimer() {
            @Override
            public void handle(long l) {
                updateScreen();
            }
        };
    }

    @FXML
    private void onMenuFileQuit() {
        System.exit(0);
    }

    @FXML
    private void onMenuHelpAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Java Chip-8 Emulator");
        alert.showAndWait().ifPresent(action -> {
            if (action == ButtonType.OK) {
                alert.close();
            }
        });
    }

    private void paintPixel(boolean white, int x, int y) {
        if ((x > Chip8.SCREEN_WIDTH || y > Chip8.SCREEN_HEIGHT) || (x < 0 || y < 0)) {
            throw new IllegalArgumentException(String.format("Invalid x, y: %d, %d", x, y));
        }

        if (white) {
            graphicsContext.setFill(Color.WHITE);
        } else {
            graphicsContext.setFill(Color.BLACK);
        }
        graphicsContext.fillRect(x * SCALE, y * SCALE, SCALE, SCALE);
    }

    private void clearScreen() {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void updateScreen() {
        for (int x = 0; x < Chip8.SCREEN_WIDTH; x++) {
            for (int y = 0; y < Chip8.SCREEN_HEIGHT; y++) {
                if (Chip8.getInstance().getMemory().getVideoMemory().getUpdateFlag(x, y) || Chip8.getInstance().getMemory().getVideoMemory().isFullUpdateFlag()) {
                    paintPixel(Chip8.getInstance().getMemory().getVideoMemory().getMemory(x, y), x, y);
                    Chip8.getInstance().getMemory().getVideoMemory().setUpdateFlag(x, y);
                }
            }
        }

        Chip8.getInstance().getMemory().getVideoMemory().setFullUpdateFlag(false);
    }
}