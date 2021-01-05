import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;

import controlP5.CallbackEvent;
import controlP5.ControlEvent;
import controlP5.ControlFont;
import controlP5.ControlListener;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Slider;
import controlP5.Textfield;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;


public class CanvasViewer extends PApplet  {
	// Develop in Eclipse following https://happycoding.io/tutorials/java/processing-in-java
	// Note: Batik uses a really old version of the W3C Document APIs: https://stackoverflow.com/questions/13676937/how-to-find-package-org-w3c-dom-svg
	
	private int windowX;
	private int windowY;
	private static final int SLIDER_HEIGHT = 100;
	private static final int SLIDER_MARGIN = 20;
	private static final int PANEL_WIDTH = 200;
	private static final int CANVAS_MARGIN = 50;
	private static final int FONT_SIZE = 40;
	
	// Note: all physical distances are in millimeters
	private static final double MM_PER_INCH = 25.4;
	//private static final double canvasWidth = 18 * MM_PER_INCH;
	private static final double canvasWidth = 15 * MM_PER_INCH;
	private static final double canvasHeight = 11 * MM_PER_INCH;
	private static final double machineWidth = 43 * MM_PER_INCH;
	private static final double machineHeight = 24.5 * MM_PER_INCH;
	private static final double canvasLeftX = (machineWidth - canvasWidth) / 2;
	private static final double canvasRightX = canvasLeftX + canvasWidth;
	private static final double canvasTopY = machineHeight - canvasHeight - 6 * MM_PER_INCH;
	private static final double canvasBottomY = machineHeight - 3 * MM_PER_INCH;
	
	private static final Point homingLR = new Point(36 * MM_PER_INCH, 35.5 * MM_PER_INCH);
	
	private PVector canvasStart = new PVector(CANVAS_MARGIN, CANVAS_MARGIN);
	private float canvasScale = 1;
	
	private final List<SVGGraphicsElement> svgGraphics = new ArrayList<>();
	
	private int prevSliderValue = 0;
	
	private ControlP5 controlP5;
	private SmartControl<Slider> slider;
	private double scaleX = 1;
	private double scaleY = 1;
	private double lineWidth = 1;
	private boolean ready = false;
	private double drawSpeed = 0;
	private double travelSpeed = 0;
	
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
		
		// Make canvas as big as possible using whole-number scaling, or fractional scaling if canvas is bigger than the window
		if (canvasWidth > usableX || canvasHeight > usableY) {
			canvasScale = (float) (1 / Math.max(Math.ceil(canvasWidth / usableX) , Math.ceil(canvasHeight / usableY)));
		} else {
			canvasScale = (float) Math.min(Math.floor(usableX / canvasWidth), Math.floor(usableY / canvasHeight));
		}
		System.out.format("Using canvas scale factor of %.2f\n", canvasScale);
				
		controlP5 = new ControlP5(this);
		slider = new SmartControl<>(controlP5.addSlider("lineNumber")
			.setPosition(50, windowY - SLIDER_HEIGHT - SLIDER_MARGIN)
			.setHeight(SLIDER_HEIGHT)
			.setWidth((windowX * 8) / 10)
			.setRange(0,  1)
			.setSliderMode(Slider.FLEXIBLE)
			.snapToTickMarks(true), this::sliderChanged);
		
		slider.getControl().getValueLabel().setSize(FONT_SIZE);
		slider.getControl().getCaptionLabel().set("Line #").setSize(FONT_SIZE).setColor(255).setPaddingX(10);
		
		final Document doc;
		try {
		    String parser = XMLResourceDescriptor.getXMLParserClassName();
		    SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		     //doc = f.createDocument("example.svg");
		     //doc = f.createDocument("torus.svg");
		    doc = f.createDocument("squares.svg");
		} catch (IOException ex) {
			ex.printStackTrace();
			exit();
			return;
		}
		
