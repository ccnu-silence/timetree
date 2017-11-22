package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.yey.kindergaten.R;
import com.yey.kindergaten.widget.CustomAutoCompleteTextView;


public class BindPostBoxFragment extends Fragment{
	CustomAutoCompleteTextView emil_et;
    String state;
    List<Map<String,String>> aList = new ArrayList<Map<String,String>>();
	String[] from = {"txt"};
	int[] to = { R.id.txt};
    private  String[] lastname_email={"@qq.com","@163.com","@yahoo.com","@sina.com",
    		          "@126.com","@sohu.com" };
    Map<String, String>email=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments()!=null){
			state=getArguments().getString("state");
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		View view=inflater.inflate(R.layout.bindpostboxfragment,container, false);	
		emil_et=(CustomAutoCompleteTextView) view.findViewById(R.id.id_write_emil_et);
		emil_et.setThreshold(3);
		emil_et.setDropDownBackgroundResource(R.color.white);						

		emil_et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable edit) {
                String num=edit.toString();
                if(aList!=null){
                	aList.clear();
               }                 
            	   for(int i=0;i<lastname_email.length;i++){                 
                  	 if(num.contains("@")){
                  		 String lastname=num.substring(num.indexOf("@"));                  		
                  	     String  nums=num.substring(0,num.indexOf("@"));                 		 
                  	    if(lastname_email[i].contains(lastname)){
                  	    	System.out.println("xaxa");
                  		     if(lastname.contains("q")){
                  		    	Map<String, String>email_q=new HashMap<String, String>();
                  		    	email_q.put("txt",nums+lastname_email[0]); 	                  	    	
                             	aList.add(email_q); 
                             	break;
                  		     }else if(lastname.contains("1")){
                  		    	Map<String, String>email_11=new HashMap<String, String>();
                  		    	Map<String, String>email_12=new HashMap<String, String>();
                  		    	email_11.put("txt",nums+lastname_email[1]); 
                  		    	email_12.put("txt",nums+lastname_email[4]); 
                  		    	aList.add(email_11); 
                  		    	aList.add(email_12); 
                  		    	break;
                  		     }else if(lastname.contains("y")){
                  		    	Map<String, String>email_q1=new HashMap<String, String>();
                  		    	email_q1.put("txt",nums+lastname_email[2]); 	 
                             	aList.add(email_q1); 
                             	break;
                  		     }else if(lastname.contains("s")){
                  		     	Map<String, String>email_s1=new HashMap<String, String>();
              		    	    Map<String, String>email_s2=new HashMap<String, String>();
              		    	    email_s1.put("txt",nums+lastname_email[3]); 
              		    	    email_s2.put("txt",nums+lastname_email[5]); 
              		    	    aList.add(email_s1); 
              		    	    aList.add(email_s2); 
              		    	     break;
                   		     }		  
                  		 }                		 
                  	 }else{
                  	 	 email=new HashMap<String, String>();
                  	  	 email.put("txt",num+lastname_email[i]); 	 
                      	 aList.add(email);  
                  	 }                                         	 
                   }                                       
      		    SimpleAdapter adapter = new SimpleAdapter(BindPostBoxFragment.this.getActivity(), aList, R.layout.autocomplete_layout, from, to);
       		    emil_et.setAdapter(adapter);
                
			}
		});
		
	  
		
		return view;
	}
}
