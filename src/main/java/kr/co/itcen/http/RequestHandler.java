package kr.co.itcen.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;

public class RequestHandler extends Thread {
	private Socket socket;
	private static String ROOT = "";
	static {
		ROOT = RequestHandler.class.getClass().getResource("/webapp").getPath();
	}
	
	public RequestHandler( Socket socket ) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			// get IOStream
			OutputStream outputStream = socket.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			// logging Remote Host IP Address & Port
			InetSocketAddress inetSocketAddress = ( InetSocketAddress )socket.getRemoteSocketAddress();
			consoleLog( "connected from " + inetSocketAddress.getAddress().getHostAddress() + ":" + inetSocketAddress.getPort() );

			String request = null;

			while(true) {
				String line = br.readLine();
				
				System.out.println(line);
				// 브라우저가 연결을 끊으면
				if(line == null) {
					break;
				}

				// header만 읽음
				if("".equals(line)) {
					break;
				}

				if(request == null) {
					request = line;
					break;
				}
			}

			String[] tokens = request.split(" ");
			if("GET".equals(tokens[0])){ 			//GET 방식의 경우 처리한다
				consoleLog("request:" + request);
				responseStaticResource(outputStream, tokens[1], tokens[2]);
			} else {								//POST, PUT, DELETE 명령은 무시
				consoleLog("bad request" + request);
				response400error(outputStream, tokens[2]);
			}
			// 예제 응답입니다.
			// 서버 시작과 테스트를 마친 후, 주석 처리 합니다.
			//			outputStream.write( "HTTP/1.1 200 OK\r\n".getBytes( "UTF-8" ) );
			//			outputStream.write( "Content-Type:text/html; charset=utf-8\r\n".getBytes( "UTF-8" ) );
			//			outputStream.write( "\r\n".getBytes() );
			//			outputStream.write( "<h1>이 페이지가 잘 보이면 실습과제 SimpleHttpServer를 시작할 준비가 된 것입니다.</h1>".getBytes( "UTF-8" ) );

		} catch( Exception ex ) {
			consoleLog( "error:" + ex );
		} finally {
			// clean-up
			try{
				if( socket != null && socket.isClosed() == false ) {
					socket.close();
				}

			} catch( IOException ex ) {
				consoleLog( "error:" + ex );
			}
		}			
	}

	private void responseStaticResource(OutputStream outputStream, String url, String protocol) throws IOException {
		if("/".equals(url)) {
			url = "/index.html";
		}

		File file = new File(ROOT + url);					//루트를 webapp 폴더로 잡는다.
		if(!file.exists()) {									//존재하지 않는 파일을 요청하면
			consoleLog("File Not Found:" + url);
			response404error(outputStream, protocol);		//404 error를 반환, 404error 페이즈의 헤더 부분과 바디를 보내면된다.
			return;
		}

		//nio
		byte[] body = Files.readAllBytes(file.toPath());		//file 경로명을 통해 file의 내용을 모두 읽어들인다.
		String contentType = Files.probeContentType(file.toPath());

		//응답
		outputStream.write( (protocol + "HTTP/1.1 200 OK\r\n").getBytes( "UTF-8" ) );
		outputStream.write( ("Content-Type:" + contentType + "; charset=utf-8\r\n").getBytes( "UTF-8" ) );
		outputStream.write( "\r\n".getBytes() );				//헤더의 끝
		outputStream.write( body );
	}

	public void consoleLog( String message ) {
		System.out.println( "[RequestHandler#" + getId() + "] " + message );
	}

	private void response404error(OutputStream outputStream, String protocol) throws IOException{
		String page404 = "/error/404.html";
		File file = new File(ROOT + page404);		
		System.out.println(ROOT + page404);
		
		if(!file.exists()) {									//존재하지 않는 파일을 요청하면
			consoleLog("File Not Found:" + page404);
			return;
		}
		
		System.out.println("404error 페이지");
		//nio
		byte[] body = Files.readAllBytes(file.toPath());		//file 경로명을 통해 file의 내용을 모두 읽어들인다.
		String contentType = Files.probeContentType(file.toPath());

		outputStream.write( (protocol + "HTTP/1.1 200 OK\r\n").getBytes( "UTF-8" ) );
		outputStream.write( ("Content-Type:" + contentType + "; charset=utf-8\r\n").getBytes( "UTF-8" ) );	
		outputStream.write( "\r\n".getBytes() );		// \r이 커서를 맨앞으로 보내는거
		outputStream.write(body);
	}
	
	private void response400error(OutputStream outputStream, String protocol) throws IOException{
		String page404 = "/error/400.html";
		File file = new File(ROOT + page404);		
		System.out.println(ROOT + page404);
		
		if(!file.exists()) {									//존재하지 않는 파일을 요청하면
			consoleLog("File Not Found:" + page404);
			return;
		}
		
		System.out.println("404error 페이지");
		//nio
		byte[] body = Files.readAllBytes(file.toPath());		//file 경로명을 통해 file의 내용을 모두 읽어들인다.
		String contentType = Files.probeContentType(file.toPath());

		outputStream.write( (protocol + "HTTP/1.1 200 OK\r\n").getBytes( "UTF-8" ) );
		outputStream.write( ("Content-Type:" + contentType + "; charset=utf-8\r\n").getBytes( "UTF-8" ) );	
		outputStream.write( "\r\n".getBytes() );		// \r이 커서를 맨앞으로 보내는거
		outputStream.write(body);
	}
}
