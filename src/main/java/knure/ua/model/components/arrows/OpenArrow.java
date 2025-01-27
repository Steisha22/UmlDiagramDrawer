package knure.ua.model.components.arrows;

import javafx.scene.canvas.GraphicsContext;
import org.javatuples.Pair;

/**Describes how to draw an open-ended arrowhead*/
public class OpenArrow extends ArrowStart {
    protected double vectorX;
    protected double vectorY;
    @Override
    public void drawHead(GraphicsContext gc, Pair<Double, Double> lastPoint, Pair<Double, Double> secondLast) {
        super.drawHead(gc, lastPoint, secondLast);

        //shift absolute coordinates to vector from last point
        vectorX = secondLast.getValue0() - lastPoint.getValue0();
        vectorY = secondLast.getValue1() - lastPoint.getValue1();

        //draw clockwise
        rotateAndDraw(gc, lastPoint, vectorX, vectorY, ROTATION_ANGLE_RADIANS);
        //draw counter-clockwise
        rotateAndDraw(gc, lastPoint, vectorX, vectorY, 2 * Math.PI - ROTATION_ANGLE_RADIANS);
    }
}
