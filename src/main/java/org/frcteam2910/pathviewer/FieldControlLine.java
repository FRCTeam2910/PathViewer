package org.frcteam2910.pathviewer;

import javafx.scene.shape.Line;

public class FieldControlLine extends Line {
    private final FieldPrimaryControlPoint primaryControlPoint;
    private FieldSecondaryControlPoint secondaryControlPoint = null;

    public FieldControlLine(FieldPrimaryControlPoint primaryControlPoint, FieldSecondaryControlPoint secondaryControlPoint) {
        getStyleClass().add("field-control-line");

        this.primaryControlPoint = primaryControlPoint;
        this.secondaryControlPoint = secondaryControlPoint;

        startXProperty().bind(primaryControlPoint.centerXProperty());
        startYProperty().bind(primaryControlPoint.centerYProperty());
        endXProperty().bind(secondaryControlPoint.centerXProperty());
        endYProperty().bind(secondaryControlPoint.centerYProperty());
    }

    public FieldControlLine(FieldRotationControlPoint rotationControlPoint, FieldPrimaryControlPoint primaryControlPoint) {
        getStyleClass().add("field-rotation-line");

        this.primaryControlPoint = primaryControlPoint;

        startXProperty().bind(rotationControlPoint.centerXProperty());
        startYProperty().bind(rotationControlPoint.centerYProperty());
        endXProperty().bind(primaryControlPoint.centerXProperty());
        endYProperty().bind(primaryControlPoint.centerYProperty());
    }

    public FieldPrimaryControlPoint getPrimaryControlPoint() {
        return primaryControlPoint;
    }

    public FieldSecondaryControlPoint getSecondaryControlPoint() {
        return secondaryControlPoint;
    }
}
