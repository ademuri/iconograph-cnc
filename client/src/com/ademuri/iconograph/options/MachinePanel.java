package com.ademuri.iconograph.options;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;

import org.ini4j.Ini;

import com.ademuri.iconograph.CanvasViewer;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListenerWithExceptions;
import com.fazecast.jSerialComm.SerialPortEvent;

public class MachinePanel extends JPanel {
	private static final String MACHINE_CONFIG = "machine";
	private static final int MONITOR_MAX_LINES = 5000;

	private DistanceInput machineWidth;
	private DistanceInput machineHeight;

	private DistanceInput canvasWidth;
	private DistanceInput canvasHeight;
	private DistanceInput canvasOffsetX;
	private DistanceInput canvasOffsetY;

	private DistanceInput probeX;
	private DistanceInput probeY;

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

		probeX = new DistanceInput("Probe X", defaultFont, "-347");
		probeX.setConfig(ini, MACHINE_CONFIG, "probe_x");
		distances.add(probeX);

		probeY = new DistanceInput("Probe Y", defaultFont, "-356");
		probeY.setConfig(ini, MACHINE_CONFIG, "probe_y");
		distances.add(probeY);

		JPanel control = new JPanel();
		control.setBorder(new LineBorder(new Color(0, 0, 0)));
		control.setLayout(new BoxLayout(control, BoxLayout.Y_AXIS));
		add(control);

		JLabel serialPortLabel = new JLabel("Serial Port");
		serialPortLabel.setFont(defaultFont);
		control.add(serialPortLabel);

		JComboBox<String> serialPortChooser = new JComboBox<>();
		List.of(SerialPort.getCommPorts()).stream().map(serialPort -> serialPort.getSystemPortName())
				.forEach(portName -> serialPortChooser.addItem(portName));
		serialPortChooser.setEditable(true);
		serialPortChooser.setFont(defaultFont);
		for (int i = 0; i < serialPortChooser.getItemCount(); i++) {
			if (serialPortChooser.getItemAt(i).startsWith("ttyUSB")) {
				serialPortChooser.setSelectedIndex(i);
				break;
			}
		}
		control.add(serialPortChooser);

		TextInput baudRate = new TextInput("Baud Rate", defaultFont, "400000");
		baudRate.setConfig(ini, MACHINE_CONFIG, "baud_rate");
		control.add(baudRate);

		JButton refreshButton = new JButton("Refresh");
		refreshButton.setFont(defaultFont);
		refreshButton.addActionListener(event -> {
			serialPortChooser.removeAllItems();
			List.of(SerialPort.getCommPorts()).stream().map(serialPort -> serialPort.getSystemPortName())
					.forEach(portName -> serialPortChooser.addItem(portName));
		});
		control.add(refreshButton);

		JTextArea serialLog = new JTextArea(10, 24);
		serialLog.setFont(defaultFont);
		serialLog.setEditable(false);
		JScrollPane serialScroll = new JScrollPane(serialLog);

		JButton connectButton = new JButton("Connect");
		connectButton.setFont(defaultFont);
		connectButton.addActionListener(event -> {
			if (serialPort != null && serialPort.isOpen()) {
				connectButton.setText("Connect");
				serialPort.removeDataListener();
				serialPort.closePort();
				return;
			}
			
			serialPort = SerialPort.getCommPort((String) serialPortChooser.getSelectedItem());
			serialPort.setBaudRate(Integer.parseInt(baudRate.getInput().getText()));
			serialPort.setParity(SerialPort.NO_PARITY);
			serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
			serialPort.setNumDataBits(8);
			if (!serialPort.openPort()) {
				serialLog.append("Failed to open serial port\n");
			}
			serialPort.addDataListener(new SerialPortDataListenerWithExceptions() {
				@Override
				public void serialEvent(SerialPortEvent arg0) {
					long bytesAvailable = serialPort.bytesAvailable();
					if (bytesAvailable <= 0) {
						return;
					}

					byte bytes[] = new byte[(int) bytesAvailable];
					serialPort.readBytes(bytes, bytesAvailable);
					String s = new String(bytes, StandardCharsets.US_ASCII);
					serialLog.append(s);

					if (serialLog.getLineCount() > MONITOR_MAX_LINES) {
						try {
							serialLog.replaceRange("", 0,
									serialLog.getLineStartOffset(serialLog.getLineCount() - MONITOR_MAX_LINES));
						} catch (BadLocationException e) {
							// TODO: better error handling
							e.printStackTrace();
						}
					}
				}

				@Override
				public int getListeningEvents() {
					return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
				}

				@Override
				public void catchException(Exception arg0) {
					arg0.printStackTrace();
				}
			});
			connectButton.setText("Disconnect");
		});
		control.add(connectButton);

		JTextField serialSend = new JTextField();
		serialSend.setFont(defaultFont);
		serialSend.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					if (serialPort != null && serialPort.isOpen()) {
						String toSend = serialSend.getText() + "\r\n";
						byte[] buffer = toSend.getBytes(StandardCharsets.US_ASCII);
						int ret = serialPort.writeBytes(buffer, buffer.length);
						serialLog.append("> " + toSend);
						serialSend.setText("");

						if (ret != buffer.length) {
							System.err.format("Tried to write %d bytes to serial, but instead wrote %d\n", buffer.length, ret);
						}
					}
				}
			}
		});
		control.add(serialSend);
		control.add(serialScroll);
	}

	public MachineConfig getMachineConfig() {
		return MachineConfig.builder()
				.setMachineWidth(machineWidth.getValue().get())
				.setMachineHeight(machineHeight.getValue().get())
				.setCanvasWidth(canvasWidth.getValue().get())
				.setCanvasHeight(canvasHeight.getValue().get())
				.setCanvasOffsetX(canvasOffsetX.getValue().get())
				.setCanvasOffsetY(canvasOffsetY.getValue().get())
				.setProbeX(probeX.getValue().get())
				.setProbeY(probeY.getValue().get())
				.build();
	}
}
