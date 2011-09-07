package de.unihamburg.vsis.jadexAndroid_test;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class BaseActivity extends Activity {
	
	protected Button findButtonById(int id){
		View v = findViewById(id);
		if (v instanceof Button) {
			return (Button) v;
		} else {
			throw new ClassCastException("findButtonById called for a non-button id!");
		}
	}
	
	protected TextView findTextViewById(int id){
		View v = findViewById(id);
		if (v instanceof TextView) {
			return (TextView) v;
		} else {
			throw new ClassCastException("findTextViewById called for a non-textView id!");
		}
	}
	
	protected ListView findListViewById(int id){
		View v = findViewById(id);
		if (v instanceof ListView) {
			return (ListView) v;
		} else {
			throw new ClassCastException("findListViewById called for a non-listView id!");
		}
	}
}
