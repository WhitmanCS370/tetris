class ShapeCoordinates {
    private static final int[][][] coordsTable = new int[][][]{
            {{0, 0}, {0, 0}, {0, 0}, {0, 0}}, // NoShape
            {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}}, // ZShape
            {{0, -1}, {0, 0}, {1, 0}, {1, 1}}, // SShape
            {{0, -1}, {0, 0}, {0, 1}, {0, 2}}, // LineShape
            {{-1, 0}, {0, 0}, {1, 0}, {0, 1}}, // TShape
            {{0, 0}, {1, 0}, {0, 1}, {1, 1}}, // SquareShape
            {{-1, -1}, {0, -1}, {0, 0}, {0, 1}}, // LShape
            {{1, -1}, {0, -1}, {0, 0}, {0, 1}} // MirroredLShape
    };

    public static int[][] getCoordinates(ShapeType shapeType) {
        return coordsTable[shapeType.getValue()];
    }
}