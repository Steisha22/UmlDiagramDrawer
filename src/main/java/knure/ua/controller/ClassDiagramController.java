package knure.ua.controller;

import javafx.fxml.FXML;
import knure.ua.controller.canvasstate.AddComponentState;
import knure.ua.model.components.shapes.ClassShape;

public class ClassDiagramController  extends CommonDiagramController {

	@FXML
	protected void initialize() {
		super.initialize();
	}

	/**handler for adding a new ClassBox to the canvas*/
	@FXML
	public void drawNewClass() {
		canvasContentManagementController.setCurrentCanvasState(
				new AddComponentState(canvasContentManagementController, new ClassShape()));
	}

}
