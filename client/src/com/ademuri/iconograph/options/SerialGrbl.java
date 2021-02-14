package com.ademuri.iconograph.options;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListenerWithExceptions;
import com.fazecast.jSerialComm.SerialPortEvent;

public class SerialGrbl {
	private SerialPort serialPort = null;
	private Consumer<String> callback = null;
	
	public boolean isOpen() {
		return serialPort != null && serialPort.isOpen();
	}

	public boolean open(String port, int baudRate) {
		if (isOpen()) {
			throw new IllegalArgumentException("Can't open serial port while it's already open");
		}
		
		serialPort = SerialPort.getCommPort(port);
		serialPort.setBaudRate(baudRate);
		serialPort.setParity(SerialPort.NO_PARITY);
		serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
		serialPort.setNumDataBits(8);
		if (!serialPort.openPort()) {
			return false;
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
				if (callback != null) {
					callback.accept(s);
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
		
		return true;
	}
	
	public void close() {
		if (serialPort != null) {
			serialPort.removeDataListener();
			serialPort.closePort();
		}
	}
	
	public void sendGcode(String gcode) {
		if (serialPort == null || !serialPort.isOpen()) {
			return;
		}
		
		String toSend = gcode + "\n";
		byte[] buffer = toSend.getBytes(StandardCharsets.US_ASCII);
		int ret = serialPort.writeBytes(buffer, buffer.length);

		if (ret != buffer.length) {
			System.err.format("Tried to write %d bytes to serial, but instead wrote %d\n", buffer.length, ret);
		}
	}

	public void setReceivedCallback(Consumer<String> callback) {
		if (serialPort == null) {
			throw new IllegalArgumentException("serialPort not initialized");
		}
		this.callback = callback;
	}
}
