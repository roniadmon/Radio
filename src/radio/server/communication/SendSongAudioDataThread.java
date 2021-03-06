package radio.server.communication;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import radio.client.ClientConfig;
import radio.core.utilities.AudioUtil;
import radio.server.ServerConfig;

public class SendSongAudioDataThread extends Thread {

	private Socket clientSocket;
	private File songFile;
	private boolean isSending = true;
	
	public SendSongAudioDataThread(Socket clientSocket, File songFile) {
		this.songFile = songFile;
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		try {
			sendSongAudioData(songFile);
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		isSending=false;
	}

	public void sendSongAudioData(File songFile) throws IOException, UnsupportedAudioFileException {
		DatagramSocket socket = null;
		AudioInputStream audioIS = null;
		try {
			socket = new DatagramSocket();
			audioIS = AudioUtil.getFAudioInputStream(songFile);
			int count;
			byte[] songBytes = new byte[ServerConfig.DATAGRAM_PACKET_SIZE];
			while (isSending == true && (count = audioIS.read(songBytes)) > 0) {
				DatagramPacket packet = new DatagramPacket(songBytes, count, clientSocket.getInetAddress(), ClientConfig.UdpPort);
				socket.send(packet);	
				Thread.sleep(3);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		finally {
			if (socket != null) 
				socket.close();
			if (audioIS != null) 
				audioIS.close();
		}
	}
}
