package net.minecraft.util;

/**
 * Lithium / Sodium inspired fast math routines for performance critical paths.
 */
public class FastMath {

    private static final float[] SIN_TABLE_FAST = new float[4096];
    private static final float RAD_TO_INDEX = 4096.0F / (float) (Math.PI * 2.0);

    static {
        for (int i = 0; i < 4096; ++i) {
            SIN_TABLE_FAST[i] = (float) Math.sin((double) i * Math.PI * 2.0 / 4096.0);
        }
    }

    public static float sin(float value) {
        return SIN_TABLE_FAST[(int) (value * RAD_TO_INDEX) & 4095];
    }

    public static float cos(float value) {
        return SIN_TABLE_FAST[(int) (value * RAD_TO_INDEX + 1024.0F) & 4095];
    }

    public static int floor(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }

    public static int floor(float value) {
        int i = (int) value;
        return value < (float) i ? i - 1 : i;
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static int clamp(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }
}
