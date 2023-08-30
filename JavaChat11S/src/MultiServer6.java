import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MultiServer6
{
	ServerSocket serverSocket = null;
	Socket socket = null;
	Map<String, PrintWriter> clientMap;

	//생성자
	public MultiServer6()
	{
		//클라이언트의 출력스트림을 저장할 해쉬맵 생성
		clientMap = new HashMap<String, PrintWriter>();
		// 해쉬맵 동기화 설정
		Collections.synchronizedMap(clientMap);
	}
	
	public void init()
	{
		try
		{
			serverSocket = new ServerSocket(9999);  // 9999포트로 서버소켓 객체 생성
			System.out.println("서버가 시작되었습니다.");
			
			while (true)
			{
				socket = serverSocket.accept();
				System.out.println(socket.getInetAddress() + ":" + socket.getPort());
				
				Thread msr = new MultiServerT(socket);	// 쓰레드 생성
				msr.start(); 	// 쓰레드 시동
			}		
		}catch(Exception e) {
			e.printStackTrace();  
		}finally
		{
			try 
			{
				serverSocket.close();
			}catch(Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	//접속된 모든 클라이언트들에게 메시지를 전달.
	public void sendAllMsg(String msg, String name)
	{
		// 출력스트림을 순차적으로 얻어와서 해당 메시지를 출력한다.
		Iterator it = clientMap.keySet().iterator();
		
		while (it.hasNext())
		{
			
			try
			{
				PrintWriter it_out = (PrintWriter)clientMap.get(it.next());
				if (name.equals(""))
				{
					it_out.println(msg);
				} else
				{
					it_out.println(name + " > " + msg);
				}
			}catch(Exception e) 
			{
				System.out.println("예외:" + e);
			}
			
		}
	}
	
	public static void main(String[] args)
	{
		// 서버객체 생성
		MultiServer6 ms = new MultiServer6();
		ms.init();
	}
	
	////////////////////////////////////////////////////////////
	/// 내부 클래스
	/// 클라이언트로부터 읽어온 메시지를 다른 클라이언트(socket)에 보내는 역활을 하는 메서드
	
	class MultiServerT extends Thread
	{
		Socket socket;
		PrintWriter out = null;
		BufferedReader in = null;
		String name = "";

		// 생성자
		public MultiServerT(Socket socket)
		{
			this.socket = socket;
			try
			{
				out = new PrintWriter(this.socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(
										this.socket.getInputStream()));
			} catch (Exception e)
			{
				System.out.println("예외:" + e);
			}
		}
		
		// 쓰레드를 사용하기 위해서 run()메서드 재장의
		@Override
		public void run()
		{
			String s = "";
			
			try
			{
				name = in.readLine();
				System.out.println("["+ name + "]님이 대화방에 입장하셨습니다.");
				
				// 현재 객체가 가지고 있는 소켓을 제외하고 다른 소켓(클라이언트)들에게
				// 접속을 알림.
				sendAllMsg(name + "님이 입장하였습니다.", "");
				
				// 해쉬맵에 키를 name으로 출력스트림 객체를 저장.
				clientMap.put(name, out);
				
				// 입력 스트림이 null이 아니면 반복
				while (in != null)
				{
					s = in.readLine();
					
					if ( s.equals("q") || s.equals("Q"))
						break;
					
					System.out.println(name + ">" +s);
					sendAllMsg(s, name);
				}
				System.out.println("쓰레드 종료.");
				
			} catch (Exception e)
			{
				System.out.println("예외:" + e);
			}finally {
				// 예외가 발생할때 퇴장. 해쉬맵에서 데이터 제거
				// 보통 종료하거나 나가면 java.net.SocketException: 예외발생
				clientMap.remove(name);
				sendAllMsg(name + "님이 퇴장하였습니다.", "");
				System.out.println("현재 접속자 수는 " +clientMap.size() 
							+ "명 입니다.");
			
				try
				{
					in.close();
					out.close();
					
					socket.close();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}	
	}
}