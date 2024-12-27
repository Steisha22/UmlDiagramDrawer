package knure.ua.controller.canvasstate;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import knure.ua.controller.CanvasContentManagementController;
import knure.ua.model.components.arrows.ResizableLine;
import org.javatuples.Pair;

public class AddConnectionState extends CanvasState {
    //the new connection to draw on the canvas
    ResizableLine resizableLine;

    /**
     * Constructor
     *
     * @param canvasContentManagementController the controller for the main window using this state
     * @param newConnection                      the new component to draw on the canvas
     */
    public AddConnectionState(CanvasContentManagementController canvasContentManagementController, ResizableLine newConnection) {
        super(canvasContentManagementController);
        this.resizableLine = newConnection;
        newConnection.setStart(canvasContentManagementController.findClosestPointOnComponentEdge(newConnection.getStart().getValue0(),
                newConnection.getStart().getValue1()));
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
            resizableLine.setEnd(canvasContentManagementController.findClosestPointOnComponentEdge(mouseEvent.getX(), mouseEvent.getY()));
            canvasContentManagementController.getCanvasDrawController().drawFinalComponent(resizableLine);
            canvasContentManagementController.addComponent(resizableLine);
            exitState(true);
        }
    }

    @Override
    public void mouseMoveHandler(MouseEvent mouseEvent) {
        //draw a preview of where the component will be drawn on the canvas
        resizableLine.setEnd(new Pair<Double, Double>(mouseEvent.getX(), mouseEvent.getY()));
        canvasContentManagementController.getCanvasDrawController().drawPreviewComponent(resizableLine);
    }
}
