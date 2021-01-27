import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGGraphicsElement;
import org.apache.batik.anim.dom.SVGOMLineElement;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMPolylineElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;

import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Slider;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

public class CanvasViewer extends PApplet {
	// Develop in Eclipse following
	// https://happycoding.io/tutorials/java/processing-in-java
	// Note: Batik uses a really old version of the W3C Document APIs:
	// https://stackoverflow.com/questions/13676937/how-to-find-package-org-w3c-dom-svg

	private int windowX;
	private int windowY;
	private static final int SLIDER_HEIGHT = 100;
	private static final int SLIDER_MARGIN = 20;
	private static final int PANEL_WIDTH = 200;
	private static final int CANVAS_MARGIN = 50;
	private static final int FONT_SIZE = 40;

	// Note: all physical distances are in millimeters
	private static final double MM_PER_INCH = 25.4;
	private static final double canvasWidth = 14 * MM_PER_INCH;
	private static final double canvasHeight = 10 * MM_PER_INCH;
	private static final double machineWidth = 1076;
	private static final double machineHeight = 24.5 * MM_PER_INCH;
	private static final double canvasLeftX = (machineWidth - canvasWidth) / 2;
	private static final double canvasRightX = canvasLeftX + canvasWidth;
	private static final double canvasTopY = machineHeight - canvasHeight - 7 * MM_PER_INCH;
	private static final double canvasBottomY = canvasTopY + canvasHeight;
	
	// Default acceleration used for non-drawing jogging, in mm/sec2
	private static final double DEFAULT_ACCELERATION = 100.0;

	// This is the longest length of a straight line segment, in mm
	private double maxLineSegmentLength = 0.5;
	
	// This is the longest length of a path segment, in mm
	private double maxPathSegmentLength = 0.2;

	private static final Point homingLR = new Point(923, 918);

	// Where the carriage should go after drawing
	private static final Point finalPosition = new Point(canvasRightX, canvasTopY);

	private PVector canvasStart = new PVector(CANVAS_MARGIN, CANVAS_MARGIN);
	private float canvasScale = 1;

	private List<List<Point>> lines = new ArrayList<>();

	private int prevSliderValue = 0;

	private ControlP5 controlP5;
	private SmartControl<Slider> slider;
	private double scaleX = 1;
	private double scaleY = 1;
	private double offsetX = 0;
	private double offsetY = 0;
	private double lineWidth = 1;
	private Element svgRootElement;
	private OptionsWindow optionsWindow = null;

	public void setSize(int width, int height) {
		windowX = width;
		windowY = height;
	}

	public void setLocation(int x, int y) {
		surface.setLocation(x, y);
	}

	public void settings() {
		size(windowX, windowY);
	}

	public void setup() {
		double usableX = windowX - (PANEL_WIDTH + CANVAS_MARGIN * 2);
		double usableY = windowY - (SLIDER_HEIGHT + SLIDER_MARGIN + CANVAS_MARGIN * 2);

		// Make canvas as big as possible using whole-number scaling, or fractional
		// scaling if canvas is bigger than the window
		if (canvasWidth > usableX || canvasHeight > usableY) {
			canvasScale = (float) (1 / Math.max(Math.ceil(canvasWidth / usableX), Math.ceil(canvasHeight / usableY)));
		} else {
			canvasScale = (float) Math.min(Math.floor(usableX / canvasWidth), Math.floor(usableY / canvasHeight));
		}
		System.out.format("Using canvas scale factor of %.2f\n", canvasScale);

		controlP5 = new ControlP5(this);
		slider = new SmartControl<>(controlP5.addSlider("lineNumber")
				.setPosition(50, windowY - SLIDER_HEIGHT - SLIDER_MARGIN).setHeight(SLIDER_HEIGHT)
				.setWidth((windowX * 8) / 10).setRange(0, 1).setSliderMode(Slider.FLEXIBLE).snapToTickMarks(true),
				this::sliderChanged);

		slider.getControl().getValueLabel().setSize(FONT_SIZE);
		slider.getControl().getCaptionLabel().set("Line #").setSize(FONT_SIZE).setColor(255).setPaddingX(10);
	}

