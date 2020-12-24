import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
	private static final double canvasWidth = 18 * MM_PER_INCH;
	private static final double canvasHeight = 12 * MM_PER_INCH;
	private static final double machineWidth = 42 * MM_PER_INCH;
	private static final double machineHeight = 21 * MM_PER_INCH;
	private static final double canvasLeftX = (machineWidth - canvasWidth) / 2;
	private static final double canvasRightX = canvasLeftX + canvasWidth;
	private static final double canvasTopY = machineHeight - canvasHeight;
	private static final double canvasBottomY = machineHeight;
	
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
		System.out.format("(%f, %f) (%f, %f)\n", svgWidth, svgHeight, canvasWidth, canvasHeight);
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
		System.out.format("lineWidth: %f\n", lineWidth);
		redraw();
	}
	
	public double getScaleX() {
		return scaleX;
	}
	
	public double getScaleY() {
		return scaleY;
	}
	
	private String penDownGcode() {
		return String.format("G01 F%d, Z%f\n ; Pen down", 100, 300);
	}
	
	private String penUpGcode() {
		return String.format("G01 F%d, Z%f\n ; Pen up", 100, -300);
	}
		
	private String pathToGcode(SVGOMPathElement path) {
		List<String> codes = new ArrayList<>();
		codes.add(penDownGcode());
		
		float length = path.getTotalLength();
		float step = length / 10;
		for (float i = 0; i <= length; i += step) {
			SVGPoint point = path.getPointAtLength(i);
			double x = point.getX() * scaleX;
			double y = point.getY() * scaleY;
			
			if (x > canvasWidth || x < 0 || y > canvasHeight || y < 0) {
				System.err.format("Warning: truncating line outside of canvas, offending point: (%f, %f)\n", x, y);
				break;
			}
			
			double nextL = Math.sqrt(sq(point.getX()) + sq(point.getY()));
			double nextR = Math.sqrt(Math.pow(machineWidth - point.getX(), 2) + sq(point.getY()));
			codes.add(String.format("G01 F%d X%f Y%f", 100, nextL, nextR));
		}
		codes.add(penUpGcode());
		return String.join("\n", codes) + "\n\n";
	}
	
	private String polylineToGcode(SVGOMPolylineElement polyline) {
		return "";
	}
	
	public void generateGcode() {
		try {
			BufferedWriter writer = Files.newBufferedWriter(Path.of("out.gcode"));
			writer.append("G90 ; Absolute positioning\n\n");
			for (SVGGraphicsElement graphic : svgGraphics) {
				if (graphic instanceof SVGOMPathElement) {
					writer.append(pathToGcode((SVGOMPathElement) graphic));
				} else if (graphic instanceof SVGOMPolylineElement) {
					writer.append(polylineToGcode((SVGOMPolylineElement) graphic));
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
