package net.almorabea.cryptoghost;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.spongycastle.util.encoders.Base64;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

//Ahmad Almorabea

public class Decryption extends AppCompatActivity {
	
	ImageView image ; 
	Button decrypt ; 
	RadioButton dec_pass ; 
	RadioButton dec_other_pass ; 
	EditText rename ; 
	 SharedPreferences sp ;
	  private static final int READ_REQUEST_CODE = 42 ;
	// TextView path ;
	 EditText decrypted_name ; 
	 Context context;
	 String GPass  = "" ;
	 boolean doubleBackToExitPressedOnce;
    String extension = "" ;
    ProgressDialog progress;
	  
	 Uri globalUri = null  ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.decryption);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		sp = getSharedPreferences("net.almorabea.cryptoghost.Register" ,Context.MODE_PRIVATE);
		
		context = this ; 
		
		startup();
		
	}
	
	
	
	public void startup()
	{
		 image 		    = (ImageView) findViewById(R.id.imageD) ; 
		 decrypt        = (Button) findViewById(R.id.decryptB); 
		 dec_pass       = (RadioButton) findViewById(R.id.dec_pass); 
		 dec_other_pass = (RadioButton) findViewById(R.id.dec_other_pass); ; 
		 decrypted_name = (EditText) findViewById(R.id.decrypted_name) ; 
		// path           = (TextView) findViewById(R.id.path);
		
		
		 
		 image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

            if (Build.VERSION.SDK_INT < 19)
            {
/*
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                //i.addCategory(Intent.CATEGORY_OPENABLE);

                i.setType("*");

                startActivityForResult(i,READ_REQUEST_CODE);
*/
                Intent photoPickerIntent = new Intent(Intent. ACTION_GET_CONTENT , android.provider.MediaStore.Images.Media. EXTERNAL_CONTENT_URI);
                //photoPickerIntent.setType( "*/*" );
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/CryptoGhost/");
                photoPickerIntent.setDataAndType(uri, "*/*");
                startActivityForResult(photoPickerIntent,READ_REQUEST_CODE);


            }
                else
            {

                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);

                i.setType("*/*");

                startActivityForResult(i,READ_REQUEST_CODE);
            }


				
				
			}
		});
		 
		 
		 decrypt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			
				
				
				
				if (globalUri == null)
				{
					Toast.makeText(getBaseContext(), context.getString(R.string.choose_file), Toast.LENGTH_SHORT).show();
				}
				else
				{
					if(decrypted_name.getText().toString().equals(""))
					{
						Toast.makeText(getBaseContext(), context.getString(R.string.choose_name_dec_file), Toast.LENGTH_SHORT).show();
					}
					else
					{
						
					if(dec_other_pass.isChecked() == true)
					{


						showCustomAlertDialog();
						
						
					}else
					{
						////////

                        progress = ProgressDialog.show(context, context.getString(R.string.decrypting), context.getString(R.string.pls_wait), true);
                        new Thread(new Runnable() {
                            @Override
                            public void run()
                            {
                                // do the thing that takes a long time
                                try {
                                    Decrypt();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run()
                                        {

                                            progress.dismiss();


                                            //
                                        }
                                    });

                                } catch (NoSuchAlgorithmException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();


						
					}
					
					
				}
			
			}
			}
			 
			 
			 
			 
			 
		 });
		 
		 
		
	}
	
	
	

	@SuppressLint("NewApi")
	@SuppressWarnings("null")
	public void Decrypt() throws Exception 
	{
		
		 String FingerPrint = sp.getString("FP", "");
		 

		 
		 String tt = Utility.toHex(FingerPrint.getBytes()) ;
		 
		 String keyBase64 = FingerPrint.replaceFirst(".*[$]", "");
		 byte [] keyBytes = Base64.decode(keyBase64);
		 
		 

		 
	
		 		
		 byte[] nonce = new byte[16] ; 
		 
		 

	        byte[] byteArray = null;
	        
	        try
	        {
	        @SuppressWarnings("resource")
			InputStream inputStream = getContentResolver().openInputStream(globalUri) ;
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        byte[] b = new byte[1024*8];
	        int bytesRead =0;
	        
	        while ((bytesRead = inputStream.read(b)) != -1)
	        {
	        bos.write(b, 0, bytesRead);
	        }
	        
	        byteArray = bos.toByteArray();
	        }
	        catch (IOException e)
	        {
	        e.printStackTrace();
	        }
	        
	   	 byte cipherText [] = new byte [byteArray.length];
	        
	        cipherText = byteArray ;
            ///////////// Extension /////////
           String temp = "";
           byte a1 [] = new byte [11];
        for(int i = 0 ;  i <= 10  ; i++)
        {
            a1[i] += cipherText[i];
        }

        temp = new String(a1);
        int  temp1 = 0 ;

        for(int i = 0 ;  i <temp.length() ; i++)
        {
            if(String.valueOf(temp.charAt(i)).equals("|"))
            {
                break;
            }else {
                extension += String.valueOf(temp.charAt(i));
                temp1++;
            }
        }

	       //////////////////Extension //////////////

	        // getting nonce

            temp1 = temp1+1;
	        for(int i = 0 ; i < 16; i++ )
	        {
	        	nonce[i] = cipherText[temp1];
              //  Toast.makeText(getBaseContext(),"counter : " + counter+ "len : "+ temp1,Toast.LENGTH_SHORT).show();
                temp1 = temp1+1 ;

	        }
	        

	        
	     cipherText =   Arrays.copyOfRange(cipherText, temp1,  cipherText.length);


	       
	            
	            try{
	            
	             SecretKeySpec key = new SecretKeySpec (keyBytes, "AES") ;
	            
	            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC") ; 
	            
	               IvParameterSpec zeroIve = new IvParameterSpec(nonce); 
	            
	             cipher.init(Cipher.ENCRYPT_MODE, key,zeroIve);
	            
	            IvParameterSpec decryptionIv = new IvParameterSpec (cipher.doFinal(nonce),0,16) ;
	            
	             cipher.init(Cipher.DECRYPT_MODE, key,decryptionIv);
	            
	            byte [] plainText = new byte [cipher.getOutputSize(cipherText.length)];
	            
	            int ptLength = cipher.update(cipherText, 0 , cipherText.length , plainText, 0 ) ; 
	            
	            ptLength  += cipher.doFinal(plainText,ptLength);



			        
			        String Name = decrypted_name.getText().toString() ; 
			        
			        File f = new File(Environment.getExternalStorageDirectory()+"/CryptoGhost/Decrypted Files", Name + "."+extension);
		
			        if (f.exists()) {
			              f.delete();
			             // Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show();
			        }
		
			        try {
			          FileOutputStream fos=new FileOutputStream(f.getPath());
		
			          fos.write(plainText);
			          fos.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(getBaseContext(), context.getString(R.string.file_dec_success), Toast.LENGTH_SHORT).show();

                            }
                        });


			       
			        }
			        catch (IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                String folder_main = "CryptoGhost";
                                String folder_enc = "Encrypted Files";
                                String folder_dec = "Decrypted Files";
                                String folder_res = "Restore";

                                File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                                File f1 = new File(Environment.getExternalStorageDirectory(), folder_main + "/" + folder_enc);
                                File f2 = new File(Environment.getExternalStorageDirectory(), folder_main + "/" + folder_dec);
                                File f3 = new File(Environment.getExternalStorageDirectory(), folder_main + "/" + folder_res);
                                if (!f.exists()) {
                                    f.mkdirs();
                                    f1.mkdir();
                                    f2.mkdir();
                                    f3.mkdir();
                                }

                            }
                        });

			        }
		        
	        /////Extensions//////
                }catch(NoSuchAlgorithmException a){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_9), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                catch(NoSuchProviderException b) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_10), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(NoSuchPaddingException c) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_3), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(InvalidKeyException d) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_11), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(InvalidAlgorithmParameterException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_5), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(IllegalBlockSizeException f) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_6), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(BadPaddingException g) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_7), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(ShortBufferException h){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_8), Toast.LENGTH_LONG).show();
                        }
                    });

                }


                    extension = "";
	        
	        
	        
	        
	}
	
	
	@SuppressLint("NewApi")
	@SuppressWarnings("null")
	public void Decrypt_external(String password) throws Exception {
		
		
		  MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] keyBytes = digest.digest(password.getBytes("UTF-8"));
		
		
		 byte[] nonce = new byte[16] ; 
		 
		 
	       // File file = new File(Environment.getExternalStorageDirectory()+"/CryptoGhost/", "a.jpg");
	        byte[] byteArray = null;
	        
	        try
	        {
	        @SuppressWarnings("resource")
			InputStream inputStream = getContentResolver().openInputStream(globalUri) ;
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        byte[] b = new byte[1024*8];
	        int bytesRead =0;
	        
	        while ((bytesRead = inputStream.read(b)) != -1)
	        {
	        bos.write(b, 0, bytesRead);
	        }
	        
	        byteArray = bos.toByteArray();
	        }
	        catch (IOException e)
	        {
	        e.printStackTrace();
	        }
	        
	   	 byte cipherText [] = new byte [byteArray.length];
	        
	        cipherText = byteArray ;


        ///////////// Extension /////////
        String temp = "";
        byte a1 [] = new byte [11];
        for(int i = 0 ;  i <= 10  ; i++)
        {
            a1[i] += cipherText[i];
        }

        temp = new String(a1);
        int  temp1 = 0 ;

        for(int i = 0 ;  i <temp.length() ; i++)
        {
            if(String.valueOf(temp.charAt(i)).equals("|"))
            {
                break;
            }else {
                extension += String.valueOf(temp.charAt(i));
                temp1++;
            }
        }

        //////////////////Extension //////////////

        // getting nonce

        temp1 = temp1+1;
        for(int i = 0 ; i < 16; i++ )
        {
            nonce[i] = cipherText[temp1];
            //  Toast.makeText(getBaseContext(),"counter : " + counter+ "len : "+ temp1,Toast.LENGTH_SHORT).show();
            temp1 = temp1+1 ;

        }



        cipherText =   Arrays.copyOfRange(cipherText, temp1,  cipherText.length);






        try{
	            
	             SecretKeySpec key = new SecretKeySpec (keyBytes, "AES") ;
	            
	            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC") ; 
	            
	               IvParameterSpec zeroIve = new IvParameterSpec(nonce); 
	            
	             cipher.init(Cipher.ENCRYPT_MODE, key,zeroIve);
	            
	            IvParameterSpec decryptionIv = new IvParameterSpec (cipher.doFinal(nonce),0,16) ; 
	            
	             cipher.init(Cipher.DECRYPT_MODE, key,decryptionIv);
	            
	            byte [] plainText = new byte [cipher.getOutputSize(cipherText.length)];
	            
	            int ptLength = cipher.update(cipherText, 0 , cipherText.length , plainText, 0 ) ; 
	            
	            ptLength  += cipher.doFinal(plainText,ptLength);
	            

			        
			        String Name = decrypted_name.getText().toString() ; 
			        
			        File f = new File(Environment.getExternalStorageDirectory()+"/CryptoGhost/Decrypted Files", Name + "."+extension);
		
			        if (f.exists()) {
			              f.delete();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(getBaseContext(), context.getString(R.string.replace_file), Toast.LENGTH_SHORT).show();

                            }
                        });

			        }
		
			        try {
			          FileOutputStream fos=new FileOutputStream(f.getPath());
		
			          fos.write(plainText);
			          fos.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(getBaseContext(), context.getString(R.string.file_dec_success), Toast.LENGTH_SHORT).show();

                            }
                        });


			       
			        }
			        catch (IOException e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {

                                String folder_main = "CryptoGhost";
                                String folder_enc = "Encrypted Files";
                                String folder_dec = "Decrypted Files";
                                String folder_res = "Restore";

                                File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                                File f1 = new File(Environment.getExternalStorageDirectory(), folder_main + "/" + folder_enc);
                                File f2 = new File(Environment.getExternalStorageDirectory(), folder_main + "/" + folder_dec);
                                File f3 = new File(Environment.getExternalStorageDirectory(), folder_main + "/" + folder_res);
                                if (!f.exists()) {
                                    f.mkdirs();
                                    f1.mkdir();
                                    f2.mkdir();
                                    f3.mkdir();
                                }

                              //  Toast.makeText(getBaseContext(), context.getString(R.string.saving_problem), Toast.LENGTH_LONG).show();

                            }
                        });


			        }


            /////Exceptions////
                }catch(NoSuchAlgorithmException a){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_9), Toast.LENGTH_LONG).show();
                    }
                });
        }
                catch(NoSuchProviderException b) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_10), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(NoSuchPaddingException c) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_3), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(InvalidKeyException d) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_11), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(InvalidAlgorithmParameterException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_5), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(IllegalBlockSizeException f) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_6), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(BadPaddingException g) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_7), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(ShortBufferException h){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.dec_problem_8), Toast.LENGTH_LONG).show();
                        }
                    });

                }

		        extension = "";
	}
	
	
	
	
	
	 public void showCustomAlertDialog(){
	        LayoutInflater layoutInflater = LayoutInflater.from(context);

	        View promptView = layoutInflater.inflate(R.layout.custom_dec, null);
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
	        alertDialogBuilder.setView(promptView);
	        final EditText password = (EditText)promptView.findViewById(R.id.password_c);
	       
	        // setup a dialog window
	        alertDialogBuilder
	                .setTitle("Decrypt")
	                .setCancelable(false)
	                .setPositiveButton("Decrypt", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id) {

	                    	
	                    	 if(password.getText().toString().length() < 10) {
	        					 
	     					    Toast.makeText(getBaseContext(), context.getString(R.string.pass_less_10), Toast.LENGTH_SHORT).show();
	     					    return;
	     					 }
	                 	 
	                 	 else
	                 	 {
	                 		 
	                 			 if(password.getText().toString().subSequence(0, 9).equals("123456789"))
	                 			 {
	                 				 Toast.makeText(getBaseContext(), context.getString(R.string.pass_not_acceptable), Toast.LENGTH_SHORT).show();
	                 			 }
	                 			 else
	                 			 {
	                 				  Toast.makeText(context, context.getString(R.string.pass_col) + password.getText().toString() ,Toast.LENGTH_SHORT).show();
	                 				  GPass = password.getText().toString() ;

                                     progress = ProgressDialog.show(context, context.getString(R.string.decrypting), context.getString(R.string.pls_wait), true);
                                     new Thread(new Runnable() {
                                         @Override
                                         public void run()
                                         {
                                             // do the thing that takes a long time
                                             try {
                                                 Decrypt_external(GPass);

                                                 runOnUiThread(new Runnable() {
                                                     @Override
                                                     public void run()
                                                     {

                                                         progress.dismiss();


                                                         //
                                                     }
                                                 });

                                             } catch (NoSuchAlgorithmException e) {
                                                 // TODO Auto-generated catch block
                                                 e.printStackTrace();
                                             } catch (Exception e) {
                                                 e.printStackTrace();
                                             }

                                         }
                                     }).start();


	                 				  
	                 			 
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
	    
	
	
	
	
	  @Override
	   public void onActivityResult (int reqCode , int resCode, Intent resultData)
	   {
        //   image 		    = (ImageView) findViewById(R.id.imageD) ;

           if(reqCode == READ_REQUEST_CODE && resCode == Activity.RESULT_OK)
		  {
			  
			  Uri uri = null ; 
			  
			  if(resultData != null)
			  {
                  try {
                      uri = resultData.getData();

                      //  showImage(uri);
                      globalUri = uri;


                      if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {

                          image.setImageResource(R.drawable.lock_144);
                      }
                      else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {

                          image.setImageResource(R.drawable.lock_144);
                      }
                      else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {

                          image.setImageResource(R.drawable.lock_96);
                      }
                      else {

                          image.setImageResource(R.drawable.lock_96);
                      }

                   //   image.setImageResource(R.drawable.lock_96);


                  }catch (Exception x){    }
				  
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
				Intent i = new Intent(Decryption.this,Encryption.class);
				startActivity(i);
				return false;
			}
		});
	      
	      menu.add(context.getString(R.string.Help)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
				
					finish();
					Intent i = new Intent(Decryption.this,Help.class);
					startActivity(i);
					return false;
					
					
				}
			});
	      
	      menu.add(context.getString(R.string.Settings)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {

					finish();
					Intent i = new Intent(Decryption.this,Settings.class);
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
		    Toast.makeText(this, context.getString(R.string.back_to_exit), Toast.LENGTH_SHORT).show();

		    new Handler().postDelayed(new Runnable() {

		        @Override
		        public void run() {
		            doubleBackToExitPressedOnce=false;                       
		        }
		    }, 2000);
		}








	
}
