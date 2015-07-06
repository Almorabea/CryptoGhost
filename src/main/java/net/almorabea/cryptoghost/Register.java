package net.almorabea.cryptoghost;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import scrypttest.SCryptUtil;
import scrypttest.Utilites;
import blatest.Blake2b;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//Ahmad Almorabea

public class Register extends AppCompatActivity {


    private static final int SELECT_PICTURE = 100;
    boolean doubleBackToExitPressedOnce;
    SharedPreferences sp;
    String collected = null;
    private String filemanagerstring;
    private String selectedImagePath;
    Context context;
    ProgressDialog progress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        context = this ;
        sp = getSharedPreferences("net.almorabea.cryptoghost.Register", Context.MODE_PRIVATE);
        //sp.edit().clear().commit();


        check();



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

        startup();













    }

    public void startup() {


        final EditText password = (EditText) findViewById(R.id.password_c1);
        final EditText password1 = (EditText) findViewById(R.id.password1);
        final EditText email = (EditText) findViewById(R.id.email);

        final Button register = (Button) findViewById(R.id.login);
        final TextView restore = (TextView) findViewById(R.id.restore) ;


        restore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                showCustomAlertDialog();
                /*
                Intent intent = new Intent();
                intent.setType("**");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
                */

            }
        });


        register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

             final   String p = password.getText().toString();  // password
             final   String p1 = password1.getText().toString(); //
             final   String em = email.getText().toString();    //email


                boolean check_p = check_password(p, p1);

                if (check_p == true) {

                    if (!email.getText().toString().contains("@") ){
                        Toast.makeText(getBaseContext(), context.getString(R.string.email_format_not_correct), Toast.LENGTH_LONG).show();
                        return;
                    } else if(email.getText().toString().length() < 5)
                    {
                        Toast.makeText(getBaseContext(), context.getString(R.string.email_too_short), Toast.LENGTH_LONG).show();
                    }
                    else if(!email.getText().toString().contains("."))
                    {
                        Toast.makeText(getBaseContext(), context.getString(R.string.email_format_not_correct), Toast.LENGTH_LONG).show();
                    }

                    else {




                            //	beg.setText(String.valueOf(System.currentTimeMillis()));



                      //  progress = ProgressDialog.show(context, "Registering","Loading....", true);

                            new Thread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    // do the thing that takes a long time


                                    try {
                                        Register_P(p, em);
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    }

                                  //  progress.dismiss();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run()
                                        {


                                        }
                                    });
                                }
                            }).start();


                            sp.edit().putBoolean("firstrun", false).commit();
                            setContentView(R.layout.register);
                         //   Toast.makeText(getBaseContext(), "first time", Toast.LENGTH_LONG).show();
                           // progress.dismiss();


                        finish();
                        Intent i = new Intent(Register.this, Encryption.class);
                        startActivity(i);

                    }

                } else {

                }


            }


        });


    }





    public byte [] load( ) throws Exception {


        File file = new File(Environment.getExternalStorageDirectory()+"/CryptoGhost/Restore/key.crypto");
       // FileOutputStream f = new FileOutputStream(file);
        File path = Environment.getDataDirectory();
        InputStream f = new FileInputStream(file);

        byte[] dataArray = new byte[f.available()];

        while (f.read(dataArray) != 0) {

            collected = new String(dataArray);

            f.close();
            return  dataArray;
        }


      //  return collected;
        return  null;
    }



    private void Register_P(String p, String e) throws NoSuchAlgorithmException {
        String password = p;
        String email = e;

        String FP = "";

        ///// equation
        String full = password + email;   // attaching password and email

        byte strbyte[] = full.getBytes();  // convert string to bytes

        Blake2b.Mac b2b = Blake2b.Mac.newInstance(strbyte);




        String hashed = Utilites.toHex(b2b.digest());


        FP = SCryptUtil.scrypt(hashed, 16384, 8, 1);





        sp.edit().putString("email", email).commit();
        sp.edit().putString("password", password).commit();
        sp.edit().putString("FP", FP).commit();


    }


    private boolean check_password(String p, String p1) {

        if (p.equals(p1)) {

            int check_length = p.length();

            if (check_length < 10) {
                Toast.makeText(getBaseContext(), context.getString(R.string.pass_less_10), Toast.LENGTH_SHORT).show();
            } else {

                if (p.subSequence(0, 9).equals("123456789")) {
                    Toast.makeText(getBaseContext(), context.getString(R.string.pass_not_acceptable), Toast.LENGTH_SHORT).show();
                } else {

                    return true;


                }


            }


        } else {

            Toast.makeText(getBaseContext(), context.getString(R.string.pass_dont_match), Toast.LENGTH_SHORT).show();


        }

        return false;

    }


    public void check() {

        if (sp.getBoolean("firstrun", true)) {

            final Context con = this;
            final AlertDialog.Builder builder = new AlertDialog.Builder(con);


            builder.setCancelable(true);
            builder.setTitle(context.getString(R.string.important));
            builder.setMessage(context.getString(R.string.im_message));
            // builder.setInverseBackgroundForced(true);
            builder.setPositiveButton(context.getText(R.string.ok_got_it),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {


                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            //
        } else {
            finish();
            Intent i = new Intent(Register.this, Login.class);
            startActivity(i);
         //   Toast.makeText(this, "not a first time", Toast.LENGTH_LONG).show();
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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    //////////////

    ///Decrypt /////////


    public String  Decrypt_external(byte [] m , String email ,String password) throws Exception {


        String full = email + password ;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(full.getBytes("UTF-8"));


        byte[] nonce = new byte[16] ;



        byte cipherText [] = m;




        // getting nonce
        for(int i = 0 ; i < 16; i++ )
        {
            nonce[i] = cipherText[i];
        }




        cipherText =   Arrays.copyOfRange(cipherText, 16,  cipherText.length);





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

            System.out.println("plain : " + Utility.toHex(plainText,ptLength) + " byte : " + ptLength );


            System.out.println("Plain Text  : "+ Utility.toHex(plainText) + " Bytes : " + ptLength);

            Log.d("plain text te",Utility.toHex(plainText) );




            sp.edit().putBoolean("firstrun", false).commit();


            sp.edit().putString("email", email).commit();
            sp.edit().putString("password", password).commit();

             sp.edit().putString("FP",  new String(plainText) ).commit();

            finish();
            Intent i = new Intent(Register.this, Encryption.class);
            startActivity(i);








            return new String(plainText) ;

        }catch(NoSuchAlgorithmException a){   			Toast.makeText(this, context.getString(R.string.bad_key), Toast.LENGTH_LONG).show(); }
        catch(NoSuchProviderException b) { 			    Toast.makeText(this, context.getString(R.string.bad_key), Toast.LENGTH_LONG).show();}
        catch(NoSuchPaddingException c) { 			    Toast.makeText(this, context.getString(R.string.bad_key), Toast.LENGTH_LONG).show();}
        catch(InvalidKeyException d) {  				Toast.makeText(this, context.getString(R.string.bad_key), Toast.LENGTH_LONG).show();}
        catch(InvalidAlgorithmParameterException e) {   Toast.makeText(this, context.getString(R.string.bad_key), Toast.LENGTH_LONG).show();}
        catch(IllegalBlockSizeException f) {  			Toast.makeText(this, context.getString(R.string.bad_key), Toast.LENGTH_LONG).show();}
        catch(BadPaddingException g) {  				Toast.makeText(this, context.getString(R.string.bad_key), Toast.LENGTH_LONG).show();}
        catch(ShortBufferException h){				    Toast.makeText(this, context.getString(R.string.bad_key), Toast.LENGTH_LONG).show();}



       return  null ;

    }


    ///////Show custom /////

    public void showCustomAlertDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View promptView = layoutInflater.inflate(R.layout.backup_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        final EditText password = (EditText)promptView.findViewById(R.id.password_c);
        final EditText email = (EditText)promptView.findViewById(R.id.password_c1);

        // setup a dialog window
        alertDialogBuilder
                .setTitle("Restore")
                .setCancelable(false)
                .setPositiveButton("Restore", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String full = email.getText().toString() + password.getText().toString();

                        try {

                          String dec = Decrypt_external(load(), email.getText().toString(), password.getText().toString());

                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(),context.getString(R.string.corrupted_file),Toast.LENGTH_LONG).show() ;
                            e.printStackTrace();
                        }




                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }





    }