	public void loadSvg(String filename) {
		final Document doc;
		try {
			String parser = XMLResourceDescriptor.getXMLParserClassName();
			SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
			doc = f.createDocument(filename);
		} catch (IOException ex) {
			ex.printStackTrace();
			exit();
			return;
		}
		// See
		// https://stackoverflow.com/questions/26027313/how-to-load-and-parse-svg-documents
		UserAgent userAgent = new UserAgentAdapter();
		DocumentLoader loader = new DocumentLoader(userAgent);
		BridgeContext bridgeContext = new BridgeContext(userAgent, loader);
		bridgeContext.setDynamicState(BridgeContext.DYNAMIC);
		// Enable CSS- and SVG-specific enhancements.
		(new GVTBuilder()).build(bridgeContext, doc);

		svgRootElement = doc.getDocumentElement();
		SVGLength width = ((SVGOMSVGElement) svgRootElement).getWidth().getBaseVal();
		SVGLength height = ((SVGOMSVGElement) svgRootElement).getHeight().getBaseVal();

		double scale = 1;
		double svgWidth = width.getValueInSpecifiedUnits();
		double svgHeight = height.getValueInSpecifiedUnits();
		if (canvasWidth > svgWidth) {
			scale = Math.min(canvasWidth / svgWidth, canvasHeight / svgHeight);
		} else {
			scale = 1 / Math.min(svgWidth / canvasWidth, svgHeight / canvasHeight);
		}
		scaleX = scale;
		scaleY = scale;

		scaleFromSvg();
		if (!lines.isEmpty()) {
			slider.getControl().setNumberOfTickMarks(lines.size() + 1).setRange(0, lines.size()).setValue(lines.size());
		}
	}

	public void scaleFromSvg() {
		if (svgRootElement == null) {
			return;
		}
		
		synchronized (this) {
			SvgParser parser = new SvgParser(scaleX, scaleY, maxPathSegmentLength, maxLineSegmentLength);
			lines = new ArrayList<>();
			traverse(parser, svgRootElement);
			lines = sort(lines);
		}
	}

	public void createCalibration() {
		synchronized (this) {
			lines = new ArrayList<>();
			List<Point> boundPoints = List.of(new Point(0, 0), new Point(canvasWidth, 0),
					new Point(canvasWidth, canvasHeight), new Point(0, canvasHeight), new Point(0, 0));
			List<Point> boundPointsInterpolated = new ArrayList<>();
			for (int i = 1; i < boundPoints.size(); i++) {
				Point prevBound = boundPoints.get(i - 1);
				Point curBound = boundPoints.get(i);
				for (double j = 0; j <= 1; j += .01) {
					boundPointsInterpolated.add(new Point(prevBound.x + (curBound.x - prevBound.x) * j,
							prevBound.y + (curBound.y - prevBound.y) * j));
				}
			}
			lines.add(boundPointsInterpolated);
			double xStep = canvasWidth / 3;
			double yStep = canvasHeight / 3;
			double length = 5;
			for (double x = xStep / 2; x < canvasWidth; x += xStep) {
				for (double y = yStep / 2; y < canvasHeight; y += yStep) {
					lines.add(new ArrayList<>(List.of(new Point(x - length / 2, y), new Point(x + length / 2, y))));
					lines.add(new ArrayList<>(List.of(new Point(x, y - length / 2), new Point(x, y + length / 2))));
				}
			}
			slider.getControl().setNumberOfTickMarks(lines.size() + 1).setRange(0, lines.size()).setValue(lines.size());
		}
	}

	public void createConsistencyTest() {
		synchronized (this) {
			lines = new ArrayList<>();
			double step = 20;
			double length = canvasHeight - 40;
			for (double offset = 0; offset < length; offset += step) {
				lines.add(new ArrayList<>(List.of(new Point(20, 20 + offset), new Point(20, 20 + offset + step))));
				lines.add(new ArrayList<>(List.of(new Point(canvasWidth - 20, 20 + offset),
						new Point(canvasWidth - 20, 20 + offset + step))));
			}
			slider.getControl().setNumberOfTickMarks(lines.size() + 1).setRange(0, lines.size()).setValue(lines.size());
		}
	}
	
