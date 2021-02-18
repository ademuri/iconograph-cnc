package com.ademuri.iconograph.options;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListenerWithExceptions;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListenerWithExceptions;

public class SerialGrbl {
	private enum WriteState {
		WRITE,
		PAUSE,
	}
	
	private final ConcurrentLinkedQueue<String> writeBuffer = new ConcurrentLinkedQueue<>();
	private WriteState writeState = WriteState.WRITE;
	private SerialPort serialPort = null;
	private Consumer<String> receivedCallback = null;
	private Consumer<String> sentCallback = null;
	
	//public SerialGrbl(
	
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
		
		
		serialPort.addDataListener(new SerialPortMessageListenerWithExceptions() {
			@Override
			public void serialEvent(SerialPortEvent event) {
				String s = new String(event.getReceivedData(), StandardCharsets.US_ASCII);
				if (receivedCallback != null) {
					receivedCallback.accept(s);
				}
				
				if (s.strip().equals("ok")) {
					synchronized(writeBuffer) {
						unbufferGcode();
					}
				} else {
					System.err.format("Got non-ok response from GRBL: %s\n", s);
					writeState = WriteState.PAUSE;
				}
			}

			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
			}

			@Override
			public void catchException(Exception arg0) {
				arg0.printStackTrace();
			}

			@Override
			public boolean delimiterIndicatesEndOfMessage() {
				return true;
			}

			@Override
			public byte[] getMessageDelimiter() {
				// TODO Auto-generated method stub
				return "\n".getBytes();
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
	
	public void sendGcode(List<String> gcode) {
		if (serialPort == null || !serialPort.isOpen()) {
			return;
		}
		
		synchronized(writeBuffer) {
			boolean wasEmpty = writeBuffer.isEmpty();
			writeBuffer.addAll(gcode);
			if (wasEmpty) {
				writeState = WriteState.WRITE;
				unbufferGcode();
			}
		}
	}
	
	public void sendGcode(String gcode) {
		sendGcode(List.of(gcode));
	}
	
	public void clearBuffer() {
		synchronized(writeBuffer) {
			writeBuffer.clear();
		}
	}
	
	public long getBufferSize() {
		return writeBuffer.size();
	}
	
	/** Writes out one gcode from the buffer */
	private boolean unbufferGcode() {
		if (writeBuffer.isEmpty()) {
			return true;
		}
		
		String toSend = writeBuffer.poll() + "\n";
		byte[] buffer = toSend.getBytes(StandardCharsets.US_ASCII);
		int ret = serialPort.writeBytes(buffer, buffer.length);

		if (ret != buffer.length) {
			System.err.format("Tried to write %d bytes to serial, but instead wrote %d\n", buffer.length, ret);
			sentCallback.accept(toSend.substring(0, ret));
			return false;
		}
		if (sentCallback != null) {
			sentCallback.accept(toSend);
		}
		
		return true;
	}
	
	public void pause() {
		synchronized(writeBuffer) {
			this.writeState = WriteState.PAUSE;
		}
	}
	
	public void unpause() {
		synchronized(writeBuffer) {
			if (this.writeState != WriteState.WRITE) {
				this.writeState = WriteState.WRITE;
				unbufferGcode();
			}
		}
	}

	public void setReceivedCallback(Consumer<String> callback) {
		this.receivedCallback = callback;
	}
	
	public void setSentCallback(Consumer<String> callback) {
		this.sentCallback = callback;
	}
}
