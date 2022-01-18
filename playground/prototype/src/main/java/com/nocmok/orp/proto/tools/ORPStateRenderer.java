package com.nocmok.orp.proto.tools;

import com.nocmok.orp.proto.solver.ORPInstance;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class ORPStateRenderer {

    private GraphRenderer graphRenderer;
    private PathRenderer pathRenderer;
    private VehicleRenderer vehicleRenderer;
    private AffineTransformation transformation;
    private ColorGenerator colorPalette = new ColorGenerator();

    public ORPStateRenderer(AffineTransformation transformation) {
        this.transformation = transformation;
        this.graphRenderer = new GraphRenderer(transformation);
        this.pathRenderer = new PathRenderer(transformation);
        this.vehicleRenderer = new VehicleRenderer(transformation);
    }

    public void render(Canvas canvas, ORPInstance state) {
        graphRenderer.render(canvas, state.getGraph());
        Color[] colors = colorPalette.generateColors(state.getVehicleList().size());
        int n = 0;
        for (var vehicle : state.getVehicleList()) {
            pathRenderer.render(canvas, state.getGraph(), vehicle.getSchedule(), colors[n]);
            vehicleRenderer.render(canvas, vehicle, colors[n]);
            ++n;
        }
    }
}