	public void createKinematicsCalibration() {
		synchronized (this) {
			lines = new ArrayList<>();
			lines.add(new ArrayList<>(List.of(new Point(0, 10), new Point(canvasWidth, 10))));
			lines.add(new ArrayList<>(List.of(new Point(canvasWidth, 0), new Point(canvasWidth, canvasHeight))));
			lines.add(new ArrayList<>(List.of(new Point(canvasWidth, canvasHeight - 10), new Point(0, canvasHeight - 10))));
			lines.add(new ArrayList<>(List.of(new Point(0, canvasHeight), new Point(0, 0))));
			
			lines = Point.interpolateLines(lines, maxLineSegmentLength);
		
			slider.getControl().setNumberOfTickMarks(lines.size() + 1).setRange(0, lines.size()).setValue(lines.size());
		}
	}
	
	// Draws the canvas outline
	public void createCornerCalibration() {
		synchronized (this) {
			lines = new ArrayList<>();
			lines.add(new ArrayList<>(List.of(new Point(0, 0), new Point(canvasWidth, 0))));
			lines.add(new ArrayList<>(List.of(new Point(canvasWidth, 0), new Point(canvasWidth, canvasHeight))));
			lines.add(new ArrayList<>(List.of(new Point(canvasWidth, canvasHeight), new Point(0, canvasHeight))));
			lines.add(new ArrayList<>(List.of(new Point(0, canvasHeight), new Point(0, 0))));
			
			lines = Point.interpolateLines(lines, maxLineSegmentLength);
		
			slider.getControl().setNumberOfTickMarks(lines.size() + 1).setRange(0, lines.size()).setValue(lines.size());
		}
	}

