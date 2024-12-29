package knure.ua.model.components.shapes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import knure.ua.model.components.DrawableComponent;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.net.URL;

/**Represents a class box that contains three sections: for the class title, fields, and methods*/
@Getter
@Setter
public class ClassShape extends BoxComponent {
	@Getter @Setter
	private TextField fieldName;

	@Getter @Setter
	private TextField methodName;

	@Getter @Setter
	private TextArea fieldsArea;

	@Getter @Setter
	private TextArea methodsArea;

	@Getter(lombok.AccessLevel.NONE) @Setter(lombok.AccessLevel.NONE)
	private ComboBox<AccessLevel> fieldAccessLevel;

	@Getter(lombok.AccessLevel.NONE) @Setter(lombok.AccessLevel.NONE)
	private ComboBox<AccessLevel> methodAccessLevel;

	@Getter(lombok.AccessLevel.NONE) @Setter(lombok.AccessLevel.NONE)
	private ComboBox<ClassStereotype> stereotype;

	private String fields;
	private String methods;
	private ClassStereotype classStereotype;

	//default sizes for newly created components
	private static final int DEFAULT_HEIGHT = 80;
	private static final int DEFAULT_WIDTH = 100;

	/**Constructor*/
    public ClassShape() {
		super("Class", 0, 0, DEFAULT_HEIGHT, DEFAULT_WIDTH);
		fields = "";
		methods = "";
		classStereotype = ClassStereotype.None;

		fieldName = new TextField("");
		methodName = new TextField("");

		fieldAccessLevel = new ComboBox<>();
		methodAccessLevel = new ComboBox<>();
		stereotype = new ComboBox<>();
	}

	/**
	 * Constructor
	 *
	 * @param title the title to write on the component
	 * @param centerX the x coordinate in the center of the object to draw
	 * @param centerY the y coordinate in the center of the object to draw
	 * @param height  the height of the box
	 * @param width the width of the box
	 * @param fields the contents to write into the second section
	 * @param methods the contents to write into the third section
	 */
	public ClassShape(String title, String fields, String methods, ClassStereotype classStereotype, double centerX, double centerY, double height, double width) {
		super(title, centerX, centerY, height, width);
		this.fields = fields;
		this.methods = methods;
		this.classStereotype = classStereotype;

		fieldName = new TextField("");
		methodName = new TextField("");

		fieldAccessLevel = new ComboBox<>();
		methodAccessLevel = new ComboBox<>();
		stereotype = new ComboBox<>();
	}

//	@Override
//	public void draw(GraphicsContext gc, Color color, int lineWidth) {
//		// get the top left of the class box to draw
//		double startX = centerX - (width / 2);
//		double startY = centerY - (height / 2);
//
//		// draw outside box
//		super.draw(gc, color, lineWidth);
//
//		// if stereotype exist, draw it above the class name
//		if (classStereotype != null && !classStereotype.getStereotype().isEmpty()) {
//			drawStereotype(gc, startX, startY);  // draw stereotype
////			startY += 10;  // Сдвигаем Y на высоту стереотипа
//		}
//
//		// draw first divider and field text
//		double topOfFieldSection = drawDividerAndText(gc, title, fields, startX, startY);
//		drawDividerAndText(gc, fields, methods, startX, topOfFieldSection);
//	}

	@Override
	public void draw(GraphicsContext gc, Color color, int lineWidth) {
		// Получаем координаты для рисования
		double startX = centerX - (width / 2);
		double startY = centerY - (height / 2);

		gc.setStroke(color);
		gc.setLineWidth(lineWidth);
		gc.setLineDashes(0);
		gc.strokeLine(startX, startY, startX, startY + height);
		gc.strokeLine(startX, startY + height, startX + width, startY + height);
		gc.strokeLine(startX + width, startY + height, startX + width, startY);
		gc.strokeLine(startX + width, startY, startX, startY);

		final Text throwaway = new Text(title);
		new Scene(new Group(throwaway));
		double startYTitle = getTitleYCoord();

		double heightOfPreviousSection = throwaway.getLayoutBounds().getHeight();

		// Если задан стереотип, рисуем его над названием класса
		if (classStereotype != null && !classStereotype.getStereotype().isEmpty()) {
			double stereotypeHeight = drawStereotype(gc, startX, startY);  // Рисуем стереотип
			startYTitle = startYTitle + stereotypeHeight + 2;
			heightOfPreviousSection += stereotypeHeight;
		}

		gc.setFill(Color.BLACK);
		gc.fillText(title, centerX - (throwaway.getLayoutBounds().getWidth() / 2), startYTitle);

		// draw first divider and field text
		double topOfFieldSection = drawDividerAndText(gc, heightOfPreviousSection, fields, startX, startY);
		drawDividerAndText(gc, fields, methods, startX, topOfFieldSection);
	}

