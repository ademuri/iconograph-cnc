import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.Controller;

public class SmartControl <T extends Controller> implements ControlListener {
	protected final T control;
	protected final ControlCallback callback;
	protected float prevValue = 0;
	
	SmartControl(T control, ControlCallback callback) {
		this.control = control;
		this.callback = callback;
		
		control.addListener(this);
	}
	
	SmartControl(T control) {
		this(control, null);
	}
	
	public T getControl() {
		return control;
	}
	
	public float getValue() {
		return control.getValue();
	}
	
	public String getStringValue() {
		return control.getStringValue();
	}
	
	@Override
	public void controlEvent(ControlEvent arg0) {
		if (arg0 != null) {
			callback.controlEvent(arg0);
		}
	}
	
	public interface ControlCallback {
		void controlEvent(ControlEvent arg0);
	}
}
