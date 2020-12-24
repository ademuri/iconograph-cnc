import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.Font;
import java.awt.GraphicsConfiguration;

public class OptionsWindow extends JFrame implements KeyListener {

	private final CanvasViewer canvasViewer;
	private JPanel contentPane;
	private JTextField scaleX;
	private final JLabel lblNewLabel = new JLabel("Scale X");
	private JTextField scaleY;
	
	private final int processingWidth;
	private final int processingHeight;
	private final int leftBound;
	private final int upperBound;

	/**
	 * Create the frame.
	 */
	public OptionsWindow(CanvasViewer canvasViewer) {
		this.canvasViewer = canvasViewer;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(2, 0, 0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		
		scaleX = new JTextField();
		scaleX.setFont(new Font("Dialog", Font.PLAIN, 18));
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
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		panel.add(lblNewLabel);
		lblNewLabel.setLabelFor(scaleX);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1);
		
		scaleY = new JTextField();
		scaleY.setFont(new Font("Dialog", Font.PLAIN, 18));
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
		lblNewLabel_1.setFont(new Font("Dialog", Font.BOLD, 18));
		panel_1.add(lblNewLabel_1);
		lblNewLabel_1.setLabelFor(scaleY);
		addKeyListener(this);
		setLocationRight();

		GraphicsConfiguration config = getGraphicsConfiguration();
	    Rectangle bounds = config.getBounds();
	    Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);
	    System.out.format("width: %d\n", getWidth());
	    processingWidth = bounds.width - insets.right - insets.left - getWidth();
	    processingHeight = bounds.height - insets.top - insets.bottom;
	    leftBound = bounds.x + insets.left;
	    upperBound = bounds.y + insets.top;
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
			System.exit(0);
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
	}

	@Override
	public void keyTyped(KeyEvent event) {
		setScale();
	}
	
	private void setScale() {
		canvasViewer.setScale(scaleX.getText(), scaleY.getText());
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
