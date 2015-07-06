package net.almorabea.cryptoghost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.almorabea.cryptoghost.R;


public class DialogActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
         final EditText password = (EditText)findViewById(R.id.password_c);
         final EditText password1 = (EditText)findViewById(R.id.password_c1);
        Button cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(RESULT_CANCELED,i);
                finish();
            }
        });
        Button encrypt = (Button)findViewById(R.id.login);
        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            
            			 Intent i = new Intent();
                         i.putExtra("password",password.getText().toString());
                         i.putExtra("password1",password1.getText().toString());
                         setResult(RESULT_OK, i);
                         finish();
            	
            		 
            		 
            		 
            	 
            	
              
            }
        });
    }

}
