package net.almorabea.cryptoghost;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

//Ahmad Almorabea

public class Encryption extends AppCompatActivity {
	
	
	ImageView imageE ; 
	byte [] global = null ; 
	String GPass  = "" ;
 	String real ; 
	RadioButton encrypt_P ;
	RadioButton encrypt_share ;
	 Context context;
	 SharedPreferences sp ;
	 TextView path ; 
	 EditText encrypted_name ; 
	 boolean doubleBackToExitPressedOnce;
    String extension = "" ;
    ProgressDialog progress;
   ///   String realPath = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encryption);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		context = this;
		
		sp = getSharedPreferences("net.almorabea.cryptoghost.Register" ,Context.MODE_PRIVATE);
		
		startup();


        ////////// Start - Check if the user took key backup or not  //////
        boolean backup_check = sp.getBoolean("BUP",false);
        if(backup_check == true)
        {

        }else {
            Toast.makeText(getBaseContext(), context.getString(R.string.heavy_dev), Toast.LENGTH_LONG).show();
            Toast.makeText(getBaseContext(), context.getString(R.string.heavy_dev_1), Toast.LENGTH_LONG).show();
        }
		////////// END - Check if the user took key backup or not  //////
	}
	
	
	public void startup ()
	{
		
		imageE = (ImageView) findViewById (R.id.imageD);
		Button encrypt = (Button) findViewById(R.id.decryptB);
		encrypt_P = (RadioButton) findViewById(R.id.encrypt_password);
		encrypt_share = (RadioButton) findViewById(R.id.encrypt_share);
		path   = (TextView) findViewById(R.id.path);
		encrypted_name = (EditText) findViewById (R.id.encrypted_name);
		
		imageE.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {


                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    // 2. pick image only
                    intent.setType("*/*");
                    // 3. start activity
                    startActivityForResult(intent, 0);

								
			}
			
			
		}  );
		
		encrypt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

			
				
				
				if (global == null )
				{
					Toast.makeText(getBaseContext(), context.getString(R.string.choose_file), Toast.LENGTH_SHORT).show();
				}
				
				else 
				{   
					
				if(encrypted_name.getText().toString().equals(""))
				{
					Toast.makeText(getBaseContext(), context.getString(R.string.choose_name_enc_file), Toast.LENGTH_SHORT).show();
				}
				else
				{
				

						if (encrypt_P.isChecked() == false && encrypt_share.isChecked() == false )
						{
							Toast.makeText(getBaseContext(), context.getString(R.string.choose_option), Toast.LENGTH_SHORT).show();
						}
						else 
						{
							
							if(encrypt_P.isChecked() == true )
							{
                                ////////

                                progress = ProgressDialog.show(context,context.getString(R.string.encrypting),context.getString(R.string.pls_wait), true);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        // do the thing that takes a long time
                                        try {
                                            Encrypt(global);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run()
                                                {
                                                    Toast.makeText(getBaseContext(), context.getString(R.string.file_enc_success) , Toast.LENGTH_SHORT).show();
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
                                        /*
                                        try {
                                            Encrypt(global);
                                        }catch (Exception n )
                                        {

                                        }

                                        */


                                /////////
							}
							else 
							{
								 showCustomAlertDialog();
								
							}
							
						}
					
					
					

				
				
				
				}
				///
				}
				

				
			}
			
			
			
			
		});
		
		
		
		
	}
	
	

	
	@SuppressLint("TrulyRandom") 
	public void Encrypt( byte[] input) throws Exception 
	{
		
		 String FingerPrint = sp.getString("FP", "");
		 
		 
				 String tt = Utility.toHex(FingerPrint.getBytes()) ;
				 
				 String keyBase64 = FingerPrint.replaceFirst(".*[$]", "");
				 byte [] keyBytes = Base64.decode(keyBase64);

		        byte [] msgNumber = new byte [16] ;
		        
		         SecureRandom SR = new SecureRandom();
		         
		         SR.nextBytes(msgNumber);
		      
		       
		        try{
		        IvParameterSpec zeroIve = new IvParameterSpec(msgNumber); 
		        
		        SecretKeySpec key = new SecretKeySpec (keyBytes, "AES") ;
		        
		        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC") ; 
		        

		        /// encryption 
		        
		        cipher.init(Cipher.ENCRYPT_MODE, key,zeroIve);
		        
		        IvParameterSpec encryptionIv = new IvParameterSpec (cipher.doFinal(msgNumber),0,16) ; 
		        
		         cipher.init(Cipher.ENCRYPT_MODE, key ,encryptionIv);
		        

		        
		        
		    byte []   cipherText = new byte [cipher.getOutputSize(input.length)];
		        
		     int    ctLength = cipher.update(input,0,input.length,cipherText, 0);
		        
		        ctLength += cipher.doFinal(cipherText, ctLength);
		        

		       



					   
			    String Name = encrypted_name.getText().toString();
					   
		        File photo = new File(Environment.getExternalStorageDirectory()+"/CryptoGhost/Encrypted Files", Name + ".cg");

		        if (photo.exists()) {
		              photo.delete();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getBaseContext(), context.getString(R.string.replace_file), Toast.LENGTH_SHORT).show();

                        }
                    });



		        }

		        try {
		          FileOutputStream fos=new FileOutputStream(photo.getPath());

		          

		          
		          byte[] Full = new byte[cipherText.length + msgNumber.length];
		          
		          ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                  outputStream.write(extension.getBytes());
		          outputStream.write( msgNumber );
		          outputStream.write( cipherText );
		          
		          Full =outputStream.toByteArray( );


		         
		          fos.write(Full);
		          fos.close();
		          


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getBaseContext(), context.getString(R.string.file_enc_success) , Toast.LENGTH_SHORT).show();

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
		           
		        
		        /////////Exceptions/////
		        }
               catch(NoSuchAlgorithmException a){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_1), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(NoSuchProviderException b) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_2), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(NoSuchPaddingException c) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_3), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(InvalidKeyException d) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_4), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(InvalidAlgorithmParameterException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_5), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(IllegalBlockSizeException f) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_6), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(BadPaddingException g) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_7), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(ShortBufferException h){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_8), Toast.LENGTH_LONG).show();
                        }
                    });

                }



    }






	@SuppressLint("TrulyRandom") 
	public void Encrypt_share ( byte[] input,String password) throws Exception
	{


		        MessageDigest digest = MessageDigest.getInstance("SHA-256");
		        byte[] keyBytes = digest.digest(password.getBytes("UTF-8"));



		        byte [] msgNumber = new byte [16] ;

		         SecureRandom SR = new SecureRandom();

		         SR.nextBytes(msgNumber);




		        try{
		        IvParameterSpec zeroIve = new IvParameterSpec(msgNumber);

		        SecretKeySpec key = new SecretKeySpec (keyBytes, "AES") ;

		        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC") ;


		        /// encryption

		        cipher.init(Cipher.ENCRYPT_MODE, key,zeroIve);

		        IvParameterSpec encryptionIv = new IvParameterSpec (cipher.doFinal(msgNumber),0,16) ;

		         cipher.init(Cipher.ENCRYPT_MODE, key ,encryptionIv);




		    byte []   cipherText = new byte [cipher.getOutputSize(input.length)];

		     int    ctLength = cipher.update(input,0,input.length,cipherText, 0);

		        ctLength += cipher.doFinal(cipherText, ctLength);

		        System.out.println("cipher : " + Utility.toHex(cipherText,ctLength) + "  byte : " + ctLength);







			    String Name = encrypted_name.getText().toString();

		        File photo = new File(Environment.getExternalStorageDirectory()+"/CryptoGhost/Encrypted Files", Name + ".cg");

		        if (photo.exists()) {
		              photo.delete();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.replace_file), Toast.LENGTH_SHORT).show();
                        }
                    });

		        }

		        try {
		          FileOutputStream fos=new FileOutputStream(photo.getPath());





		          byte[] Full = new byte[cipherText.length + msgNumber.length];

		          ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                  outputStream.write(extension.getBytes());
		          outputStream.write( msgNumber );
		          outputStream.write( cipherText );


		          Full =outputStream.toByteArray( );



		          fos.write(Full);
		          fos.close();

		      //    System.out.println("Full : " + Utility.toHex(Full));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getBaseContext(), context.getString(R.string.file_enc_success) , Toast.LENGTH_SHORT).show();

                        }
                    });



                    //////////////////// share

                 //   boolean isPNG = (photo.getPath().toLowerCase().endsWith(".png")) ? true : false;

                    Intent i = new Intent(Intent.ACTION_SEND);
                    //Set type of file


                    i.setType("*/*");//With png image file or set "image/*" type
                    Uri imgUri = Uri.fromFile(new File(photo.getPath()));//Absolute Path of image
                    i.putExtra(Intent.EXTRA_STREAM, imgUri);//Uri of image
                    startActivity(Intent.createChooser(i, "Share via"));



                    ///////////////// end share


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

                         //   Toast.makeText(getBaseContext(), context.getString(R.string.saving_problem), Toast.LENGTH_LONG).show();

                        }
                    });


		        }



                    ////////Exceptions//////
               }


                catch(NoSuchAlgorithmException a){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_1), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(NoSuchProviderException b) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_2), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(NoSuchPaddingException c) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_3), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(InvalidKeyException d) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_4), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(InvalidAlgorithmParameterException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_5), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(IllegalBlockSizeException f) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_6), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(BadPaddingException g) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_7), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(ShortBufferException h){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), context.getString(R.string.enc_problem_8), Toast.LENGTH_LONG).show();
                        }
                    });

                }





	}

	
	
	  @Override
	     protected void onActivityResult(int reqCode, int resCode, Intent data) {

          if(reqCode == 0 && resCode == Activity.RESULT_OK) {

              Uri uri = null;

              if (data != null) {
                  try {
                      uri = data.getData();


                      @SuppressWarnings("resource")
                      InputStream inputStream = getContentResolver().openInputStream(uri);
                      ByteArrayOutputStream bos = new ByteArrayOutputStream();


                      String path;
                      int size = 15728640;
                    //  path = uri.getPath();

                      try {

                          path = getRealPathFromURI(context, uri);

                          int length = path.length() ;

                          if(isNumeric(path.substring(length-2,length)))
                          {
                              if (path.contains("image"))
                              {
                                  extension = "jpg|";


                              }else if(path.contains("audio"))
                              {
                                  extension = "mp3|";


                              }
                              else if(path.contains("video"))
                              {
                                  extension = "mp4|";
                              }
                              else{
                                  Toast.makeText(getBaseContext(),context.getString(R.string.choose_from_internal),Toast.LENGTH_LONG).show();
                              }

                          }
                          else
                          {
                              extension = path.substring(path.lastIndexOf(".") + 1, path.length());
                              extension = extension + "|";
                          }


                      }catch (Exception x)
                      {

                          path = uri.getPath();

                          int length = path.length() ;

                          if(isNumeric(path.substring(length-2,length))) {
                              if (path.contains("image")) {
                                  extension = "jpg|";


                              } else if (path.contains("audio")) {
                                  extension = "mp3|";


                              } else if (path.contains("video")) {
                                  extension = "mp4|";
                              } else {
                                  Toast.makeText(getBaseContext(),context.getString(R.string.choose_from_internal),Toast.LENGTH_LONG).show();

                              }


                          }
                          else
                          {
                              extension = path.substring(path.lastIndexOf(".") + 1, path.length());
                              extension = extension + "|";
                          }

                      }


                      if (inputStream.available() > size ) {

                          Toast.makeText(getBaseContext(), context.getString(R.string.size_big), Toast.LENGTH_LONG).show();
                          imageE.setImageResource(R.drawable.ic_pick_photo);

                      } else {

                          byte[] b = new byte[1024 * 8];
                          int bytesRead = 0;

                          while ((bytesRead = inputStream.read(b)) != -1) {
                              bos.write(b, 0, bytesRead);
                          }

                          global = bos.toByteArray();

                          if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {

                              imageE.setImageResource(R.drawable.lock_144);
                          }
                          else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {

                              imageE.setImageResource(R.drawable.lock_144);
                          }
                          else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {

                              imageE.setImageResource(R.drawable.lock_96);
                          }
                          else {

                              imageE.setImageResource(R.drawable.lock_96);
                          }


                      }

                      }catch(FileNotFoundException e){
                          e.printStackTrace();
                      }catch(IOException e){
                          e.printStackTrace();
                      }





              }
          }


         /*
		  if (reqCode == 1) {
	            if(resCode == RESULT_OK){
	                String username = data.getStringExtra("password");
	                String password = data.getStringExtra("password1");

	            }
	            if (resCode == RESULT_CANCELED) {
	             //   Toast.makeText(context, "Custom Activity Dialog Cancelled", Toast.LENGTH_SHORT).show();
	            }
	        }
		  
	        if(resCode == Activity.RESULT_OK && data != null){
	            String realPath="";
	            // SDK < API11

                    if (Build.VERSION.SDK_INT < 11)
                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                        // SDK >= 11 && SDK < 19
                    else if (Build.VERSION.SDK_INT < 19) {
                        realPath = "";

                            realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

                    }
                    // SDK > 19 (Android 4.4)
                    else
                        realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());

	            
	            setTextViews(Build.VERSION.SDK_INT, data.getData().getPath(),realPath);
	        }
	   */


	       }
	  
	  private void setTextViews(int sdk, String uriPath,String realPath){


	        Bitmap bitmap = null;
	        try {
                Uri  uriFromPath = Uri.fromFile(new File(realPath));
	            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriFromPath));
                imageE.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
	            e.printStackTrace();
                Toast.makeText(context, "Error 702 : ", Toast.LENGTH_SHORT).show();
	        }
          catch(Exception d)
          {
              Toast.makeText(context, "Error 713 : " , Toast.LENGTH_SHORT).show();
          }

          long  size_file = 5500 ;

          try {
              size_file = getFileSize(new File(realPath));

          }catch (Exception n )
          {
              Toast.makeText(context, "Error 715 : " , Toast.LENGTH_SHORT).show();
          }

            size_file = (size_file) / 1000;  //KB

            ByteArrayOutputStream stream = null ;
	       
	     //   ByteArrayOutputStream stream = new ByteArrayOutputStream();

	        try{


	        	BitmapFactory.Options options = new BitmapFactory.Options();
	            Bitmap preview_bitmap = BitmapFactory.decodeStream(new FileInputStream(realPath), null, options);

                stream = new ByteArrayOutputStream();

	         //  long w =   preview_bitmap.getWidth() * preview_bitmap.getHeight() ;
	           


	             if( size_file >= 4500 )
	             {
	            	 preview_bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                     //preview_bitmap.recycle();

	             }
	             else if ( size_file >2500)
	             {

	            	  preview_bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                    // preview_bitmap.recycle();
	             }
	             
	             else
	             {
	            	  preview_bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    // preview_bitmap.recycle();

	             }


            }catch(Exception x) { Toast.makeText(context, "Error 717 : " , Toast.LENGTH_SHORT).show();}


          try {


              byte[] byteArray = stream.toByteArray();

              global = byteArray;
              real   = realPath;



          }catch (Exception x)
          {
              Toast.makeText(getBaseContext(),context.getString(R.string.choose_from_gallery),Toast.LENGTH_LONG).show();

          }
	        //////////////// save the file to desk in small size //////////////////////////



	        /////////////////

	}
	    
	    
	   
	  public void showCustomAlertDialog(){
	        LayoutInflater layoutInflater = LayoutInflater.from(context);

	        View promptView = layoutInflater.inflate(R.layout.custom_dialog, null);
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
	        alertDialogBuilder.setView(promptView);
	        final EditText password = (EditText)promptView.findViewById(R.id.password_c);
	        final EditText password1 = (EditText)promptView.findViewById(R.id.password_c1);
	        // setup a dialog window
	        alertDialogBuilder
	                .setTitle("Encrypt & Share")
	                .setCancelable(false)
	                .setPositiveButton("Encrypt", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            if (password.getText().toString().length() < 10) {

                                Toast.makeText(getBaseContext(), context.getString(R.string.pass_less_10), Toast.LENGTH_SHORT).show();
                                return;
                            } else {

                                if (password.getText().toString().equals(password1.getText().toString())) {

                                    if (password.getText().toString().subSequence(0, 9).equals("123456789") ||password.getText().toString().subSequence(0, 9).equals("0123456789")) {
                                        Toast.makeText(getBaseContext(), context.getString(R.string.pass_not_acceptable), Toast.LENGTH_SHORT).show();
                                    } else {

                                        GPass = password.getText().toString();

                                        if (GPass.equals("")) {
                                            Toast.makeText(getBaseContext(), context.getString(R.string.enter_pass), Toast.LENGTH_SHORT).show();
                                        } else {

                                            ////

                                            progress = ProgressDialog.show(context, context.getString(R.string.encrypting), context.getString(R.string.pls_wait), true);
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // do the thing that takes a long time
                                                    try {
                                                        Encrypt_share(global, GPass);

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                progress.dismiss();


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

                                        //////
                                    }


                                } else {
                                    Toast.makeText(getBaseContext(), context.getString(R.string.pass_dont_match), Toast.LENGTH_SHORT).show();
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
	    public boolean onCreateOptionsMenu(Menu menu) {
	      MenuInflater inflater = getMenuInflater();
	      
	      menu.add(context.getString(R.string.Dec)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {

				finish();
				Intent i = new Intent(Encryption.this,Decryption.class);
				startActivity(i);
				return false;
				
			
			}
		});
	      
	      menu.add(context.getString(R.string.Help)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {

					finish();
					Intent i = new Intent(Encryption.this,Help.class);
					startActivity(i);
					
					return false;
				}
			});
	      
	      
	      menu.add(context.getString(R.string.Settings)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {

					finish();
					Intent i = new Intent(Encryption.this,Settings.class);
					startActivity(i);
					
					return false;
				}
			});
	      
	      inflater.inflate(R.menu.register, menu);
	      return true;
	    } 
	    
	    
	    //////// get size 
	    public long getFileSize(final File file) {
	        if (file == null || !file.exists())
	            return 0;
	        if (!file.isDirectory())
	            return file.length();
	        final List<File> dirs = new LinkedList<File>();
	        dirs.add(file);
	        long result = 0;
	        while (!dirs.isEmpty()) {
	            final File dir = dirs.remove(0);
	            if (!dir.exists())
	                continue;
	            final File[] listFiles = dir.listFiles();
	            if (listFiles == null || listFiles.length == 0)
	                continue;
	            for (final File child : listFiles) {
	                result += child.length();
	                if (child.isDirectory())
	                    dirs.add(child);
	            }
	        }

	        return result;
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



    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;

        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            if(cursor.getString(column_index) == null)
            {

                return contentUri.getPath();
            }
            else {
                return cursor.getString(column_index);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
	
}
