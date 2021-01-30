package com.ademuri.iconograph.options;

import static java.util.stream.Collectors.toList;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.ini4j.Ini;

import com.ademuri.iconograph.CanvasViewer;
import com.fazecast.jSerialComm.SerialPort;

public class MachinePanel extends JPanel {
	private static final String MACHINE_CONFIG = "machine";
	
	private DistanceInput machineWidth;
	private DistanceInput machineHeight;
	
	private DistanceInput canvasWidth;
	private DistanceInput canvasHeight;
	private DistanceInput canvasOffsetX;
	private DistanceInput canvasOffsetY;
	
	private SerialPort serialPort = null;
	
	public MachinePanel(Font defaultFont, Ini ini, CanvasViewer canvasViewer) {
		JPanel distances = new JPanel();
		distances.setBorder(new LineBorder(new Color(0, 0, 0)));
		distances.setLayout(new BoxLayout(distances, BoxLayout.Y_AXIS));
		add(distances);
		
		machineWidth = new DistanceInput("Machine Width", defaultFont, "1000");
		machineWidth.setConfig(ini, MACHINE_CONFIG, "machine_width");
		distances.add(machineWidth);
		
		machineHeight = new DistanceInput("Machine Height", defaultFont, "500");
		machineHeight.setConfig(ini, MACHINE_CONFIG, "machine_height");
		distances.add(machineHeight);
		
		canvasWidth = new DistanceInput("Canvas Width", defaultFont, "300");
		canvasWidth.setConfig(ini, MACHINE_CONFIG, "canvas_width");
		canvasWidth.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				canvasViewer.setMachineConfig(getMachineConfig());
			}
		});
		distances.add(canvasWidth);
		
		canvasHeight = new DistanceInput("Canvas Height", defaultFont, "200");
		canvasHeight.setConfig(ini, MACHINE_CONFIG, "canvas_height");
		canvasHeight.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				canvasViewer.setMachineConfig(getMachineConfig());
			}
		});
		distances.add(canvasHeight);
		
		canvasOffsetX = new DistanceInput("Canvas Offset X", defaultFont, "300");
		canvasOffsetX.setConfig(ini, MACHINE_CONFIG, "canvas_offset_x");
		distances.add(canvasOffsetX);
		
		canvasOffsetY = new DistanceInput("Canvas Offset Y", defaultFont, "200");
		canvasOffsetY.setConfig(ini, MACHINE_CONFIG, "canvas_offset_y");
		distances.add(canvasOffsetY);
		
		
		JPanel control = new JPanel();
		control.setBorder(new LineBorder(new Color(0, 0, 0)));
		control.setLayout(new BoxLayout(control, BoxLayout.Y_AXIS));
		add(control);
		
		JLabel serialPortLabel = new JLabel("Serial Port");
		serialPortLabel.setFont(defaultFont);
		control.add(serialPortLabel);
		
		JComboBox<String> serialPortChooser = new JComboBox<>();
		List.of(SerialPort.getCommPorts())
		.stream()
		.map(serialPort -> serialPort.getSystemPortName())
		.forEach(portName -> serialPortChooser.addItem(portName));
		serialPortChooser.setEditable(true);
		serialPortChooser.setFont(defaultFont);
		control.add(serialPortChooser);
		
		TextInput baudRate = new TextInput("Baud Rate", defaultFont, "400000");
		baudRate.setConfig(ini, MACHINE_CONFIG, "baud_rate");
		control.add(baudRate);
		
		JButton refreshButton = new JButton("Refresh");
		refreshButton.setFont(defaultFont);
		refreshButton.addActionListener(event -> {
			serialPortChooser.removeAllItems();
			List.of(SerialPort.getCommPorts())
					.stream()
					.map(serialPort -> serialPort.getSystemPortName())
					.forEach(portName -> serialPortChooser.addItem(portName));
		});
		control.add(refreshButton);
		
		JButton connectButton = new JButton("Connect");
		connectButton.setFont(defaultFont);
		connectButton.addActionListener(event -> {
			serialPort = SerialPort.getCommPort((String) serialPortChooser.getSelectedItem());
		});
		control.add(connectButton);
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
