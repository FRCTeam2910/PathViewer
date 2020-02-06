package org.frcteam2910.pathviewer;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import org.frcteam2910.common.control.Path;
import org.frcteam2910.common.control.SplinePathBuilder;
import org.frcteam2910.common.control.SplinePathSegment;
import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.math.spline.CubicBezierSpline;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;

public class FieldDisplay extends Pane {
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

    private ObservableList<FieldPathSection> sections = FXCollections.observableArrayList();

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
        field = new Field(new Image("org/frcteam2910/pathviewer/2020-field.png"), new Vector2(54.0 * 12.0, 27.0 * 12.0), new Vector2(76, 64), new Vector2(2696 - 76, 1688 - 64));
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
        drawPane.setPrefHeight(field.getSize().y);
        drawPane.setLayoutX(field.getCoord().x);
        drawPane.setLayoutY(field.getCoord().y);
        drawPane.setScaleX(field.getScale());
        drawPane.setScaleY(field.getScale());

        sections.addListener((ListChangeListener<FieldPathSection>) c -> {
            while (c.next()) {
                for (FieldPathSection section : c.getAddedSubList()) {
                    section.onAdded(anchorGroup, controlLineGroup, splineGroup);
                }

                for (FieldPathSection section : c.getRemoved()) {
                    section.onRemove(anchorGroup, controlLineGroup, splineGroup, c.getList());
                }
            }
        });

        drawPane.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                if (anchorGroup.getChildren().isEmpty()) {
                    FieldPrimaryControlPoint anchor = new FieldPrimaryControlPoint(
                            mouseEvent.getX(),
                            mouseEvent.getY()
                    );
                    anchorGroup.getChildren().add(anchor);
                } else {
                    FieldPrimaryControlPoint start = (FieldPrimaryControlPoint) anchorGroup.getChildren().get(anchorGroup.getChildren().size() - 1);
                    FieldPrimaryControlPoint end = new FieldPrimaryControlPoint(
                            mouseEvent.getX(),
                            mouseEvent.getY()
                    );

                    sections.add(new FieldPathSection(start, end));
                }
            } else if (mouseEvent.getButton() == MouseButton.MIDDLE) {
                removeLastPoint();
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
        int groupLength = anchorGroup.getChildren().size();

        int sectionLength = sections.size();
        anchorGroup.getChildren().remove(groupLength - 1, groupLength);
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
                                controlPoints[0].y
                        );
                    } else {
                        startAnchor = sections.get(sections.size() - 1).endAnchor;
                    }

                    FieldPrimaryControlPoint endAnchor = new FieldPrimaryControlPoint(
                            controlPoints[3].x,
                            controlPoints[3].y
                    );

                    FieldPathSection section = new FieldPathSection(startAnchor, endAnchor);
                    section.controlAnchors[0].setCenter(controlPoints[1]);
                    section.controlAnchors[1].setCenter(controlPoints[2]);

                    sections.add(section);
                });
    }
}
