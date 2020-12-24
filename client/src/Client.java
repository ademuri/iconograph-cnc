import processing.core.PApplet;


public class Client  {
	// Develop in Eclipse following https://happycoding.io/tutorials/java/processing-in-java
	// Note: Batik uses a really old version of the W3C Document APIs: https://stackoverflow.com/questions/13676937/how-to-find-package-org-w3c-dom-svg
	public static void main(String[] args) throws Exception {
		String[] processingArgs = {"Iconograph CNC Client"};
		CanvasViewer canvasViewer = new CanvasViewer();
		PApplet.runSketch(processingArgs, canvasViewer);
		
		// TODO: do literally anything other than sleep for synchronization
		Thread.sleep(2000);
		OptionsWindow optionsWindow = new OptionsWindow(canvasViewer);
	}
}
