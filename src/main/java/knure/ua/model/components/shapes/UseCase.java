package knure.ua.model.components.shapes;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import knure.ua.model.components.DrawableComponent;

public class UseCase extends BoxComponent {

    private static final int DEFAULT_HEIGHT = 50;
    private static final int DEFAULT_WIDTH = 100;

    public UseCase(){
        super("Use Case", 0, 0, DEFAULT_HEIGHT, DEFAULT_WIDTH);
    }

    public UseCase(String title, double centerX, double centerY, double height, double width) {
        super(title, centerX, centerY, height, width);
    }

    @Override
    protected double getTitleYCoord(){
        final Text throwaway = new Text(title);
        new Scene(new Group(throwaway));
        return centerY + (throwaway.getLayoutBounds().getHeight()/4);
    }

    @Override
    public DrawableComponent createCopy() {
        return new UseCase(this.title, this.centerX, this.centerY, this.height, this.width);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UseCase that = (UseCase) o;
        return title.equals(that.title) && centerX == that.getCenterX() && centerY == that.centerY
                && height == that.height && width == that.width;
    }

    @Override
    public void draw(GraphicsContext gc, Color color, int lineWidth) {
        double startX = centerX - (width / 2);
        double startY = centerY - (height / 2);
        gc.setStroke(color);
        gc.setLineWidth(lineWidth);
        gc.setLineDashes(0);

        gc.setFill(Color.DODGERBLUE);
        gc.fillOval(startX, startY, width, height);
        gc.strokeOval(startX, startY, width, height);

        final Text name = new Text(title);
        new Scene(new Group(name));
        gc.setFill(Color.BLACK);
        gc.fillText(title, centerX - (name.getLayoutBounds().getWidth() / 2), getTitleYCoord());
    }
}
