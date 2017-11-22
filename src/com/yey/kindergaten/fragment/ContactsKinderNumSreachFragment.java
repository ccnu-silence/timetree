package com.yey.kindergaten.fragment;

import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.ServiceSreachKinderResultActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ContactsKinderNumSreachFragment extends Fragment implements OnClickListener{

	EditText editText;
	TextView   sreachbtn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.contacts_kindernumsreachfg, null);
		editText=(EditText) view.findViewById(R.id.contact_addfriend_edittext);
		sreachbtn=(TextView) view.findViewById(R.id.contact_addfriend_sreachbt);
		sreachbtn.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
	   switch (v.getId()) {
	case R.id.contact_addfriend_sreachbt:
		Intent intent=new Intent(getActivity(),ServiceSreachKinderResultActivity.class);
		startActivity(intent);
		break;

	default:
		break;
	}
		
	}
}
