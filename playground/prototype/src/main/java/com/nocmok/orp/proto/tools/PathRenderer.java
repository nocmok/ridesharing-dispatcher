package com.nocmok.orp.proto.tools;

import com.nocmok.orp.proto.graph.Graph;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.List;

public class PathRenderer {

    private Color pathColor = Color.SKYBLUE;
    private double pathWidth = 2;
    private AffineTransformation transformation;

    public PathRenderer(AffineTransformation transformation) {
        this.transformation = transformation;
    }

    public void render(Canvas canvas, Graph graph, List<Integer> path, Color color) {
        if (path.size() < 2) {
            return;
        }

        var prevNodeIt = path.iterator();
        var nextNodeIt = path.iterator();

        nextNodeIt.next();

        var g2 = canvas.getGraphicsContext2D();
        g2.setLineWidth(pathWidth);
        g2.setStroke(color);

        while (nextNodeIt.hasNext()) {
            var prevNodeGps = graph.getGps(prevNodeIt.next());
            var nextNodeGps = graph.getGps(nextNodeIt.next());

            g2.strokeLine(transformation.translateX(prevNodeGps.x), transformation.translateY(prevNodeGps.y),
                    transformation.translateX(nextNodeGps.x), transformation.translateY(nextNodeGps.y));
        }
    }
}
