package net.almorabea.cryptoghost;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.io.File;
import scrypttest.SCryptUtil;
import scrypttest.Utilites;
import blatest.Blake2b;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import static android.widget.Toast.LENGTH_SHORT;

//Ahmad Almorabea

public class Settings extends AppCompatActivity {


    Button ChangePassword;
    Button BugReport;
    Button SendSuggestions;
    Button DestroyAccount;
    Button Backup_key;
    SharedPreferences sp;
    Context context;
    String FILENAME, jour;
    String folder_key  = "Key";
    boolean doubleBackToExitPressedOnce;
    String FullPath ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sp = getSharedPreferences("net.almorabea.cryptoghost.Register", Context.MODE_PRIVATE);

        context = this;

        startup();

    }


    public void startup() {
        ChangePassword = (Button) findViewById(R.id.changePassword);
        BugReport = (Button) findViewById(R.id.bugReport);
        SendSuggestions = (Button) findViewById(R.id.sendSuggestions);
        DestroyAccount = (Button) findViewById(R.id.destroyAccount);
        Backup_key = (Button) findViewById(R.id.backup_key);

        final Context con = this;
        final AlertDialog.Builder builder = new AlertDialog.Builder(con);


        ChangePassword.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {



                builder.setCancelable(true);
                builder.setTitle(context.getString(R.string.confirm));
                builder.setMessage(context.getString(R.string.change_pass_message));
                // builder.setInverseBackgroundForced(true);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                showCustomAlertDialog();

                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }


        });


        Backup_key.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = sp.getString("email", "");
                String password = sp.getString("password", "");
                String FingerPrint = sp.getString("FP", "");



                FullPath = Environment.getExternalStorageDirectory()+"/CryptoGhost/"+folder_key ;

                Toast.makeText(getBaseContext(),context.getString(R.string.key_saved),Toast.LENGTH_LONG).show();

                File f = new File(FullPath);

                f.mkdir();

                String full = email + password ;

                String Enc = null;
                try {
                    Enc = Encrypt(FingerPrint.getBytes(),full);

                    sp.edit().putBoolean("BUP",true).commit();

                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(getBaseContext(),context.getString(R.string.encryption_error),Toast.LENGTH_SHORT).show();
                }

                /*
                String nonce = Enc.substring(0,31);
                String cipher = Enc.substring(32,Enc.length());
                */





             //   save(Enc);




            }
        });


        BugReport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"bug@cryptoghost.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "/Crypto Ghost-Bug/");
                i.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getBaseContext(), context.getString(R.string.email_clients), LENGTH_SHORT).show();
                }

            }


        });


        SendSuggestions.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"cg@cryptoghost.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "/Crypto Ghost-Suggestion/");
                i.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getBaseContext(), context.getString(R.string.email_clients), LENGTH_SHORT).show();
                }

            }


        });


        DestroyAccount.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                builder.setCancelable(true);
                builder.setTitle(context.getString(R.string.confirm));
                builder.setMessage(context.getString(R.string.destroy_message));
                // builder.setInverseBackgroundForced(true);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                sp.edit().clear().commit();

                                Intent i = new Intent(Settings.this, Register.class);
                                startActivity(i);
                                finish();

                                Toast.makeText(getBaseContext(), context.getString(R.string.account_deleted), LENGTH_SHORT).show();

                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            }


        });


    }


    private void Register_P(String p, String e) throws NoSuchAlgorithmException {
        String password = p;
        String email = e;

        String FP = "";

        ///// equation
        String full = password + email;   // attaching password and email togather

        byte strbyte[] = full.getBytes();  // convert string to bytes

        Blake2b.Mac b2b = Blake2b.Mac.newInstance(strbyte);

        //Base64 ba = new Base64 ();


        String hashed = Utilites.toHex(b2b.digest());


        FP = SCryptUtil.scrypt(hashed, 16384, 8, 1);


        sp.edit().putString("email", email).commit();
        sp.edit().putString("password", password).commit();
        sp.edit().putString("FP", FP).commit();


    }


    public void showCustomAlertDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View promptView = layoutInflater.inflate(R.layout.custom_dialog1, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        final EditText password = (EditText) promptView.findViewById(R.id.password_c);
        final EditText password1 = (EditText) promptView.findViewById(R.id.password_c1);
        final EditText email = (EditText) promptView.findViewById(R.id.email1);
        // setup a dialog window
        alertDialogBuilder
                .setTitle("Change Password")
                .setCancelable(false)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        if (password.getText().toString().length() < 10) {

                            Toast.makeText(getBaseContext(), context.getString(R.string.pass_less_10), LENGTH_SHORT).show();
                            return;
                        } else {

                            if (password.getText().toString().equals(password1.getText().toString())) {

                                if (password.getText().toString().subSequence(0, 9).equals("123456789")) {
                                    Toast.makeText(getBaseContext(), context.getString(R.string.pass_not_acceptable), LENGTH_SHORT).show();
                                } else {


                                    try {

                                        if (email.getText().toString().length() < 6) {
                                            Toast.makeText(getBaseContext(), context.getString(R.string.email_too_short), LENGTH_SHORT).show();
                                        } else {
                                            Register_P(password1.getText().toString(), email.getText().toString());
                                        }


                                    } catch (NoSuchAlgorithmException e) {
                                        Toast.makeText(getBaseContext(), context.getString(R.string.problem_saving_password), LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }


                            } else {
                                Toast.makeText(getBaseContext(), context.getString(R.string.pass_dont_match), LENGTH_SHORT).show();
                            }


                        }


                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Toast.makeText(context, "Custom AlertDialog Cancelled", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }


    public String  Encrypt(byte [] m, String k) throws NoSuchAlgorithmException {

        byte[] msgNumber = new byte[16];

        SecureRandom SR = new SecureRandom();

        SR.nextBytes(msgNumber);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(k.getBytes());

        String theK =  new String (keyBytes) ;




        byte[] cipherText = null ;
        IvParameterSpec zeroIve = null ;

        try {
             zeroIve = new IvParameterSpec(msgNumber);

            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");



            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIve);

            IvParameterSpec encryptionIv = new IvParameterSpec(cipher.doFinal(msgNumber), 0, 16);

            cipher.init(Cipher.ENCRYPT_MODE, key, encryptionIv);



            cipherText = new byte[cipher.getOutputSize(m.length)];

            int ctLength = cipher.update(m, 0, m.length, cipherText, 0);

            ctLength += cipher.doFinal(cipherText, ctLength);



            byte[] Full ;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write(msgNumber);
            outputStream.write( cipherText );

            Full =outputStream.toByteArray( );



            save(Full);



        } catch (NoSuchAlgorithmException a) {
            Toast.makeText(this, context.getString(R.string.encryption_error), Toast.LENGTH_LONG).show();
        } catch (NoSuchProviderException b) {
            Toast.makeText(this, context.getString(R.string.encryption_error), Toast.LENGTH_LONG).show();
        } catch (NoSuchPaddingException c) {
            Toast.makeText(this, context.getString(R.string.encryption_error), Toast.LENGTH_LONG).show();
        } catch (InvalidKeyException d) {
            Toast.makeText(this, context.getString(R.string.encryption_error), Toast.LENGTH_LONG).show();
        } catch (InvalidAlgorithmParameterException e) {
            Toast.makeText(this, context.getString(R.string.encryption_error), Toast.LENGTH_LONG).show();
        } catch (IllegalBlockSizeException f) {
            Toast.makeText(this, context.getString(R.string.encryption_error), Toast.LENGTH_LONG).show();
        } catch (BadPaddingException g) {
            Toast.makeText(this, context.getString(R.string.encryption_error), Toast.LENGTH_LONG).show();
        } catch (ShortBufferException h) {
            Toast.makeText(this, context.getString(R.string.encryption_error), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String n = "" ;
        for(int i = 0 ; i <msgNumber.length; i++)
        {

            n += msgNumber[i] ;
        }

        String full = n + Utility.toHex(cipherText);


        return full;
    }




    public void save(byte [] full) {



        try {
            File file = new File(FullPath+"/"+"key.crypto");
            FileOutputStream fos = new FileOutputStream(file);


            if (full == null) {
                fos.close();
            } else {

                fos.write(full);
                fos.close();
            }
        } catch (Exception x) {

        }
    }







    @Override
	     protected void onActivityResult(int reqCode, int resCode, Intent data) {
		  
		  if (reqCode == 1) {
	            if(resCode == RESULT_OK){
	                String password1 = data.getStringExtra("password");
	                String password2 = data.getStringExtra("password1");


	            }
	            if (resCode == RESULT_CANCELED) {
	              //  Toast.makeText(context, "Custom Activity Dialog Cancelled", LENGTH_SHORT).show();
	            }
	        }
		  
	  }



	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	      MenuInflater inflater = getMenuInflater();
	      
	      menu.add(context.getString(R.string.Enc)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
			
				finish();
				Intent i = new Intent(Settings.this,Encryption.class);
				startActivity(i);
				return false;
			}
		});
	      
	      menu.add(context.getString(R.string.Dec)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
				
					finish();
					Intent i = new Intent(Settings.this,Decryption.class);
					startActivity(i);
					return false;
					
					
				}
			});
	      
	      menu.add(context.getString(R.string.Help)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {

					finish();
					Intent i = new Intent(Settings.this,Help.class);
					startActivity(i);
					
					return false;
				}
			});
	      
	      
	      inflater.inflate(R.menu.register, menu);
	      return true;
	    } 
	
	 @Override
		public void onBackPressed() {
		 
			
			if (doubleBackToExitPressedOnce) {
		        super.onBackPressed();
		        return;
		    }

		    this.doubleBackToExitPressedOnce = true;
		    Toast.makeText(this, context.getString(R.string.back_to_exit), LENGTH_SHORT).show();

		    new Handler().postDelayed(new Runnable() {

		        @Override
		        public void run() {
		            doubleBackToExitPressedOnce=false;                       
		        }
		    }, 2000);
		} 
	
}
