package com.ademuri.iconograph.gcode;

public class GcodeCommand {
	private final String command;
	
	public GcodeCommand() {
		this.command = "";
	}
	
	public GcodeCommand(String command) {
		this.command = command;
	}

	public String toString() {
		return command;
	}
}
