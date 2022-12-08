package org.frcteam2910.pathviewer;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.frcteam2910.common.math.Vector2;

import java.util.ArrayList;

public class FieldPrimaryControlPoint extends FieldPoint {
    public static final int RADIUS = 3;
    public static final String STYLE_CLASS_NAME = "field-primary-control-point";
    public final ArrayList<FieldSecondaryControlPoint> connectedSecondaryControlPoints = new ArrayList<>();

    public FieldRotationControlPoint connectedRotationControlPoint;
    private FieldControlLine rotationLine;

    private boolean rotate = true;

    Rectangle rectangle;

    public FieldPrimaryControlPoint(DoubleProperty x, DoubleProperty y) {
        super(x, y, RADIUS);

        getStyleClass().add(STYLE_CLASS_NAME);
        setOnMouseDragged(this::dragHandler);
    }

    public FieldPrimaryControlPoint(double x, double y, Group outlineGroup, Group rotationGroup, Group controlLinesGroup) {
        super(x, y, RADIUS);

        this.rectangle = addRectangle(x, y);
        outlineGroup.getChildren().add(rectangle);

        connectedRotationControlPoint = new FieldRotationControlPoint(x, y, this);
        rotationGroup.getChildren().add(connectedRotationControlPoint);

        rotationLine = new FieldControlLine(connectedRotationControlPoint, this);
        controlLinesGroup.getChildren().add(rotationLine);

        getStyleClass().add(STYLE_CLASS_NAME);
        setOnMouseDragged(this::dragHandler);
    }

    public void dragHandler(MouseEvent mouseEvent) {
        Vector2 firstConnectedPointDistance = null;
        Vector2 secondConnectedPointDistance = null;
        Vector2 rotationOffset = null;
        
        if(connectedSecondaryControlPoints.size() > 0) {
            firstConnectedPointDistance = connectedSecondaryControlPoints.get(0).getCenter().subtract(getCenter());
        }
        if(connectedSecondaryControlPoints.size() > 1) {
            secondConnectedPointDistance = connectedSecondaryControlPoints.get(1).getCenter().subtract(getCenter());
        }

        if (connectedRotationControlPoint != null)
            rotationOffset = connectedRotationControlPoint.getCenter().subtract(getCenter());

        setCenterX(mouseEvent.getX());
        setCenterY(mouseEvent.getY());

        rectangle.relocate(mouseEvent.getX() - rectangle.getWidth() / 2, mouseEvent.getY() - rectangle.getWidth() / 2);

        if (connectedRotationControlPoint != null)
            connectedRotationControlPoint.setCenter(getCenter().add(rotationOffset));

        if(connectedSecondaryControlPoints.size() > 0) {
            connectedSecondaryControlPoints.get(0).setCenter(getCenter().add(firstConnectedPointDistance));
        }
        if(connectedSecondaryControlPoints.size() > 1) {
            connectedSecondaryControlPoints.get(1).setCenter(getCenter().add(secondConnectedPointDistance));
        }
    }

    public void setConnectedSecondaryControlPoint(FieldSecondaryControlPoint connectedSecondaryPoint) {
        connectedSecondaryControlPoints.add(connectedSecondaryPoint);
    }

    public void setConnectedRotationControlPoint(FieldRotationControlPoint connectedRotationControlPoint) {
        this.connectedRotationControlPoint = connectedRotationControlPoint;
    }

    public void setRotationLine(FieldControlLine rotationLine) {
        this.rotationLine = rotationLine;
    }


    private Rectangle addRectangle(double x, double y) {
        double size = 50;
        Rectangle rectangle = new Rectangle(size, size);
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.relocate(x - size / 2, y - size / 2);
        return rectangle;
    }

    public boolean getRotatable() {
        return rotate;
    }

    public void toggleRotatable() {
        rotate = !rotate;

        rectangle.setVisible(rotate);
            connectedRotationControlPoint.setVisible(rotate);
            rotationLine.setVisible(rotate);
    }
}
