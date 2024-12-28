package knure.ua.controller;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import knure.ua.model.components.DrawableComponent;
import knure.ua.model.components.arrows.ArrowType;
import knure.ua.model.components.arrows.ResizableLine;
import knure.ua.model.components.shapes.Actor;
import knure.ua.model.components.shapes.BoxComponent;
import knure.ua.model.components.shapes.UseCase;
import org.javatuples.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

			System.out.println("Report created successfully: " + file.getAbsolutePath());
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
			Paragraph actorHeader = new Paragraph("Role: " + actor.getTitle())
					.setFontSize(18)
					.setBold();
			document.add(actorHeader);

			Paragraph actorDescription = new Paragraph("Use Cases:")
					.setFontSize(14);
			document.add(actorDescription);

			// Перечисление use cases, связанных с актёром
			for (UseCase useCase : useCases) {
				if (isLinked(actor, useCase, relationships)) {
					document.add(new Paragraph(" - " + useCase.getTitle()));
				}
			}

			// Обработка наследования
			Actor parentActor = getParentActor(actor, relationships);
			if (parentActor != null) {
				document.add(new Paragraph("Inherited from: " + parentActor.getTitle()));
			}
		}

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

//	private boolean isLinked(Actor actor, UseCase useCase, List<ResizableLine> relationships) {
//		for (ResizableLine line : relationships) {
//			// Сравнение координат актёра и use case
//			if (line.getStart().equals(new Pair<>(actor.getCenterX(), actor.getCenterY())) &&
//					line.getEnd().equals(new Pair<>(useCase.getCenterX(), useCase.getCenterY()))) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	private Actor getParentActor(Actor actor, List<ResizableLine> relationships) {
//		for (ResizableLine line : relationships) {
//			if (line.getArrowType().equals(ArrowType.Inheritance) &&
//					line.getEnd().equals(new Pair<>(actor.getCenterX(), actor.getCenterY()))) {
//				return (Actor) findComponentByCoordinates(line.getStart(), relationships);
//			}
//		}
//		return null;
//	}

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

		// Если элемент - прямоугольник
		if (element instanceof Actor || element instanceof UseCase) {
			double left = element.getCenterX() - (element.getWidth() / 2) - buffer;
			double right = element.getCenterX() + (element.getWidth() / 2) + buffer;
			double top = element.getCenterY() - (element.getHeight() / 2) - buffer;
			double bottom = element.getCenterY() + (element.getHeight() / 2) + buffer;

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

	private Actor getParentActor(Actor actor, List<ResizableLine> relationships) {
		for (ResizableLine line : relationships) {
			if (line.getArrowType().equals(ArrowType.Inheritance) &&
					isPointInsideElement(line.getEnd(), actor)) {
				return (Actor) findComponentByCoordinates(line.getStart(), relationships);
			}
		}
		return null;
	}

	private String getLinkedTitle(Pair<Double, Double> center, List<? extends BoxComponent> components) {
		for (BoxComponent component : components) {
			if (new Pair<>(component.getCenterX(), component.getCenterY()).equals(center)) {
				return component.getTitle();
			}
		}
		return "Unknown";
	}

	private BoxComponent findComponentByCoordinates(Pair<Double, Double> coordinates, List<? extends BoxComponent> components) {
		for (BoxComponent component : components) {
			if (new Pair<>(component.getCenterX(), component.getCenterY()).equals(coordinates)) {
				return component;
			}
		}
		return null;
	}
}
