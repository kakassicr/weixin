package com.chat.activity;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.chat.R;

public class ListActivity extends Activity implements OnClickListener{
	private Button back;
	private String[] data = { "Apple", "Banana", "Orange", "Watermelon",
			"Pear", "Grape", "Pineapple", "Strawberry", "Cherry", "Mango" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat);
		back=(Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				ListActivity.this, android.R.layout.simple_list_item_1, data);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
					Intent intent = new Intent(ListActivity.this,
							ChatActivity.class);
					startActivity(intent);
					finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}
}

