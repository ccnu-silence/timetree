package com.yey.kindergaten.fragment;

import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.CaptureActivity;
import com.yey.kindergaten.util.AppConstants;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ContactsBarcodeSearchFragment extends Fragment implements OnClickListener{

	Button barsreachbtn;
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.contacts_barcodesreachfg, null);
		barsreachbtn=(Button) view.findViewById(R.id.addkind_barsreach);
		barsreachbtn.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		Intent intent;
         switch (v.getId()) {
		case R.id.addkind_barsreach:
			 intent=new Intent(getActivity(),CaptureActivity.class);
			 intent.putExtra("state", AppConstants.CONTACTS);
			 startActivity(intent);
			
			break;

		default:
			break;
		}
		
	}
}
