package knure.ua.model.components.arrows;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.javatuples.Pair;

public class UseCaseArrow extends OpenArrow {

	@Override
	public void drawHead(GraphicsContext gc, Pair<Double, Double> lastPoint, Pair<Double, Double> secondLast) {
		super.drawHead(gc, lastPoint, secondLast);

		// Считаем середину линии
		double midX = (lastPoint.getValue0() + secondLast.getValue0()) / 2;
		double midY = (lastPoint.getValue1() + secondLast.getValue1()) / 2;

		// Вычисляем вектор между точками
		double vectorX = lastPoint.getValue0() - secondLast.getValue0();
		double vectorY = lastPoint.getValue1() - secondLast.getValue1();

		// Вычисляем длину линии для нормализации вектора
		double lineLength = Math.sqrt(vectorX * vectorX + vectorY * vectorY);

		// Нормализуем вектор, чтобы вычислить перпендикуляр
		double normalX = -vectorY / lineLength; // Перпендикулярный вектор X
		double normalY = vectorX / lineLength;  // Перпендикулярный вектор Y

		// Проверяем направление линии и корректируем смещение текста
		double textOffset = 10; // Базовое расстояние над линией
		if (vectorY > 0) { // Если линия направлена вверх
			textOffset = -textOffset; // Смещаем текст ниже линии
		}

		double textX = midX + normalX * textOffset;
		double textY = midY + normalY * textOffset;

		// Рисуем текст
		gc.setFill(Color.BLACK);
		gc.fillText(arrowType(), textX, textY);

		// Восстанавливаем исходное состояние GraphicsContext
		gc.restore();
	}


	protected String arrowType() {
		return "";
	}
}
