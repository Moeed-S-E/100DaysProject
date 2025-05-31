package app;

import java.util.HashMap;
import java.util.Map;

public class LudoConstants {
    public static final int BOARD_SIZE = 450;
    public static final int GRID_SIZE = 15;
    public static final double SQUARE_SIZE = BOARD_SIZE / (double) GRID_SIZE;

    // COORDINATES_MAP from your constants.js
    public static final Map<Integer, double[]> COORDINATES_MAP = new HashMap<>();
    static {
        COORDINATES_MAP.put(0, new double[]{6, 13});
        COORDINATES_MAP.put(1, new double[]{6, 12});
        COORDINATES_MAP.put(2, new double[]{6, 11});
        COORDINATES_MAP.put(3, new double[]{6, 10});
        COORDINATES_MAP.put(4, new double[]{6, 9});
        COORDINATES_MAP.put(5, new double[]{5, 8});
        COORDINATES_MAP.put(6, new double[]{4, 8});
        COORDINATES_MAP.put(7, new double[]{3, 8});
        COORDINATES_MAP.put(8, new double[]{2, 8});
        COORDINATES_MAP.put(9, new double[]{1, 8});
        COORDINATES_MAP.put(10, new double[]{0, 8});
        COORDINATES_MAP.put(11, new double[]{0, 7});
        COORDINATES_MAP.put(12, new double[]{0, 6});
        COORDINATES_MAP.put(13, new double[]{1, 6});
        COORDINATES_MAP.put(14, new double[]{2, 6});
        COORDINATES_MAP.put(15, new double[]{3, 6});
        COORDINATES_MAP.put(16, new double[]{4, 6});
        COORDINATES_MAP.put(17, new double[]{5, 6});
        COORDINATES_MAP.put(18, new double[]{6, 5});
        COORDINATES_MAP.put(19, new double[]{6, 4});
        COORDINATES_MAP.put(20, new double[]{6, 3});
        COORDINATES_MAP.put(21, new double[]{6, 2});
        COORDINATES_MAP.put(22, new double[]{6, 1});
        COORDINATES_MAP.put(23, new double[]{6, 0});
        COORDINATES_MAP.put(24, new double[]{7, 0});
        COORDINATES_MAP.put(25, new double[]{8, 0});
        COORDINATES_MAP.put(26, new double[]{8, 1});
        COORDINATES_MAP.put(27, new double[]{8, 2});
        COORDINATES_MAP.put(28, new double[]{8, 3});
        COORDINATES_MAP.put(29, new double[]{8, 4});
        COORDINATES_MAP.put(30, new double[]{8, 5});
        COORDINATES_MAP.put(31, new double[]{9, 6});
        COORDINATES_MAP.put(32, new double[]{10, 6});
        COORDINATES_MAP.put(33, new double[]{11, 6});
        COORDINATES_MAP.put(34, new double[]{12, 6});
        COORDINATES_MAP.put(35, new double[]{13, 6});
        COORDINATES_MAP.put(36, new double[]{14, 6});
        COORDINATES_MAP.put(37, new double[]{14, 7});
        COORDINATES_MAP.put(38, new double[]{14, 8});
        COORDINATES_MAP.put(39, new double[]{13, 8});
        COORDINATES_MAP.put(40, new double[]{12, 8});
        COORDINATES_MAP.put(41, new double[]{11, 8});
        COORDINATES_MAP.put(42, new double[]{10, 8});
        COORDINATES_MAP.put(43, new double[]{9, 8});
        COORDINATES_MAP.put(44, new double[]{8, 9});
        COORDINATES_MAP.put(45, new double[]{8, 10});
        COORDINATES_MAP.put(46, new double[]{8, 11});
        COORDINATES_MAP.put(47, new double[]{8, 12});
        COORDINATES_MAP.put(48, new double[]{8, 13});
        COORDINATES_MAP.put(49, new double[]{8, 14});
        COORDINATES_MAP.put(50, new double[]{7, 14});
        COORDINATES_MAP.put(51, new double[]{6, 14});
        // Home entrances and bases
        COORDINATES_MAP.put(100, new double[]{7, 13});
        COORDINATES_MAP.put(101, new double[]{7, 12});
        COORDINATES_MAP.put(102, new double[]{7, 11});
        COORDINATES_MAP.put(103, new double[]{7, 10});
        COORDINATES_MAP.put(104, new double[]{7, 9});
        COORDINATES_MAP.put(105, new double[]{7, 8});
        COORDINATES_MAP.put(200, new double[]{7, 1});
        COORDINATES_MAP.put(201, new double[]{7, 2});
        COORDINATES_MAP.put(202, new double[]{7, 3});
        COORDINATES_MAP.put(203, new double[]{7, 4});
        COORDINATES_MAP.put(204, new double[]{7, 5});
        COORDINATES_MAP.put(205, new double[]{7, 6});
        // Blue base
        COORDINATES_MAP.put(500, new double[]{1.5, 10.58});
        COORDINATES_MAP.put(501, new double[]{3.57, 10.58});
        COORDINATES_MAP.put(502, new double[]{1.5, 12.43});
        COORDINATES_MAP.put(503, new double[]{3.57, 12.43});
        // Green base
        COORDINATES_MAP.put(600, new double[]{10.5, 1.58});
        COORDINATES_MAP.put(601, new double[]{12.54, 1.58});
        COORDINATES_MAP.put(602, new double[]{10.5, 3.45});
        COORDINATES_MAP.put(603, new double[]{12.54, 3.45});
    }

    public static final int[] BLUE_BASE = {500, 501, 502, 503};
    public static final int[] GREEN_BASE = {600, 601, 602, 603};

    public static final int BLUE_START = 0;
    public static final int GREEN_START = 26;

    public static final int[] BLUE_HOME_ENTRANCE = {100, 101, 102, 103, 104, 105};
    public static final int[] GREEN_HOME_ENTRANCE = {200, 201, 202, 203, 204, 205};

    public static final int BLUE_HOME = 105;
    public static final int GREEN_HOME = 205;

    public static final int BLUE_TURNING = 50;
    public static final int GREEN_TURNING = 24;

    public static final int[] SAFE_POSITIONS = {0, 8, 13, 21, 26, 34, 39, 47};
}
