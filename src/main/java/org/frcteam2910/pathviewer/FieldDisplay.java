package org.frcteam2910.pathviewer;

import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.math.spline.BezierSpline;
import org.frcteam2910.common.math.spline.Spline;

public class FieldDisplay {
    @FXML
    private Pane root;
    @FXML
    private Group group;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private Group splineGroup;
    @FXML
    private Group anchorGroup;
    @FXML
    private Group controlLineGroup;

    private ObservableList<PathSection> sections = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        sections.addListener((ListChangeListener<PathSection>) c -> {
            while (c.next()) {
                for (PathSection section : c.getAddedSubList()) {
                    if (!anchorGroup.getChildren().contains(section.startAnchor)) {
                        anchorGroup.getChildren().add(section.startAnchor);
                    }
                    anchorGroup.getChildren().addAll(section.controlAnchors);
                    if (!anchorGroup.getChildren().contains(section.endAnchor)) {
                        anchorGroup.getChildren().add(section.endAnchor);
                    }
                    controlLineGroup.getChildren().addAll(section.controlLines);

                    splineGroup.getChildren().add(section);
                }

                for (PathSection section : c.getRemoved()) {
                    boolean canRemoveStart = true;
                    boolean canRemoveEnd = true;
                    for (PathSection s : c.getList()) {
                        if (s.endAnchor == section.startAnchor) {
                            canRemoveStart = false;
                        }
                        if (s.startAnchor == section.endAnchor) {
                            canRemoveEnd = false;
                        }
                    }
                    if (canRemoveStart) {
                        anchorGroup.getChildren().remove(section.startAnchor);
                    }
                    anchorGroup.getChildren().removeAll(section.controlAnchors);
                    if (canRemoveEnd) {
                        anchorGroup.getChildren().remove(section.endAnchor);
                    }
                    controlLineGroup.getChildren().removeAll(section.controlLines);

                    splineGroup.getChildren().remove(section);
                }
            }
        });

        Anchor start = new Anchor(Color.TOMATO, 50, 50, 5);
        Anchor mid = new Anchor(Color.TOMATO, 200, 200, 5);

        sections.add(new PathSection(start, mid));

        Anchor end = new Anchor(Color.TOMATO, 20, 421, 5);

        sections.add(new PathSection(mid, end));

        sections.remove(0);
    }

    private static class PathSection extends CubicCurve {
        public final Anchor startAnchor;
        public final Anchor endAnchor;

        public final Anchor[] controlAnchors;
        public final ControlLine[] controlLines;

        public PathSection(Anchor startAnchor, Anchor endAnchor) {
            Vector2 start = startAnchor.getCenter();
            Vector2 end = endAnchor.getCenter();
            Vector2 delta = end.subtract(start);

            setStartX(start.x);
            setStartY(start.y);
            setControlX1(start.x + 20 * delta.normal().x);
            setControlY1(start.y + 20 * delta.normal().y);
            setControlX2(end.x - 20 * delta.normal().x);
            setControlY2(end.y - 20 * delta.normal().y);
            setEndX(end.x);
            setEndY(end.y);

            // Setup curve colors
            setStroke(Color.BLUEVIOLET);
            setStrokeWidth(4);
            setStrokeLineCap(StrokeLineCap.ROUND);
            setFill(Color.TRANSPARENT);

            // Bind end points to anchor positions
            startXProperty().bind(startAnchor.centerXProperty());
            startYProperty().bind(startAnchor.centerYProperty());
            endXProperty().bind(endAnchor.centerXProperty());
            endYProperty().bind(endAnchor.centerYProperty());

            this.startAnchor = startAnchor;
            this.endAnchor = endAnchor;
            controlAnchors = new Anchor[]{
                    new Anchor(Color.FORESTGREEN, controlX1Property(), controlY1Property(), 3),
                    new Anchor(Color.FORESTGREEN, controlX2Property(), controlY2Property(), 3)
            };

            controlLines = new ControlLine[controlAnchors.length + 1];
            for (int i = 0; i < controlLines.length; i++) {
                if (i == 0) {
                    controlLines[i] = new ControlLine(startAnchor, controlAnchors[0]);
                } else if (i == controlLines.length - 1) {
                    controlLines[i] = new ControlLine(endAnchor, controlAnchors[controlAnchors.length - 1]);
                } else {
                    controlLines[i] = new ControlLine(controlAnchors[i - 1], controlAnchors[i]);
                }
            }
        }

        public Spline toSpline() {
            Vector2[] controlPoints = new Vector2[controlAnchors.length + 2];
            controlPoints[0] = startAnchor.getCenter();
            for (int i = 0; i < controlAnchors.length; i++) {
                controlPoints[i + 1] = controlAnchors[i].getCenter();
            }
            controlPoints[controlPoints.length - 1] = endAnchor.getCenter();

            return new BezierSpline(controlPoints);
        }
    }

    private static class ControlLine extends Line {
        public ControlLine(Anchor start, Anchor end) {
            startXProperty().bind(start.centerXProperty());
            startYProperty().bind(start.centerYProperty());
            endXProperty().bind(end.centerXProperty());
            endYProperty().bind(end.centerYProperty());

            setStrokeWidth(2);
            setStroke(Color.FORESTGREEN.deriveColor(0, 1, 1, 0.5));
        }
    }

    private static class Anchor extends Circle {
        public Anchor(Color color, DoubleProperty x, DoubleProperty y, double radius) {
            this(color, x.get(), y.get(), radius);
            x.bind(centerXProperty());
            y.bind(centerYProperty());
        }

        public Anchor(Color color, double x, double y, double radius) {
            super(x, y, radius);
            setFill(color.deriveColor(1, 1, 1, 0.5));
            setStroke(color);
            setStrokeWidth(2);
            setStrokeType(StrokeType.OUTSIDE);

            setOnMouseDragged(mouseEvent -> {
                setCenterX(mouseEvent.getX());
                setCenterY(mouseEvent.getY());
            });
        }

        public Vector2 getCenter() {
            return new Vector2(getCenterX(), getCenterY());
        }
    }
}
