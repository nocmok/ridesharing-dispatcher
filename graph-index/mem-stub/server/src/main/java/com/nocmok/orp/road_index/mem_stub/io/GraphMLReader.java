package com.nocmok.orp.road_index.mem_stub.io;

import com.nocmok.orp.road_index.mem_stub.solver.Graph;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphMLReader implements GraphReader {

    @Override public boolean canReadFiles(File... files) {
        return !locateGraphMlFiles(files).isEmpty();
    }

    private List<File> locateGraphMlFiles(File... files) {
        return Arrays.stream(files)
                .filter(file -> file.getName().matches(".*\\.xml"))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override public Graph readGraph(File... files) {
        var graphMlFiles = locateGraphMlFiles(files);
        if (graphMlFiles.isEmpty()) {
            throw new RuntimeException("cannot locate any GraphML file");
        }
        var graphMLFile = graphMlFiles.get(0);
        try {
            var parser = SAXParserFactory.newInstance().newSAXParser();
            var xmlHandler = new GraphMLHandler();
            parser.parse(graphMLFile, xmlHandler);
            if (xmlHandler.getGraph().isEmpty()) {
                throw new RuntimeException(Objects.toString(xmlHandler.getErrors()));
            }
            return xmlHandler.getGraph().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class GraphMLHandler extends DefaultHandler {
        private Map<String, Map<String, Graph.Link>> adjacencyList = new HashMap<>();
        private Map<String, Graph.Node> nodes = new HashMap<>();
        private List<String> errors = new ArrayList<>();
        private Map<String, String> attributesEncoding = new HashMap<>();
        private Map<String, String> attributesDecoding = new HashMap<>();

        private List<ContentHandler> handlers = new ArrayList<>();

        public GraphMLHandler() {
            handlers.add(new KeyHandler(this));
            handlers.add(new NodeHandler(this));
            handlers.add(new EdgeHandler(this));
        }

        @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            for (var handler : handlers) {
                handler.startElement(uri, localName, qName, attributes);
            }
        }

        @Override public void characters(char[] ch, int start, int length) throws SAXException {
            for (var handler : handlers) {
                handler.characters(ch, start, length);
            }
        }

        @Override public void endElement(String uri, String localName, String qName) throws SAXException {
            for (var handler : handlers) {
                handler.endElement(uri, localName, qName);
            }
        }

        public List<String> getErrors() {
            return errors;
        }

        public Optional<Graph> getGraph() {
            if (errors.isEmpty()) {
                return Optional.of(new InMemoryGraph(adjacencyList, nodes));
            }
            return Optional.empty();
        }
    }

    private static class KeyHandler extends DefaultHandler {
        private final static String KEY = "key";

        private GraphMLHandler graphMLHandler;

        public KeyHandler(GraphMLHandler graphMLHandler) {
            this.graphMLHandler = graphMLHandler;
        }

        @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (!KEY.equals(qName)) {
                return;
            }
            graphMLHandler.attributesEncoding.put(attributes.getValue("attr.name"), attributes.getValue("id"));
            graphMLHandler.attributesDecoding.put(attributes.getValue("id"), attributes.getValue("attr.name"));
        }
    }

    private static class NodeHandler extends DefaultHandler {
        private final static String NODE = "node";
        private final static String DATA = "data";
        private final static String KEY = "key";

        private final static String ID = "id";
        private final static String LATITUDE = "y";
        private final static String LONGITUDE = "x";
        private final static Set<String> requiredFields = Set.of(
                ID,
                LATITUDE,
                LONGITUDE
        );

        private final GraphMLHandler graphMLHandler;
        /**
         * Прочитанные аттрибуты
         */
        private Map<String, String> alreadyReadFields;

        private Set<String> fieldsToReadFromElementContent = new HashSet<>();

        private boolean waitForNestedElements = false;

        public NodeHandler(GraphMLHandler graphMLHandler) {
            this.graphMLHandler = graphMLHandler;
        }

        private void tryReadFieldsFromXMLAttributes(Attributes attributes, Map<String, String> fields) {
            for (var fieldName : requiredFields) {
                if (attributes.getValue(fieldName) == null) {
                    continue;
                }
                fields.put(fieldName, attributes.getValue(fieldName));
            }
        }

        private String decodeField(String encodedField) {
            return graphMLHandler.attributesDecoding.get(encodedField);
        }

        private void readFieldFromElementContent(String fieldName) {
            fieldsToReadFromElementContent.add(fieldName);
        }

        private void tryReadFieldFromNestedElement(String elementName, Attributes elementAttributes, Map<String, String> fields) {
            if (!DATA.equals(elementName)) {
                return;
            }
            var fieldName = decodeField(elementAttributes.getValue(KEY));
            if (fieldName == null) {
                handleError("cannot find decoding for key " + elementAttributes.getValue(KEY));
                return;
            }
            if (requiredFields.contains(fieldName)) {
                readFieldFromElementContent(fieldName);
            }
        }

        @Override public void characters(char[] ch, int start, int length) throws SAXException {
            for (var field : fieldsToReadFromElementContent) {
                alreadyReadFields.put(field, new String(ch, start, length));
            }
            fieldsToReadFromElementContent.clear();
        }

        @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (NODE.equals(qName)) {
                this.alreadyReadFields = new HashMap<>();
                tryReadFieldsFromXMLAttributes(attributes, this.alreadyReadFields);
                this.waitForNestedElements = true;
            } else {
                if (!waitForNestedElements) {
                    return;
                }
                tryReadFieldFromNestedElement(qName, attributes, this.alreadyReadFields);
            }
        }

        private Graph.Node parseNodeFromFields(Map<String, String> fields) {
            return new Graph.Node(
                    fields.get(ID),
                    Double.parseDouble(fields.get(LATITUDE)),
                    Double.parseDouble(fields.get(LONGITUDE))
            );
        }

        private void handleError(String errorMessage) {
            graphMLHandler.errors.add(errorMessage);
        }

        @Override public void endElement(String uri, String localName, String qName) throws SAXException {
            if (!NODE.equals(qName)) {
                return;
            }

            waitForNestedElements = false;

            if (alreadyReadFields.size() < requiredFields.size()) {
                var notProvidedFields = new ArrayList<>(requiredFields);
                notProvidedFields.removeAll(alreadyReadFields.keySet());
                handleError("required fields for node element not provided " + notProvidedFields);
            } else {
                try {
                    var node = parseNodeFromFields(alreadyReadFields);
                    graphMLHandler.nodes.put(node.getId(), node);
                } catch (Exception e) {
                    handleError("node attributes was in invalid format");
                }
            }
        }
    }

    private static class EdgeHandler extends DefaultHandler {
        private final static String EDGE = "edge";
        private final static String DATA = "data";
        private final static String KEY = "key";
        private final static String ROAD_COST = "travel_time";
        private final static String SOURCE = "source";
        private final static String TARGET = "target";

        private final static Set<String> requiredFields = Set.of(
                ROAD_COST,
                SOURCE,
                TARGET
        );

        private GraphMLHandler graphMLHandler;

        private Map<String, String> alreadyReadFields;

        private boolean waitForNestedElements = false;

        private Set<String> fieldsToReadFromElementContent = new HashSet<>();

        public EdgeHandler(GraphMLHandler graphMLHandler) {
            this.graphMLHandler = graphMLHandler;
        }

        private Graph.Link parseLinkFromFields(Map<String, String> fields) {
            return new Graph.Link(
                    fields.get(SOURCE) + ":" + fields.get(TARGET),
                    fields.get(SOURCE),
                    fields.get(TARGET),
                    Double.parseDouble(fields.get(ROAD_COST))
            );
        }

        private void tryReadFieldsFromAttributes(Attributes attributes, Map<String, String> fields) {
            for (var fieldName : requiredFields) {
                if (attributes.getValue(fieldName) == null) {
                    continue;
                }
                alreadyReadFields.put(fieldName, attributes.getValue(fieldName));
            }
        }

        private void readFieldFromElementContent(String fieldName) {
            fieldsToReadFromElementContent.add(fieldName);
        }

        private String decodeField(String encodedField) {
            return graphMLHandler.attributesDecoding.get(encodedField);
        }

        private void tryReadFieldsFromNestedElement(String elementName, Attributes elementAttributes, Map<String, String> fields) {
            if (!DATA.equals(elementName)) {
                return;
            }
            var fieldName = decodeField(elementAttributes.getValue(KEY));
            if (fieldName == null) {
                handleError("cannot find decoding for key " + elementAttributes.getValue(KEY));
                return;
            }
            if (requiredFields.contains(fieldName)) {
                readFieldFromElementContent(fieldName);
            }
        }

        private void handleError(String errorMessage) {
            graphMLHandler.errors.add(errorMessage);
        }

        @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (EDGE.equals(qName)) {
                this.alreadyReadFields = new HashMap<>();
                tryReadFieldsFromAttributes(attributes, this.alreadyReadFields);
                waitForNestedElements = true;
            } else {
                if (!waitForNestedElements) {
                    return;
                }
                tryReadFieldsFromNestedElement(qName, attributes, this.alreadyReadFields);
            }
        }

        @Override public void characters(char[] ch, int start, int length) throws SAXException {
            for (var field : fieldsToReadFromElementContent) {
                alreadyReadFields.put(field, new String(ch, start, length));
            }
            fieldsToReadFromElementContent.clear();
        }

        @Override public void endElement(String uri, String localName, String qName) throws SAXException {
            if (!EDGE.equals(qName)) {
                return;
            }

            waitForNestedElements = false;

            if (alreadyReadFields.size() < requiredFields.size()) {
                var notProvidedFields = new ArrayList<>(requiredFields);
                notProvidedFields.removeAll(alreadyReadFields.keySet());
                handleError("required fields for edge element not provided " + notProvidedFields);
            } else {
                try {
                    var link = parseLinkFromFields(alreadyReadFields);
                    graphMLHandler.adjacencyList
                            .computeIfAbsent(link.getStartNodeId(), (k) -> new HashMap<>())
                            .put(link.getEndNodeId(), link);
                } catch (Exception e) {
                    handleError("edge attributes was in invalid format");
                }
            }
        }
    }
}
