package com.ademuri.iconograph;
import com.ademuri.iconograph.options.OptionsWindow;

import processing.core.PApplet;

public class Client {
	// Develop in Eclipse following
	// https://happycoding.io/tutorials/java/processing-in-java
	// Note: Batik uses a really old version of the W3C Document APIs:
	// https://stackoverflow.com/questions/13676937/how-to-find-package-org-w3c-dom-svg
	public static void main(String[] args) throws Exception {
		String[] processingArgs = { "Iconograph CNC Client" };
		CanvasViewer canvasViewer = new CanvasViewer();
		OptionsWindow optionsWindow = new OptionsWindow(canvasViewer);
		canvasViewer.setSize(optionsWindow.getProcessingWidth(), optionsWindow.getProcessingHeight());
		canvasViewer.setOptionsWindow(optionsWindow);

		PApplet.runSketch(processingArgs, canvasViewer);
		canvasViewer.setLocation(optionsWindow.getLeftBound(), optionsWindow.getUpperBound());
		optionsWindow.init();
		optionsWindow.setVisible(true);
	}
}
