package com.ademuri.iconograph.options;

import java.awt.Font;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DistanceInput extends TextInput {
	private static final Pattern distancePattern = Pattern.compile("([\\d.,]*)\\s*(\\w*)");

	public DistanceInput(String name, Font font, String defaultValue) {
		super(name, font, defaultValue);
	}
	
	/** Parses the field for distance measurements. Supported units are mm (default) and in. */
	public Optional<Double> getValue() {
		Matcher matcher = distancePattern.matcher(input.getText());
		if (!matcher.matches()) {
			return Optional.empty();
		}
		
		String valueString = matcher.group(1);
		String units = matcher.group(2);
		double value = Double.parseDouble(valueString);
		if (units.isBlank() || units.equals("mm")) {
			return Optional.of(value);
		}
		
		if (!units.equals("in")) {
			throw new IllegalArgumentException("Invalid unit: " + units);
		}
		
		return Optional.of(value * 25.4);
	}

}
