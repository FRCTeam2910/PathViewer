package org.frcteam2910.pathviewer;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.StrokeLineCap;
import org.frcteam2910.common.math.Vector2;

class FieldPathSection extends CubicCurve {
    public static final String STYLE_CLASS_NAME = "field-path-section";

    private static final double INITIAL_SECONDARY_POINT_DISTANCE = 12;

    public final FieldPrimaryControlPoint startAnchor;
    public final FieldPrimaryControlPoint endAnchor;

    public final FieldSecondaryControlPoint[] controlAnchors;
    public final FieldControlLine[] controlLines;

    public FieldPathSection(FieldPrimaryControlPoint startAnchor, FieldPrimaryControlPoint endAnchor) {
        getStyleClass().add(STYLE_CLASS_NAME);

        Vector2 start = startAnchor.getCenter();
        Vector2 end = endAnchor.getCenter();
        Vector2 delta = end.subtract(start);

        setStartX(start.x);
        setStartY(start.y);
        setControlX1(start.x + INITIAL_SECONDARY_POINT_DISTANCE * delta.normal().x);
        setControlY1(start.y + INITIAL_SECONDARY_POINT_DISTANCE * delta.normal().y);
        setControlX2(end.x - INITIAL_SECONDARY_POINT_DISTANCE * delta.normal().x);
        setControlY2(end.y - INITIAL_SECONDARY_POINT_DISTANCE * delta.normal().y);
        setEndX(end.x);
        setEndY(end.y);

        // Bind end points to anchor positions
        startXProperty().bind(startAnchor.centerXProperty());
        startYProperty().bind(startAnchor.centerYProperty());
        endXProperty().bind(endAnchor.centerXProperty());
        endYProperty().bind(endAnchor.centerYProperty());

        this.startAnchor = startAnchor;
        this.endAnchor = endAnchor;
        controlAnchors = new FieldSecondaryControlPoint[]{
                new FieldSecondaryControlPoint(controlX1Property(), controlY1Property()),
                new FieldSecondaryControlPoint(controlX2Property(), controlY2Property())
        };

        controlLines = new FieldControlLine[]{
                new FieldControlLine(startAnchor, controlAnchors[0]),
                new FieldControlLine(endAnchor, controlAnchors[1])
        };
    }

    public void onAdded(Group anchorGroup, Group controlLineGroup, Group splineGroup) {
        if (!anchorGroup.getChildren().contains(startAnchor)) {
            anchorGroup.getChildren().add(startAnchor);
        }
        anchorGroup.getChildren().addAll(controlAnchors);
        if (!anchorGroup.getChildren().contains(endAnchor)) {
            anchorGroup.getChildren().add(endAnchor);
        }
        controlLineGroup.getChildren().addAll(controlLines);

        splineGroup.getChildren().add(this);
    }

    public void onRemove(Group anchorGroup, Group controlLineGroup, Group splineGroup, ObservableList<? extends FieldPathSection> otherSections) {
        boolean canRemoveStart = true;
        boolean canRemoveEnd = true;
        for (FieldPathSection s : otherSections) {
            if (s.endAnchor == startAnchor) {
                canRemoveStart = false;
            }
            if (s.startAnchor == endAnchor) {
                canRemoveEnd = false;
            }
        }
        if (canRemoveStart) {
            anchorGroup.getChildren().remove(startAnchor);
        }
        anchorGroup.getChildren().removeAll(controlAnchors);
        if (canRemoveEnd) {
            anchorGroup.getChildren().remove(endAnchor);
        }
        controlLineGroup.getChildren().removeAll(controlLines);

        splineGroup.getChildren().remove(this);
    }
}