	/**
	 * draws a stereotype above the class name if it exists
	 *
	 * @param gc the GraphicsContext of the canvas to draw on
	 * @param startX the x coordinate of the left side of the component
	 * @param startY the y coordinate of the top of the component
	 */
	private double drawStereotype(GraphicsContext gc, double startX, double startY) {
		Text stereotypeName = new Text(classStereotype.getStereotype());
		new Scene(new Group(stereotypeName)); // Вычисляем размеры текста
		double stereotypeWidth = stereotypeName.getLayoutBounds().getWidth();
		double stereotypeHeight = stereotypeName.getLayoutBounds().getHeight();

		gc.setFill(Color.BLACK);
		gc.fillText(classStereotype.getStereotype(),centerX - (stereotypeWidth / 2), getTitleYCoord());
		return stereotypeHeight;
//		gc.fillText(classStereotype.getStereotype(), startX + (width / 2) - (stereotypeWidth / 2), startY - stereotypeHeight);
	}


	private double drawDividerAndText(GraphicsContext gc, String textInPreviousSection, String textToDraw,
									  double startX, double topOfPreviousSection) {
		Text throwaway = new Text(textInPreviousSection);
		new Scene(new Group(throwaway));
		return drawDividerAndText(gc, throwaway.getLayoutBounds().getHeight(), textToDraw, startX, topOfPreviousSection);
	}

	/**
	 * draws a horizontal line as a section divider and draws the text for that section
	 *
	 * @param gc the GraphicsContext of the canvas to draw on
	 * @param heightOfPreviousSection the height of the previous section to calculate offset for divider
	 * @param textToDraw the text to draw
	 * @param startX the x coordinate of the left side of the component
	 * @param topOfPreviousSection the y coordinate of the top of the previous section of the component
	 * @return th y coorinate of the top of the newly drawn section
	 */
	private double drawDividerAndText(GraphicsContext gc, double heightOfPreviousSection, String textToDraw,
									  double startX, double topOfPreviousSection){
		double topOfThisSection = topOfPreviousSection + heightOfPreviousSection + 10;
		//only draw the divider and text if the start of the section would be before the bottom of the component
		if(topOfThisSection < centerY - (height / 2) + height) {
			gc.strokeLine(startX, topOfThisSection, startX + width, topOfThisSection);
			gc.fillText(textToDraw, startX + 10, topOfThisSection + 17);
		}
		return topOfThisSection;
	}


//	private void drawStereotype(GraphicsContext gc, double startX, double startY) {
//		Text stereotypeName = new Text(classStereotype.getStereotype());
//		new Scene(new Group(stereotypeName)); // Вычисляем размеры текста
//		double stereotypeWidth = stereotypeName.getLayoutBounds().getWidth();
//		double stereotypeHeight = stereotypeName.getLayoutBounds().getHeight();
//
//		// Стереотип рисуем выше, корректируя Y
//		gc.setFill(Color.BLACK);
//		gc.fillText(classStereotype.getStereotype(), startX + (width / 2) - (stereotypeWidth / 2), startY - stereotypeHeight - 5);
//	}
//	private void drawStereotype(GraphicsContext gc, double startX, double startY) {
//		Text stereotypeName = new Text(classStereotype.getStereotype());
//		new Scene(new Group(stereotypeName)); // Вычисляем размеры текста
//		double stereotypeWidth = stereotypeName.getLayoutBounds().getWidth();
//		double stereotypeHeight = stereotypeName.getLayoutBounds().getHeight();
//
//		gc.setFill(Color.BLACK);
//		gc.fillText(classStereotype.getStereotype(), startX + (width / 2) - (stereotypeWidth / 2), startY - stereotypeHeight);
//	}

	@Override
	protected double getTitleYCoord(){
		final Text throwaway = new Text(title);
		new Scene(new Group(throwaway));
		return centerY - (height / 2) + throwaway.getLayoutBounds().getHeight();
	}

