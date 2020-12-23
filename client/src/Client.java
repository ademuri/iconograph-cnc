import processing.core.PApplet;


public class Client extends PApplet {
	// Develop in Eclipse following https://happycoding.io/tutorials/java/processing-in-java
	
	public void settings() {
		size(1280, 720);
	}
	
	public void draw() {
		
	}
	
	public void keyPressed() {
		if (key == ESC) {
			exit();
		}
	}


	public static void main(String[] args) {
		String[] processingArgs = {"MySketch"};
		Client client = new Client();
		PApplet.runSketch(processingArgs, client);
	}
}
