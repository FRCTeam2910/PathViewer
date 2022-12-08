package org.frcteam2910.pathviewer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import org.frcteam2910.common.control.Path;
import org.frcteam2910.common.control.SplinePathBuilder;
import org.frcteam2910.common.control.SplinePathSegment;
import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.math.spline.CubicBezierSpline;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FieldDisplay extends Pane {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

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
    @FXML
    private Group rotationGroup;
    @FXML
    private Group outlineGroup;

    private Field field;

    private ObservableList<FieldPathSection> sections = FXCollections.observableArrayList();

    private SimpleObjectProperty<FieldPoint> selectedPoint = new SimpleObjectProperty<>(null);

    private ArrayList<FieldPrimaryControlPoint> lastPoint = new ArrayList<>();

    public FieldDisplay() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FieldDisplay.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        final int xOffset = 312;
        final int yOffset = 160;
        //TODO: move origin from top left without moving pane
        field = new Field(new Image("org/frcteam2910/pathviewer/2020-field.png"), new Vector2(629.25, 323.25), new Vector2(2784 / 629.25 * 0 * (5 + xOffset), 1452 / 323.25 * 0 * (42 + yOffset)), new Vector2(2784, 1452));
        Image image = field.getImage();
        backgroundImage.setImage(image);
        Scale scale = new Scale();
        scale.xProperty().bind(Bindings.createDoubleBinding(() ->
                        Math.min(getWidth() / image.getWidth(), getHeight() / image.getHeight()),
                widthProperty(), heightProperty()));
        scale.yProperty().bind(Bindings.createDoubleBinding(() ->
                        Math.min(getWidth() / image.getWidth(), getHeight() / image.getHeight()),
                widthProperty(), heightProperty()));

        group.getTransforms().add(scale);

        drawPane.setPrefWidth(field.getSize().x);
        drawPane.setPrefHeight(field.getSize().y/* + 108*/);
        drawPane.setLayoutX(field.getCoord().x);
        drawPane.setLayoutY(field.getCoord().y);
        drawPane.setScaleX(field.getScale());
        drawPane.setScaleY(field.getScale());
        drawPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM)));

        sections.addListener((ListChangeListener<FieldPathSection>) c -> {
            while (c.next()) {
                for (FieldPathSection section : c.getAddedSubList()) {
                    section.onAdded(anchorGroup, controlLineGroup, splineGroup);
                }

                for (FieldPathSection section : c.getRemoved()) {
                    section.onRemove(anchorGroup, controlLineGroup, splineGroup, outlineGroup, rotationGroup, c.getList());
                }
            }
        });

        drawPane.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                if (anchorGroup.getChildren().isEmpty()) {
                    FieldPrimaryControlPoint anchor = new FieldPrimaryControlPoint(
                            mouseEvent.getX(),
                            mouseEvent.getY(),
                            outlineGroup,
                            rotationGroup,
                            controlLineGroup
                    );
                    lastPoint.add(anchor);
                    anchorGroup.getChildren().add(anchor);
                } else {
                    FieldPrimaryControlPoint start = (FieldPrimaryControlPoint) anchorGroup.getChildren().get(anchorGroup.getChildren().size() - 1);
                    FieldPrimaryControlPoint end = new FieldPrimaryControlPoint(
                            mouseEvent.getX(),
                            mouseEvent.getY(),
                            outlineGroup,
                            rotationGroup,
                            controlLineGroup
                    );
                    lastPoint.add(end);
                    FieldPathSection path = new FieldPathSection(start, end);

                    if (anchorGroup.getChildren().size() > 1) {
                        FieldSecondaryControlPoint secondaryConnectedPoint = (FieldSecondaryControlPoint)anchorGroup.getChildren().get(anchorGroup.getChildren().size() - 2);
                        path.getFirstSecondaryControlPoint().setConnectedSecondaryControlPoint(secondaryConnectedPoint);
                        secondaryConnectedPoint.setConnectedSecondaryControlPoint(path.getFirstSecondaryControlPoint());
                    }

                    sections.add(path);
                }
            } else if (mouseEvent.getButton() == MouseButton.MIDDLE) {
                removeLastPoint();
            }
        });
        drawPane.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                if (mouseEvent.getTarget() instanceof FieldPoint) {
                    // Select point
                    if (mouseEvent.getClickCount() == 2 && anchorGroup.getChildren().contains(mouseEvent.getTarget()) && mouseEvent.getTarget() != anchorGroup.getChildren().get(0)) {
                        try {
                            FieldPrimaryControlPoint point = (FieldPrimaryControlPoint) getSelectedPoint();
                            assert point != null;
                            point.toggleRotatable();
                        } catch (Exception ignore) {}
                    } else {
                        setSelectedPoint((FieldPoint) mouseEvent.getTarget());
                    }
                } else {
                    // Deselect point
                    setSelectedPoint(null);
                }
            }
        });
        selectedPointProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false);
            }
            if (newValue != null) {
                newValue.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true);
            }
        });
    }

    /**
     * Clears the current path from the display.
     */
    public void clearPath() {
        sections.clear();
    }

    public void removeLastPoint() {
        int sectionLength = sections.size();

        sections.remove(sectionLength - 1, sectionLength);
    }

    /**
     * Gets the path that is currently on the display.
     *
     * @return The path or null if no path is on the display.
     */
    @CheckForNull
    public Path getPath() {
        if (sections.isEmpty()) {
            return null;
        }

        SplinePathBuilder builder = new SplinePathBuilder(
                sections.get(0).startAnchor.getCenter(),
                Rotation2.ZERO,
                Rotation2.ZERO
        );
        sections.forEach(section -> builder.bezier(
                section.controlAnchors[0].getCenter(),
                section.controlAnchors[1].getCenter(),
                section.endAnchor.getCenter()
        ));

        return builder.build();
    }

    /**
     * Sets the path that is currently on the display.
     *
     * @param path The path to display.
     */
    public void setPath(@Nonnull Path path) {
        clearPath();

        Arrays.stream(path.getSegments())
                .map(s -> (SplinePathSegment) s)
                .forEachOrdered(s -> {
                    CubicBezierSpline spline = CubicBezierSpline.convert(s.getSpline());
                    Vector2[] controlPoints = spline.getControlPoints();

                    FieldPrimaryControlPoint startAnchor;
                    if (sections.isEmpty()) {
                        startAnchor = new FieldPrimaryControlPoint(
                                controlPoints[0].x,
                                controlPoints[0].y,
                                outlineGroup,
                                rotationGroup,
                                controlLineGroup
                        );
                    } else {
                        startAnchor = sections.get(sections.size() - 1).endAnchor;
                    }

                    FieldPrimaryControlPoint endAnchor = new FieldPrimaryControlPoint(
                            controlPoints[3].x,
                            controlPoints[3].y,
                            outlineGroup,
                            rotationGroup,
                            controlLineGroup
                    );

                    FieldPathSection section = new FieldPathSection(startAnchor, endAnchor);
                    section.controlAnchors[0].setCenter(controlPoints[1]);
                    section.controlAnchors[1].setCenter(controlPoints[2]);

                    sections.add(section);
                });
    }

    public ObjectProperty<FieldPoint> selectedPointProperty() {
        return selectedPoint;
    }

    @CheckForNull
    public FieldPoint getSelectedPoint() {
        return selectedPointProperty().get();
    }

    public void setSelectedPoint(@Nullable FieldPoint point) {
        selectedPointProperty().set(point);
    }
}
