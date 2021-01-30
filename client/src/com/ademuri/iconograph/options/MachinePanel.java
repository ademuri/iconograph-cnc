package com.ademuri.iconograph.options;

import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JPanel;

import org.ini4j.Ini;

import com.ademuri.iconograph.CanvasViewer;

public class MachinePanel extends JPanel {
	private static final String MACHINE_CONFIG = "machine";
	
	private DistanceInput machineWidth;
	private DistanceInput machineHeight;
	
	private DistanceInput canvasWidth;
	private DistanceInput canvasHeight;
	private DistanceInput canvasOffsetX;
	private DistanceInput canvasOffsetY;
	
	public MachinePanel(Font defaultFont, Ini ini, CanvasViewer canvasViewer) {
		machineWidth = new DistanceInput("Machine Width", defaultFont, "1000");
		machineWidth.setConfig(ini, MACHINE_CONFIG, "machine_width");
		add(machineWidth);
		
		machineHeight = new DistanceInput("Machine Height", defaultFont, "500");
		machineHeight.setConfig(ini, MACHINE_CONFIG, "machine_height");
		add(machineHeight);
		
		canvasWidth = new DistanceInput("Canvas Width", defaultFont, "300");
		canvasWidth.setConfig(ini, MACHINE_CONFIG, "canvas_width");
		canvasWidth.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				canvasViewer.setMachineConfig(getMachineConfig());
			}
		});
		add(canvasWidth);
		
		canvasHeight = new DistanceInput("Canvas Height", defaultFont, "200");
		canvasHeight.setConfig(ini, MACHINE_CONFIG, "canvas_height");
		canvasHeight.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				canvasViewer.setMachineConfig(getMachineConfig());
			}
		});
		add(canvasHeight);
		
		canvasOffsetX = new DistanceInput("Canvas Offset X", defaultFont, "300");
		canvasOffsetX.setConfig(ini, MACHINE_CONFIG, "canvas_offset_x");
		add(canvasOffsetX);
		
		canvasOffsetY = new DistanceInput("Canvas Offset Y", defaultFont, "200");
		canvasOffsetY.setConfig(ini, MACHINE_CONFIG, "canvas_offset_y");
		add(canvasOffsetY);
		
	}
	
	public MachineConfig getMachineConfig() {
		return MachineConfig.builder()
				.setMachineWidth(machineWidth.getValue().get())
				.setMachineHeight(machineHeight.getValue().get())
				.setCanvasWidth(canvasWidth.getValue().get())
				.setCanvasHeight(canvasHeight.getValue().get())
				.setCanvasOffsetX(canvasOffsetX.getValue().get())
				.setCanvasOffsetY(canvasOffsetY.getValue().get())
				.build();
	}
}
