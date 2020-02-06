package org.frcteam2910.pathviewer;

import javafx.scene.shape.Line;

public class FieldControlLine extends Line {
    public static final String STYLE_CLASS_NAME = "field-control-line";

    private final FieldPrimaryControlPoint primaryControlPoint;
    private final FieldSecondaryControlPoint secondaryControlPoint;

    public FieldControlLine(FieldPrimaryControlPoint primaryControlPoint, FieldSecondaryControlPoint secondaryControlPoint) {
        getStyleClass().add(STYLE_CLASS_NAME);

        this.primaryControlPoint = primaryControlPoint;
        this.secondaryControlPoint = secondaryControlPoint;

        startXProperty().bind(primaryControlPoint.centerXProperty());
        startYProperty().bind(primaryControlPoint.centerYProperty());
        endXProperty().bind(secondaryControlPoint.centerXProperty());
        endYProperty().bind(secondaryControlPoint.centerYProperty());
    }

    public FieldPrimaryControlPoint getPrimaryControlPoint() {
        return primaryControlPoint;
    }

    public FieldSecondaryControlPoint getSecondaryControlPoint() {
        return secondaryControlPoint;
    }
}
