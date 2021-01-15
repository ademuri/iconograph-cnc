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
	private JTextField scaleX;
	private final JLabel lblNewLabel = new JLabel("Scale X");
	private JTextField scaleY;

	private final int processingWidth;
	private final int processingHeight;
	private final int leftBound;
	private final int upperBound;
	private final double monitorScale;
	private final Ini ini;
	private JPanel panel_2;
	private JTextField drawSpeed;
	private JLabel lblNewLabel_2;
	private JPanel panel_3;
	private JTextField travelSpeed;
	private JLabel lblNewLabel_3;
	private JPanel panel_4;
	private JTextField lineWidth;
	private JLabel lblNewLabel_4;
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
		Section speedConfig = ini.get(SECTION_SPEEDS);

		Font defaultFont = new Font("Dialog", Font.PLAIN, getTextSize());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, (int) (450 * monitorScale), (int) (300 * monitorScale));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(defaultFont);
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

		JPanel panel = new JPanel();
		drawingPanel.add(panel);

		scaleX = new JTextField();
		scaleX.setFont(defaultFont);
		scaleX.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setScale();
			}
		});
		panel.add(scaleX);
		scaleX.setToolTipText("");
		scaleX.setColumns(10);
		scaleX.addKeyListener(this);
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, getTextSize()));
		panel.add(lblNewLabel);
		lblNewLabel.setLabelFor(scaleX);

		JPanel panel_1 = new JPanel();
		drawingPanel.add(panel_1);

		scaleY = new JTextField();
		scaleY.setFont(defaultFont);
		scaleY.addKeyListener(this);
		scaleY.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setScale();
			}
		});
		panel_1.add(scaleY);
		scaleY.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Scale Y");
		lblNewLabel_1.setFont(new Font("Dialog", Font.BOLD, getTextSize()));
		panel_1.add(lblNewLabel_1);
		lblNewLabel_1.setLabelFor(scaleY);

		panel_2 = new JPanel();
		drawingPanel.add(panel_2);

		drawSpeed = new JTextField();
		drawSpeed.setFont(defaultFont);
		drawSpeed.setText(speedConfig.getOrDefault("draw_speed", "2000"));
		drawSpeed.setColumns(10);
		drawSpeed.addKeyListener(this);
		panel_2.add(drawSpeed);

		lblNewLabel_2 = new JLabel("Draw Speed");
		lblNewLabel_2.setFont(new Font("Dialog", Font.BOLD, getTextSize()));
		panel_2.add(lblNewLabel_2);

		panel_3 = new JPanel();
		drawingPanel.add(panel_3);

		travelSpeed = new JTextField();
		travelSpeed.setFont(defaultFont);
		travelSpeed.setText(speedConfig.getOrDefault("travel_speed", "2000"));
		travelSpeed.setColumns(10);
		travelSpeed.addKeyListener(this);
		panel_3.add(travelSpeed);

		lblNewLabel_3 = new JLabel("Travel Speed");
		lblNewLabel_3.setFont(new Font("Dialog", Font.BOLD, getTextSize()));
		panel_3.add(lblNewLabel_3);

		panel_4 = new JPanel();
		drawingPanel.add(panel_4);

		lineWidth = new JTextField();
		lineWidth.setText("2");
		lineWidth.setFont(defaultFont);
		lineWidth.setColumns(10);
		lineWidth.addKeyListener(this);
		panel_4.add(lineWidth);

		lblNewLabel_4 = new JLabel("Line Width");
		lblNewLabel_4.setFont(new Font("Dialog", Font.BOLD, getTextSize()));
		panel_4.add(lblNewLabel_4);

		panel_5 = new JPanel();
		drawingPanel.add(panel_5);

		generateGcode = new JButton("Generate G-Code");
		generateGcode.setFont(defaultFont);
		generateGcode.addActionListener(event -> {
			updateSpeeds();
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

	private void tryStoreIni() {
		try {
			ini.store();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int getTextSize() {
		return (int) (18 * monitorScale);
	}

	public void init() {
		scaleX.setText(String.format("%.2f", canvasViewer.getScaleX()));
		scaleY.setText(String.format("%.2f", canvasViewer.getScaleY()));
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
			updateSpeeds();
			System.exit(0);
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
	}

	@Override
	public void keyTyped(KeyEvent event) {
		if (event.getSource().equals(lineWidth)) {
			setLineWidth();
		} else if (event.getSource().equals(drawSpeed)) {
			updateSpeeds();
		} else if (event.getSource().equals(travelSpeed)) {
			updateSpeeds();
		}
	}

	private void updateSpeeds() {
		ini.put(SECTION_SPEEDS, "draw_speed", drawSpeed.getText());
		ini.put(SECTION_SPEEDS, "travel_speed", travelSpeed.getText());
		tryStoreIni();
	}

	private void setScale() {
		canvasViewer.setScale(scaleX.getText(), scaleY.getText());
	}

	private void setLineWidth() {
		canvasViewer.setLineWidth(lineWidth.getText());
	}

	private void doGenerateGcode() {
		canvasViewer.setDrawSpeed(drawSpeed.getText());
		canvasViewer.setTravelSpeed(travelSpeed.getText());
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
