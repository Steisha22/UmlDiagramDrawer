package knure.ua.controller;

import javafx.fxml.FXML;
import knure.ua.controller.canvasstate.AddComponentState;
import knure.ua.model.components.shapes.Actor;
import knure.ua.model.components.shapes.UseCase;

public class UseCaseDiagramController extends CommonDiagramController {

    @FXML
    protected void initialize() {
        super.initialize();
    }

    /**handler for adding a new useCase to the canvas*/
    @FXML
    public void drawNewUseCase() {
        canvasContentManagementController.setCurrentCanvasState(
                new AddComponentState(canvasContentManagementController, new UseCase()));
    }

    /**handler for adding a new Actor to the canvas*/
    @FXML
    public void drawNewActor() {
        canvasContentManagementController.setCurrentCanvasState(
                new AddComponentState(canvasContentManagementController, new Actor()));
    }

}
