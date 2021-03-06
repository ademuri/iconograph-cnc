package com.ademuri.iconograph.gcode;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CarriageMove extends GcodeCommand {
	public abstract double feedRate();
	public abstract double x();
	public abstract double y();
	
	public static CarriageMove create(double feedRate, double x, double y) {
		return new AutoValue_CarriageMove(feedRate, x, y);
	}
	
	@Override
	public String toString() {
		return String.format("G01 F%.0f X%.3f Y%.3f", feedRate(), x(), y());
	}
}
