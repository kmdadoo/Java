import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Receiver4 extends Thread
{
	Socket socket;
	BufferedReader in = null;
	
	//Socket 을 매개변수로 받는 생성자.
	public Receiver4(Socket socket)
	{
		this.socket = socket;
		
		try
		{
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		}catch(IOException e) 
		{
			System.out.println("예외:"+e);
		}
	}
	
	//run() 메소드 재정의
	@Override
	public void run()
	{
		while (in != null)
		{
			try
			{
				// 입력은 main 쓰레드에서 받기 때문에 다음 코드는 작동하지 않는다
				/*
				String s = in.nextLine();
									
				if (s.equals("q") || s.equals("Q"))
				{
					break;
				}else
				{
					System.out.println("Thread Receive : " + s);
				}
				*/
				System.out.println("Thread Receive : " + in.readLine());
			}catch(java.net.SocketException e) 
			{
				// 여기서 while 문을 멈춰 주어야 한다.
				break;
			}catch(Exception e) 
			{
				System.out.println("예외:"+e);
			}
		}
		
		try
		{
			in.close();
		}catch(Exception e) 
		{
			System.out.println("예외3:"+e);
		}
	}
}
