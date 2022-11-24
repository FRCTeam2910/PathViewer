package org.frcteam2910.pathviewer;

import javafx.beans.property.DoubleProperty;
import javafx.scene.input.MouseEvent;
import org.frcteam2910.common.math.Vector2;

import java.util.ArrayList;

public class FieldPrimaryControlPoint extends FieldPoint {
    public static final int RADIUS = 3;
    public static final String STYLE_CLASS_NAME = "field-primary-control-point";
    public final ArrayList<FieldSecondaryControlPoint> connectedSecondaryControlPoints = new ArrayList<>();

    public FieldPrimaryControlPoint(DoubleProperty x, DoubleProperty y) {
        super(x, y, RADIUS);

        getStyleClass().add(STYLE_CLASS_NAME);
        setOnMouseDragged(this::dragHandler);
    }

    public FieldPrimaryControlPoint(double x, double y) {
        super(x, y, RADIUS);

        getStyleClass().add(STYLE_CLASS_NAME);
        setOnMouseDragged(this::dragHandler);
    }

    public void dragHandler(MouseEvent mouseEvent) {
        Vector2 firstConnectedPointDistance = null;
        Vector2 secondConnectedPointDistance = null;
        
        if(connectedSecondaryControlPoints.size() > 0) {
            firstConnectedPointDistance = connectedSecondaryControlPoints.get(0).getCenter().subtract(getCenter());
        }
        if(connectedSecondaryControlPoints.size() > 1) {
            secondConnectedPointDistance = connectedSecondaryControlPoints.get(1).getCenter().subtract(getCenter());
        }
        setCenterX(mouseEvent.getX());
        setCenterY(mouseEvent.getY());
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
}
