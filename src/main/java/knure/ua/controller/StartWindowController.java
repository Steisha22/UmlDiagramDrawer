package knure.ua.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import knure.ua.model.components.DrawableComponent;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class StartWindowController {

    private static final String USE_CASE = "Use Case Diagram";
    private static final String CLASS_DIAGRAM = "Class Diagram";
    private static final String STATE_CHART = "State Chart Diagram";

    @FXML
    private Button createDiagramButton;

    @FXML
    private Button openDiagramButton;

    @FXML
    private ComboBox<String> selectorDiagramType;

    @FXML
    public void initialize() {
        selectorDiagramType.getItems().addAll(USE_CASE, CLASS_DIAGRAM, STATE_CHART);
    }

    @FXML
    public void getCreateDiagramType(ActionEvent event) {
        String selectedDiagram = selectorDiagramType.getValue();
        if (selectedDiagram != null) {
            if (USE_CASE.equals(selectedDiagram)) {
                loadUseCaseDiagram(true);
            } else if (CLASS_DIAGRAM.equals(selectedDiagram)) {
                loadClassDiagram(true);
            }
        } else {
            showErrorAlert("No diagram type selected");
        }
    }

    @FXML
    public void getOpenDiagramType(ActionEvent event) {
        String selectedDiagram = selectorDiagramType.getValue();
        if (selectedDiagram != null) {
            if (selectedDiagram.equals("Use Case Diagram")) {
                loadUseCaseDiagram(false);
            }
        } else {
            showErrorAlert("No diagram type selected");
        }
    }

    /**handler for loading a new set of DrawableComponents onto the canvas*/
    private void loadDiagram(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
            Pane diagramPane = loader.load();

            Stage diagramStage = new Stage();
            diagramStage.setTitle(title);
            diagramStage.setScene(new Scene(diagramPane));

            CommonDiagramController controller = loader.getController();
            ArrayList<DrawableComponent> components = new FileController(null).loadDrawnComponents(diagramStage.getScene().getWindow());
            controller.loadCanvasContents(components);
            diagramStage.show();

            diagramStage.requestFocus();
            Stage.getWindows().stream().filter(Window::isFocused).findFirst().ifPresent(
                    currentWindow -> currentWindow.getScene().setOnKeyPressed(Stage.getWindows().get(0).getScene().getOnKeyPressed()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNewDiagramWindow(String fxmlPath, String title) {
        try {
            URL url = new File(fxmlPath).toURI().toURL();
            Pane diagramPane = FXMLLoader.load(url);

            Stage diagramStage = new Stage();
            diagramStage.setTitle(title);
            diagramStage.setScene(new Scene(diagramPane));

            diagramStage.show();
            diagramStage.requestFocus();

            Stage.getWindows().stream().filter(Window::isFocused).findFirst().ifPresent(
                    currentWindow -> currentWindow.getScene().setOnKeyPressed(Stage.getWindows().get(0).getScene().getOnKeyPressed()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUseCaseDiagram(boolean isNewDiagram) {
        String fxmlPath = "F:\\Prodjects\\UmlDiagramDrawer\\src\\main\\resources\\knure\\ua\\useCaseWindow.fxml";

        if (isNewDiagram) {
            createNewDiagramWindow(fxmlPath, USE_CASE);
        } else {
            loadDiagram(fxmlPath, USE_CASE);
        }
    }

    private void loadClassDiagram(boolean isNewDiagram) {
        String fxmlPath = "F:\\Prodjects\\UmlDiagramDrawer\\src\\main\\resources\\knure\\ua\\classDiagramWindow.fxml";

        if (isNewDiagram) {
            createNewDiagramWindow(fxmlPath, CLASS_DIAGRAM);
        } else {
            loadDiagram(fxmlPath, CLASS_DIAGRAM);

        }
    }

    private void showErrorAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    @FXML
    public void quit() {
        Platform.exit();
    }

}

