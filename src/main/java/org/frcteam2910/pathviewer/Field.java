package org.frcteam2910.pathviewer;

import javafx.scene.image.Image;
import org.frcteam2910.common.math.Vector2;

import java.util.function.Supplier;

public class Field {
    private final Supplier<Image> imageSupplier;
    private Image image;
    private final Vector2 size;
    private final double scale;
    private final Vector2 coord;

    public Field(Supplier<Image> imageSupplier, Vector2 size, Vector2 pixelOffset, Vector2 pixelSize) {
        this.imageSupplier = imageSupplier;
        this.size = size;
        this.coord = pixelOffset.add(pixelSize.subtract(size).scale(0.5));
        this.scale = ((pixelSize.x / size.x) + (pixelSize.y / size.y)) / 2;
    }

    public Field(Image image, Vector2 size, Vector2 pixelOffset, Vector2 pixelSize) {
        this(() -> image, size, pixelOffset, pixelSize);
    }

    public Image getImage() {
        if (image == null) {
            image = imageSupplier.get();
        }
        return image;
    }

    public Vector2 getSize() {
        return size;
    }

    public Vector2 getCoord() {
        return coord;
    }

    public double getScale() {
        return scale;
    }
}
