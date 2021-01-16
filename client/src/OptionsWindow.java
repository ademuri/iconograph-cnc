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

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

public class OptionsWindow extends JFrame implements KeyListener {
	private static final String CONFIG_FILE = "config.ini";
	private static final String SECTION_SPEEDS = "speeds";
	private static final String LAST_DIR_SVG = "LAST_DIR_SVG";

	private final CanvasViewer canvasViewer;
	private JPanel contentPane;
	private TextInput scaleX;
	private TextInput scaleY;

	private final int processingWidth;
	private final int processingHeight;
	private final int leftBound;
	private final int upperBound;
	private final double monitorScale;
	private final Ini ini;
	private TextInput drawSpeed;
	private TextInput travelSpeed;
	private TextInput lineWidth;
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
			ini.add(SECTION_SPEEDS);
			ini.store(new File(CONFIG_FILE));
		}
		ini.setFile(new File(CONFIG_FILE));

		Font defaultFont = new Font("Dialog", Font.PLAIN, getTextSize());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, (int) (450 * monitorScale), (int) (400 * monitorScale));
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
			setFontSize(fileChooser.getComponents());
			fileChooser.setPreferredSize(new Dimension(getProcessingWidth(), getProcessingHeight()));
			int r = fileChooser.showOpenDialog(this);
			if (r == JFileChooser.APPROVE_OPTION) {
				canvasViewer.loadSvg(fileChooser.getSelectedFile().getAbsolutePath());
				Preferences prefs = Preferences.userRoot().node(getClass().getName());
				prefs.put(LAST_DIR_SVG, fileChooser.getSelectedFile().getAbsolutePath());
			}
		});
		panel_6.add(btnLoadSvg);

		scaleX = new TextInput("Scale X", defaultFont, "1");
		scaleX.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setScale();
			}
		});
		drawingPanel.add(scaleX);

		scaleY = new TextInput("Scale Y", defaultFont, "1");
		scaleY.getInput().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setScale();
			}
		});
		drawingPanel.add(scaleY);

		drawSpeed = new TextInput("Draw Speed", defaultFont, "2000");
		drawSpeed.setConfig(ini, SECTION_SPEEDS, "draw_speed");
		drawingPanel.add(drawSpeed);

		travelSpeed = new TextInput("Travel Speed", defaultFont, "3000");
		travelSpeed.setConfig(ini, SECTION_SPEEDS, "travel_speed");
		drawingPanel.add(travelSpeed);

		lineWidth = new TextInput("Line Width", defaultFont, "2");
		lineWidth.setConfig(ini, SECTION_SPEEDS, "line_width");
		drawingPanel.add(lineWidth);

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

		Preferences prefs = Preferences.userRoot().node(getClass().getName());
		if (!prefs.get(LAST_DIR_SVG, "").isEmpty()) {
			fileChooser.setCurrentDirectory(new File(prefs.get(LAST_DIR_SVG, "")));
		}
	}

	private void setFontSize(Component[] components) {
		for (Component component : components) {
			if (component instanceof Container) {
				setFontSize(((Container) component).getComponents());
			}
			component.setFont(new Font("Dialog", Font.PLAIN, getTextSize()));
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

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			drawSpeed.saveToIni();
			travelSpeed.saveToIni();
			lineWidth.saveToIni();
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

	private void setLineWidth() {
		canvasViewer.setLineWidth(lineWidth.getInput().getText());
	}

	private void doGenerateGcode() {
		canvasViewer.setDrawSpeed(drawSpeed.getInput().getText());
		canvasViewer.setTravelSpeed(travelSpeed.getInput().getText());
		canvasViewer.generateGcode();
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
