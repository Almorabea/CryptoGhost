package net.almorabea.cryptoghost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//Ahmad Almorabea

public class Help extends AppCompatActivity {


    boolean doubleBackToExitPressedOnce;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button twitter = (Button) findViewById(R.id.twittericon);
        Button blog = (Button) findViewById(R.id.blogicon);
        Button mail = (Button) findViewById(R.id.emailicon);
        Button linkedin = (Button) findViewById(R.id.linkedinicon);
        TextView website = (TextView) findViewById(R.id.website);

         context = this;


        website.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cryptoghost.com"));
                startActivity(browserIntent);

            }


        });

        twitter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = getOpenTwitterIntent(getBaseContext(), "almorabea");
                startActivity(i);

            }


        });

        blog.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.almorabea.net"));
                startActivity(browserIntent);

            }


        });

        mail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ahmad@almorabea.net"});
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getBaseContext(), context.getString(R.string.email_clients), Toast.LENGTH_SHORT).show();
                }

            }


        });

        linkedin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/pub/ahmad-almorabea/94/268/245"));
                startActivity(browserIntent);

            }


        });


    }


    public static Intent getOpenTwitterIntent(Context c, String Username) {

        try {
            c.getPackageManager().getPackageInfo("com.twitter.android", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + Username));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + Username));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        menu.add(context.getString(R.string.Enc)).setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                finish();
                Intent i = new Intent(Help.this, Encryption.class);
                startActivity(i);
                return false;
            }
        });

        menu.add(context.getString(R.string.Dec)).setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                finish();
                Intent i = new Intent(Help.this, Decryption.class);
                startActivity(i);
                return false;


            }
        });

        menu.add(context.getString(R.string.Settings)).setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                finish();
                Intent i = new Intent(Help.this, Settings.class);
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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


}
