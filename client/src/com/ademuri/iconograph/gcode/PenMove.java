package com.ademuri.iconograph.gcode;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PenMove extends GcodeCommand {
	public abstract double feedRate();
	public abstract double z();
	
	public static PenMove create(double feedRate, double z) {
		return new AutoValue_PenMove(feedRate, z);
	}
	
	@Override
	public String toString() {
		return String.format("G01 F%.1f Z%.3f", feedRate(), z());
	}
}
