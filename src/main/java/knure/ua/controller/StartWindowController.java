package knure.ua.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import knure.ua.controller.canvasstate.CanvasState;
import knure.ua.model.components.DrawableComponent;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class StartWindowController {

    @FXML
    private Button createDiagramButton;

    @FXML
    private Button openDiagramButton;

    @FXML
    private ComboBox<String> selectorDiagramType;

    // Инициализация ComboBox с типами диаграмм
    @FXML
    public void initialize() {
        selectorDiagramType.getItems().addAll("Use Case Diagram", "Class Diagram", "State Chart Diagram");
    }

    @FXML
    public void getCreateDiagramType(ActionEvent event) {
        String selectedDiagram = selectorDiagramType.getValue();
        if (selectedDiagram != null) {
            System.out.println("Creating new diagram of type: " + selectedDiagram);
            if (selectedDiagram.equals("Use Case Diagram")) {
                openUseCaseDiagramWindow();
            }
        } else {
            System.out.println("No diagram type selected");
        }
    }

    @FXML
    public void getOpenDiagramType(ActionEvent event) {
        String selectedDiagram = selectorDiagramType.getValue();
        if (selectedDiagram != null) {
            if (selectedDiagram.equals("Use Case Diagram")) {
                loadUseCaseDiagram();
            }
        } else {
            System.out.println("No diagram type selected");
        }
    }

    /**handler for loading a new set of DrawableComponents onto the canvas*/
    public void loadUseCaseDiagram() {
        try {
            String fxmlPath = "F:\\Prodjects\\UmlDiagramDrawer\\src\\main\\resources\\knure\\ua\\useCaseWindow.fxml";

            // Загружаем FXML
            FXMLLoader loader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
            Pane diagramPane = loader.load();
            // Создаём новое окно
            Stage diagramStage = new Stage();
            diagramStage.setTitle("Use Case Diagram");
            diagramStage.setScene(new Scene(diagramPane));


            // Получаем контроллер нового окна
            UseCaseDiagramController controller = loader.getController();
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

    private void openUseCaseDiagramWindow() {
        try {
            // Загружаем окно диаграммы
            URL url = new File("F:\\Prodjects\\UmlDiagramDrawer\\src\\main\\resources\\knure\\ua\\useCaseWindow.fxml").toURI().toURL();
            Pane diagramPane = FXMLLoader.load(url);

            // Создаем новое окно для диаграммы
            Stage diagramStage = new Stage();
            diagramStage.setTitle("Use Case Diagram");
            diagramStage.setScene(new Scene(diagramPane));

            diagramStage.show();
            diagramStage.requestFocus();

            Stage.getWindows().stream().filter(Window::isFocused).findFirst().ifPresent(
                    currentWindow -> currentWindow.getScene().setOnKeyPressed(Stage.getWindows().get(0).getScene().getOnKeyPressed()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