	@Override
	public DrawableComponent createCopy() {
		return new ClassShape(this.title, this.fields, this.methods, this.classStereotype, this.centerX, this.centerY, this.height, this.width);
	}

	@Override
	public void updateContents() {
		super.updateContents();
		fields = fieldsArea.getText().trim();
		methods = methodsArea.getText().trim();
		classStereotype = stereotype.getValue();
	}

	public void addNewField() {
		fields = fields + "\n" + fieldAccessLevel.getValue().getAccessLevel() + " " + fieldName.getText().trim();
	}

	public void addNewMethod() {
		methods = methods + "\n" + methodAccessLevel.getValue().getAccessLevel() + " " + methodName.getText().trim();
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClassShape that = (ClassShape) o;
		return title.equals(that.title) && fields.equals(that.getFields()) && methods.equals(that.getMethods())
				&& centerX == that.getCenterX() && centerY == that.centerY && height == that.height
				&& width == that.width;
	}

	@Override
	public TitledPane loadDialog() {
		try {
			URL url = new File(getPathToDialogFxml()).toURI().toURL();
			TitledPane titledPane = FXMLLoader.load(url);
			AnchorPane contentPane = (AnchorPane) titledPane.getContent();

			for (Node node : contentPane.getChildren()) {
				if (node instanceof ComboBox && "stereotype".equals(node.getId())) {
					stereotype = (ComboBox<ClassStereotype>) node;
					stereotype.getItems().setAll(ClassStereotype.values());
					stereotype.getSelectionModel().select(classStereotype);
				} else if (node instanceof TextField && "titleTextField".equals(node.getId())) {
					titleTextField = (TextField) node;
					titleTextField.setText(getTitle());
				} else if (node instanceof TextArea && "fieldsArea".equals(node.getId())) {
					fieldsArea = (TextArea) node;
					fieldsArea.setText(fields);
				} else if (node instanceof TextArea && "methodsArea".equals(node.getId())) {
					methodsArea = (TextArea) node;
					methodsArea.setText(methods);
				}
//				else if (node instanceof Button && "addNewFieldButton".equals(node.getId())) {
//					Button addNewFieldButton = (Button) node;
//					addNewFieldButton.setOnAction((e) -> openAddNewFieldDialog());
//				} else if (node instanceof Button && "addNewMethodButton".equals(node.getId())) {
//					Button addNewMethodButton = (Button) node;
//					addNewMethodButton.setOnAction((e) -> openAddNewMethodDialog());
//				}
			}
			return titledPane;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new TitledPane();
	}

	@Override
	protected String getPathToDialogFxml() {
		return "F:\\Prodjects\\UmlDiagramDrawer\\src\\main\\resources\\knure\\ua\\classDialog.fxml";
	}

	public void openAddNewFieldDialog(Stage parentDialog) {
		// Создаем новый диалог для добавления поля
		Stage newFieldDialog = new Stage();
		newFieldDialog.initModality(Modality.APPLICATION_MODAL);

		// Загрузка FXML для нового диалога
		try {
			URL url = new File(getPathToDialogFxml()).toURI().toURL();
			TitledPane titledPane = FXMLLoader.load(url);
			AnchorPane contentPane = (AnchorPane) titledPane.getContent();

			// Извлекаем элементы для работы с полем
			TextField newFieldName = (TextField) contentPane.lookup("#fieldName");
			ComboBox<AccessLevel> newFieldAccessLevel = (ComboBox<AccessLevel>) contentPane.lookup("#fieldAccessLevel");

			// Кнопка для добавления нового поля
			Button addNewFieldButton = (Button) contentPane.lookup("#saveNewField");
			addNewFieldButton.setOnAction(event -> {
				// Добавляем новое поле
				addNewField(newFieldName, newFieldAccessLevel);
				newFieldDialog.close();
				// После закрытия нового диалога, возвращаемся к первому диалогу
				parentDialog.show();
			});

			// Устанавливаем сцену и показываем новый диалог
			Scene dialogScene = new Scene(contentPane);
			newFieldDialog.setScene(dialogScene);
			newFieldDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addNewField(TextField fieldName, ComboBox<AccessLevel> accessLevelComboBox) {
		// Логика добавления нового поля в класс
		String field = accessLevelComboBox.getValue().getAccessLevel() + " " + fieldName.getText().trim();
		fields += "\n" + field;
		// Обновление содержимого в текстовом поле с полями класса
		fieldsArea.setText(fields);
	}


}
