
package com.chat.service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.chat.common.Message;
public class ClientConServer {

	public boolean sendLoginInfoToServer(Object o,Socket s)
	{
		boolean b=false;
		try {
			Log.d("ClientConServer","123");
			ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(o);
			
			ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
			
			Message ms=(Message)ois.readObject();

//			if(ms.getMesType().equals("1"))
			{

//				ClientConServerThread ccst=new ClientConServerThread(s);
//
//				ccst.start();
//				ManageClientConServerThread.addClientConServerThread
//				(((User)o).getUserId(), ccst);
				b=true;
//			}else{

				s.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}finally{
			
		}
		return b;
	}
	
	public void SendInfoToServer(Object o)
	{
		/*try {
			Socket s=new Socket("127.0.0.1",9999);
			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}finally{
			
		}*/
	}
	
}
