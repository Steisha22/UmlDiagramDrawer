package knure.ua.controller.canvasstate;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import knure.ua.controller.CanvasContentManagementController;
import knure.ua.model.components.DrawableComponent;
import knure.ua.model.components.shapes.ClassShape;

/**Handles the editing of a component's contents*/
public class EditComponentContentsState extends CanvasState {
    //the component to edit the contents of
    private DrawableComponent componentToEdit;
    //the dialog box to edit the contents of the component
    private Stage dialog;

    /**
     * Constructor
     *
     * @param canvasContentManagementController the controller for the main window that uses this Canvas State object
     */
    public EditComponentContentsState(CanvasContentManagementController canvasContentManagementController, DrawableComponent componentToEdit) {
        super(canvasContentManagementController);
        this.componentToEdit = componentToEdit;
        dialog = new Stage();
        openDialogBox();
    }

    /**Opens a dialog box to edit the contents of the selected component*/
    private void openDialogBox() {
        dialog.initModality(Modality.APPLICATION_MODAL);

        //get the dialog box contents
        TitledPane titledPane = componentToEdit.loadDialog();
        AnchorPane contentPane = (AnchorPane) titledPane.getContent();

        for (Node node : contentPane.getChildren()) {
            if (node instanceof Button && "doneButton".equals(node.getId())) {
                Button doneButton = (Button) node;
                doneButton.setOnAction(event -> exitState(true));
            } else if (node instanceof Button && "addNewFieldButton".equals(node.getId())) {
                Button addNewFieldButton = (Button) node;
                addNewFieldButton.setOnAction((e) -> openAddNewFieldDialog());
            } else if (node instanceof Button && "addNewMethodButton".equals(node.getId())) {
                Button addNewMethodButton = (Button) node;
                addNewMethodButton.setOnAction((e) -> openAddNewMethodDialog());
            }
        }

        titledPane.setOnKeyPressed((e) -> {
            if(e.getCode() == KeyCode.ENTER){
                exitState(true);
            }
        });

        //create and display the dialog box
        Scene dialogScene = new Scene(titledPane);
        dialog.setScene(dialogScene);
        dialog.setOnCloseRequest((e) -> exitState(false));
        dialog.show();
    }

    @Override
    public void exitState(boolean saveContent) {
        if (saveContent) {
            componentToEdit.updateContents();
        }
        dialog.close();
        canvasContentManagementController.setCurrentCanvasState(new SelectComponentState(canvasContentManagementController));
    }

    // Метод для открытия нового диалога для добавления поля
    private void openAddNewFieldDialog() {
        // Закрываем текущий диалог
        dialog.close();
        ClassShape classShape = (ClassShape) componentToEdit;
        classShape.openAddNewFieldOrMethodDialog(canvasContentManagementController, "F:\\Prodjects\\UmlDiagramDrawer\\src\\main\\resources\\knure\\ua\\addNewFieldDialog.fxml");
    }

    private void openAddNewMethodDialog() {
        dialog.close();
        ClassShape classShape = (ClassShape) componentToEdit;
        classShape.openAddNewFieldOrMethodDialog(canvasContentManagementController, "F:\\Prodjects\\UmlDiagramDrawer\\src\\main\\resources\\knure\\ua\\addNewMethodDialog.fxml");
    }
}
