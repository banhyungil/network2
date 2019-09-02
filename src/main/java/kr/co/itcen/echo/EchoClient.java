package kr.co.itcen.echo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {

	private static String SERVER_IP = "192.168.1.76";
	private static int SERVER_PORT = 8000;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Socket socket = null;
		
		try {
			//1. 소켓생성
			socket = new Socket();

			//2. 서버연결
			InetSocketAddress isa = new InetSocketAddress(SERVER_IP, SERVER_PORT);
			socket.connect(isa);
			System.out.println("[TCPClient] connected");
			
			//3. IOStream 받아오기
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			//4. 쓰기
			Scanner sc = new Scanner(System.in);
			String data = null;
			while(true) {
				System.out.print(">>");
				data = sc.next();
				
				System.out.println(data);
				
				if("exit".equals(data))
					return;
				
				os.write(data.getBytes("UTF-8"));
				
				//5. 데이터 읽기
				byte[] buffer = new byte[256];
				int readByteCount = is.read(buffer);	//읽을게 없으면 blocking
				
				
				if(readByteCount == -1) {				//client가 연결을 끊은 경우
					// 정상종료 : remote socket이 close()
					// 메소드를 통해서 정상적으로 소켓을 닫은 경우
					System.out.println("[TCPClient] closed by server");
					return;
				}
				
				data = new String(buffer, 0, readByteCount, "UTF-8");
				System.out.println("<<" + data);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(socket != null && !socket.isClosed())
					socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
