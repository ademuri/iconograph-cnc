package com.ademuri.iconograph.options;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
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
	
	private static final int RECEIVE_BUFFER_SIZE = 128; 
	
	private final ConcurrentLinkedDeque<String> writeBuffer = new ConcurrentLinkedDeque<>();
	private final ConcurrentLinkedQueue<String> awaitingOkBuffer = new ConcurrentLinkedQueue<>();
	private long bufferSize = 0;
	private WriteState writeState = WriteState.WRITE;
	private SerialPort serialPort = null;
	private Consumer<String> receivedCallback = null;
	private Consumer<String> sentCallback = null;
	private Runnable errorCallback = null;
	
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
						awaitingOkBuffer.poll();
						if (writeState == WriteState.WRITE) {
							unbufferGcode();
						}
					}
				} else {
					System.err.format("Got non-ok response from GRBL: %s\n", s);
					writeState = WriteState.PAUSE;
					if (errorCallback != null) {
						errorCallback.run();
					}
				}
			}

			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
			}

			@Override
			public void catchException(Exception arg0) {
				writeState = WriteState.PAUSE;
				arg0.printStackTrace();
			}

			@Override
			public boolean delimiterIndicatesEndOfMessage() {
				return true;
			}

			@Override
			public byte[] getMessageDelimiter() {
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
		
		for (String unsent : awaitingOkBuffer) {
			// These commands weren't OK'd, so re-add them to be sent.
			writeBuffer.addFirst(unsent);
		}
		awaitingOkBuffer.clear();
		writeState = WriteState.PAUSE;
	}
	
	public void sendGcode(List<String> gcode) {
		if (serialPort == null || !serialPort.isOpen() || gcode == null || gcode.isEmpty()) {
			return;
		}
		
		synchronized(writeBuffer) {
			boolean wasEmpty = writeBuffer.isEmpty();
			writeBuffer.addAll(gcode);
			bufferSize = writeBuffer.size();
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
		return bufferSize;
	}
	
	/** Writes out one gcode from the buffer */
	private boolean unbufferGcode() {
		if (writeBuffer.isEmpty()) {
			return true;
		}
		
		int receiveBufferUsed = awaitingOkBuffer.stream().mapToInt(line -> line.length()).sum();
		while (!writeBuffer.isEmpty()) {
			String toSend = writeBuffer.peek() + "\n";
			if (receiveBufferUsed + toSend.length() > RECEIVE_BUFFER_SIZE) {
				break;
			}
			writeBuffer.poll();
			receiveBufferUsed += toSend.length();
			bufferSize--;
			
			byte[] buffer = toSend.getBytes(StandardCharsets.US_ASCII);
			int ret = serialPort.writeBytes(buffer, buffer.length);
			//System.out.println("Sent: " + toSend);

			if (ret != buffer.length) {
				System.err.format("Tried to write %d bytes to serial, but instead wrote %d\n", buffer.length, ret);
				sentCallback.accept(toSend.substring(0, ret));
				return false;
			}
			
			awaitingOkBuffer.add(toSend);
			
			if (sentCallback != null) {
				sentCallback.accept(toSend);
			}
			
		}
		
		return true;
	}
	
	public boolean isPaused() {
		return this.writeState == WriteState.PAUSE;
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
	
	public void setErrorCallback(Runnable callback) {
		this.errorCallback = callback;
	}
}
