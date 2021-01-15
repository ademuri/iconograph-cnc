import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextInput extends JPanel {
	private final JTextField input;
	private final JLabel label;

	public TextInput(String name, Font font) {
		input = new JTextField();
		input.setFont(font);
		input.setColumns(10);
		add(input);

		label = new JLabel();
		label.setFont(font);
		label.setLabelFor(input);
		label.setText(name);
		add(label);
	}

	public JTextField getInput() {
		return input;
	}
}
