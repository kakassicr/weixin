package com.chat.activity;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.chat.db.MyDatabaseHelper;

public class ChatActivity extends Activity implements OnClickListener {
	private ListView msgListView;
	private EditText inputText;
	private Button send;
	private Button back;
	static private MsgAdapter adapter;
	static private String friend;
	private TextView account2;
	static private List<Message> msgList;
	private Read read;
	private MyDatabaseHelper dbHelper;
	private SQLiteDatabase db;
	ContentValues contentValues;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.msg);
		friend = getIntent().getStringExtra("friend");
		account2 = (TextView) this.findViewById(R.id.account2);
		account2.setText(friend);// 标题显示好友名字
		msgList = new ArrayList<Message>();
		adapter = new MsgAdapter(ChatActivity.this, R.layout.msg_item, msgList);
		inputText = (EditText) findViewById(R.id.input_text);
		send = (Button) findViewById(R.id.send);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		msgListView = (ListView) findViewById(R.id.msg_list_view);
		msgListView.setAdapter(adapter);
		read = new Read();
		Log.d("ChatActivity", read.toString());
		read.execute();
		dbHelper = new MyDatabaseHelper(this, "Chat.db", null, 1);
		db = dbHelper.getWritableDatabase();
		initMsgs(); // 初始化消息数据
		msgListView.setSelection(msgList.size()); // 将ListView定位到最后一行
		contentValues = new ContentValues();
		Log.d("ChatActivity", "------------1------------");
		Log.d("ChatActivity", contentValues.toString());
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				send();
			}
		});
	}

	private void initMsgs() {
		String sql = "(sender=? and getter=?) or (sender=? and getter=?)";
		String[] paras={ListActivity.account,friend,friend,ListActivity.account};
		Cursor cursor = db.query("Messages", null, sql, paras, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				// 遍历Cursor对象，取出数据并打印
				String sender = cursor.getString(cursor
						.getColumnIndex("sender"));
				String getter = cursor.getString(cursor
						.getColumnIndex("getter"));
				String content = cursor.getString(cursor
						.getColumnIndex("content"));
				String sendTimer = cursor.getString(cursor
						.getColumnIndex("sendTimer"));
				Message msg;
				if (sender.equals(ListActivity.account)) {
					msg = new Message(content, Message.TYPE_SENT);
				} else {
					msg = new Message(content, Message.TYPE_RECEIVED);
				}
				msg.setSender(sender);
				msg.setGetter(getter);
				msg.setSendTime(sendTimer);
				msgList.add(msg);
			} while (cursor.moveToNext());
		}
		cursor.close();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			// read.cancel(true);
			Intent intent = new Intent(this, ListActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

	Socket socket;
	User user = new User();
	Message ms = null;
	static ObjectOutputStream oos;
	ObjectInputStream ois;

	private class Read extends AsyncTask<Void, Message, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Log.d("ChatActivity", "doInBackground");
				socket = LoginActivity.socket;
				oos = new ObjectOutputStream(socket.getOutputStream());
				while (true) {

					ois = new ObjectInputStream(socket.getInputStream());
					ms = (Message) ois.readObject();
					publishProgress(ms);
				}
			} catch (Exception e) {
				Toast.makeText(ChatActivity.this, "无法建立链接", Toast.LENGTH_SHORT)
						.show();
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Message... values) {
			Message msg = values[0];
			insetMessage(msg);// 写入本地数据库
			Toast.makeText(ChatActivity.this, "onProgressUpdate",
					Toast.LENGTH_SHORT).show();
			msg.setType(Message.TYPE_RECEIVED);
			if (friend.equals(msg.getSender())) {
				msgList.add(msg);
				Log.d("ChatActivity", "------------3------------");
				Log.d("ChatActivity", contentValues.toString());
				adapter.notifyDataSetChanged(); // 当有新消息时，刷新ListView中的显示
				msgListView.setSelection(msgList.size()); // 将ListView定位到最后一行
			}
			super.onProgressUpdate(values);
			Log.d("ChatActivity", msgList.get(msgList.size() - 1).getCon());
		}

	}

	public void send() {
		try {
			String content = inputText.getText().toString();

			if (!"".equals(content)) {
				Message msg = new Message(content, Message.TYPE_SENT);
				msg.setSender(ListActivity.account);
				msg.setGetter(friend);
				msg.setSendTime(new Timestamp(System.currentTimeMillis())
						.toString());
				insetMessage(msg);// 写入本地数据库
				msgList.add(msg);
				Log.d("ChatActivity", "------------2------------");
				Log.d("ChatActivity", contentValues.toString());
				adapter.notifyDataSetChanged(); // 当有新消息时，刷新ListView中的显示
				msgListView.setSelection(msgList.size()); // 将ListView定位到最后一行
				inputText.setText("");
				oos.writeObject(msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insetMessage(Message msg) {
		contentValues.put("sender", msg.getSender());
		contentValues.put("getter", msg.getGetter());
		contentValues.put("content", msg.getCon());
		contentValues.put("sendTimer", msg.getSendTime());
		contentValues.put("isGet", msg.getIsGet());
		db.insert("Messages", null, contentValues); // 插入数据
		contentValues.clear();
	}
}