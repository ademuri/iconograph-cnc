import processing.core.PApplet;


public class Client  {
	private static final int OPTIONS_WIDTH = 300;
	
	// Develop in Eclipse following https://happycoding.io/tutorials/java/processing-in-java
	// Note: Batik uses a really old version of the W3C Document APIs: https://stackoverflow.com/questions/13676937/how-to-find-package-org-w3c-dom-svg
	public static void main(String[] args) throws Exception {
		String[] processingArgs = {"Iconograph CNC Client"};
		CanvasViewer canvasViewer = new CanvasViewer();
		OptionsWindow optionsWindow = new OptionsWindow(canvasViewer);		
		
		PApplet.runSketch(processingArgs, canvasViewer);
		while (!canvasViewer.isReady()) {
			Thread.sleep(100);
		}
		optionsWindow.init();
		optionsWindow.setVisible(true);
	}
}
