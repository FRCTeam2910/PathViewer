package org.frcteam2910.pathviewer;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.frcteam2910.common.Logger;
import org.frcteam2910.common.control.Path;
import org.frcteam2910.common.io.PathReader;
import org.frcteam2910.common.io.PathWriter;

import javax.annotation.CheckForNull;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Application {
    private static final Logger LOGGER = new Logger(Application.class);

    @FXML
    private BorderPane root;
    @FXML
    private FieldDisplay fieldDisplay;

    /**
     * The path file that is currently being edited.
     */
    @CheckForNull
    private File currentFile = null;

    @FXML
    private void initialize() {
    }

    @CheckForNull
    private File showOpenPathDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Path", "*.path", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        fileChooser.setTitle("Open Path");
        return fileChooser.showOpenDialog(root.getScene().getWindow());
    }

    @CheckForNull
    private File showSavePathDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Path", "*.path"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        fileChooser.setTitle("Save Path");
        return fileChooser.showSaveDialog(root.getScene().getWindow());
    }

    @FXML
    private void onOpen() {
        //TODO: rotation points not being properly set
        currentFile = showOpenPathDialog();
        if (currentFile == null) {
            // No file was selected. Don't do anything
            return;
        }

        try (PathReader reader = new PathReader(new FileReader(currentFile, StandardCharsets.UTF_8))) {
            Path path = reader.read();

            fieldDisplay.setPath(path);
        } catch (IOException e) {
            // If we were unable to read the path file, let the user know and log to console.
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setHeaderText("Unable to open path");
            alert.show();

            LOGGER.error(e);
        }
    }

    @FXML
    private void onSave() {
        Path path = fieldDisplay.getPath();
        // If there is no path on the display tell the user. We can't save an empty path.
        if (path == null) {
            new Alert(Alert.AlertType.WARNING, "This path cannot be saved because it is empty.", ButtonType.OK)
                    .show();
            return;
        }

        // Let the user select a file to save to if no file is selected.
        if (currentFile == null) {
            currentFile = showSavePathDialog();
            if (currentFile == null) {
                return;
            }
        }

        // Save the path to the current file.
        try (PathWriter writer = new PathWriter(new FileWriter(currentFile, StandardCharsets.UTF_8))) {
            writer.write(path);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setHeaderText("Unable to save path");
            alert.show();

            LOGGER.error(e);
        }
    }

    @FXML
    private void onSaveAs() {
        // If there is no path on the display tell the user. We can't save an empty path.
        if (fieldDisplay.getPath() == null) {
            new Alert(Alert.AlertType.WARNING, "This path cannot be saved because it is empty.", ButtonType.OK)
                    .show();
            return;
        }

        currentFile = showSavePathDialog();
        if (currentFile != null) {
            onSave();
        }
    }

    @FXML
    private void onExit() {
        System.exit(0);
    }
}
