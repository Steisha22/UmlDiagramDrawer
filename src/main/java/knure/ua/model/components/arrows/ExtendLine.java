package knure.ua.model.components.arrows;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.javatuples.Pair;

/**Describes how to draw a triangle-shaped arrow head and <ExtendLine> over it*/
public class ExtendLine extends ArrowStart {
	public void drawHead(GraphicsContext gc, Pair<Double, Double> lastPoint, Pair<Double, Double> secondLast) {
		super.drawHead(gc, lastPoint, secondLast);

		//shift absolute coordinates to vector from last point
		double vectorX = secondLast.getValue0() - lastPoint.getValue0();
		double vectorY = secondLast.getValue1() - lastPoint.getValue1();

		//draw clockwise
		rotateAndDraw(gc, lastPoint, vectorX, vectorY, ROTATION_ANGLE_RADIANS);
		//draw counter-clockwise
		rotateAndDraw(gc, lastPoint, vectorX, vectorY, 2 * Math.PI - ROTATION_ANGLE_RADIANS);

		// --- Рисуем текст <Extend> ---
		// Считаем середину линии
		double midX = (lastPoint.getValue0() + secondLast.getValue0()) / 2;
		double midY = (lastPoint.getValue1() + secondLast.getValue1()) / 2;

		// Вычисляем длину линии для нормализации вектора
		double lineLength = Math.sqrt(vectorX * vectorX + vectorY * vectorY);

		// Нормализуем вектор, чтобы вычислить перпендикуляр
		double normalX = -vectorY / lineLength; // Перпендикулярный вектор X
		double normalY = vectorX / lineLength;  // Перпендикулярный вектор Y

		// Смещаем текст чуть ниже центра линии
		double textOffset = 10; // Уменьшенное расстояние над линией
		double textX = midX + normalX * textOffset;
		double textY = midY + normalY * textOffset;

		// Рисуем текст
		gc.setFill(Color.BLACK);
		gc.fillText("<<Extend>>", textX, textY);

		// Восстанавливаем исходное состояние GraphicsContext
		gc.restore();
	}
}
