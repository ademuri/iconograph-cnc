import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGGraphicsElement;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMPolylineElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;

import controlP5.CallbackEvent;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Slider;


import processing.core.PApplet;
import processing.core.PVector;


public class Client extends PApplet implements ControlListener {
	// Develop in Eclipse following https://happycoding.io/tutorials/java/processing-in-java
	// Note: Batik uses a really old version of the W3C Document APIs: https://stackoverflow.com/questions/13676937/how-to-find-package-org-w3c-dom-svg
	
	private static final int windowX = 3600;
	private static final int windowY = 1800;
	
	private final List<SVGGraphicsElement> svgGraphics = new ArrayList<>();
	
	ControlP5 controlP5;
	Slider slider;
	int sliderValue = 0;
	
	public void settings() {
		size(windowX, windowY);
	}
	
	public void setup() {
		controlP5 = new ControlP5(this);
		slider = controlP5.addSlider("sliderValue")
			.setPosition(50, windowY - 120)
			.setHeight(100)
			.setWidth((windowX * 8) / 10)
			.setRange(0,  100)
			.setSliderMode(Slider.FLEXIBLE)
			.snapToTickMarks(true)
			.addListener(this);
		
		Controller<Slider> sliderController = (Controller<Slider>) controlP5.getController("sliderValue");
		sliderController.getValueLabel().setSize(30);
		sliderController.getCaptionLabel().setVisible(false);
		
		final Document doc;
		try {
		    String parser = XMLResourceDescriptor.getXMLParserClassName();
		    SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		     doc = f.createDocument("example.svg");
		    // doc = f.createDocument("torus.svg");
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
		
		slider.setNumberOfTickMarks(svgGraphics.size());
		slider.setRange(0, svgGraphics.size());
	}
	
	private void traverse(Element element) {
		if (element instanceof SVGGraphicsElement) {
			svgGraphics.add((SVGGraphicsElement) element);
			//drawSvgGraphic((SVGGraphicsElement) element);
		}
		
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			Node child = element.getChildNodes().item(i);
			if (child instanceof Element) {
				traverse((Element) child);
			}
		}
	}
	
	private void drawSvgGraphic(SVGGraphicsElement element) {
		float scale = 4;
		if (element instanceof SVGOMPathElement) {
			SVGOMPathElement pathElement = (SVGOMPathElement) element;
			float length = pathElement.getTotalLength();
			float step = length / 10;
			for (float i = 0; i < length; i += step) {
				float endLength = i + step;
				if (endLength > length) {
					endLength = length;
				}
				
				SVGPoint start = pathElement.getPointAtLength(i);
				SVGPoint end = pathElement.getPointAtLength(endLength);
				line(start.getX() * scale, start.getY() * scale, end.getX() * scale, end.getY() * scale);
			}
		} else if (element instanceof SVGOMPolylineElement) {
			SVGOMPolylineElement polyline = (SVGOMPolylineElement) element;
			SVGPointList pointList = polyline.getPoints();
			for (int i = 0; i < pointList.getNumberOfItems() - 1; i++) {
				SVGPoint start = pointList.getItem(i);
				SVGPoint end = pointList.getItem(i + 1);
				line(start.getX() * scale, start.getY() * scale, end.getX() * scale, end.getY() * scale);
			}
		}
	}
	
	public void draw() {
		clear();
		background(255, 255, 255);
		for (int i = 0; i < sliderValue; i++) {
			drawSvgGraphic(svgGraphics.get(i));
		}
	}
	
	public void keyPressed() {
		if (key == ESC) {
			exit();
		}
	}

	// TODO: move this to its own class
	int count = 0;
	int prevSliderValue = 0;

	@Override
	public void controlEvent(ControlEvent arg0) {
		sliderValue = (int) arg0.getValue();
		if (prevSliderValue != sliderValue) {
			System.out.println(count++);
			redraw();
		}
		
		prevSliderValue = sliderValue;
	}

	public static void main(String[] args) {
		String[] processingArgs = {"Iconograph CNC Client"};
		Client client = new Client();
		PApplet.runSketch(processingArgs, client);
	}
}
