package knure.ua.model.components.arrows;

import javafx.scene.canvas.GraphicsContext;
import org.javatuples.Pair;

/**Describes how to draw a triangle-shaped arrow head and <ExtendLine> over it*/
public class ExtendLine extends UseCaseArrow {
	@Override
	public void drawHead(GraphicsContext gc, Pair<Double, Double> lastPoint, Pair<Double, Double> secondLast) {
		super.drawHead(gc, lastPoint, secondLast);
	}

	@Override
	protected String arrowType() {
		return "<<Extend>>";
	}
}
