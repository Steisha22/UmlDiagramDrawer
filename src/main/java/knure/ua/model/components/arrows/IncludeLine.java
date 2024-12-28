package knure.ua.model.components.arrows;

import javafx.scene.canvas.GraphicsContext;
import org.javatuples.Pair;

/**Describes how to draw a triangle-shaped arrow head and <Include> over it*/
public class IncludeLine extends UseCaseArrow {
	@Override
	public void drawHead(GraphicsContext gc, Pair<Double, Double> lastPoint, Pair<Double, Double> secondLast) {
		super.drawHead(gc, lastPoint, secondLast);
	}

	@Override
	protected String arrowType() {
		return "<<Include>>";
	}

}
