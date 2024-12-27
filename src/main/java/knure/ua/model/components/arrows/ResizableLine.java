package knure.ua.model.components.arrows;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import knure.ua.model.components.shapes.BoxComponent;
import org.javatuples.Pair;
import knure.ua.model.components.DrawableComponent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ResizableLine extends BoxComponent {
    //the position of the start of the connection
    private Pair<Double, Double> start;
    //the position of the end of the connection
    private Pair<Double, Double> end;
    //defines the relationship that the connection represents
    private ArrowType arrowType;
    //allows the relationship of the connection to be changed in the dialog box
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private ComboBox<ArrowType> comboBox;

    //constant that allows for some wiggle-room in selecting a connection line with the mouse
    private static final double BOUNDARY_LEEWAY = 10;

    public ResizableLine() {
        super("" , 0, 0, 0, 0);
        arrowType = ArrowType.None;
        comboBox = new ComboBox<>();
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
        arrowType = ArrowType.None;
        comboBox = new ComboBox<>();
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
        comboBox = new ComboBox<>();
    }

    @Override
    public boolean checkPointInBounds(double x, double y) {
        double m =  (end.getValue1() - start.getValue1()) / (end.getValue0() - start.getValue0());
        double b = start.getValue1() - m * start.getValue0();

        //since the lines are so thin, we'll give some wiggle room as defined by the constant
        return Math.abs(m*x + b - y) <= BOUNDARY_LEEWAY &&
                ((end.getValue0() <= x && x <= start.getValue0()) ||
                        (start.getValue0() <= x && x <= end.getValue0()));
    }

    @Override
    public void draw(GraphicsContext gc, Color color, int lineWidth) {
        gc.setLineWidth(lineWidth);
        gc.setStroke(color);
        gc.setLineDashes(arrowType.getDashedLineGap());
        gc.strokeLine(start.getValue0(), start.getValue1(), end.getValue0(), end.getValue1());
        arrowType.drawHead(gc, end, start);
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
    public VBox fetchUpdateContentsDialog() {
        //the layout has two main components: a drop down menu to select the connection type,
        // and a button to switch the direction of the connection
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        Label titleLabel = new Label("Connection type: ");
        comboBox.getItems().setAll(ArrowType.values());
        comboBox.getSelectionModel().select(arrowType);
        Button switchButton = new Button("Switch Direction");
        switchButton.setOnAction((e) -> switchDirection());
        hbox.getChildren().add(titleLabel);
        hbox.getChildren().add(comboBox);
        vbox.getChildren().add(hbox);
        vbox.getChildren().add(switchButton);
        return vbox;
    }

    @Override
    public void updateContents() {
        arrowType = comboBox.getValue();
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
}
