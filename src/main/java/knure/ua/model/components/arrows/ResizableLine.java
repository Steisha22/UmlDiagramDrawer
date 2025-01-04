package knure.ua.model.components.arrows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import knure.ua.controller.StartWindowController;
import knure.ua.model.components.DrawableComponent;
import knure.ua.model.components.shapes.BoxComponent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;

import java.io.File;
import java.net.URL;

@Getter
@Setter
public class ResizableLine extends BoxComponent {
    //the position of the start of the connection
    private Pair<Double, Double> start;
    //the position of the end of the connection
    private Pair<Double, Double> end;
    //defines the relationship that the connection represents
    private ArrowType arrowType;
    private String role1Text;
    private String role2Text;
    private Cardinality multiplicityRole1Text;
    private Cardinality multiplicityRole2Text;
    //allows the relationship of the connection to be changed in the dialog box
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private ComboBox<ArrowType> arrowTypeComboBox;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private TextField role1Name;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private TextField role2Name;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private ComboBox<Cardinality> multiplicityRole1;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private ComboBox<Cardinality> multiplicityRole2;
    //constant that allows for some wiggle-room in selecting a connection line with the mouse
    private static final double BOUNDARY_LEEWAY = 10;

    public ResizableLine() {
        super("" , 0, 0, 0, 0);
        arrowType = ArrowType.Association;
        arrowTypeComboBox = new ComboBox<>();

        role1Text = "";
        role1Name = new TextField("");

        role2Text = "";
        role2Name = new TextField("");

        multiplicityRole1Text = Cardinality.None;
        multiplicityRole1 = new ComboBox<>();

        multiplicityRole2Text = Cardinality.None;
        multiplicityRole2 = new ComboBox<>();
    }

    /**
     * Constructor
     *
     * @param startX the x coordinate of the start position of the connection
     * @param startY the y coordinate of the start position of the connection
     */
    public ResizableLine(double startX, double startY){
        super("" , 0, 0, 0, 0);
        this.start = new Pair<>(startX, startY);

        arrowType = ArrowType.Association;
        arrowTypeComboBox = new ComboBox<>();

        role1Text = "";
        role1Name = new TextField("");

        role2Text = "";
        role2Name = new TextField("");

        multiplicityRole1Text = Cardinality.None;
        multiplicityRole1 = new ComboBox<>();

        multiplicityRole2Text = Cardinality.None;
        multiplicityRole2 = new ComboBox<>();
    }

    /**
     * Constructor
     *
     * @param startX the x coordinate of the start position of the connection
     * @param startY the y coordinate of the start position of the connection
     * @param endX the x coordinate of the end position of the connection
     * @param endY the y coordinate of the end position of the connection
     * @param connectionHead the type of connection made
     */
    public ResizableLine(double startX, double startY, double endX, double endY, ArrowType connectionHead){
        super("" , 0, 0, 0, 0);
        this.start = new Pair<>(startX, startY);
        this.end = new Pair<>(endX, endY);

        this.arrowType = connectionHead;
        arrowTypeComboBox = new ComboBox<>();

        //TODO
        role1Text = "";
        role1Name = new TextField("");

        role2Text = "";
        role2Name = new TextField("");

        multiplicityRole1Text = Cardinality.None;
        multiplicityRole1 = new ComboBox<>();

        multiplicityRole2Text = Cardinality.None;
        multiplicityRole2 = new ComboBox<>();
    }

