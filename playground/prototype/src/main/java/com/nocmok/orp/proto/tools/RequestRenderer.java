package com.nocmok.orp.proto.tools;

import com.nocmok.orp.proto.solver.Request;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RequestRenderer {

    private AffineTransformation transformation;
    private double size = 20;

    public RequestRenderer(AffineTransformation transformation) {
        this.transformation = transformation;
    }

    private void renderDepartureShape(GraphicsContext gc, double x, double y, double size) {
        double h = Math.cos(Math.PI / 6) * size;
        double[] xPoints = {x + 2 * h / 3, x - h / 3, x - h / 3};
        double[] yPoints = {y, y - size / 2, y + size / 2};
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    private void renderArrivalShape(GraphicsContext gc, double x, double y, double size) {
        double h = Math.cos(Math.PI / 6) * size;
        double[] xPoints = {x - 2 * h / 3, x + h / 3, x + h / 3};
        double[] yPoints = {y, y - size / 2, y + size / 2};
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    public void render(Canvas canvas, Request request, Color color) {
        // |> - начало
        // <| - конец

        canvas.getGraphicsContext2D().setFill(color);

        renderDepartureShape(canvas.getGraphicsContext2D(),
                transformation.translateX(request.getDeparturePoint().x),
                transformation.translateY(request.getDeparturePoint().y) - 15,
                size);

        renderArrivalShape(canvas.getGraphicsContext2D(),
                transformation.translateX(request.getArrivalPoint().x),
                transformation.translateY(request.getArrivalPoint().y) - 15,
                size);

        canvas.getGraphicsContext2D().setFill(Color.BLACK);

        canvas.getGraphicsContext2D().fillText(Integer.toString(request.getRequestId()),
                transformation.translateX(request.getDeparturePoint().x),
                transformation.translateY(request.getDeparturePoint().y) - 15);

        canvas.getGraphicsContext2D().fillText(Integer.toString(request.getRequestId()),
                transformation.translateX(request.getArrivalPoint().x),
                transformation.translateY(request.getArrivalPoint().y) - 15);
    }
}
