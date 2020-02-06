package org.frcteam2910.pathviewer;

import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Circle;
import org.frcteam2910.common.math.Vector2;

public class FieldPoint extends Circle {
    public static final String STYLE_CLASS_NAME = "field-point";

    public FieldPoint(double x, double y, double radius) {
        super(x, y, radius);
        getStyleClass().add(STYLE_CLASS_NAME);
    }

    public FieldPoint(DoubleProperty x, DoubleProperty y, double radius) {
        this(x.get(), y.get(), radius);
        x.bind(centerXProperty());
        y.bind(centerYProperty());
    }

    public FieldPoint(Vector2 position, double radius) {
        this(position.x, position.y, radius);
    }

    public Vector2 getCenter() {
        return new Vector2(getCenterX(), getCenterY());
    }

    public void setCenter(Vector2 center) {
        setCenterX(center.x);
        setCenterY(center.y);
    }

    public void setCenter(double x, double y) {
        setCenterX(x);
        setCenterY(y);
    }
}
