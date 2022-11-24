package org.frcteam2910.pathviewer;

import javafx.beans.property.DoubleProperty;
import javafx.scene.input.MouseEvent;
import org.frcteam2910.common.math.Vector2;

public class FieldSecondaryControlPoint extends FieldPoint {
    public static final int RADIUS = 3;
    public static final String STYLE_CLASS_NAME = "field-secondary-control-point";
    public final FieldPrimaryControlPoint connectedPrimaryControlPoint;
    public FieldSecondaryControlPoint connectedSecondaryControlPoint = null;

    public FieldSecondaryControlPoint(DoubleProperty x, DoubleProperty y, FieldPrimaryControlPoint connectedPrimaryControlPoint) {
        super(x, y, RADIUS);

        this.connectedPrimaryControlPoint = connectedPrimaryControlPoint;

        getStyleClass().add(STYLE_CLASS_NAME);
        setOnMouseDragged(this::dragHandler);
    }

    public FieldSecondaryControlPoint(double x, double y, FieldPrimaryControlPoint connectedPrimaryControlPoint) {
        super(x, y, RADIUS);

        this.connectedPrimaryControlPoint = connectedPrimaryControlPoint;

        getStyleClass().add(STYLE_CLASS_NAME);
        setOnMouseDragged(this::dragHandler);
    }

    private void dragHandler(MouseEvent mouseEvent) {
        setCenterX(mouseEvent.getX());
        setCenterY(mouseEvent.getY());
        if(connectedSecondaryControlPoint != null) {
            Vector2 distanceFromThisToCenter = getCenter().subtract(connectedPrimaryControlPoint.getCenter());
            Vector2 distanceFromConnectedToCenter = connectedSecondaryControlPoint.getCenter().subtract(connectedPrimaryControlPoint.getCenter());

            distanceFromConnectedToCenter = distanceFromThisToCenter.normal().scale(-distanceFromConnectedToCenter.length);

            connectedSecondaryControlPoint.setCenter(connectedPrimaryControlPoint.getCenter().add(distanceFromConnectedToCenter));
        }
    }

    public void setConnectedSecondaryControlPoint(FieldSecondaryControlPoint connectedSecondaryControlPoint){
        this.connectedSecondaryControlPoint = connectedSecondaryControlPoint;
    }
}
