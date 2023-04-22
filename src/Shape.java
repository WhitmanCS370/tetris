class Shape {
    private ShapeType shapeType;
    private final int[][] coordinates = new int[4][2];

    public Shape() {
        setShape(ShapeType.NoShape);
    }

    public void setShape(ShapeType shapeType) {
        var coords = ShapeCoordinates.getCoordinates(shapeType);
        for (int i = 0; i < 4; i++) {
            System.arraycopy(coords[i], 0, coordinates[i], 0, 2);
        }
        this.shapeType = shapeType;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public int x(int index) {
        return coordinates[index][0];
    }

    public int y(int index) {
        return coordinates[index][1];
    }

    public void setX(int index, int x) {
        coordinates[index][0] = x;
    }

    public void setY(int index, int y) {
        coordinates[index][1] = y;
    }

    public int minX() {
        int m = coordinates[0][0];
        for (int i = 1; i < 4; i++) {
            m = Math.min(m, coordinates[i][0]);
        }
        return m;
    }

    public int minY() {
        int m = coordinates[0][1];
        for (int i = 1; i < 4; i++) {
            m = Math.min(m, coordinates[i][1]);
        }
        return m;
    }

    public Shape rotateLeft() {
        if (shapeType == ShapeType.SquareShape) {
            return this;
        }
        var result = new Shape();
        result.shapeType = shapeType;

        for (int i = 0; i < 4; i++) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }

        return result;
    }

    public Shape rotateRight() {
        if (shapeType == ShapeType.SquareShape) {
            return this;
        }
        var result = new Shape();
        result.shapeType = shapeType;

        for (int i = 0; i < 4; i++) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }

        return result;
    }
}