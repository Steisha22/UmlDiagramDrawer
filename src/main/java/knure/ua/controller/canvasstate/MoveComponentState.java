package knure.ua.controller.canvasstate;

import javafx.scene.input.MouseEvent;
import knure.ua.controller.CanvasContentManagementController;
import knure.ua.model.components.shapes.BoxComponent;

/**Handles the repositioning of a component on the canvas*/
public class MoveComponentState extends CanvasState{
    //the component to reposition on the canvas
    BoxComponent componentToDrag;

    /**
     * Constructor
     *
     * @param canvasContentManagementController the controller for the main window using this state
     * @param componentToDrag the component to move on the canvas
     */
    public MoveComponentState(CanvasContentManagementController canvasContentManagementController, BoxComponent componentToDrag){
        super(canvasContentManagementController);
        this.componentToDrag = componentToDrag;
    }

    @Override
    public void exitState(boolean saveChanges){
        canvasContentManagementController.setCurrentCanvasState(new SelectComponentState(canvasContentManagementController));
    }

    @Override
    public void mouseDraggedHandler(MouseEvent mouseEvent) {
        //draw a preview of where the component will now be drawn
        componentToDrag.setCenterX(mouseEvent.getX());
        componentToDrag.setCenterY(mouseEvent.getY());
        canvasContentManagementController.getCanvasDrawController().drawPreviewComponent(componentToDrag);
    }

    @Override
    public void mouseReleasedHandler(MouseEvent mouseEvent) {
        //redraw the component in it's new canvas position
        componentToDrag.setCenterX(mouseEvent.getX());
        componentToDrag.setCenterY(mouseEvent.getY());
        canvasContentManagementController.getCanvasDrawController().drawFinalComponent(componentToDrag);
        exitState(true);
    }
}
