package com.ademuri.iconograph.options;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import com.ademuri.iconograph.CanvasViewer;
import com.ademuri.iconograph.GcodeConfig;

import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import javax.swing.SwingConstants;

public class OptionsWindow extends JFrame implements KeyListener {
	private static final String CONFIG_FILE = "config.ini";
	private static final String SECTION_DRAWING = "drawing";
	private static final String LAST_DIR_SVG = "LAST_DIR_SVG";

	private final CanvasViewer canvasViewer;
	private final MachinePanel machinePanel;
	private JPanel contentPane;
	private TextInput scaleX;
	private TextInput scaleY;
	private TextInput offsetX;
	private TextInput offsetY;
	private TextInput drawSpeed;
	private TextInput travelSpeed;
	private TextInput penDown;
	private TextInput penUp;
	private TextInput penSpeed;
	private TextInput lineWidth;
	private TextInput lineSegment;
	private TextInput pathSegment;
	private TextInput acceleration;

	private final int processingWidth;
	private final int processingHeight;
	private final int leftBound;
	private final int upperBound;
	private final double monitorScale;
	private final Ini ini;

	private JPanel panel_5;
	private JButton generateGcode;
	private JPanel panel_6;
	private JButton btnLoadSvg;

	private final JFileChooser fileChooser = new JFileChooser();
	private JTabbedPane tabbedPane;
	private JPanel drawingPanel;
	private JPanel calPanel;
	private JPanel panel_7;
	private JButton btnCalPen;
	private JButton btnCalAlignment;
	private JPanel panel_8;
	private JPanel penConfig;
	private JLabel lblPenSettings;
	private JPanel transformPanel;

	/**
	 * Create the frame.
	 */
	public OptionsWindow(CanvasViewer canvasViewer) throws InvalidFileFormatException, IOException {
		this.canvasViewer = canvasViewer;
		monitorScale = Toolkit.getDefaultToolkit().getScreenResolution() / 96.0;

		this.ini = new Ini();
		if (Files.exists(Paths.get(CONFIG_FILE))) {
			ini.load(new File(CONFIG_FILE));
		} else {
			ini.add(SECTION_DRAWING);
			ini.store(new File(CONFIG_FILE));
		}
		ini.setFile(new File(CONFIG_FILE));

		Font defaultFont = new Font("Dialog", Font.PLAIN, getTextSize());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, (int) (450 * monitorScale), (int) (1000 * monitorScale));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(defaultFont);
		tabbedPane.addKeyListener(this);
		contentPane.add(tabbedPane);

		drawingPanel = new JPanel();
		tabbedPane.addTab("Drawing", null, drawingPanel, null);
		drawingPanel.setLayout(new BoxLayout(drawingPanel, BoxLayout.Y_AXIS));

		panel_6 = new JPanel();
		drawingPanel.add(panel_6);

		btnLoadSvg = new JButton("Load SVG");
		btnLoadSvg.setFont(defaultFont);
		btnLoadSvg.addActionListener(event -> {
			int r = fileChooser.showOpenDialog(this);
			if (r == JFileChooser.APPROVE_OPTION) {
				canvasViewer.loadSvg(fileChooser.getSelectedFile().getAbsolutePath());
				scaleX.getInput().setText(String.format("%3.2f", canvasViewer.getScaleX()));
				scaleY.getInput().setText(String.format("%3.2f", canvasViewer.getScaleY()));
				Preferences prefs = Preferences.userRoot().node(getClass().getName());
				prefs.put(LAST_DIR_SVG, fileChooser.getSelectedFile().getAbsolutePath());
			}
		});
		panel_6.add(btnLoadSvg);
		
