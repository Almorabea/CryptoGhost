package net.almorabea.cryptoghost;

import java.security.NoSuchAlgorithmException;

import scrypttest.SCryptUtil;
import scrypttest.Utilites;
import blatest.Blake2b;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
	
	
	EditText Password ; 
	EditText Email    ; 
	Button   Login    ;
    ProgressDialog progress;
	
	boolean doubleBackToExitPressedOnce;
	 SharedPreferences sp ;
    Context context ;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

        context = this ;
		sp = getSharedPreferences("net.almorabea.cryptoghost.Register" ,Context.MODE_PRIVATE);
		
		startup();
	
	}
		
	
	public void startup ()
	{
		
		Password = (EditText) findViewById(R.id.password_c1);
		Email    = (EditText) findViewById(R.id.email_l);
		Login    = (Button)   findViewById(R.id.login);
	
		
		Login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				final String email    = Email.getText().toString();
				final String password = Password.getText().toString();
				
			//	final  boolean check ;
                boolean dd = false ;

                progress = ProgressDialog.show(context, context.getString(R.string.logging),context.getString(R.string.pls_wait), true);
                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        // do the thing that takes a long time
                            try {
                                final     boolean      check = Authentication(password , email );


                                if (check == true )
                                {

                                    finish () ;

                                    Intent i = new Intent (Login.this,Encryption.class);

                                    startActivity(i);



                                }
                                else
                                {


                                }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {

                               progress.dismiss();

                                if(check == false) {
                                    Password.setText("");
                                    Email.setText("");
                                    Toast.makeText(getBaseContext(),context.getString(R.string.email_pass_wrong), Toast.LENGTH_LONG).show();
                                }
                            //
                            }
                        });

                            } catch (NoSuchAlgorithmException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                    }
                }).start();


             //   Toast.makeText(getBaseContext(), "Your email or password is wronge", Toast.LENGTH_LONG).show();

				

				
				
				
				
			}
			
			
			
			
		});
		
	}
	
	private boolean Authentication (String p , String e ) throws NoSuchAlgorithmException
	{
		
		String email       = sp.getString("email", "");
		String password    = sp.getString("password","");
        String FingerPrint = sp.getString("FP", "");
		
		/*
		Toast.makeText(this, email +" ", Toast.LENGTH_LONG).show();
		Toast.makeText(this, "Auth : "+password + " ", Toast.LENGTH_LONG).show();
		Toast.makeText(this, FingerPrint + " ", Toast.LENGTH_LONG).show();
		*/
			
		String FP = "" ; 
		
		///// equation 
		
		  String full =  p + e ;   // attaching password and email togather
		  
		  byte strbyte [] = full.getBytes();  // convert string to bytes 
		
		 Blake2b.Mac b2b = Blake2b.Mac.newInstance(strbyte);
         
         //Base64 ba = new Base64 ();
      
          String hashed = Utilites.toHex(b2b.digest());
          
        //  FP =       SCryptUtil.scrypt(hashed,16384, 8, 1);
          
          boolean c =     SCryptUtil.check(hashed, FingerPrint);
     
          if(c == true )
          {
        	  return true ; 
          }
          else{
        	  return false ; 
          }
		
	
		
	}
	
	@Override
	public void onBackPressed() {
	 
		
		if (doubleBackToExitPressedOnce) {
	        super.onBackPressed();
	        return;
	    }

	    this.doubleBackToExitPressedOnce = true;
	    Toast.makeText(this, context.getString(R.string.back_to_exit), Toast.LENGTH_SHORT).show();

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	            doubleBackToExitPressedOnce=false;                       
	        }
	    }, 2000);
	} 
	
	
}
