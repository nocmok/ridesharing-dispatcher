package com.nocmok.orp.proto.tools;

import com.nocmok.orp.proto.solver.ORPInstance;
import com.nocmok.orp.proto.solver.Request;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import lombok.Setter;

public class ORPStateRenderer {

    private GraphRenderer graphRenderer;
    private RouteRenderer routeRenderer;
    private VehicleRenderer vehicleRenderer;
    private RequestRenderer requestRenderer;

    private AffineTransformation transformation;
    private ColorGenerator colorPalette = new ColorGenerator();

    @Setter
    private boolean renderVehicles = true;
    @Setter
    private boolean renderRoutes = true;
    @Setter
    private boolean renderServingRequests = true;
    @Setter
    private boolean renderServedRequests = false;
    @Setter
    private boolean renderDeniedRequests = false;

    public ORPStateRenderer(AffineTransformation transformation) {
        this.transformation = transformation;
        this.graphRenderer = new GraphRenderer(transformation);
        this.routeRenderer = new RouteRenderer(transformation);
        this.vehicleRenderer = new VehicleRenderer(transformation);
        this.requestRenderer = new RequestRenderer(transformation);
    }

    public void render(Canvas canvas, ORPInstance state) {
        Color[] colors = colorPalette.generateColors(state.getVehicleList().size());

        graphRenderer.render(canvas, state.getGraph());

        for (var request : state.getRequestLog()) {
            if (request.getState() == Request.State.SERVING && renderServingRequests) {
                requestRenderer.render(canvas, request, Color.LIGHTGREEN);
            }
        }

        int v = 0;
        for (var vehicle : state.getVehicleList()) {
            if (renderRoutes) {
                routeRenderer.render(canvas, state.getGraph(), vehicle.getSchedule(), colors[v]);
            }
            if (renderVehicles) {
                vehicleRenderer.render(canvas, vehicle, colors[v]);
            }
            ++v;
        }
    }
}
