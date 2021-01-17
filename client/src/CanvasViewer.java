import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGGraphicsElement;
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
	// private static final double canvasWidth = 18 * MM_PER_INCH;
	// private static final double canvasWidth = 15.5 * MM_PER_INCH;
	private static final double canvasWidth = 14 * MM_PER_INCH;
	private static final double canvasHeight = 10 * MM_PER_INCH;
	private static final double machineWidth = 43 * MM_PER_INCH;
	private static final double machineHeight = 24.5 * MM_PER_INCH;
	private static final double canvasLeftX = (machineWidth - canvasWidth) / 2;
	private static final double canvasRightX = canvasLeftX + canvasWidth;
	private static final double canvasTopY = machineHeight - canvasHeight - 7 * MM_PER_INCH;
	private static final double canvasBottomY = canvasTopY + canvasHeight;

	private final double maxLineSegmentLength = 2;

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

		// Needed so that draw() isn't called in the middle of updating
		svgRootElement = doc.getDocumentElement();
		SVGLength width = ((SVGOMSVGElement) svgRootElement).getWidth().getBaseVal();
		width.convertToSpecifiedUnits(SVGLength.SVG_LENGTHTYPE_MM);
		SVGLength height = ((SVGOMSVGElement) svgRootElement).getHeight().getBaseVal();
		height.convertToSpecifiedUnits(SVGLength.SVG_LENGTHTYPE_MM);

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
		slider.getControl().setNumberOfTickMarks(lines.size() + 1).setRange(0, lines.size()).setValue(lines.size());
	}

	public void scaleFromSvg() {
		synchronized (this) {
			lines = new ArrayList<>();

			traverse(svgRootElement);
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
			lines.add(new ArrayList<>(List.of(new Point(0, 0), new Point(canvasWidth, 0),
					new Point(canvasWidth, canvasHeight), new Point(0, canvasHeight), new Point(0, 0))));
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

	private void traverse(Element element) {
		if (element instanceof SVGGraphicsElement) {
			if (element instanceof SVGOMPathElement) {
				List<Point> line = pathToPoints((SVGOMPathElement) element);
				if (!line.isEmpty()) {
					lines.add(line);
				}
			} else if (element instanceof SVGOMPolylineElement) {
				List<Point> line = polylineToPoints((SVGOMPolylineElement) element);
				if (!line.isEmpty()) {
					lines.add(line);
				}
			}
		}

		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			Node child = element.getChildNodes().item(i);
			if (child instanceof Element) {
				traverse((Element) child);
			}
		}
	}

	private static List<List<Point>> sort(List<List<Point>> lines) {
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

	public void sliderChanged(ControlEvent arg0) {
		int sliderValue = (int) arg0.getValue();
		if (prevSliderValue != sliderValue) {
			redraw();
		}

		prevSliderValue = sliderValue;
	}

	public void setScale(String x, String y) {
		scaleX = parseDoubleOrDefault(x, scaleX);
		scaleY = parseDoubleOrDefault(y, scaleY);
		scaleFromSvg();
		redraw();
	}

	public void setOffset(double x, double y) {
		this.offsetX = x;
		this.offsetY = y;
		redraw();
	}

	public void setLineWidth(String w) {
		lineWidth = parseDoubleOrDefault(w, 1);
		redraw();
	}

	public double getScaleX() {
		return scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	private static String penDownGcode(GcodeConfig config) {
		// return String.format("G01 F%f Z%f ; Pen down\nG04 P0.1 ; Delay for 0.1s\n",
		// 200.0, -0.85);
		return String.format("G01 F%f Z%f ; Pen down\nG04 P0.1 ; Delay for 0.1s\n", config.penSpeed(),
				config.penDown());
	}

	private static String penUpGcode(GcodeConfig config) {
		return String.format("G01 F%f Z%f ; Pen up\n", config.penSpeed(), config.penUp());
	}

	private List<Point> pathToPoints(SVGOMPathElement path) {
		List<Point> points = new ArrayList<>();
		double length = path.getTotalLength();
		if (length <= 0) {
			return points;
		}

		double step = length
				/ Math.ceil(length * Math.sqrt(Math.pow(scaleX, 2) + Math.pow(scaleY, 2)) / maxLineSegmentLength);
		for (double i = 0; i <= length; i += step) {
			SVGPoint point = path.getPointAtLength((float) i);
			try {
				double x = point.getX() * scaleX;
				double y = point.getY() * scaleY;
				points.add(new Point(x, y));
			} catch (Exception e) {
				System.err.format(
						"Got exception while converting path (%s) with length %.3f to points at length %.3f: %s\n",
						path.getTextContent(), length, i, e.getMessage());
			}
		}
		return points;
	}

	private List<Point> polylineToPoints(SVGOMPolylineElement polyline) {
		List<Point> points = new ArrayList<>();
		SVGPointList pointList = polyline.getPoints();
		Point prevPoint = null;
		for (int i = 0; i < pointList.getNumberOfItems(); i++) {
			SVGPoint svgPoint = pointList.getItem(i);
			Point point = new Point(svgPoint.getX() * scaleX, svgPoint.getY() * scaleY);
			if (prevPoint == null) {
				points.add(point);
			} else {
				double xDelta = point.x - prevPoint.x;
				double yDelta = point.y - prevPoint.y;
				double length = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta, 2));
				double steps = Math.ceil(length / maxLineSegmentLength);
				if (steps < 2) {
					steps = 2;
				}
				for (int step = 1; step < steps; step++) {
					Point interpolatedPoint = new Point(prevPoint.x + xDelta * step / steps,
							prevPoint.y + yDelta * step / steps);
					points.add(interpolatedPoint);
				}
			}

			prevPoint = point;
		}
		if (prevPoint != null) {
			points.add(prevPoint);
		}
		return points;
	}

	private Point machineToBeltPoint(Point machinePoint) {
		Kinematics kinematics = new Kinematics(machineWidth);
		Point beltPoint = kinematics.computePoint(machinePoint.x, machinePoint.y);
		return new Point(beltPoint.x - homingLR.x, beltPoint.y - homingLR.y);
	}

	public void generateGcode(GcodeConfig config) {
		try {
			BufferedWriter writer = Files.newBufferedWriter(Path.of("out.gcode"));
			writer.append("G90 ; Absolute positioning\n\n");

			boolean penDown = false;
			writer.append(penUpGcode(config));

			// Prime line
			ArrayList<Point> primeLine = new ArrayList<>();
			primeLine.add(new Point(0, 0));
			primeLine.add(new Point(50, 0));
			primeLine.add(new Point(0, 0));
			primeLine.add(new Point(50, 0));
			lines.add(0, primeLine);

			for (int i = 0; i < lines.size(); i++) {
				writer.append("\n");
				List<Point> points = lines.get(i);
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
							writer.append(String.format("G01 F%f X%.3f Y%.3f\n", config.travelSpeed(), beltPoint.x,
									beltPoint.y));
							writer.append(penDownGcode(config));
							penDown = true;
						} else {
							writer.append(String.format("G01 F%f X%.3f Y%.3f\n", config.drawSpeed(), beltPoint.x,
									beltPoint.y));
						}
					}
				}
				if (i < lines.size() - 1) {
					Point lastPoint = points.get(points.size() - 1);
					Point nextPoint = lines.get(i + 1).get(0);

					if (Math.abs(nextPoint.x - lastPoint.x) < .01 && Math.abs(nextPoint.y - lastPoint.y) < .01) {
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
			writer.append(String.format("G01 F%f X%.3f Y%.3f\n", config.travelSpeed(), finalPositionBelt.x,
					finalPositionBelt.y));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
