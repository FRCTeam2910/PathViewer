package org.frcteam2910.pathviewer;

import javafx.scene.image.Image;
import org.frcteam2910.common.math.Vector2;

import java.util.function.Supplier;

public class Field {
    private final Supplier<Image> imageSupplier;
    private Image image;
    private final double width;
    private final double length;
    private final double scale;
    private final Vector2 coord;

    public Field(Supplier<Image> imageSupplier, double width, double length, Vector2 pixelOffset, double pixelWidth, double pixelLength) {
        this.imageSupplier = imageSupplier;
        this.width = width;
        this.length = length;
        this.coord = pixelOffset.add(pixelWidth / 2 - width / 2, pixelLength / 2 - length / 2);
        this.scale = ((pixelWidth / width) + (pixelLength / length)) / 2;
    }

    public Field(Image image, double width, double length, Vector2 pixelOffset, double pixelWidth, double pixelLength) {
        this(() -> image, width, length, pixelOffset, pixelWidth, pixelLength);
    }

    public Image getImage() {
        if (image == null) {
            image = imageSupplier.get();
        }
        return image;
    }

    public double getWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }

    public Vector2 getCoord() {
        return coord;
    }

    public double getScale() {
        return scale;
    }
}
