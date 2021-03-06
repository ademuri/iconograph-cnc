package com.ademuri.iconograph.gcode;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Dwell extends GcodeCommand {
	public abstract double seconds();
	
	public static Dwell create(double seconds) {
		return new AutoValue_Dwell(seconds);
	}
	
	@Override
	public String toString() {
		return String.format("G04 P%.1f ; Delay for %.1fs", seconds(), seconds());
	}
}
