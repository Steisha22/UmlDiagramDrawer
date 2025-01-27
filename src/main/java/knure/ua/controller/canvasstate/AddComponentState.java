package knure.ua.controller.canvasstate;


import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import knure.ua.controller.CanvasContentManagementController;
import knure.ua.model.components.shapes.BoxComponent;

/**Handles the addition of a DrawableComponent onto the canvas*/
public class AddComponentState extends CanvasState {
    //the component to add onto the canvas drawing
    protected BoxComponent newComponent;

    /**
     * Constructor
     *
     * @param canvasContentManagementController the controller for the main window using this state
     * @param newComponent the new component to draw on the canvas
     */
    public AddComponentState(CanvasContentManagementController canvasContentManagementController, BoxComponent newComponent){
        super(canvasContentManagementController);
        this.newComponent = newComponent;
    }

    @Override
    public void exitState(boolean saveContent) {
        canvasContentManagementController.setCurrentCanvasState(new SelectComponentState(canvasContentManagementController));
    }

    @Override
    public void enterState() {
        super.enterState();
        canvasContentManagementController.setHighlightedComponent(null);
    }

    @Override
    public void mousePressedHandler(MouseEvent mouseEvent) {
        if(mouseEvent.getButton() == MouseButton.SECONDARY){
            // a right click indicates cancelling the new component addition
            exitState(true);
        } else if(mouseEvent.getButton() == MouseButton.PRIMARY){
            //draw the component on the canvas
            newComponent.setCenterX(mouseEvent.getX());
            newComponent.setCenterY(mouseEvent.getY());
            canvasContentManagementController.getCanvasDrawController().drawFinalComponent(newComponent);
            canvasContentManagementController.addComponent(newComponent);
            exitState(true);
        }
    }

    @Override
    public void mouseMoveHandler(MouseEvent mouseEvent) {
        //draw a preview of where the component will be drawn on the canvas
        newComponent.setCenterX(mouseEvent.getX());
        newComponent.setCenterY(mouseEvent.getY());
        canvasContentManagementController.getCanvasDrawController().drawPreviewComponent(newComponent);
    }
}
