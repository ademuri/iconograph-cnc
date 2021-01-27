import com.google.auto.value.AutoValue;
import processing.core.PApplet;

@AutoValue
public abstract class Color {	
	public static Color create(double red, double green, double blue) {
		return new AutoValue_Color((int)red, (int)green, (int)blue);
	}

	public abstract int red();
	public abstract int green();
	public abstract int blue();
	
	public static Color black() {
		return create(0, 0, 0);
	}
	
	public String toString() {
		return String.format("#%02X%02X%02X", red(), green(), blue());
	}
}
