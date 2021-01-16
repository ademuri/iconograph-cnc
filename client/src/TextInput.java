import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

public class TextInput extends JPanel {
	protected final JTextField input;
	protected final JLabel label;
	
	protected Ini ini = null;
	protected String configSection = "";
	protected String configName = "";

	public TextInput(String name, Font font, String defaultValue) {
		input = new JTextField();
		input.setFont(font);
		input.setColumns(10);
		input.setText(defaultValue);
		add(input);

		label = new JLabel();
		label.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		label.setLabelFor(input);
		label.setText(name);
		add(label);
	}

	public JTextField getInput() {
		return input;
	}
	
	public void saveToIni() {
		synchronized(ini) {
			ini.put(configSection, configName, input.getText());
			try {
				ini.store();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setConfig(Ini ini, String configSection, String configName) {
		if (this.ini != null) {
			throw new IllegalStateException(String.format("setConfig called multiple times for TextInput %s", label.getText()));
		}
		this.ini = ini;
		this.configSection = configSection;
		this.configName = configName;
		
		input.setText(ini.get(configSection).getOrDefault(configName, input.getText()));
		
		this.input.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				saveToIni();
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
	}
}
