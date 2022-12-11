package org.frcteam2910.pathviewer;

import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import org.frcteam2910.common.math.Vector2;

public class FieldRotationControlPoint extends FieldPoint{
    public static final int RADIUS = 3;
    public static final String STYLE_CLASS_NAME = "field-rotation-control-point";
    public static final int DISTANCE = 40;
    FieldPrimaryControlPoint primaryControlPoint;

    Rectangle  rectangle;

    public FieldRotationControlPoint(double x, double y, FieldPrimaryControlPoint primaryControlPoint) {
        super(x + DISTANCE, y, RADIUS);

        this.primaryControlPoint = primaryControlPoint;

        rectangle = primaryControlPoint.rectangle;

        getStyleClass().add(STYLE_CLASS_NAME);

        setOnMouseDragged(this::dragHandler);
    }

    private void dragHandler(MouseEvent mouseEvent) {
        double angle = getAngle(new Vector2(mouseEvent.getX(), mouseEvent.getY()), primaryControlPoint.getCenter());
        double y = Math.sin(angle) * DISTANCE;
        double x = Math.cos(angle) * DISTANCE;

        this.setCenter(primaryControlPoint.getCenterX() + x, primaryControlPoint.getCenterY() + y);

        primaryControlPoint.rectangle.setRotate(Math.toDegrees(angle));
    }

    private double getAngle(Vector2 primaryCoord, Vector2 secondaryCoord) {
        return Math.atan2(primaryCoord.y - secondaryCoord.y, primaryCoord.x - secondaryCoord.x);
    }

    public void setRotatePoint(double angle) {
        if(angle > 180) {
            angle = angle - 360;
        }
        double y = Math.sin(Math.toRadians(angle)) * DISTANCE;
        double x = Math.cos(Math.toRadians(angle)) * DISTANCE;

        this.setCenter(primaryControlPoint.getCenterX() + x, primaryControlPoint.getCenterY() + y);


        primaryControlPoint.rectangle.setRotate(angle);
    }
}
