package com.chat.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.chat.R;

public class ListActivity extends Activity implements OnClickListener{
	public static String account;
	public static String[] friendlist;
	private Button back;
	private TextView account2;//标题中自己的用户名

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friendlist);
		back=(Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		account2=(TextView) findViewById(R.id.account2);
//		String account=getIntent().getStringExtra("account");	
		account2.setText(account);
//		String friendlist=getIntent().getStringExtra("friendlist");a
//		String[] friendlist2=friendlist.split(" ");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				ListActivity.this, android.R.layout.simple_list_item_1, friendlist);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
					Intent intent = new Intent(ListActivity.this,
							ChatActivity.class);
					intent.putExtra("friend", friendlist[index]);
					startActivity(intent);
					finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			try {
				LoginActivity.socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}
}

