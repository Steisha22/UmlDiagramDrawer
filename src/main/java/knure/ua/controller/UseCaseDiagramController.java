package knure.ua.controller;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import knure.ua.controller.canvasstate.AddComponentState;
import knure.ua.model.components.DrawableComponent;
import knure.ua.model.components.arrows.ResizableLine;
import knure.ua.model.components.shapes.Actor;
import knure.ua.model.components.shapes.UseCase;

import java.util.ArrayList;

public class UseCaseDiagramController {
    // the root node of the main window fxml
    @FXML
    public AnchorPane workArea;
    //the fxml Pane node containing the canvas
    @FXML
    private ScrollPane scrollPane;
    //the fxml canvas node to be drawn on
    @FXML
    private Canvas canvas;

    //the controller for the Canvas contents
    private CanvasContentManagementController canvasContentManagementController;

    private FileController fileController;

    //the maximum dimension for the canvas drawing
    private static final double CANVAS_MAX_SIZE = 4000;
    //the amount to increase canvas size by when scrolling
    private static final double CANVAS_SIZE_INCREASE = 100;

    /**initialize method once FXML loads*/
    @FXML
    private void initialize() {
        //initialize the canvas content management controller
        canvasContentManagementController = new CanvasContentManagementController(canvas);
        fileController = new FileController(canvasContentManagementController);

        //set a listener to redraw the canvas when the window is resized
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
                canvasContentManagementController.getCanvasDrawController().redrawCanvas();
        workArea.sceneProperty().addListener((observableScene, oldScene, newScene) ->
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    workArea.getScene().getWindow().widthProperty().addListener(stageSizeListener);
                    workArea.getScene().getWindow().heightProperty().addListener(stageSizeListener);
                }));

        //set canvas size of center pane
        canvasContentManagementController.getCanvasDrawController().issueDrawingCommand(() -> {
            canvas.heightProperty().setValue(scrollPane.heightProperty().getValue());
            canvas.widthProperty().setValue(scrollPane.widthProperty().getValue());
        });
        //set listeners to grow canvas size
        scrollPane.vvalueProperty().addListener((observableValue, number, t1) -> {
            if (t1.doubleValue() == scrollPane.getVmax() && canvas.heightProperty().getValue() < CANVAS_MAX_SIZE) {
                canvasContentManagementController.getCanvasDrawController().issueDrawingCommand(() ->
                        canvas.heightProperty().setValue(canvas.heightProperty().getValue() + CANVAS_SIZE_INCREASE));
            }
        });
        scrollPane.hvalueProperty().addListener((observableValue, number, t1) -> {
            if(t1.doubleValue() == scrollPane.getHmax() && canvas.widthProperty().getValue() < CANVAS_MAX_SIZE){
                canvasContentManagementController.getCanvasDrawController().issueDrawingCommand(() ->
                        canvas.widthProperty().setValue(canvas.widthProperty().getValue() + CANVAS_SIZE_INCREASE));
            }
        });
    }

    /**handler for adding a new SingleSectionClassBox to the canvas*/
    @FXML
    public void drawNewUseCase(){
        canvasContentManagementController.setCurrentCanvasState(
                new AddComponentState(canvasContentManagementController, new UseCase()));
    }

    /**handler for adding a new TwoSectionClassBox to the canvas*/
    @FXML
    public void drawNewActor(){
        canvasContentManagementController.setCurrentCanvasState(
                new AddComponentState(canvasContentManagementController, new Actor()));
    }

    /**handler for loading a new set of DrawableComponents onto the canvas*/
    public void loadCanvasContents(ArrayList<DrawableComponent> components) {
        canvasContentManagementController.setDrawnComponents(components);
        canvas.heightProperty().set(368.0);
        canvas.widthProperty().set(512.0);

        //after loading the file, we need to refresh the canvas drawing and reset the keystroke handler by resetting state
        canvasContentManagementController.getCanvasDrawController().redrawCanvas();
        canvasContentManagementController.getCurrentCanvasState().enterState();
    }

    /**handler for saving the current set of DrawableComponents on the canvas to a file*/
    @FXML
    public void saveCurrentCanvasContents() {
        fileController.saveDrawnComponents(workArea.getScene().getWindow(),
                canvasContentManagementController.getDrawnComponents());
    }

    /**handler for saving the current set of DrawableComponents on the canvas as a PNG image**/
    @FXML
    public void exportCanvasToImageOnClick(){
        fileController.exportCanvasToImage(workArea.getScene().getWindow(), canvas);
    }

    public CanvasContentManagementController getCanvasContentManagementController() {
        return canvasContentManagementController;
    }
}
