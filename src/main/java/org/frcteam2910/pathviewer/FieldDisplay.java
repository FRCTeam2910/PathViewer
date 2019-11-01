package org.frcteam2910.pathviewer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Scale;
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
    public Pane drawPane;
    @FXML
    private Group splineGroup;
    @FXML
    private Group anchorGroup;
    @FXML
    private Group controlLineGroup;

    private Field field;

    private ObservableList<PathSection> sections = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        field = new Field(new Image("org/frcteam2910/pathviewer/2019-field.jpg"), 54.0, 27.0, new Vector2(217, 40), 615 - 40, 1372 - 217);
        Image image = field.getImage();
        backgroundImage.setImage(image);
        Scale scale = new Scale();
        scale.xProperty().bind(Bindings.createDoubleBinding(() ->
                        Math.min(root.getWidth() / image.getWidth(), root.getHeight() / image.getHeight()),
                root.widthProperty(), root.heightProperty()));
        scale.yProperty().bind(Bindings.createDoubleBinding(() ->
                        Math.min(root.getWidth() / image.getWidth(), root.getHeight() / image.getHeight()),
                root.widthProperty(), root.heightProperty()));

        group.getTransforms().add(scale);

        drawPane.setPrefHeight(field.getLength());
        drawPane.setPrefWidth(field.getWidth());
        drawPane.setLayoutX(field.getCoord().x);
        drawPane.setLayoutY(field.getCoord().y);
        drawPane.setScaleX(field.getScale());
        drawPane.setScaleY(field.getScale());

        sections.addListener((ListChangeListener<PathSection>) c -> {
            while (c.next()) {
                for (PathSection section : c.getAddedSubList()) {
                    section.onAdded(anchorGroup, controlLineGroup, splineGroup);
                }

                for (PathSection section : c.getRemoved()) {
                    section.onRemove(anchorGroup, controlLineGroup, splineGroup, c.getList());
                }
            }
        });

        drawPane.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                if (anchorGroup.getChildren().isEmpty()) {
                    Anchor anchor = new Anchor(Color.TOMATO, mouseEvent.getX(), mouseEvent.getY(), 0.25);
                    anchorGroup.getChildren().add(anchor);
                } else {
                    Anchor start = (Anchor) anchorGroup.getChildren().get(anchorGroup.getChildren().size() - 1);
                    Anchor end = new Anchor(Color.TOMATO, mouseEvent.getX(), mouseEvent.getY(), 0.25);

                    sections.add(new PathSection(start, end));
                }
            }
        });
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
            setControlX1(start.x + 1 * delta.normal().x);
            setControlY1(start.y + 1 * delta.normal().y);
            setControlX2(end.x - 1 * delta.normal().x);
            setControlY2(end.y - 1 * delta.normal().y);
            setEndX(end.x);
            setEndY(end.y);

            // Setup curve colors
            setStroke(Color.BLUEVIOLET);
            setStrokeWidth(0.1);
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
                    new Anchor(Color.FORESTGREEN, controlX1Property(), controlY1Property(), 0.15),
                    new Anchor(Color.FORESTGREEN, controlX2Property(), controlY2Property(), 0.15)
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

        public void onRemove(Group anchorGroup, Group controlLineGroup, Group splineGroup, ObservableList<? extends PathSection> otherSections) {
            boolean canRemoveStart = true;
            boolean canRemoveEnd = true;
            for (PathSection s : otherSections) {
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

            setStrokeWidth(0.1);
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
            setStrokeWidth(0.1);
            setStrokeType(StrokeType.OUTSIDE);

            setOnMouseDragged(mouseEvent -> {
                System.out.printf("(%.3f, %.3f)%n", mouseEvent.getX(), mouseEvent.getY());
                setCenterX(mouseEvent.getX());
                setCenterY(mouseEvent.getY());
            });
        }

        public Vector2 getCenter() {
            return new Vector2(getCenterX(), getCenterY());
        }
    }
}
