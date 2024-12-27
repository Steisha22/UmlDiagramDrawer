package knure.ua;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        URL url = new File("F:\\Prodjects\\UmlDiagramDrawer\\src\\main\\resources\\knure\\ua\\controller\\startWindow.fxml").toURI().toURL();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("startWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("UML redactor");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}