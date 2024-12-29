package knure.ua.controller;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import javafx.scene.control.Alert;
import knure.ua.model.components.DrawableComponent;
import knure.ua.model.components.arrows.ArrowType;
import knure.ua.model.components.arrows.ResizableLine;
import knure.ua.model.components.shapes.Actor;
import knure.ua.model.components.shapes.BoxComponent;
import knure.ua.model.components.shapes.ClassShape;
import knure.ua.model.components.shapes.UseCase;
import org.javatuples.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PdfReportController {
	private CanvasContentManagementController canvasContentManagementController;

	public PdfReportController(CanvasContentManagementController canvasContentManagementController) {
		this.canvasContentManagementController = canvasContentManagementController;
	}

	public void generateUseCaseReport() {
		try {
			// Генерация уникального имени файла
			String fileName = "Use Case Diagram Report " + UUID.randomUUID() + ".pdf";
			File file = new File(fileName);

			List<Actor> actors = getActorsFromCanvas();
			List<UseCase> useCases = getUseCasesFromCanvas();
			List<ResizableLine> relationships = getRelationshipsFromCanvas();

			generateUseCaseReport(file, actors, useCases, relationships);

			String message = "Report created successfully: " + file.getAbsolutePath();
			System.out.println(message);
			showSuccessAlert(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<Actor> getActorsFromCanvas() {
		List<Actor> actors = new ArrayList<>();
		for (DrawableComponent component : canvasContentManagementController.getDrawnComponents()) {
			if (component instanceof Actor) {
				actors.add((Actor) component);
			}
		}
		return actors;
	}

	private List<UseCase> getUseCasesFromCanvas() {
		List<UseCase> useCases = new ArrayList<>();
		for (DrawableComponent component : canvasContentManagementController.getDrawnComponents()) {
			if (component instanceof UseCase) {
				useCases.add((UseCase) component);
			}
		}
		return useCases;
	}

	private List<ResizableLine> getRelationshipsFromCanvas() {
		List<ResizableLine> relationships = new ArrayList<>();
		for (DrawableComponent component : canvasContentManagementController.getDrawnComponents()) {
			if (component instanceof ResizableLine) {
				relationships.add((ResizableLine) component);
			}
		}
		return relationships;
	}

	public void generateUseCaseReport(File file, List<Actor> actors, List<UseCase> useCases, List<ResizableLine> relationships) throws IOException {
		// Создание PDF-документа
		PdfWriter writer = new PdfWriter(file);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);

		// Добавление заголовка
		Text titleText = new Text("Use Case Report")
				.setFontSize(24)
				.setBold()
				.setFontColor(new DeviceRgb(0, 102, 204));
		Paragraph title = new Paragraph().add(titleText).setTextAlignment(TextAlignment.CENTER);
		document.add(title);

		// Информация о каждом актёре
		for (Actor actor : actors) {
			// Разделительная линия
			document.add(new Paragraph("\n").setBorderTop(new SolidBorder(new DeviceRgb(200, 200, 200), 1)));
			Paragraph actorHeader = new Paragraph("Role: " + actor.getTitle())
					.setFontSize(18)
					.setBold();
			document.add(actorHeader);

			Paragraph actorDescription = new Paragraph("Use Cases:")
					.setFontSize(14);
			document.add(actorDescription);

			// Перечисление use cases, связанных с актёром
			addUseCases4Actor(document, actor, relationships, useCases);

			// Рекурсивное добавление унаследованных функций
			addInheritedUseCases(document, actor, relationships, useCases, actors.stream().filter(a -> !a.equals(actor)).collect(Collectors.toList()));
		}

		// Разделительная линия
		document.add(new Paragraph("\n").setBorderTop(new SolidBorder(new DeviceRgb(200, 200, 200), 1)));
		// Информация о зависимостях Use Case
		Paragraph dependenciesHeader = new Paragraph("Relationships between Use Cases:")
				.setFontSize(18)
				.setBold();
		document.add(dependenciesHeader);

		for (ResizableLine line : relationships) {
			if (line.getArrowType().equals(ArrowType.Include) || line.getArrowType().equals(ArrowType.Extend)) {
				String dependency = line.getArrowType().toString() + ": " +
						getLinkedTitle(line.getStart(), useCases) + " -> " +
						getLinkedTitle(line.getEnd(), useCases);
				document.add(new Paragraph(dependency));
			}
		}

		// Закрытие документа
		document.close();
	}

	private void addUseCases4Actor(Document document, Actor currentActor, List<ResizableLine> relationships, List<UseCase> useCases) {
		for (UseCase useCase : useCases) {
			if (isLinked(currentActor, useCase, relationships)) {
				document.add(new Paragraph(" - " + useCase.getTitle()));
			}
		}
	}

	private void addInheritedUseCases(Document document, Actor currentActor, List<ResizableLine> relationships, List<UseCase> useCases, List<Actor> otherActors) {
		Actor parentActor = getParentActor(currentActor, relationships, otherActors);

		if (parentActor != null) {
			document.add(new Paragraph("Inherited from: " + parentActor.getTitle()));

			// Перечисляем use cases, связанные с родительским актёром
			addUseCases4Actor(document, parentActor, relationships, useCases);

			// Рекурсивно обрабатываем следующего родителя
			addInheritedUseCases(document, parentActor, relationships, useCases, otherActors.stream().filter(a -> !a.equals(parentActor)).collect(Collectors.toList()));
		}
	}


	private boolean isLinked(BoxComponent element, UseCase useCase, List<ResizableLine> relationships) {
		for (ResizableLine line : relationships) {
			Pair<Double, Double> start = line.getStart();
			Pair<Double, Double> end = line.getEnd();

			// Проверяем, начинается ли линия из зоны элемента и заканчивается в зоне Use Case
			if (isPointInsideElement(start, element) && isPointInsideElement(end, useCase)) {
				return true;
			}
		}
		return false;
	}

	private boolean isPointInsideElement(Pair<Double, Double> point, BoxComponent element) {
		double x = point.getValue0();
		double y = point.getValue1();

		// Задаём буфер (погрешность)
		double buffer = 10.0;

		// Фиксированная высота текста под актёром
		double textHeight = 20.0;

		// Если элемент - прямоугольник
		if (element instanceof Actor) {
			double left = element.getCenterX() - (element.getWidth() / 2) - buffer;
			double right = element.getCenterX() + (element.getWidth() / 2) + buffer;
			double top = element.getCenterY() - (element.getHeight() / 2) - buffer;
			double bottom = element.getCenterY() + (element.getHeight() / 2) + buffer + textHeight;

			// Проверка попадания точки в увеличенный прямоугольник
			return x >= left && x <= right && y >= top && y <= bottom;
		}

		// Если элемент - эллипс
		if (element instanceof UseCase) {
			double a = (element.getWidth() / 2) + buffer; // Большая полуось
			double b = (element.getHeight() / 2) + buffer; // Малая полуось
			double centerX = element.getCenterX();
			double centerY = element.getCenterY();

			// Проверяем уравнение эллипса
			return Math.pow(x - centerX, 2) / Math.pow(a, 2) + Math.pow(y - centerY, 2) / Math.pow(b, 2) <= 1;
		}

		// Для других типов компонентов можно добавить дополнительные проверки (например, эллипс).
		return false;
	}

	private Actor getParentActor(Actor currentActor, List<ResizableLine> relationships, List<Actor> otherActors) {
		for (ResizableLine line : relationships) {
			// Проверяем, начинается ли линия внутри текущего актёра и является ли она типа Inheritance
			if (line.getArrowType().equals(ArrowType.Inheritance) && isPointInsideElement(line.getStart(), currentActor)) {
				// Проверяем, заканчивается ли линия в пределах другого актёра
				for (Actor actor : otherActors) {
					if (isPointInsideElement(line.getEnd(), actor)) {
						return actor;
					}
				}
			}
		}
		return null;
	}

	private String getLinkedTitle(Pair<Double, Double> center, List<? extends BoxComponent> components) {
		for (BoxComponent component : components) {
			if (isPointInsideElement(center, component)) {
				return component.getTitle();
			}
		}
		return "Unknown";
	}

	private void showSuccessAlert(String message){
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText(message);
		alert.showAndWait();
	}


	public void generateClassDiagramReport() {
		try {
			String fileName = "Class Diagram Report " + UUID.randomUUID() + ".pdf";
			File file = new File(fileName);

			List<ClassShape> classes = getClassesFromCanvas();
			List<ResizableLine> relationships = getRelationshipsFromCanvas();

			generateClassDiagramReport(file, classes, relationships);

			String message = "Report created successfully: " + file.getAbsolutePath();
			System.out.println(message);
			showSuccessAlert(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<ClassShape> getClassesFromCanvas() {
		List<ClassShape> classes = new ArrayList<>();
		for (DrawableComponent component : canvasContentManagementController.getDrawnComponents()) {
			if (component instanceof ClassShape) {
				classes.add((ClassShape) component);
			}
		}
		return classes;
	}

	public void generateClassDiagramReport(File file, List<ClassShape> classes, List<ResizableLine> relationships) throws IOException {
		// Создание PDF-документа
		PdfWriter writer = new PdfWriter(file);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);

		// Добавление заголовка
		Text titleText = new Text("Class Diagram Report")
				.setFontSize(24)
				.setBold()
				.setFontColor(new DeviceRgb(0, 102, 204));
		Paragraph title = new Paragraph().add(titleText).setTextAlignment(TextAlignment.CENTER);
		document.add(title);

		// Информация о каждом классе
		for (ClassShape classShape : classes) {
			addClassSection(document, classShape, classes, relationships);
		}

		// Закрытие документа
		document.close();
	}

	private void addClassSection(Document document, ClassShape classShape, List<ClassShape> allClasses, List<ResizableLine> relationships) {
		// Разделительная линия
		document.add(new Paragraph("\n").setBorderTop(new SolidBorder(new DeviceRgb(200, 200, 200), 1)));

		// Название секции - имя класса
		Paragraph classHeader = new Paragraph("Class: " + classShape.getTitle())
				.setFontSize(18)
				.setBold();
		document.add(classHeader);

		// Добавление стереотипа
		if (classShape.getClassStereotype() != null && !classShape.getClassStereotype().getStereotype().isEmpty()) {
			document.add(new Paragraph("Stereotype: " + classShape.getClassStereotype().getStereotype())
					.setFontSize(14));
		}

		// Поля класса
		document.add(new Paragraph("Fields:").setFontSize(14).setBold());
		addFieldsOrMethods(document, classShape.getFields());

		// Методы класса
		document.add(new Paragraph("Methods:").setFontSize(14).setBold());
		addFieldsOrMethods(document, classShape.getMethods());

		// Рекурсивное добавление наследованных полей и методов
		addInheritedFieldsAndMethods(document, classShape, allClasses.stream().filter(c -> !c.equals(classShape)).collect(Collectors.toList()), relationships);
	}

	private void addFieldsOrMethods(Document document, String content) {
		if (content == null || content.isBlank()) {
			document.add(new Paragraph(" • None").setFontSize(12));
			return;
		}

		String[] lines = content.split("\n");
		for (String line : lines) {
			document.add(new Paragraph(" • " + line).setFontSize(12));
		}
	}

	private void addInheritedFieldsAndMethods(Document document, ClassShape currentClass, List<ClassShape> allClasses, List<ResizableLine> relationships) {
		ClassShape parentClass = getParentClass(currentClass, relationships, allClasses);

		if (parentClass != null) {
			document.add(new Paragraph("Inherited from: " + parentClass.getTitle()).setFontSize(14).setBold());

			// Добавляем унаследованные поля
			document.add(new Paragraph("Fields:").setFontSize(14).setBold());
			addFieldsOrMethods(document, parentClass.getFields());

			// Добавляем унаследованные методы
			document.add(new Paragraph("Methods:").setFontSize(14).setBold());
			addFieldsOrMethods(document, parentClass.getMethods());

			// Рекурсивный вызов для следующего уровня
			addInheritedFieldsAndMethods(document, parentClass, allClasses, relationships);
		}
	}

	private ClassShape getParentClass(ClassShape currentClass, List<ResizableLine> relationships, List<ClassShape> allClasses) {
		for (ResizableLine line : relationships) {
			if (line.getArrowType().equals(ArrowType.Inheritance) && isPointInsideClassElement(line.getStart(), currentClass)) {
				for (ClassShape potentialParent : allClasses) {
					if (isPointInsideClassElement(line.getEnd(), potentialParent)) {
						return potentialParent;
					}
				}
			}
		}
		return null;
	}

	private boolean isPointInsideClassElement(Pair<Double, Double> point, BoxComponent element) {
		double x = point.getValue0();
		double y = point.getValue1();

		// Задаём буфер (погрешность)
		double buffer = 10.0;

		double left = element.getCenterX() - (element.getWidth() / 2) - buffer;
		double right = element.getCenterX() + (element.getWidth() / 2) + buffer;
		double top = element.getCenterY() - (element.getHeight() / 2) - buffer;
		double bottom = element.getCenterY() + (element.getHeight() / 2) + buffer;

		return x >= left && x <= right && y >= top && y <= bottom;
	}

}
