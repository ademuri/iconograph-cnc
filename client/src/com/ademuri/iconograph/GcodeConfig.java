package com.ademuri.iconograph;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GcodeConfig {
	abstract double drawSpeed();
	abstract double travelSpeed();
	abstract double acceleration();
	
	abstract double penDown();
	abstract double penUp();
	abstract double penSpeed();
	
	public static Builder builder() {
		return new AutoValue_GcodeConfig.Builder();
	}
	
	@AutoValue.Builder
	public abstract static class Builder {
		public abstract Builder setDrawSpeed(double value);
		public abstract Builder setTravelSpeed(double value);
		public abstract Builder setAcceleration(double value);
		public abstract Builder setPenDown(double value);
		public abstract Builder setPenUp(double value);
		public abstract Builder setPenSpeed(double value);
		public abstract GcodeConfig build();
	}
}
