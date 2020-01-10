package sample;

public class Settings {
    public final static double SCENE_WIDTH = 1152;
    public final static double SCENE_HEIGHT = 768;
    public final static double TILE_WIDTH = 64;
    public final static double TILE_HEIGHT = 64;
    public final static double ROW_CELL_COUNT=18;
    public final static double COLUMN_CELL_COUNT=12;
    public static final int BORDER_CONSTANT=0;
    public static final int TILES_CONSTANT=1;

    public static int playerSpeed = 2;
    public static int attackSpeed = 1000;
    public static int attackInterval = 1000;

    public static boolean isLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}