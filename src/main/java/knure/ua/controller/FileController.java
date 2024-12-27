package knure.ua.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import knure.ua.model.components.DrawableComponent;
import knure.ua.model.components.arrows.ArrowType;
import knure.ua.model.components.arrows.Cardinality;
import org.javatuples.Pair;
import org.reflections.ReflectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;

/**Handles file operations for the system*/
public class FileController {
    private CanvasContentManagementController canvasContentManagementController;
    private FileChooser fileChooser;

    private static final String ROOT_TAG_NAME = "canvas";

    public FileController(CanvasContentManagementController canvasContentManagementController) {
        this.canvasContentManagementController = canvasContentManagementController;
        fileChooser = new FileChooser();
    }

    public void saveDrawnComponents(Window window, ArrayList<DrawableComponent> drawableComponents, Canvas canvas) {
        FileChooser.ExtensionFilter xmlExtFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        FileChooser.ExtensionFilter jsonExtFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        FileChooser.ExtensionFilter pngExtFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(xmlExtFilter);
        fileChooser.getExtensionFilters().add(jsonExtFilter);
        fileChooser.getExtensionFilters().add(pngExtFilter);

        //Show save file dialog
        File selectedFile = fileChooser.showSaveDialog(window);
        if (selectedFile != null) {
            String fileName = selectedFile.getName();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                String extension = fileName.substring(dotIndex + 1);
                if ("xml".equals(extension)) {
                    exportAsXml(drawableComponents, selectedFile);
                } else if ("json".equals(extension)) {
                    exportAsJson(drawableComponents, selectedFile);
                } else if ("png".equals(extension)) {
                    exportAsPng(canvas, selectedFile);
                }
            } else {
                exportAsXml(drawableComponents, selectedFile);
            }
        }
    }

    private void exportAsXml(ArrayList<DrawableComponent> drawableComponents, File selectedFile) {
        try {
            //generate save file
            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement(ROOT_TAG_NAME);
            doc.appendChild(rootElement);

            //add element for each drawn component
            for (DrawableComponent component : drawableComponents) {
                Element componentElement = doc.createElement(component.getClass().getName());
                //invoke all getters of each component to populate component tag
                Set<Method> getters = ReflectionUtils.getAllMethods(component.getClass(),
                        ReflectionUtils.withModifier(Modifier.PUBLIC), ReflectionUtils.withPrefix("get"),
                        ReflectionUtils.withParametersCount(0));
                for(Method getter : getters){
                    if(!getter.getName().equals("getClass")) {
                        Element fieldElement = doc.createElement(getter.getName().substring(3));
                        fieldElement.appendChild(doc.createTextNode(getter.invoke(component).toString()));
                        componentElement.appendChild(fieldElement);
                    }
                }
                rootElement.appendChild(componentElement);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(selectedFile);
            transformer.transform(source, result);
        } catch(Exception e){
            showErrorAlert("Save failed:", e.getMessage());
        }
    }

    private void exportAsJson(ArrayList<DrawableComponent> drawableComponents, File selectedFile) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode rootNode = objectMapper.createArrayNode();

            for (DrawableComponent component : drawableComponents) {
                ObjectNode componentNode = rootNode.addObject();
                componentNode.put("type", component.getClass().getName());

                // Получить все геттеры и их значения через рефлексию
                Set<Method> getters = ReflectionUtils.getAllMethods(component.getClass(),
                        ReflectionUtils.withModifier(Modifier.PUBLIC), ReflectionUtils.withPrefix("get"),
                        ReflectionUtils.withParametersCount(0));
                for (Method getter : getters) {
                    if (!getter.getName().equals("getClass")) {
                        String fieldName = getter.getName().substring(3); // Убираем "get"
                        Object value = getter.invoke(component);
                        if (value != null) {
                            componentNode.put(fieldName, value.toString());
                        }
                    }
                }
            }

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(selectedFile, rootNode);
        } catch (Exception e) {
            showErrorAlert("Save failed:", e.getMessage());
        }
    }

    public void exportAsPng(Canvas canvas, File selectedFile){
        canvasContentManagementController.setHighlightedComponent(null);
        canvasContentManagementController.getCanvasDrawController().issueDrawingCommand(() -> {
            //capture canvas image
            WritableImage image = canvas.snapshot(new SnapshotParameters(), null);

            if (selectedFile != null) {
                try {
                    //save file
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", selectedFile);
                } catch (IOException e) {
                    //print error alert
                    showErrorAlert("Export to image failed:", e.getMessage());
                }
            }
        });
    }


    public ArrayList<DrawableComponent> loadDrawnComponents(Window window){
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null) {
            String fileName = selectedFile.getName();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                String extension = fileName.substring(dotIndex + 1);
                if ("xml".equals(extension)) {
                    return loadXmlDiagram(selectedFile);
                } else if ("json".equals(extension)) {
                    return loadJsonDiagram(selectedFile);
                }
            } else {
                return loadXmlDiagram(selectedFile);
            }
        }
        return new ArrayList<>();
    }

    private ArrayList<DrawableComponent> loadXmlDiagram(File selectedFile) {
        ArrayList<DrawableComponent> drawableComponents = new ArrayList<>();
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(selectedFile);

            //a list of all the component tags
            NodeList componentTags = document.getElementsByTagName(ROOT_TAG_NAME).item(0).getChildNodes();
            for(int i = 0; i < componentTags.getLength(); i++){
                Node componentTag = componentTags.item(i);
                //create a new instance of the class associated with the tag with no args constructor
                Class<?> clazz = Class.forName(componentTag.getNodeName());
                DrawableComponent drawableComponent = (DrawableComponent) clazz.getConstructor().newInstance();
                //get all the setters for the class associated with the tag
                Set<Method> setters = ReflectionUtils.getAllMethods(clazz,
                        ReflectionUtils.withModifier(Modifier.PUBLIC), ReflectionUtils.withPrefix("set"),
                        ReflectionUtils.withParametersCount(1));
                //get all the sub tags for each field
                NodeList fieldTags = componentTag.getChildNodes();
                for(Method setter : setters){
                    //call each setter to populate DrawableComponent
                    String fieldTagContents = null;
                    for(int j = 0; j < fieldTags.getLength(); j++){
                        if(setter.getName().contains(fieldTags.item(j).getNodeName())){
                            fieldTagContents = fieldTags.item(j).getTextContent();
                            break;
                        }
                    }
                    if(fieldTagContents != null){
                        Class<?> parameterType = setter.getParameterTypes()[0];
                        if (parameterType.equals(double.class)) {
                            setter.invoke(drawableComponent, Double.parseDouble(fieldTagContents));
                        } else if (parameterType.equals(String.class)) {
                            setter.invoke(drawableComponent, fieldTagContents);
                        } else if (parameterType.equals(Pair.class)) {
                            //to make a pair from the string, we have to remove the square
                            //brackets from ends and split on the comma separating each part
                            String[] pairAsTextList = fieldTagContents.
                                    substring(1, fieldTagContents.length()-1).split(", ");
                            setter.invoke(drawableComponent, new Pair<>(Double.parseDouble(pairAsTextList[0]),
                                    Double.parseDouble(pairAsTextList[1])));
                        } else if (parameterType.equals(ArrowType.class)) {
                            setter.invoke(drawableComponent, ArrowType.valueOf(fieldTagContents));
                        } else if (parameterType.equals(Cardinality.class)) {
                            setter.invoke(drawableComponent, Cardinality.valueOf(fieldTagContents));
                        }
                    }
                }
                //add component to list
                drawableComponents.add(drawableComponent);
            }
        } catch(Exception e){
            //print error alert
            e.printStackTrace();
            showErrorAlert("Load failed:", e.getMessage());
        }
        return drawableComponents;
    }

    private ArrayList<DrawableComponent> loadJsonDiagram(File selectedFile) {
        ArrayList<DrawableComponent> drawableComponents = new ArrayList<>();
        if (selectedFile != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(selectedFile);

                for (JsonNode componentNode : rootNode) {
                    // Получаем имя класса компонента
                    String className = componentNode.get("type").asText();
                    Class<?> clazz = Class.forName(className);

                    // Создаём новый экземпляр класса
                    DrawableComponent drawableComponent = (DrawableComponent) clazz.getConstructor().newInstance();

                    // Проходим по всем методам-сеттерам и вызываем их
                    Set<Method> setters = ReflectionUtils.getAllMethods(clazz,
                            ReflectionUtils.withModifier(Modifier.PUBLIC), ReflectionUtils.withPrefix("set"),
                            ReflectionUtils.withParametersCount(1));

                    for (Method setter : setters) {
                        String fieldName = setter.getName().substring(3); // Убираем "set"
                        JsonNode fieldNode = componentNode.get(fieldName);

                        if (fieldNode != null) {
                            Class<?> parameterType = setter.getParameterTypes()[0];

                            if (parameterType.equals(double.class)) {
                                setter.invoke(drawableComponent, fieldNode.asDouble());
                            } else if (parameterType.equals(String.class)) {
                                setter.invoke(drawableComponent, fieldNode.asText());
                            } else if (parameterType.equals(Pair.class)) {
                                String[] pairValues = fieldNode.asText()
                                        .substring(1, fieldNode.asText().length() - 1) // Убираем скобки
                                        .split(", ");
                                setter.invoke(drawableComponent, new Pair<>(
                                        Double.parseDouble(pairValues[0]),
                                        Double.parseDouble(pairValues[1])
                                ));
                            } else if (parameterType.equals(ArrowType.class)) {
                                setter.invoke(drawableComponent, ArrowType.valueOf(fieldNode.asText()));
                            } else if (parameterType.equals(Cardinality.class)) {
                                setter.invoke(drawableComponent, Cardinality.valueOf(fieldNode.asText()));
                            }
                        }
                    }

                    drawableComponents.add(drawableComponent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Load failed:", e.getMessage());
            }
        }
        return drawableComponents;
    }

    /**
     * displays an alert box with the passed message
     *
     * @param message the message to display on the alert box
     * @param error details to add to the message
     */
    private void showErrorAlert(String message, String error){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(message);
        alert.setContentText(error);
        alert.showAndWait();
    }
}
