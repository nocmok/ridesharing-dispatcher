package com.nocmok.orp.proto.tools;

import javafx.scene.paint.Color;

public class ColorGenerator {

    // [255,0,0],[255,255,0],[0,255,0],[0,255,255],[0,0,255],[255,0,255],[255,0,0]
    private int[][] basicColors = {
            {255, 0, 0},
            {255, 255, 0},
            {0, 255, 0},
            {0, 255, 255},
            {0, 0, 255},
            {255, 0, 255},
            {255, 0, 0},
    };

    private double[][] vectors = {
            {0, 255, 0},
            {-255, 0, 0},
            {0, 0, 255},
            {0, -255, 0},
            {255, 0, 0},
            {0, 0, 255},
            {0, 0, 0},
    };

    public ColorGenerator() {
    }

    // value - значение между 0 и 6
    private Color generateColor(double value) {
        value = Double.max(0, Double.min(6, value));
        int floor = (int) Math.floor(value);
        double delta = value - floor;
        int r = basicColors[floor][0] + (int) (vectors[floor][0] * delta);
        int g = basicColors[floor][1] + (int) (vectors[floor][1] * delta);
        int b = basicColors[floor][2] + (int) (vectors[floor][2] * delta);
        return Color.rgb(r, g, b);
    }

    public Color[] generateColors(int n) {
        Color[] colors = new Color[n];
        double value = 0;
        double step = 5.5 / n;
        for (int i = 0; i < n; ++i) {
            colors[i] = generateColor(value);
            value += step;
        }
        return colors;
    }
}
