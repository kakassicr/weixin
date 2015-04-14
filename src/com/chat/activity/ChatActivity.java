package com.chat.activity;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.R;
import com.chat.common.Message;
import com.chat.common.User;

public class ChatActivity extends Activity implements OnClickListener {
	private ListView msgListView;
	private EditText inputText;
	private Button send;
	private Button back;
	private MsgAdapter adapter;
	private String friend;
	private TextView account2;
	private List<Message> msgList = new ArrayList<Message>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.msg);
		friend = getIntent().getStringExtra("friend");
		account2 = (TextView) this.findViewById(R.id.account2);
		account2.setText(friend);// 标题显示好友名字
		initMsgs(); // 初始化消息数据
		adapter = new MsgAdapter(ChatActivity.this, R.layout.msg_item, msgList);
		inputText = (EditText) findViewById(R.id.input_text);
		send = (Button) findViewById(R.id.send);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		msgListView = (ListView) findViewById(R.id.msg_list_view);
		msgListView.setAdapter(adapter);
		connect();
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {			
				send();
			}
		});
	}

	private void initMsgs() {
		Message msg1 = new Message("Hello guy.", Message.TYPE_RECEIVED);
		msgList.add(msg1);
		Message msg2 = new Message("Hello. Who is that?", Message.TYPE_SENT);
		msgList.add(msg2);
		Message msg3 = new Message("This is Tom. Nice talking to you. ",
				Message.TYPE_RECEIVED);
		msgList.add(msg3);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			Intent intent = new Intent(this, ListActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

	Socket socket = null;
	User user = new User();
	Message ms=null;
	ObjectOutputStream oos;
	public void connect() {
		AsyncTask<Void, Message, Void> read = new AsyncTask<Void, Message, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					socket = LoginActivity.socket;
					oos = new ObjectOutputStream(
							socket.getOutputStream());			
					while (true) {
						ObjectInputStream ois = new ObjectInputStream(
								socket.getInputStream());
						ms = (Message) ois.readObject();
						publishProgress(ms);
					}
				} catch (Exception e) {
					Toast.makeText(ChatActivity.this, "无法建立链接",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Message... values) {
				Message msg =values[0];
				msg.setType(Message.TYPE_RECEIVED);
				msgList.add(msg);
				adapter.notifyDataSetChanged(); // 当有新消息时，刷新ListView中的显示
				msgListView.setSelection(msgList.size()); // 将ListView定位到最后一行
				super.onProgressUpdate(values);
			}
		};
		read.execute();
	}

	public void send() {
		try {
			String content = inputText.getText().toString();
			if (!"".equals(content)) {
				Message msg = new Message(content, Message.TYPE_SENT);
				msg.setSender(ListActivity.account);
				msg.setGetter(friend);
				msg.setSendTime(new java.util.Date().toString());	
				oos.writeObject(msg);
				msgList.add(msg);
				adapter.notifyDataSetChanged(); // 当有新消息时，刷新ListView中的显示

				msgListView.setSelection(msgList.size()); // 将ListView定位到最后一行
				inputText.setText("");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}