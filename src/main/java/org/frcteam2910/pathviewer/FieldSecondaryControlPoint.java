package org.frcteam2910.pathviewer;

import javafx.beans.property.DoubleProperty;

public class FieldSecondaryControlPoint extends FieldPoint {
    public static final int RADIUS = 3;
    public static final String STYLE_CLASS_NAME = "field-secondary-control-point";

    public FieldSecondaryControlPoint(DoubleProperty x, DoubleProperty y) {
        super(x, y, RADIUS);

        getStyleClass().add(STYLE_CLASS_NAME);
        setOnMouseDragged(mouseEvent -> {
            setCenterX(mouseEvent.getX());
            setCenterY(mouseEvent.getY());
        });
    }

    public FieldSecondaryControlPoint(double x, double y) {
        super(x, y, RADIUS);

        getStyleClass().add(STYLE_CLASS_NAME);
        setOnMouseDragged(mouseEvent -> {
            setCenterX(mouseEvent.getX());
            setCenterY(mouseEvent.getY());
        });
    }
}
