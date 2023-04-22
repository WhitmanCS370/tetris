import java.awt.*;

enum ShapeType {
    NoShape(new Color(0, 0, 0), 0),

    ZShape(new Color(204, 102, 102), 1),
    SShape(new Color(102, 204, 102), 2),
    LineShape(new Color(102, 102, 204), 3),
    TShape(new Color(204, 204, 102), 4),
    SquareShape(new Color(204, 102, 204), 5),
    LShape(new Color(102, 204, 204), 6),
    MirroredLShape(new Color(218, 170, 0), 7);

    private final Color color;
    private final int value;

    ShapeType(Color color, int value) {
        this.color = color;
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }
}