		// See https://stackoverflow.com/questions/26027313/how-to-load-and-parse-svg-documents
		UserAgent userAgent = new UserAgentAdapter();
	    DocumentLoader loader = new DocumentLoader(userAgent);
	    BridgeContext bridgeContext = new BridgeContext(userAgent, loader);
	    bridgeContext.setDynamicState(BridgeContext.DYNAMIC);

	    // Enable CSS- and SVG-specific enhancements.
	    (new GVTBuilder()).build(bridgeContext, doc);
		
		Element rootElement = doc.getDocumentElement();
		traverse(rootElement);
		
		svgGraphics.sort(Comparator.<SVGGraphicsElement>comparingDouble(CanvasViewer::svgLength).reversed());
		
		slider.getControl().setNumberOfTickMarks(svgGraphics.size() + 1)
			.setRange(0, svgGraphics.size())
			.setValue(svgGraphics.size());
		
		SVGLength width = ((SVGOMSVGElement) rootElement).getWidth().getBaseVal();
		width.convertToSpecifiedUnits(SVGLength.SVG_LENGTHTYPE_MM);
		SVGLength height = ((SVGOMSVGElement) rootElement).getHeight().getBaseVal();
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
		ready = true;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	private static float svgLength(Element element) {
		if (element instanceof SVGOMPathElement) {
			SVGOMPathElement path = (SVGOMPathElement) element;
			return path.getTotalLength();
		} else if (element instanceof SVGOMPolylineElement) {
			SVGOMPolylineElement polyline = (SVGOMPolylineElement) element;
			float length = 0;
			for (int i = 0; i < polyline.getPoints().getNumberOfItems() - 1; i++) {
				SVGPoint start = polyline.getPoints().getItem(i);
				SVGPoint end = polyline.getPoints().getItem(i + 1);
				length +=  Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2));
			}
			return length;
		}
		
		return 0;
	}
	
	private void traverse(Element element) {
		if (element instanceof SVGGraphicsElement) {
			svgGraphics.add((SVGGraphicsElement) element);
		}
		
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			Node child = element.getChildNodes().item(i);
			if (child instanceof Element) {
				traverse((Element) child);
			}
		}
	}
	
	private static float parseFloatOrDefault(String text, float defaultValue) {
		try {
			return Float.parseFloat(text);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	private void canvasLine(float x1, float y1, float x2, float y2) {
		line(canvasStart.x + x1 * canvasScale * scaleX, canvasStart.y + y1 * canvasScale * scaleY, canvasStart.x +  x2 * canvasScale * scaleX, canvasStart.y + y2 * canvasScale * scaleY);
	}
	
	private void line(double d, double e, double f, double g) {
		line((float) d, (float) e, (float) f, (float) g);
	}

	private void drawSvgGraphic(SVGGraphicsElement element) {
		if (element instanceof SVGOMPathElement) {
			SVGOMPathElement pathElement = (SVGOMPathElement) element;
			float length = pathElement.getTotalLength();
			float step = length / 20;
			for (float i = 0; i < length; i += step) {
				float endLength = i + step;
				if (endLength > length) {
					endLength = length;
				}
				
				SVGPoint start = pathElement.getPointAtLength(i);
				SVGPoint end = pathElement.getPointAtLength(endLength);
				canvasLine(start.getX(), start.getY(), end.getX(), end.getY());
			}
		} else if (element instanceof SVGOMPolylineElement) {
			SVGOMPolylineElement polyline = (SVGOMPolylineElement) element;
			SVGPointList pointList = polyline.getPoints();
			for (int i = 0; i < pointList.getNumberOfItems() - 1; i++) {
				SVGPoint start = pointList.getItem(i);
				SVGPoint end = pointList.getItem(i + 1);
				canvasLine(start.getX(), start.getY(), end.getX(), end.getY());
			}
		}
	}
	
	public void draw() {
		clear();
		background(50, 50, 50);
		fill(255);
		rect(canvasStart.x, canvasStart.y, (float)(canvasStart.x + canvasWidth * canvasScale), (float)(canvasStart.y + canvasHeight * canvasScale));
		strokeWeight((float)(lineWidth * canvasScale));
		for (int i = 0; i < slider.getValue(); i++) {
			drawSvgGraphic(svgGraphics.get(i));
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
		scaleX = parseFloatOrDefault(x, 1);
		scaleY = parseFloatOrDefault(y, 1);
		redraw();
	}
	
	public void setLineWidth(String w) {
		lineWidth = parseFloatOrDefault(w, 1);
		redraw();
	}
	
	public void setDrawSpeed(String speed) {
		drawSpeed = parseFloatOrDefault(speed, 0);
	}
	
	public void setTravelSpeed(String speed) {
		travelSpeed = parseFloatOrDefault(speed, 0);
	}
	
	public double getScaleX() {
		return scaleX;
	}
	
	public double getScaleY() {
		return scaleY;
	}
	
	private String penDownGcode() {
		return String.format("G01 F%f Z%f ; Pen down\n", 500.0, -3.0);
	}
	
	private String penUpGcode() {
		return String.format("G01 F%f Z%f ; Pen up\n", 500.0, 3.0);
	}
		
	private List<Point> pathToPoints(SVGOMPathElement path) {
		List<Point> points = new ArrayList<>();
		float length = path.getTotalLength();
		float step = length / 10;
		for (float i = 0; i <= length; i += step) {
			SVGPoint point = path.getPointAtLength(i);
			double x = point.getX() * scaleX;
			double y = point.getY() * scaleY;
			points.add(new Point(x, y));
		}
		return points;
	}
	
	private List<Point> polylineToPoints(SVGOMPolylineElement polyline) {
		List<Point> points = new ArrayList<>();
		SVGPointList pointList = polyline.getPoints();
		for (int i = 0; i < pointList.getNumberOfItems(); i++) {
			SVGPoint point = pointList.getItem(i);
			points.add(new Point(point.getX() * scaleX, point.getY() * scaleY));
		}
		return points;
	}
	
	final double h3 = 30; // Center of mass distance from pen?
	final double r = 20 * 2 / (2 * Math.PI); // Pulley radius: 20 teeth, 2mm pitch
	final double h = 24.4;	// Distance from belt support center to pen center
	final double theta = 55; // Angle between belt support points and line to pen center
	
	private double f1(double x, double y, double phi, double y1, double y2) {
		final double phi1 = theta - phi;
		final double phi2 = theta + phi;
		double tanGamma = (y + y1 - h * Math.sin(phi1)) / (x - h * Math.cos(phi1));
		double tanLambda = (y + y2 - h * Math.sin(phi2)) / (x - h * Math.cos(phi2));
		return h3 * Math.sin(phi) 
				+ Math.abs(h / (tanGamma + tanLambda)) * (Math.sin(phi2) - Math.sin(phi1) + tanGamma * Math.cos(phi1) - tanLambda * Math.cos(phi2));
	}
	
	private double f2(double x, double y, double phi, double y1, double y2) {
		final double phi1 = theta - phi;
		return Math.sqrt((Math.pow(y1, 2) - Math.pow(r, 2))) / r
				- (y + y1 - h * Math.sin(phi1)) / (x - h * Math.cos(phi1));
	}
	
	private double f3(double x, double y, double phi, double y1, double y2) {
		final double phi2 = theta + phi;
		return Math.sqrt((Math.pow(y2, 2) - Math.pow(r, 2))) / r
				- (y + y2 - h * Math.sin(phi2)) / (machineWidth - (x + h * Math.cos(phi2)));
	}
	
	private double[][] finiteDifferenceJacobian(double x, double y, double phi, double y1, double y2) {
		// See https://math.stackexchange.com/questions/728666/calculate-jacobian-matrix-without-closed-form-or-analytical-form
		final double DELTA = 0.0001;
		double[][] ret = new double[3][3];
		ret[0][0] = (f1(x, y, phi + DELTA, y1, y2) - f1(x, y, phi - DELTA, y1, y2)) / (DELTA * 2);
		ret[0][1] = (f1(x, y, phi, y1 + DELTA, y2) - f1(x, y, phi, y1 - DELTA, y2)) / (DELTA * 2);
		ret[0][2] = (f1(x, y, phi, y1, y2 + DELTA) - f1(x, y, phi, y1, y2 - DELTA)) / (DELTA * 2);
		ret[1][0] = (f2(x, y, phi + DELTA, y1, y2) - f2(x, y, phi - DELTA, y1, y2)) / (DELTA * 2);
		ret[1][1] = (f2(x, y, phi, y1 + DELTA, y2) - f2(x, y, phi, y1 - DELTA, y2)) / (DELTA * 2);
		ret[1][2] = (f2(x, y, phi, y1, y2 + DELTA) - f2(x, y, phi, y1, y2 - DELTA)) / (DELTA * 2);
		ret[2][0] = (f3(x, y, phi + DELTA, y1, y2) - f3(x, y, phi - DELTA, y1, y2)) / (DELTA * 2);
		ret[2][1] = (f3(x, y, phi, y1 + DELTA, y2) - f3(x, y, phi, y1 - DELTA, y2)) / (DELTA * 2);
		ret[2][2] = (f3(x, y, phi, y1, y2 + DELTA) - f3(x, y, phi, y1, y2 - DELTA)) / (DELTA * 2);
		return ret;
	}
	
	private Point computePoint(double x, double y) {
		// See https://robotics.stackexchange.com/a/11410
		final double DELTA = 0.001;
		double phi = 0;
		double y1 = r + 1;
		double y2 = r + 1;
		int n = 0;
		//System.out.format("\n\nPoint (%f, %f)\n", x, y);
		
		// Perform multivariate Newton-Raphson method
		// See example here: https://atozmath.com/example/CONM/NewtonRaphson2.aspx?he=e
		while (true) {
			double[][] jacobian_data = finiteDifferenceJacobian(x, y, phi, y1, y2);
			for (double[] row : jacobian_data) {
				for (double entry : row) {
					if (Double.isNaN(entry)) {
						throw new RuntimeException(String.format("No jacobian exists for point (%f, %f), params (%3.2f, %4.2f, %4.2f)", x, y, phi, y1, y2));
					}
				}
			}
			RealMatrix jacobian = MatrixUtils.createRealMatrix(finiteDifferenceJacobian(x, y, phi, y1, y2));
			RealMatrix jacobianInverse = new LUDecomposition(jacobian).getSolver().getInverse();
			for (double[] row : jacobianInverse.getData()) {
				for (double entry : row) {
					if (Double.isNaN(entry)) {
						throw new RuntimeException(String.format("No inverse exists for point (%f, %f), Jacobian: %s", x, y, jacobian));
					}
				}
			}
			
			double fx_n_data[] = {f1(x, y, phi, y1, y2), f2(x, y, phi, y1, y2), f3(x, y, phi, y1, y2)};
			RealVector fx_n = MatrixUtils.createRealVector(fx_n_data);
			
			RealVector deltaX = jacobianInverse.preMultiply(fx_n);
			double deltaPhi = deltaX.getEntry(0);
			double deltaY1 = deltaX.getEntry(1);
			double deltaY2 = deltaX.getEntry(2);
			
			//System.out.format("Round %3d: (%3.2f, %4.2f, %4.2f), %s, %s\n", n, deltaPhi, deltaY1, deltaY2, jacobian, jacobianInverse);
			
			n++;
			
			if (n > 1_000) {
				throw new RuntimeException("Iteration limit exceeded.");
			}
			
			if (Math.abs(deltaPhi) < DELTA && Math.abs(deltaY1) < DELTA && Math.abs(deltaY2) < DELTA) {
				break;
			}
			phi -= deltaPhi;
			y1 -= deltaY1;
			y2 -= deltaY2;
		}
		
		final double phi1 = theta - phi;
		final double phi2 = theta + phi;
		double tanGamma = (y + y1 - h * Math.sin(phi1)) / (x - h * Math.cos(phi1));
		double tanLambda = (y + y2 - h * Math.sin(phi2)) / (x - h * Math.cos(phi2));
		
		double gamma = Math.atan(tanGamma);
		double lambda = Math.atan(tanLambda);
		
		double left = Math.sqrt(Math.pow(x - h * Math.cos(phi1), 2) + Math.pow(y + y1 - h * Math.sin(phi1), 2))
				- r * tanGamma + r * gamma;
		double right = Math.sqrt(Math.pow(machineWidth - x + h * Math.cos(phi2), 2) + Math.pow(y + y2 - h * Math.sin(phi2), 2))
				- r * tanLambda + r * lambda;
		
		//System.out.format("Final: (phi: %3.2f) (y1: %3.2f) (y2: %3.2f); (phi1: %3.2f) (phi2: %3.2f)\n", phi, y1, y2, phi1, phi2);
		
		return new Point(left, right);
	}
	
	public void generateGcode() {
		// First, process all SVG elements into Points. Group each SVG primitives like polylines and paths into their own lists of points - each inner list should be connected.
		List<List<Point>> pointLists = new ArrayList<>();
		for (SVGGraphicsElement graphic : svgGraphics) {
			if (graphic instanceof SVGOMPathElement) {
				pointLists.add(pathToPoints((SVGOMPathElement) graphic));
			} else if (graphic instanceof SVGOMPolylineElement) {
				pointLists.add(polylineToPoints((SVGOMPolylineElement) graphic));
			}
		}
		
		try {
			BufferedWriter writer = Files.newBufferedWriter(Path.of("out.gcode"));
			writer.append("G90 ; Absolute positioning\n\n");
			
			boolean penDown = false;
			for (int i = 0; i < pointLists.size(); i++) {
				writer.append("\n");
				List<Point> points = pointLists.get(i);
				for (int j = 0; j < points.size(); j++) {
					Point machinePoint = new Point(canvasLeftX + points.get(j).x, canvasTopY + points.get(j).y);
					if (machinePoint.x > canvasRightX || machinePoint.x < canvasLeftX) {
						throw new IllegalArgumentException(String.format("Point X out of bounds: (%f, %f), X: %f -> %f", machinePoint.x, machinePoint.y, canvasLeftX, canvasRightX));
					}
					if (machinePoint.y > canvasBottomY || machinePoint.y < canvasTopY) {
						throw new IllegalArgumentException(String.format("Point Y out of bounds: (%f, %f),  Y: %f -> %f", machinePoint.x, machinePoint.y, canvasTopY, canvasBottomY));
					}
					Point beltPoint = computePoint(machinePoint.x, machinePoint.y);
					beltPoint = new Point(beltPoint.x - homingLR.x, beltPoint.y - homingLR.y);
					if (j == 0 && !penDown) {
						writer.append(String.format("G01 F%f X%f Y%f\n", travelSpeed, beltPoint.x, beltPoint.y));
						writer.append(penDownGcode());
						penDown = true;
					} else {
						writer.append(String.format("G01 F%f X%f Y%f\n", drawSpeed, beltPoint.x, beltPoint.y));
					}
				}
				if (i < pointLists.size() - 1) {
					Point lastPoint = points.get(points.size() - 1);
					Point nextPoint = pointLists.get(i + 1).get(0);
					
					if (Math.abs(nextPoint.x - lastPoint.x) < .01 && Math.abs(nextPoint.y - lastPoint.y) < .01) {
						// Keep pen down
						penDown = true;
					} else {
						writer.append(penUpGcode());
						penDown = false;
					}
				} else {
					writer.append(penUpGcode());
					penDown = false;
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class Point {
		final double x;
		final double y;
		
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}
}