	private void traverse(SvgParser parser, Element element) {
		if (element instanceof SVGGraphicsElement) {
			List<Point> line = parser.parse((SVGGraphicsElement) element);
			if (!line.isEmpty()) {
				lines.add(line);
			}
		}

		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			Node child = element.getChildNodes().item(i);
			if (child instanceof Element) {
				traverse(parser, (Element) child);
			}
		}
	}

	private static List<List<Point>> sort(List<List<Point>> lines) {
		if (lines.isEmpty()) {
			return lines;
		}
		
		List<List<Point>> sorted = new ArrayList<>();
		List<List<Point>> startXSorted = new ArrayList<>();
		startXSorted.addAll(lines);
		startXSorted.sort((line1, line2) -> {
			if (line1.isEmpty()) {
				return 1;
			}
			if (line2.isEmpty()) {
				return -1;
			}

			return Double.compare(line1.get(0).x, line2.get(0).x);
		});
		List<List<Point>> endXSorted = new ArrayList<>();
		endXSorted.addAll(lines);
		endXSorted.sort((line1, line2) -> {
			if (line1.isEmpty()) {
				return 1;
			}
			if (line2.isEmpty()) {
				return -1;
			}

			return Double.compare(line1.get(line1.size() - 1).x, line2.get(line2.size() - 1).x);
		});

		List<Point> line = startXSorted.remove(0);
		endXSorted.remove(line);
		sorted.add(line);
		while (!startXSorted.isEmpty()) {
			double shortestDistance = Double.MAX_VALUE;
			List<Point> shortestNext = null;
			boolean reverse = false;
			Point start = line.get(line.size() - 1);
			for (List<Point> maybeNextLine : startXSorted) {
				if (maybeNextLine.isEmpty()) {
					// Dumb, but possible because of SVG parsing problems
					continue;
				}
				double distance = start.distanceTo(maybeNextLine.get(0));
				if (distance < shortestDistance) {
					shortestDistance = distance;
					shortestNext = maybeNextLine;
				}
				if (maybeNextLine.get(0).x - start.x > shortestDistance) {
					break;
				}
			}
			for (List<Point> maybeNextLine : endXSorted) {
				if (maybeNextLine.isEmpty()) {
					// Dumb, but possible because of SVG parsing problems
					continue;
				}
				double distance = start.distanceTo(maybeNextLine.get(maybeNextLine.size() - 1));
				if (distance < shortestDistance) {
					shortestDistance = distance;
					shortestNext = maybeNextLine;
					reverse = true;
				}
				if (maybeNextLine.get(0).x - start.x > shortestDistance) {
					break;
				}
			}

			startXSorted.remove(shortestNext);
			endXSorted.remove(shortestNext);
			if (reverse) {
				Collections.reverse(shortestNext);
			}
			sorted.add(shortestNext);
			line = shortestNext;
		}

		return sorted;
	}

	private static double parseDoubleOrDefault(String text, double defaultValue) {
		try {
			return Double.parseDouble(text);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private void canvasLine(double x1, double y1, double x2, double y2) {
		line(canvasStart.x + x1 * canvasScale, canvasStart.y + y1 * canvasScale, canvasStart.x + x2 * canvasScale,
				canvasStart.y + y2 * canvasScale);
	}

	private void line(double d, double e, double f, double g) {
		line((float) d, (float) e, (float) f, (float) g);
	}

	private void drawLine(List<Point> line) {
		for (int i = 1; i < line.size(); i++) {
			Point prevPoint = line.get(i - 1).translate(offsetX, offsetY);
			Point point = line.get(i).translate(offsetX, offsetY);
			if (prevPoint.x >= 0 && prevPoint.x <= canvasWidth && prevPoint.y >= 0 && prevPoint.y <= canvasHeight) {
				canvasLine(prevPoint.x, prevPoint.y, point.x, point.y);
			}
		}
	}

	public void draw() {
		synchronized (this) {
			if (lines.isEmpty()) {
				return;
			}

			clear();
			background(50, 50, 50);
			fill(255);
			rect(canvasStart.x, canvasStart.y, (float) (canvasWidth * canvasScale),
					(float) (canvasHeight * canvasScale));
			strokeWeight((float) (lineWidth * canvasScale));
			// Note: sometimes the values don't line up, maybe an SVG parsing problem
			for (int i = 0; i < slider.getValue() && i < lines.size(); i++) {
				drawLine(lines.get(i));
			}
		}
	}

	public void keyPressed() {
		if (key == ESC) {
			exit();
		}
	}
	
	private int mouseStartX = 0;
	private int mouseStartY = 0;
	public void mousePressed() {
		if (mouseX > canvasStart.x && mouseX < canvasStart.x + canvasWidth * canvasScale
				&& mouseY > canvasStart.y && mouseY < canvasStart.y + canvasHeight * canvasScale) {
			mouseStartX = mouseX;
			mouseStartY = mouseY;
		}
	}
	
	public void mouseDragged() {
		if (mouseX > canvasStart.x && mouseX < canvasStart.x + canvasWidth * canvasScale
				&& mouseY > canvasStart.y && mouseY < canvasStart.y + canvasHeight * canvasScale) {
			offsetX = offsetX + mouseX - mouseStartX;
			offsetY = offsetY + mouseY - mouseStartY;
			mouseStartX = mouseX;
			mouseStartY = mouseY;
			
			if (optionsWindow != null) {
				optionsWindow.setOffsetX(offsetX);
				optionsWindow.setOffsetY(offsetY);
			}
		}
	}
	
	public void mouseWheel(MouseEvent event) {
		this.setScale(scaleX + event.getCount() / 20.0, scaleY + event.getCount() / 20.0);
		optionsWindow.setScaleX(scaleX);
		optionsWindow.setScaleY(scaleY);
	}

	public void sliderChanged(ControlEvent arg0) {
		int sliderValue = (int) arg0.getValue();
		if (prevSliderValue != sliderValue) {
			redraw();
		}

		prevSliderValue = sliderValue;
	}

	public void setScale(String x, String y) {
		setScale(parseDoubleOrDefault(x, scaleX), parseDoubleOrDefault(y, scaleY));
	}
	
	public void setScale(double x, double y) {
		scaleX = x;
		scaleY = y;
		scaleFromSvg();
		redraw();
	}

	public void setOffset(double x, double y) {
		this.offsetX = x;
		this.offsetY = y;
		redraw();
	}

	public void setLineWidth(String w) {
		lineWidth = parseDoubleOrDefault(w, lineWidth);
		redraw();
	}
	
	public void setLineSegment(double value) {
		maxLineSegmentLength = value;
		scaleFromSvg();
		redraw();
	}
	
	public void setPathSegment(double value) {
		maxPathSegmentLength = value;
		scaleFromSvg();
		redraw();
	}
	
	public void setOptionsWindow(OptionsWindow optionsWindow) {
		this.optionsWindow = optionsWindow;
	}

	public double getScaleX() {
		return scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	private static String penDownGcode(GcodeConfig config) {
		return String.format("G04 P0.1 ; Delay for 0.1s\nG01 F%.1f Z%.3f ; Pen down\n", config.penSpeed(),
				config.penDown());
	}

	private static String penUpGcode(GcodeConfig config) {
		return String.format("G01 F%.1f Z%.3f ; Pen up\n", config.penSpeed(), config.penUp());
	}

	private Point machineToBeltPoint(Point machinePoint) {
		Kinematics kinematics = new Kinematics(machineWidth);
		Point beltPoint = kinematics.computePoint(machinePoint.x, machinePoint.y);
		return new Point(beltPoint.x - homingLR.x, beltPoint.y - homingLR.y);
	}

	public void generateGcode(GcodeConfig config) {
		try {
			BufferedWriter writer = Files.newBufferedWriter(Path.of("out.gcode"));
			writer.append("G90 ; Absolute positioning\n");
			writer.append(String.format("$120=%.2f ; X-Axis acceleration\n", config.acceleration()));
			writer.append(String.format("$121=%.2f ; Y-Axis acceleration\n", config.acceleration()));
			writer.append("\n");

			boolean penDown = false;
			writer.append(penUpGcode(config));

			// Prime line
			ArrayList<Point> primeLine = new ArrayList<>();
			primeLine.add(new Point(0, 0));
			primeLine.add(new Point(20, 0));
			primeLine.add(new Point(0, 0));
			primeLine.add(new Point(20, 0));
			lines.add(0, primeLine);

			for (int i = 0; i < lines.size(); i++) {
				writer.append("\n");
				List<Point> points = lines.get(i);
				writer.append(String.format("; Line %d\n", i - 1));
				for (int j = 0; j < points.size(); j++) {
					Point machinePoint = points.get(j).translate(canvasLeftX, canvasTopY).translate(offsetX, offsetY);
					if (machinePoint.x > canvasRightX || machinePoint.x < canvasLeftX) {
						System.err.format("Point X out of bounds: %s, X: %f -> %f, canvas point: %s\n", machinePoint,
								canvasLeftX, canvasRightX, points.get(j));
						if (penDown) {
							writer.append(penUpGcode(config));
							penDown = false;
						}
					} else if (machinePoint.y > canvasBottomY || machinePoint.y < canvasTopY) {
						System.err.format("Point Y out of bounds: %s,  Y: %f -> %f, canvas point: %s\n", machinePoint,
								canvasTopY, canvasBottomY, points.get(j));
						if (penDown) {
							writer.append(penUpGcode(config));
							penDown = false;
						}
					} else {
						Point beltPoint = machineToBeltPoint(machinePoint);
						if (!penDown) {
							writer.append(String.format("G01 F%.0f X%.3f Y%.3f\n", config.travelSpeed(), beltPoint.x,
									beltPoint.y));
							writer.append(penDownGcode(config));
							penDown = true;
						} else {
							writer.append(String.format("G01 F%.0f X%.3f Y%.3f\n", config.drawSpeed(), beltPoint.x,
									beltPoint.y));
						}
					}
				}
				if (i < lines.size() - 1) {
					Point lastPoint = points.get(points.size() - 1);
					Point nextPoint = lines.get(i + 1).get(0);
					double samePointThreshold = lineWidth / 3;

					if (Math.abs(nextPoint.x - lastPoint.x) < samePointThreshold && Math.abs(nextPoint.y - lastPoint.y) < samePointThreshold) {
						// Keep pen down
						penDown = true;
					} else {
						writer.append(penUpGcode(config));
						penDown = false;
					}
				} else {
					writer.append(penUpGcode(config));
					penDown = false;
				}
			}

			writer.append("\n; Final position\n");
			writer.append(penUpGcode(config));
			Point finalPositionBelt = machineToBeltPoint(finalPosition);
			writer.append(String.format("G01 F%f X%.3f Y%.3f\n\n", config.travelSpeed(), finalPositionBelt.x,
					finalPositionBelt.y));

			writer.append(String.format("$120=%.2f ; X-Axis acceleration\n", DEFAULT_ACCELERATION));
			writer.append(String.format("$121=%.2f ; Y-Axis acceleration\n\n", DEFAULT_ACCELERATION));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
