package org.frcteam2910.pathviewer;

import javafx.beans.property.DoubleProperty;

public class FieldPrimaryControlPoint extends FieldPoint {
    public static final int RADIUS = 3;
    public static final String STYLE_CLASS_NAME = "field-primary-control-point";

    public FieldPrimaryControlPoint(DoubleProperty x, DoubleProperty y) {
        super(x, y, RADIUS);

        getStyleClass().add(STYLE_CLASS_NAME);
        setOnMouseDragged(mouseEvent -> {
            setCenterX(mouseEvent.getX());
            setCenterY(mouseEvent.getY());
        });
    }

    public FieldPrimaryControlPoint(double x, double y) {
        super(x, y, RADIUS);

        getStyleClass().add(STYLE_CLASS_NAME);
        setOnMouseDragged(mouseEvent -> {
            setCenterX(mouseEvent.getX());
            setCenterY(mouseEvent.getY());
        });
    }
}
