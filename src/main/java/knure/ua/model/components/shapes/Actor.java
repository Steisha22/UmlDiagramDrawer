package knure.ua.model.components.shapes;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import knure.ua.model.components.DrawableComponent;

public class Actor extends BoxComponent {
    private static final int DEFAULT_HEIGHT = 50;
    private static final int DEFAULT_WIDTH = 100;

    public Actor() {
        super("Actor", 0, 0, DEFAULT_HEIGHT, DEFAULT_WIDTH);
    }

    public Actor(String title, double centerX, double centerY, double height, double width) {
        super(title, centerX, centerY, height, width);
    }

    @Override
    protected double getTitleYCoord() {
        final Text throwaway = new Text(title);
        new Scene(new Group(throwaway));
        double headBottomY = centerY + 6;  // centerY + радиус головы
        return headBottomY + 12 + (throwaway.getLayoutBounds().getHeight() / 2); // 12 - это расстояние от ног до текста
    }

    @Override
    public DrawableComponent createCopy() {
        return new Actor(this.title, this.centerX, this.centerY, this.height, this.width);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Actor actor = (Actor) o;  // Используем Actor вместо UseCase, так как это другой класс
        return title.equals(actor.title) && centerX == actor.centerX && centerY == actor.centerY;
    }

    @Override
    public void draw(GraphicsContext gc, Color color, int lineWidth) {
        // Контейнер для перетаскивания и изменения размера
        double containerWidth = width + 20;
        double containerHeight = height + 20;
        double containerX = centerX - containerWidth / 2;
        double containerY = centerY - containerHeight / 2;

        // Нарисуем невидимый контейнер (для изменения размеров)
        gc.setStroke(Color.TRANSPARENT); // Невидимый контур
        gc.strokeRect(containerX, containerY, containerWidth, containerHeight);

        // Нарисуем самого актера
        drawActor(gc, containerX + containerWidth / 2, containerY + containerHeight / 2);
    }

    // Метод для рисования самого актера
    private void drawActor(GraphicsContext gc, double centerX, double centerY) {
        // 1. Нарисуем голову актера (круг)
        double headRadius = 6.0;       // Радиус головы
        double headCenterX = centerX;  // Позиция головы по оси X
        double headCenterY = centerY;  // Позиция головы по оси Y
        gc.setFill(Color.web("#3694ec"));  // Цвет головы (такой же как в FXML)
        gc.setStroke(Color.BLACK);
        gc.strokeOval(headCenterX - headRadius, headCenterY - headRadius, headRadius * 2, headRadius * 2);
        gc.fillOval(headCenterX - headRadius, headCenterY - headRadius, headRadius * 2, headRadius * 2);  // Рисуем голову
         // Цвет обводки головы

        // 2. Нарисуем туловище (вертикальная линия вниз)
        double torsoStartX = headCenterX;
        double torsoStartY = headCenterY + headRadius;
        double torsoEndY = torsoStartY + 20;  // Длина туловища
        gc.strokeLine(torsoStartX, torsoStartY, torsoStartX, torsoEndY);

        // 3. Нарисуем ноги (две диагональные линии)
        double legLength = 12.0;  // Длина ног
        gc.strokeLine(torsoStartX, torsoEndY, torsoStartX - legLength, torsoEndY + legLength);  // Левая нога
        gc.strokeLine(torsoStartX, torsoEndY, torsoStartX + legLength, torsoEndY + legLength);  // Правая нога

        // 4. Нарисуем руки (горизонтальная линия чуть ниже головы)
        double armsY = headCenterY - headRadius + 20;  // Расстояние между головой и руками (10 пикселей ниже головы)
        double armLength = 14.0;  // Длина рук
        gc.strokeLine(headCenterX - armLength, armsY, headCenterX + armLength, armsY);  // Руки

        // 5. Отображение названия (имени) актера под ногами
        final Text throwaway = new Text(title);
        new Scene(new Group(throwaway));
        gc.setFill(Color.BLACK);
        gc.fillText(title, headCenterX - (throwaway.getLayoutBounds().getWidth() / 2), torsoEndY + legLength + 10);

        gc.strokeLine(headCenterX - armLength, armsY, headCenterX + armLength, armsY);  // Руки
    }

    @Override
    protected String getPathToDialogFxml() {
        return "F:\\Prodjects\\UmlDiagramDrawer\\src\\main\\resources\\knure\\ua\\actorDialog.fxml";
    }

}
