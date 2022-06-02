package com.nocmok.orp.proto.tools;

import com.nocmok.orp.proto.graph.Graph;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class GraphRenderer {

    private double nodeRadius = 10;
    private double edgeWidth = 2;
    private Color nodeColor = Color.LIGHTGRAY;
    private Color edgeColor = Color.LIGHTGRAY;
    private AffineTransformation transformation;

    public GraphRenderer(AffineTransformation transformation) {
        this.transformation = transformation;
    }

    public void render(Canvas canvas, Graph graph) {
        if (graph.nNodes() < 1) {
            return;
        }

        var g2 = canvas.getGraphicsContext2D();

        g2.setStroke(edgeColor);
        g2.setLineWidth(edgeWidth);

        for (int i = 0; i < graph.nNodes(); ++i) {
            var nodeGps = graph.getGps(i);
            double nodeCanvasX = transformation.translateX(nodeGps.x);
            double nodeCanvasY = transformation.translateY(nodeGps.y);

            for (var nextNode : graph.getLinkedNodes(i)) {
                var nextNodeGps = graph.getGps(nextNode);
                double nextNodeCanvasX = transformation.translateX(nextNodeGps.x);
                double nextNodeCanvasY = transformation.translateY(nextNodeGps.y);

                g2.strokeLine(nodeCanvasX, nodeCanvasY, nextNodeCanvasX, nextNodeCanvasY);
            }
        }

        for (int i = 0; i < graph.nNodes(); ++i) {
            g2.setFill(nodeColor);
            var nodeGps = graph.getGps(i);
            double nodeCanvasX = transformation.translateX(nodeGps.x);
            double nodeCanvasY = transformation.translateY(nodeGps.y);

            g2.fillOval(nodeCanvasX - nodeRadius, nodeCanvasY - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);

            g2.setFill(Color.BLACK);
            g2.setTextAlign(TextAlignment.CENTER);
            g2.setTextBaseline(VPos.CENTER);
            g2.fillText(Integer.toString(i), nodeCanvasX, nodeCanvasY);
        }
    }
}