		TextInput canvasColor = new TextInput("Canvas Color", defaultFont, "FFFFFF");
		canvasColor.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				canvasViewer.setCanvasColor(canvasColor.getInput().getText());
			}
		});
		canvasColor.setConfig(ini, SECTION_DRAWING, "canvas_color");
		drawingPanel.add(canvasColor);

		transformPanel = new JPanel();
		transformPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		drawingPanel.add(transformPanel);
		transformPanel.setLayout(new BoxLayout(transformPanel, BoxLayout.Y_AXIS));

		scaleX = new TextInput("Scale X", defaultFont, "1");
		transformPanel.add(scaleX);

		scaleY = new TextInput("Scale Y", defaultFont, "1");
		transformPanel.add(scaleY);
		scaleY.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setScale();
			}
		});
		scaleX.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setScale();
			}
		});

		offsetX = new TextInput("Offset X", defaultFont, "0");
		transformPanel.add(offsetX);
		offsetX.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setOffset();
			}
		});
		offsetY = new TextInput("Offset Y", defaultFont, "0");
		transformPanel.add(offsetY);
		offsetY.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setOffset();
			}
		});

		drawSpeed = new TextInput("Draw Speed", defaultFont, "2000");
		drawSpeed.setConfig(ini, SECTION_DRAWING, "draw_speed");
		drawingPanel.add(drawSpeed);

		travelSpeed = new TextInput("Travel Speed", defaultFont, "3000");
		travelSpeed.setConfig(ini, SECTION_DRAWING, "travel_speed");
		drawingPanel.add(travelSpeed);

		lineWidth = new TextInput("Line Width", defaultFont, "2");
		lineWidth.setConfig(ini, SECTION_DRAWING, "line_width");
		lineWidth.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setLineWidth();
			}
		});
		drawingPanel.add(lineWidth);

		lineSegment = new TextInput("Line Segment", defaultFont, "0.5");
		lineSegment.setConfig(ini, SECTION_DRAWING, "line_segment");
		lineSegment.setToolTipText("The interpolation length for straight lines, in mm");
		lineSegment.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setLineSegment();
			}
		});
		drawingPanel.add(lineSegment);

		pathSegment = new TextInput("Path Segment", defaultFont, "0.2");
		pathSegment.setConfig(ini, SECTION_DRAWING, "path_segment");
		pathSegment.setToolTipText("The interpolation length for paths (curved lines), in mm");
		pathSegment.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setPathSegment();
			}
		});
		drawingPanel.add(pathSegment);

		acceleration = new TextInput("X/Y acceleration, mm/sec2", defaultFont, "40");
		acceleration.setConfig(ini, SECTION_DRAWING, "acceleration");
		drawingPanel.add(acceleration);

		penConfig = new JPanel();
		penConfig.setBorder(new LineBorder(new Color(0, 0, 0)));
		penConfig.setLayout(new BoxLayout(penConfig, BoxLayout.Y_AXIS));
		drawingPanel.add(penConfig);

		lblPenSettings = new JLabel("Pen Settings");
		lblPenSettings.setVerticalAlignment(SwingConstants.TOP);
		lblPenSettings.setHorizontalAlignment(SwingConstants.LEFT);
		lblPenSettings.setFont(new Font("Dialog", Font.PLAIN, (int) (getTextSize() * 0.8)));
		penConfig.add(lblPenSettings);

		penDown = new TextInput("Pen Down", defaultFont, "-0.5");
		penDown.setConfig(ini, SECTION_DRAWING, "pen_down");
		penConfig.add(penDown);

		penUp = new TextInput("Pen Up", defaultFont, "-2.0");
		penUp.setConfig(ini, SECTION_DRAWING, "pen_up");
		penConfig.add(penUp);

		penSpeed = new TextInput("Pen Speed", defaultFont, "400");
		penSpeed.setConfig(ini, SECTION_DRAWING, "pen_speed");
		penConfig.add(penSpeed);

		panel_5 = new JPanel();
		drawingPanel.add(panel_5);

		generateGcode = new JButton("Generate G-Code");
		generateGcode.setFont(defaultFont);
		generateGcode.addActionListener(event -> {
			doGenerateGcode();
		});
		panel_5.add(generateGcode);

		calPanel = new JPanel();
		tabbedPane.addTab("Calibration", null, calPanel, null);
		calPanel.setLayout(new BoxLayout(calPanel, BoxLayout.Y_AXIS));

		panel_7 = new JPanel();
		calPanel.add(panel_7);

		btnCalPen = new JButton("Pen calibration");
		btnCalPen.setFont(defaultFont);
		btnCalPen.addActionListener(event -> {
			canvasViewer.createCalibration();
		});
		panel_7.add(btnCalPen);

		panel_8 = new JPanel();
		calPanel.add(panel_8);

		btnCalAlignment = new JButton("Alignment lines");
		btnCalAlignment.setFont(defaultFont);
		btnCalAlignment.addActionListener(event -> {
			canvasViewer.createConsistencyTest();
		});
		panel_8.add(btnCalAlignment);

		JButton btnKinematicsCalibration = new JButton("Kinematics");
		btnKinematicsCalibration.setFont(defaultFont);
		btnKinematicsCalibration.addActionListener(event -> {
			canvasViewer.createKinematicsCalibration();
		});
		JPanel panel_9 = new JPanel();
		calPanel.add(panel_9);
		panel_9.add(btnKinematicsCalibration);

		JButton btnCornerCalibration = new JButton("Corners");
		btnCornerCalibration.setFont(defaultFont);
		btnCornerCalibration.addActionListener(event -> {
			canvasViewer.createCornerCalibration();
		});
		JPanel panel_10 = new JPanel();
		calPanel.add(panel_10);
		panel_10.add(btnCornerCalibration);
		
		JButton btnGridCalibration = new JButton("Grid");
		btnGridCalibration.setFont(defaultFont);
		btnGridCalibration.addActionListener(event -> {
			canvasViewer.createGridCalibration();
		});
		JPanel panel_11 = new JPanel();
		calPanel.add(panel_11);
		panel_11.add(btnGridCalibration);
		
		machinePanel = new MachinePanel(defaultFont, ini, canvasViewer);
		tabbedPane.addTab("Machine", machinePanel);

		addKeyListener(this);
		setLocationRight();

		GraphicsConfiguration config = getGraphicsConfiguration();
		Rectangle bounds = config.getBounds();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);
		processingWidth = bounds.width - insets.right - insets.left - getWidth();
		processingHeight = bounds.height - insets.top - insets.bottom - 100;
		leftBound = bounds.x + insets.left;
		upperBound = bounds.y + insets.top;

		setLineWidth();
		setLineSegment();
		setPathSegment();

		Preferences prefs = Preferences.userRoot().node(getClass().getName());
		if (!prefs.get(LAST_DIR_SVG, "").isEmpty()) {
			fileChooser.setCurrentDirectory(new File(prefs.get(LAST_DIR_SVG, "")));
		}
		
		canvasViewer.setSize(getProcessingWidth(), getProcessingHeight());
		canvasViewer.setOptionsWindow(this);
		canvasViewer.setMachineConfig(machinePanel.getMachineConfig());
		
		setFontSize(fileChooser.getComponents(), getTextSize());
		fileChooser.setPreferredSize(new Dimension(getProcessingWidth(), getProcessingHeight()));
		FileFilter svgFilter = new FileNameExtensionFilter("SVG files", "svg");
		fileChooser.addChoosableFileFilter(svgFilter);
		fileChooser.setFileFilter(svgFilter);
	}

	public static void setFontSize(Component[] components, int textSize) {
		for (Component component : components) {
			if (component instanceof Container) {
				setFontSize(((Container) component).getComponents(), textSize);
			}
			component.setFont(new Font("Dialog", Font.PLAIN, textSize));
		}
	}

	private int getTextSize() {
		return (int) (18 * monitorScale);
	}

	public void init() {
		scaleX.getInput().setText(String.format("%.2f", canvasViewer.getScaleX()));
		scaleY.getInput().setText(String.format("%.2f", canvasViewer.getScaleY()));
	}

	public int getProcessingWidth() {
		return processingWidth;
	}

	public int getProcessingHeight() {
		return processingHeight;
	}

	public int getLeftBound() {
		return leftBound;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public void setOffsetX(double value) {
		this.offsetX.getInput().setText(String.format("%.1f", value));
	}

	public void setOffsetY(double value) {
		this.offsetY.getInput().setText(String.format("%.1f", value));
	}

	public void setScaleX(double value) {
		this.scaleX.getInput().setText(String.format("%.2f", value));
	}

	public void setScaleY(double value) {
		this.scaleY.getInput().setText(String.format("%.2f", value));
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			drawSpeed.saveToIni();
			travelSpeed.saveToIni();
			lineWidth.saveToIni();
			lineSegment.saveToIni();
			pathSegment.saveToIni();
			acceleration.saveToIni();
			System.exit(0);
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
	}

	@Override
	public void keyTyped(KeyEvent event) {
	}

	private void setScale() {
		canvasViewer.setScale(scaleX.getInput().getText(), scaleY.getInput().getText());
	}

	private void setOffset() {
		canvasViewer.setOffset(Double.parseDouble(offsetX.getInput().getText()),
				Double.parseDouble(offsetY.getInput().getText()));
	}

	private void setLineWidth() {
		canvasViewer.setLineWidth(lineWidth.getInput().getText());
	}

	private void setLineSegment() {
		canvasViewer.setLineSegment(Double.parseDouble(lineSegment.getInput().getText()));
	}

	private void setPathSegment() {
		canvasViewer.setPathSegment(Double.parseDouble(pathSegment.getInput().getText()));
	}
	
	public GcodeConfig getGcodeConfig() {
		return GcodeConfig.builder().setDrawSpeed(Double.parseDouble(drawSpeed.getInput().getText()))
				.setTravelSpeed(Double.parseDouble(travelSpeed.getInput().getText()))
				.setAcceleration(Double.parseDouble(acceleration.getInput().getText()))
				.setPenDown(Double.parseDouble(penDown.getInput().getText()))
				.setPenUp(Double.parseDouble(penUp.getInput().getText()))
				.setPenSpeed(Double.parseDouble(penSpeed.getInput().getText())).build();
	}

	private void doGenerateGcode() {
		canvasViewer.setMachineConfig(machinePanel.getMachineConfig());
		canvasViewer.generateGcode(getGcodeConfig());
	}

	private void setLocationRight() {
		GraphicsConfiguration config = getGraphicsConfiguration();
		Rectangle bounds = config.getBounds();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);
		int x = bounds.x + bounds.width - insets.right - getWidth();
		int y = bounds.y + insets.top;
		setLocation(x, y);
	}
}
