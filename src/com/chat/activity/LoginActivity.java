package com.chat.activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import com.chat.R;
import com.chat.common.Message;
import com.chat.common.User;
import com.chat.service.ClientUser;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private Button login;
	private EditText account;
	private EditText password;
	private String mAccount, mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		login = (Button) findViewById(R.id.login);
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {		
				account = (EditText) findViewById(R.id.account);
				password = (EditText) findViewById(R.id.password);
				mAccount = account.getText().toString();
				mPassword = password.getText().toString();
				connect();		
			}
		});
	}

	static Socket socket = null;
	User user = new User();
	Message ms;
	public void connect() {
		Log.d("LoginActivity",mAccount);
		Log.d("LoginActivity",mPassword);
		user.setAccount(mAccount);
		user.setPassword(mPassword);
		AsyncTask<Void, String, Boolean> read = new AsyncTask<
				Void, String, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					socket = new Socket("192.168.56.1", 9999);
//					socket = new Socket("10.0.2.2", 9999);
//					socket = new Socket("192.168.1.6", 9999);
					ObjectOutputStream oos=new ObjectOutputStream(
							socket.getOutputStream());
					oos.writeObject(user);
					ObjectInputStream ois=new ObjectInputStream(
							socket.getInputStream());
					ms=(Message)ois.readObject();
				} catch (Exception e) {
					Toast.makeText(LoginActivity.this, "无法建立链接",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result&&(ms.getType()==1)) {
						Intent intent = new Intent(LoginActivity.this,
								ListActivity.class);
//						intent.putExtra("account",user.getAccount());
						ListActivity.account=user.getAccount();
						startActivity(intent);
						finish();
					} else {
						Toast.makeText(LoginActivity.this, "用户名密码错误",
								Toast.LENGTH_SHORT).show();
					}
				}
		};
		read.execute();
	}
}
