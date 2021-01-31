package com.ademuri.iconograph.options;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class MachineConfig {
	public abstract double machineWidth();
	public abstract double machineHeight();
	
	public abstract double canvasWidth();
	public abstract double canvasHeight();
	public abstract double canvasOffsetX();
	public abstract double canvasOffsetY();
	
	public abstract double probeX();
	public abstract double probeY();

	public static Builder builder() {
		return new AutoValue_MachineConfig.Builder();
	}
	
	@AutoValue.Builder
	public abstract static class Builder {
		public abstract Builder setMachineWidth(double value);
		public abstract Builder setMachineHeight(double value);
		public abstract Builder setCanvasWidth(double value);
		public abstract Builder setCanvasHeight(double value);
		public abstract Builder setCanvasOffsetX(double value);
		public abstract Builder setCanvasOffsetY(double value);
		public abstract Builder setProbeX(double value);
		public abstract Builder setProbeY(double value);
		public abstract MachineConfig build();
	}

	public double canvasRightX() {
		return canvasOffsetX() + canvasWidth();
	}
	
	public double canvasBottomY() {
		return canvasOffsetY() + canvasHeight();
	}
}
