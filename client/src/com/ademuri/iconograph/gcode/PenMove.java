package com.ademuri.iconograph.gcode;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PenMove extends GcodeCommand {
	public abstract double feedRate();
	public abstract double z();
	public abstract String comment();
	
	public static PenMove create(double feedRate, double z) {
		return new AutoValue_PenMove(feedRate, z, "");
	}
	
	public static PenMove create(double feedRate, double z, String comment) {
		return new AutoValue_PenMove(feedRate, z, comment);
	}
	
	@Override
	public String toString() {
		String command = String.format("G01 F%.1f Z%.3f", feedRate(), z()); 
		if (!comment().isBlank()) {
			command += " ; " + comment();
		}
		
		return command;
	}
}
