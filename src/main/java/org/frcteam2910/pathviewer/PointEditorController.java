package org.frcteam2910.pathviewer;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;

public class PointEditorController {
    @FXML
    private TextField txtX;
    @FXML
    private TextField txtY;
    @FXML
    private TextField txtRotationField;
    @FXML
    private Button btnUpdate;

    @FXML
    private void initialize() {
        txtX.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
        txtY.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
        txtRotationField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));

        fieldPointProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                txtX.setText(String.valueOf(newValue.getCenterX()));
                txtY.setText(String.valueOf(newValue.getCenterY()));
                txtRotationField.setText(String.valueOf(newValue.getRotate()));
            }
        });
    }

    @FXML
    private void updateValues(ActionEvent actionEvent) {
        if(getFieldPoint() == null) {
            return;
        }

        getFieldPoint().setCenter(Double.parseDouble(txtX.getText()), Double.parseDouble(txtY.getText()));
        getFieldPoint().setRotate(Double.parseDouble(txtRotationField.getText()));
    }

    private final SimpleObjectProperty<FieldPoint> fieldPoint = new SimpleObjectProperty<>();

    public ObjectProperty<FieldPoint> fieldPointProperty() {
        return fieldPoint;
    }

    public FieldPoint getFieldPoint() {
        return fieldPoint.get();
    }

    public void setFieldPoint(FieldPoint fieldPoint) {
        fieldPointProperty().set(fieldPoint);
    }
}
