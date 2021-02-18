package com.ademuri.iconograph.options;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

import org.ini4j.Ini;

import com.ademuri.iconograph.CanvasViewer;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListenerWithExceptions;
import com.fazecast.jSerialComm.SerialPortEvent;

public class MachinePanel extends JPanel {
	private static final String MACHINE_CONFIG = "machine";
	private static final String LAST_DIR_GCODE = "last-dir-gcode";
	private static final int MONITOR_MAX_LINES = 5000;

	private DistanceInput machineWidth;
	private DistanceInput machineHeight;

	private DistanceInput canvasWidth;
	private DistanceInput canvasHeight;
	private DistanceInput canvasOffsetX;
	private DistanceInput canvasOffsetY;

	private DistanceInput probeX;
	private DistanceInput probeY;

	private final JTextArea serialLog;
	private final JFileChooser fileChooser = new JFileChooser();
	private final SerialGrbl serialGrbl = new SerialGrbl();
	
	private List<String> loadedGcode = null;

	public MachinePanel(Font defaultFont, Ini ini, CanvasViewer canvasViewer) {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		JPanel distances = new JPanel();
		distances.setBorder(new LineBorder(new Color(0, 0, 0)));
		distances.setLayout(new BoxLayout(distances, BoxLayout.Y_AXIS));
		contentPanel.add(distances);

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
		contentPanel.add(control);
		
		JPanel jogPanel = new JPanel();
		jogPanel.setLayout(new BoxLayout(jogPanel, BoxLayout.Y_AXIS));
		jogPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
		control.add(jogPanel);
		
		JLabel jogLabel = new JLabel("Jog Control");
		jogLabel.setFont(defaultFont);
		jogPanel.add(jogLabel);
		
		JPanel jogTop = new JPanel();
		jogTop.setLayout(new BoxLayout(jogTop, BoxLayout.X_AXIS));
		jogPanel.add(jogTop);
		
		JButton jogTopLeft = new JButton("Top Left");
		jogTopLeft.setFont(defaultFont);
		jogTop.add(jogTopLeft);
		jogTopLeft.addActionListener(event -> {
			sendGcode(canvasViewer.topLeftGcode());
		});
		
		JButton jogTopRight = new JButton("Top Right");
		jogTopRight.setFont(defaultFont);
		jogTop.add(jogTopRight);
		jogTopRight.addActionListener(event -> {
			sendGcode(canvasViewer.topRightGcode());
		});
		
		JPanel jogBottom = new JPanel();
		jogBottom.setLayout(new BoxLayout(jogBottom, BoxLayout.X_AXIS));
		jogPanel.add(jogBottom);
		
		JButton jogBottomLeft = new JButton("Bottom Left");
		jogBottomLeft.setFont(defaultFont);
		jogBottom.add(jogBottomLeft);
		jogBottomLeft.addActionListener(event -> {
			sendGcode(canvasViewer.bottomLeftGcode());
		});
		
		JButton jogBottomRight = new JButton("Bottom Right");
		jogBottomRight.setFont(defaultFont);
		jogBottom.add(jogBottomRight);
		jogBottomRight.addActionListener(event -> {
			sendGcode(canvasViewer.bottomRightGcode());
		});

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
		
		JPanel serialButtonPanel = new JPanel();
		control.add(serialButtonPanel);

		JButton refreshButton = new JButton("Refresh");
		refreshButton.setFont(defaultFont);
		refreshButton.addActionListener(event -> {
			serialPortChooser.removeAllItems();
			List.of(SerialPort.getCommPorts()).stream().map(serialPort -> serialPort.getSystemPortName())
					.forEach(portName -> serialPortChooser.addItem(portName));
		});
		serialButtonPanel.add(refreshButton);

		serialLog = new JTextArea(10, 24);
		serialLog.setFont(defaultFont);
		serialLog.setEditable(false);
		JScrollPane serialScroll = new JScrollPane(serialLog);

		JButton connectButton = new JButton("Connect");
		connectButton.setFont(defaultFont);
		connectButton.addActionListener(event -> {
			if (serialGrbl.isOpen()) {
				serialGrbl.close();
				connectButton.setText("Connect");
				return;
			}
			if (!serialGrbl.open((String) serialPortChooser.getSelectedItem(), Integer.parseInt(baudRate.getInput().getText()))) {
				serialLog.append("Failed to open serial port\n");
				return;
			}
			
			serialGrbl.setReceivedCallback(line -> appendToSerialLog(line));
			connectButton.setText("Disconnect");
		});
		serialButtonPanel.add(connectButton);

		JTextField serialSend = new JTextField();
		serialSend.setFont(defaultFont);
		serialSend.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					if (serialGrbl.isOpen()) {
						serialGrbl.sendGcode(serialSend.getText());
						serialSend.setText("");
					}
				}
			}
		});
		control.add(serialSend);
		control.add(serialScroll);
		
		JPanel gcodePanel = new JPanel();
		gcodePanel.setLayout(new BoxLayout(gcodePanel, BoxLayout.Y_AXIS));
		control.add(gcodePanel);
		JPanel gcodeButtonPanel = new JPanel();
		gcodePanel.add(gcodeButtonPanel);
		
		JButton loadGcode = new JButton("Load Gcode");
		loadGcode.setFont(defaultFont);
		gcodeButtonPanel.add(loadGcode);
		loadGcode.addActionListener(event -> {
			int r = fileChooser.showOpenDialog(this);
			if (r == JFileChooser.APPROVE_OPTION) {
				Preferences prefs = Preferences.userRoot().node(getClass().getName());
				prefs.put(LAST_DIR_GCODE, fileChooser.getSelectedFile().getAbsolutePath());
				try {
					loadedGcode = Files.readAllLines(Path.of(fileChooser.getSelectedFile().getAbsolutePath()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton sendGcode = new JButton("Send Gcode");
		sendGcode.setFont(defaultFont);
		gcodeButtonPanel.add(sendGcode);
		sendGcode.addActionListener(event -> {
			serialGrbl.sendGcode(loadedGcode);
		});
		
		JButton pauseGcode = new JButton("Pause");
		pauseGcode.setFont(defaultFont);
		gcodeButtonPanel.add(pauseGcode);
		
		JButton clearGcode = new JButton("Clear");
		clearGcode.setFont(defaultFont);
		gcodeButtonPanel.add(clearGcode);
		clearGcode.addActionListener(event -> serialGrbl.clearBuffer());
		
		JPanel gcodeStatusPanel = new JPanel();
		gcodePanel.add(gcodeStatusPanel);
		
		JLabel gcodeFile = new JLabel("No g-code loaded");
		gcodeFile.setFont(defaultFont);
		gcodeStatusPanel.add(gcodeFile);
		
		JScrollPane scrollPane = new JScrollPane(contentPanel);
		add(scrollPane);
		
		OptionsWindow.setFontSize(fileChooser.getComponents(), defaultFont.getSize());
		fileChooser.setPreferredSize(new Dimension(800, 600));
		FileFilter svgFilter = new FileNameExtensionFilter("G-Code files", "gcode");
		fileChooser.addChoosableFileFilter(svgFilter);
		fileChooser.setFileFilter(svgFilter);
		
		Preferences prefs = Preferences.userRoot().node(getClass().getName());
		if (!prefs.get(LAST_DIR_GCODE, "").isEmpty()) {
			fileChooser.setCurrentDirectory(new File(prefs.get(LAST_DIR_GCODE, "")));
		}
		
		serialGrbl.setSentCallback(sent -> {
			appendToSerialLog(sent);
			gcodeFile.setText("Remaining: " + serialGrbl.getBufferSize());
		});
	}
	
	private void appendToSerialLog(String line) {
		serialLog.append(line);

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
	
	private void sendGcode(String gcode) {
		if (!serialGrbl.isOpen()) {
			return;
		}

		serialGrbl.sendGcode(gcode);
		serialLog.append("> " + gcode + "\n");
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
