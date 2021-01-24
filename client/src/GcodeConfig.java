import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GcodeConfig {
	abstract double drawSpeed();
	abstract double travelSpeed();
	abstract double acceleration();
	
	abstract double penDown();
	abstract double penUp();
	abstract double penSpeed();
	
	static Builder builder() {
		return new AutoValue_GcodeConfig.Builder();
	}
	
	@AutoValue.Builder
	public abstract static class Builder {
		abstract Builder setDrawSpeed(double value);
		abstract Builder setTravelSpeed(double value);
		abstract Builder setAcceleration(double value);
		abstract Builder setPenDown(double value);
		abstract Builder setPenUp(double value);
		abstract Builder setPenSpeed(double value);
		abstract GcodeConfig build();
	}
}
