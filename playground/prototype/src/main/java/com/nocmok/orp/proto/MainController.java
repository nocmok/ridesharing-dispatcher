package com.nocmok.orp.proto;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.simulator.Simulator;
import com.nocmok.orp.proto.solver.ORPInstance;
import com.nocmok.orp.proto.solver.Request;
import com.nocmok.orp.proto.solver.TaxiSolver;
import com.nocmok.orp.proto.solver.Vehicle;
import com.nocmok.orp.proto.tools.AffineTransformation;
import com.nocmok.orp.proto.tools.DimacsGraphConverter;
import com.nocmok.orp.proto.tools.DimacsParser;
import com.nocmok.orp.proto.tools.ORPStateRenderer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private GridPane root;
    @FXML
    private Canvas canvas;
    @FXML
    private Button stepButton;

    private Simulator simulator;
    private ORPStateRenderer renderer;

    private AffineTransformation getTransformationForRenderer(Graph graph, double x, double y, double xSize,
                                                              double ySize) {
        double x0 = Double.POSITIVE_INFINITY;
        double x1 = Double.NEGATIVE_INFINITY;
        double y0 = Double.POSITIVE_INFINITY;
        double y1 = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < graph.nNodes(); ++i) {
            var gps = graph.getGps(i);
            x0 = Double.min(x0, gps.x);
            x1 = Double.max(x1, gps.x);
            y0 = Double.min(y0, gps.y);
            y1 = Double.max(y1, gps.y);
        }

        return new AffineTransformation(xSize / (x1 - x0),
                ySize / (y1 - y0),
                x - x0 * xSize / (x1 - x0),
                y - y0 * ySize / (y1 - y0));
    }

    private Graph loadGraph() {
        try {
            var dimacsParser = new DimacsParser();
            var gr = dimacsParser.readGr(Main.class.getClassLoader().getResourceAsStream("USA-road-d.NY.reduced.gr"));
            var co = dimacsParser.readCo(Main.class.getClassLoader().getResourceAsStream("USA-road-d.NY.reduced.co"));
            return new DimacsGraphConverter().convert(gr, co);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearCanvas(Canvas canvas) {
        canvas.getGraphicsContext2D().setFill(Color.WHITE);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void step() {
        simulator.ticTac(4);
        clearCanvas(canvas);
        renderer.render(canvas, simulator.getState());
    }

    @Override public void initialize(URL location, ResourceBundle resources) {
        var primaryScreen = javafx.stage.Screen.getPrimary().getBounds();
        canvas.setWidth(primaryScreen.getWidth());
        canvas.setHeight(primaryScreen.getHeight());
        clearCanvas(canvas);
        stepButton.setOnMouseClicked((e) -> step());

        Graph graph = loadGraph();

        double graphWidth = 600;
        double graphHeight = 400;
        var transformation = getTransformationForRenderer(graph,
                (primaryScreen.getWidth() - graphWidth) / 2,
                (primaryScreen.getHeight() - graphHeight) / 2,
                graphWidth, graphHeight);

        var vehicles = new ArrayList<Vehicle>();
        vehicles.add(new Vehicle(List.of(0), List.of(graph.getGps(0)), Vehicle.State.PENDING, 20));
        vehicles.add(new Vehicle(List.of(1), List.of(graph.getGps(1)), Vehicle.State.PENDING, 20));
        vehicles.add(new Vehicle(List.of(2), List.of(graph.getGps(2)), Vehicle.State.PENDING, 20));
        vehicles.add(new Vehicle(List.of(3), List.of(graph.getGps(3)), Vehicle.State.PENDING, 20));
        vehicles.add(new Vehicle(List.of(4), List.of(graph.getGps(4)), Vehicle.State.PENDING, 20));
        vehicles.add(new Vehicle(List.of(5), List.of(graph.getGps(5)), Vehicle.State.PENDING, 20));
        vehicles.add(new Vehicle(List.of(6), List.of(graph.getGps(6)), Vehicle.State.PENDING, 20));
        vehicles.add(new Vehicle(List.of(7), List.of(graph.getGps(7)), Vehicle.State.PENDING, 20));




        var orpInstance = new ORPInstance(graph, vehicles);
        var solver = new TaxiSolver(orpInstance);
        this.simulator = new Simulator(orpInstance, solver);

        this.renderer = new ORPStateRenderer(transformation);

        renderer.render(canvas, orpInstance);

        var request = Request.builder()
                .userId(1)
                .departurePoint(graph.getGps(5))
                .arrivalPoint(graph.getGps(13))
                .departureTimeWindow(new int[]{0, 10_000})
                .load(1)
                .build();

        simulator.acceptRequest(request);
    }
}
