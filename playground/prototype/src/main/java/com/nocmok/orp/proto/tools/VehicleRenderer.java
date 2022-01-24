package com.nocmok.orp.proto.tools;

import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.solver.Vehicle;
import com.nocmok.orp.proto.solver.common.SimpleVehicle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class VehicleRenderer {

    private AffineTransformation transformation;
    private Color vehicleColor = Color.ROYALBLUE;
    private double vehicleSize = 20;

    public VehicleRenderer(AffineTransformation transformation) {
        this.transformation = transformation;
    }

    private void drawRectangle(GraphicsContext gc, double x, double y, double size) {
//        double h = size * Math.cos(Math.PI / 6);
        double[] xPoints = new double[] {
                x - size / 2d,
                x + size / 2d,
                x + size / 2d,
                x - size / 2d,
        };
        double[] yPoints = new double[] {
                y - size / 2d,
                y - size / 2d,
                y + size / 2d,
                y + size / 2d,
        };
        gc.fillPolygon(xPoints, yPoints, 4);
    }

    public void render(Canvas canvas, Vehicle vehicle, Color color) {
        GPS gps = vehicle.getGps();
        var gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        drawRectangle(gc, transformation.translateX(gps.x), transformation.translateY(gps.y), vehicleSize);
    }
}