    @Override
    public boolean checkPointInBounds(double x, double y) {
        // Координаты начала и конца линии
        double x1 = start.getValue0();
        double y1 = start.getValue1();
        double x2 = end.getValue0();
        double y2 = end.getValue1();

        // Вычисляем расстояние от точки (x, y) до линии
        double lineLengthSquared = Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
        if (lineLengthSquared == 0) {
            // Если линия выродилась в точку
            return Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2)) <= BOUNDARY_LEEWAY;
        }

        // Проекция точки на линию в виде параметра t
        double t = ((x - x1) * (x2 - x1) + (y - y1) * (y2 - y1)) / lineLengthSquared;
        t = Math.max(0, Math.min(1, t)); // Ограничиваем t в пределах [0, 1]

        // Координаты ближайшей точки на линии
        double closestX = x1 + t * (x2 - x1);
        double closestY = y1 + t * (y2 - y1);

        // Рассчитываем расстояние от заданной точки до ближайшей точки на линии
        double distanceSquared = Math.pow(x - closestX, 2) + Math.pow(y - closestY, 2);

        // Сравниваем расстояние с допустимым отклонением
        return distanceSquared <= Math.pow(BOUNDARY_LEEWAY, 2);
    }

    @Override
    public void draw(GraphicsContext gc, Color color, int lineWidth) {
        gc.setLineWidth(lineWidth);
        gc.setStroke(color);
        gc.setLineDashes(arrowType.getDashedLineGap());
        gc.strokeLine(start.getValue0(), start.getValue1(), end.getValue0(), end.getValue1());
        arrowType.drawHead(gc, end, start);

        // Угол наклона линии
        double angle = Math.atan2(end.getValue1() - start.getValue1(), end.getValue0() - start.getValue0());

        // Смещение для текста относительно линии
        double offset = 10; // Расстояние от линии до текста
        double edgeOffset = 40; // Отступ от краёв линии (вдоль линии)

        // Координаты для текста ролей
        double role1X = start.getValue0() - offset * Math.sin(angle) + edgeOffset * Math.cos(angle);
        double role1Y = start.getValue1() + offset * Math.cos(angle) + edgeOffset * Math.sin(angle);

        double role2X = end.getValue0() - offset * Math.sin(angle) - edgeOffset * Math.cos(angle);
        double role2Y = end.getValue1() + offset * Math.cos(angle) - edgeOffset * Math.sin(angle);

        // Координаты для текста кардинальности
        double cardinality1X = start.getValue0() + offset * Math.sin(angle) + edgeOffset * Math.cos(angle);
        double cardinality1Y = start.getValue1() - offset * Math.cos(angle) + edgeOffset * Math.sin(angle);

        double cardinality2X = end.getValue0() + offset * Math.sin(angle) - edgeOffset * Math.cos(angle);
        double cardinality2Y = end.getValue1() - offset * Math.cos(angle) - edgeOffset * Math.sin(angle);

        // Рисуем текст ролей
        gc.setFill(Color.BLACK);
        gc.fillText(role1Text, role1X, role1Y);
        gc.fillText(role2Text, role2X, role2Y);
        gc.fillText(multiplicityRole1Text.getCardinality(), cardinality1X, cardinality1Y);
        gc.fillText(multiplicityRole2Text.getCardinality(), cardinality2X, cardinality2Y);
    }

    @Override
    public TitledPane loadDialog() {
        try {
            URL url = new File(getPathToDialogFxml()).toURI().toURL();
            TitledPane titledPane = FXMLLoader.load(url);
            AnchorPane contentPane = (AnchorPane) titledPane.getContent();

            for (Node node : contentPane.getChildren()) {
                if (node instanceof TextField && "role1Name".equals(node.getId())) {
                    role1Name = (TextField) node;
                    role1Name.setText(role1Text);
                } else if (node instanceof TextField && "role2Name".equals(node.getId())) {
                    role2Name = (TextField) node;
                    role2Name.setText(role2Text);
                } else if (node instanceof ComboBox && "multiplicityRole1".equals(node.getId())) {
                    multiplicityRole1 = (ComboBox<Cardinality>) node;
                    multiplicityRole1.getItems().setAll(Cardinality.values());
                    multiplicityRole1.getSelectionModel().select(multiplicityRole1Text);
                } else if (node instanceof ComboBox && "multiplicityRole2".equals(node.getId())) {
                    multiplicityRole2 = (ComboBox<Cardinality>) node;
                    multiplicityRole2.getItems().setAll(Cardinality.values());
                    multiplicityRole2.getSelectionModel().select(multiplicityRole2Text);
                } else if (node instanceof ComboBox && "arrowTypeComboBox".equals(node.getId())) {
                    arrowTypeComboBox = (ComboBox<ArrowType>) node;
                    arrowTypeComboBox.getItems().setAll(getArrowTypesByDiagramType());
                    arrowTypeComboBox.getSelectionModel().select(arrowType);
                } else if (node instanceof Button && "switchDirectionButton".equals(node.getId())) {
                    Button switchButton = (Button) node;
                    switchButton.setOnAction((e) -> switchDirection());
                }
            }
            return titledPane;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new TitledPane();
    }

    private ArrowType[] getArrowTypesByDiagramType() {
        String diagramTitle = Stage.getWindows()
                .stream()
                .filter(window -> window.isFocused())
                .filter(window -> window instanceof Stage)
                .map(window -> ((Stage) window).getTitle())
                .findFirst().orElse("");
        if (StartWindowController.USE_CASE.equals(diagramTitle)) {
            return ArrowType.getArrowTypes4UseCaseDiagram();
        } else if (StartWindowController.CLASS_DIAGRAM.equals(diagramTitle)) {
            return ArrowType.getArrowTypes4ClassDiagram();
        }
        return ArrowType.values();
    }

    @Override
    protected double getTitleYCoord() {
        return 0;
    }

    @Override
    public DrawableComponent createCopy() {
        return new ResizableLine(this.start.getValue0(), this.start.getValue1(),
                this.end.getValue0(), this.end.getValue1(), this.arrowType);
    }

    @Override
    public void updateContents() {
        arrowType = arrowTypeComboBox.getValue();
        role1Text = !role1Name.getText().isBlank() ? role1Name.getText() : "";
        role2Text = !role2Name.getText().isBlank() ? role2Name.getText() : "";
        multiplicityRole1Text = multiplicityRole1.getValue() != Cardinality.None ? multiplicityRole1.getValue() : Cardinality.None;
        multiplicityRole2Text = multiplicityRole2.getValue() != Cardinality.None ? multiplicityRole2.getValue() : Cardinality.None;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResizableLine that = (ResizableLine) o;
        return start.getValue0().equals(that.getStart().getValue0()) && start.getValue1().equals(that.getStart().getValue1())
                && end.getValue0().equals(that.getEnd().getValue0()) && end.getValue1().equals(that.getEnd().getValue1())
                && arrowType == that.getArrowType();
    }

    /**swaps the start and end points of this Connection*/
    private void switchDirection(){
        Pair<Double, Double> temp = start;
        start = end;
        end = temp;
    }

    @Override
    protected String getPathToDialogFxml() {
        return "F:\\Prodjects\\UmlDiagramDrawer\\src\\main\\resources\\knure\\ua\\connectorDialog.fxml";
    }
}
