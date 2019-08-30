package kr.co.itcen.network2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class EchoServer {

	private static final int PORT = 8000;
	public static void main(String[] args) {
		// TODO Auto-generated method stub	    

		ServerSocket serverSocket = null;
		try {
			//1. 서버소켓 생성
			serverSocket = new ServerSocket();		//TCP/IP 설치되어있지 않다면 Exception

			InetAddress ia= InetAddress.getLocalHost();

			//2. Binding
			//	Socket에 SocketAddress(IPAddress + Port)
			//	바인딩한다.
			InetSocketAddress isa = new InetSocketAddress(ia,PORT);
			if(!serverSocket.isBound())
				serverSocket.bind(isa);
			System.out.println("[TCPServer] binding " + ia.getHostAddress() + ":" + PORT);

			//3. Accept
			//	클라이언트로부터 연결요청(Connect)을 기다린다.(Blocking)
			Socket socket = serverSocket.accept();		//요청이들어오면 socket 생성
			InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
			String remoteHostAddress = inetRemoteSocketAddress.getAddress().getHostAddress();
			int remoteHostPort = inetRemoteSocketAddress.getPort();

			System.out.println("[TCPServer] connected from client[" + remoteHostAddress
					+ ":" + remoteHostPort + "]");

			try {
				//4. IOStream 받아오기
				InputStream is = socket.getInputStream();
				OutputStream os = socket.getOutputStream();

				while(true) {
					byte[] buffer = new byte[256];
					int readByteCount = is.read(buffer);	//읽을게 없으면 blocking
					System.out.println(readByteCount);
					if(readByteCount == -1) {				//client가 연결을 끊은 경우
						// 정상종료 : remote socket이 close()
						// 메소드를 통해서 정상적으로 소켓을 닫은 경우
						System.out.println("[TCPServer] closed by client");
						break;
					}
					
					String data = new String(buffer, 0, readByteCount, "UTF-8");
					System.out.println("[TCPServer] received :" + data);
					os.write(data.getBytes());
				}	//end while
				
				//7. 데이터 쓰기
				
			} catch(SocketException e) {
				System.out.println("[TCPServer] closed abnomally");
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				if(socket != null && !socket.isClosed())
					socket.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//7. 자원정리
			try {
				if(serverSocket != null)
					serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
