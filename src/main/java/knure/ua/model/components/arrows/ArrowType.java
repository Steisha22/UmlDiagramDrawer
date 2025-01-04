package knure.ua.model.components.arrows;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.javatuples.Pair;

public enum ArrowType {

    Association(null, LineStyle.SOLID_LINE),
    Dependency(new OpenArrow(), LineStyle.DASHED_LINE),
    DirectedAssociation(new OpenArrow(), LineStyle.SOLID_LINE),
    Aggregation(new DiamondHead(Color.WHITE), LineStyle.SOLID_LINE),
    Composition(new DiamondHead(Color.BLACK), LineStyle.SOLID_LINE),
    Inheritance(new ClosedArrow(), LineStyle.SOLID_LINE),
    Implementation(new ClosedArrow(), LineStyle.DASHED_LINE),
    Include(new IncludeLine(), LineStyle.DASHED_LINE),
    Extend(new ExtendLine(), LineStyle.DASHED_LINE);

    //the strategy for drawing the arrow head
    private ArrowStart arrowStart;
    //the space to be set in between line dashes (0 if line is solid)
    @Getter
    private int dashedLineGap;

    private static class LineStyle{
        private static final int DASHED_LINE = 5;
        private static final int SOLID_LINE = 0;
    }


    /**
     * Constructor
     *
     * @param arrowStart the strategy for drawing the arrow head
     */
    ArrowType(ArrowStart arrowStart, int dashedLineGap){
        this.arrowStart = arrowStart;
        this.dashedLineGap = dashedLineGap;
    }

    /**
     * draws the head on the given GraphicsContext with the specified color and line thickness
     *
     * @param gc the GraphicsContext of the canvas to draw on

     */
    public void drawHead(GraphicsContext gc, Pair<Double, Double> lastPoint, Pair<Double, Double> secondLast) {
        if(arrowStart != null){
            arrowStart.drawHead(gc, lastPoint, secondLast);
        }
    }

    public static ArrowType[] getArrowTypes4UseCaseDiagram() {
        return new ArrowType[] {Association, DirectedAssociation, Inheritance, Include, Extend};
    }

    public static ArrowType[] getArrowTypes4ClassDiagram() {
        return new ArrowType[] {Association, DirectedAssociation, Aggregation, Composition, Dependency, Inheritance, Implementation};
    }
}
