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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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
    @FXML
    private Button runButton;
    @FXML
    private Button stopButton;
    @FXML
    private CheckBox vehicleLayerCheckBox;
    @FXML
    private CheckBox routeLayerCheckBox;
    @FXML
    private CheckBox requestLayerCheckBox;
    @FXML
    private Slider timeSlider;
    @FXML
    private Label timeLabel;
    @FXML
    private TextField departureNodeTextField;
    @FXML
    private TextField arrivalNodeTextField;
    @FXML
    private TextField clientLoadTextField;
    @FXML
    private Button submitRequestButton;

    private Simulator simulator;
    private ORPStateRenderer renderer;

    // Определяет, будет ли поток, ответственный за автоматическое итерирование симуляции вызывать ticTac
    private volatile boolean stepAutomatically = false;
    private Thread stepper;

    private int nextRequestId = 0;

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
        simulator.ticTac((int) timeSlider.getValue());
    }

    private void render() {
        clearCanvas(canvas);
        renderer.render(canvas, simulator.getState());
        timeLabel.setText(Integer.toString(simulator.getState().getTime()));
    }

    private Thread getStepper() {
        return new Thread(() -> {
            try {
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    if (MainController.this.stepAutomatically) {
                        step();
                        Platform.runLater(this::render);
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ignore) {
            }
        });
    }

    private Request createRequest(int startNode, int endNode, int load) {
        // максимальное время ожидания тс клиентом
        int maxClientWaitingTimeSeconds = 480; // 8 min

        // максимальная задержка прибытия связанная с применением райдшеринга
        int maxRidesharingLagSeconds = 480;

        return Request.builder()
                .requestId(nextRequestId++)
                .userId(1)
                .departurePoint(simulator.getState().getGraph().getGps(startNode))
                .arrivalPoint(simulator.getState().getGraph().getGps(endNode))
                .departureTimeWindow(new int[]{simulator.getState().getTime(),
                        simulator.getState().getTime() + maxClientWaitingTimeSeconds})
                .arrivalTimeWindow(new int[]{0, maxRidesharingLagSeconds})
                .load(1)
                .state(Request.State.PENDING)
                .build();
    }

    @Override public void initialize(URL location, ResourceBundle resources) {
        var primaryScreen = javafx.stage.Screen.getPrimary().getBounds();
        canvas.setWidth(primaryScreen.getWidth());
        canvas.setHeight(primaryScreen.getHeight());
        clearCanvas(canvas);

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

        // Actions

        stepButton.setOnMouseClicked((e) -> {
            step();
            render();
        });

        runButton.setOnMouseClicked((e) -> {
            this.stepAutomatically = true;
        });

        stopButton.setOnMouseClicked((e) -> {
            this.stepAutomatically = false;
        });

        vehicleLayerCheckBox.selectedProperty().addListener((e) -> {
            renderer.setRenderVehicles(vehicleLayerCheckBox.isSelected());
            clearCanvas(canvas);
            renderer.render(canvas, orpInstance);
        });

        routeLayerCheckBox.selectedProperty().addListener((e) -> {
            renderer.setRenderRoutes(routeLayerCheckBox.isSelected());
            clearCanvas(canvas);
            renderer.render(canvas, orpInstance);
        });

        requestLayerCheckBox.selectedProperty().addListener((e) -> {
            renderer.setRenderServingRequests(requestLayerCheckBox.isSelected());
            clearCanvas(canvas);
            renderer.render(canvas, orpInstance);
        });

        timeLabel.setText(Integer.toString(simulator.getState().getTime()));

        submitRequestButton.setOnMouseClicked((e) -> {
            simulator.acceptRequest(createRequest(
                    Integer.parseInt(departureNodeTextField.getText()),
                    Integer.parseInt(arrivalNodeTextField.getText()),
                    Integer.parseInt(clientLoadTextField.getText())));
            render();
        });

        this.stepper = getStepper();
        stepper.start();
    }
}
