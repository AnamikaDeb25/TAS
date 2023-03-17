package com.TAS.tas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Starting extends AppCompatActivity {

    ImageView tpic , spic;
    TextView ttxt,stxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        tpic = findViewById(R.id.tpic);
        spic = findViewById(R.id.spic);
        ttxt = findViewById(R.id.ttxt);
        stxt = findViewById(R.id.stxt);

        tpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFirstRun = getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("isFirstRun",true);
                if (isFirstRun){
                    AlertDialog.Builder b = new AlertDialog.Builder(Starting.this);
                    b.setCancelable(false);
                    b.setTitle("Privacy Policy");
                    b.setMessage(Html.fromHtml("Do you accept our privacy policy?"));
                    b.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    });
                    b.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getSharedPreferences("PREFERENCE",MODE_PRIVATE).edit().putBoolean("isFirstRun",false).apply();
                            startActivity(new Intent(Starting.this,TeacherLogin.class));
                        }
                    });
                    b.setNeutralButton("View privacy policy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent ii = new Intent(Starting.this,PrivacyPolicy.class);
                            startActivity(ii);
                        }
                    });
                    AlertDialog a = b.create();
                    a.show();
                    //((TextView)a.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                }
                else startActivity(new Intent(Starting.this,TeacherLogin.class));
            }
        });
        ttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFirstRun = getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("isFirstRun",true);
                if (isFirstRun){
                    AlertDialog.Builder b = new AlertDialog.Builder(Starting.this);
                    b.setCancelable(false);
                    b.setTitle("Privacy Policy");
                    b.setMessage(Html.fromHtml("Do you accept our privacy policy?"));
                    b.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    });
                    b.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getSharedPreferences("PREFERENCE",MODE_PRIVATE).edit().putBoolean("isFirstRun",false).apply();
                            startActivity(new Intent(Starting.this,TeacherLogin.class));
                        }
                    });
                    b.setNeutralButton("View privacy policy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent ii = new Intent(Starting.this,PrivacyPolicy.class);
                            startActivity(ii);
                        }
                    });
                    AlertDialog a = b.create();
                    a.show();
                }
                else startActivity(new Intent(Starting.this,TeacherLogin.class));
            }
        });
        spic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFirstRun = getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("isFirstRun",true);
                if (isFirstRun){
                    AlertDialog.Builder b = new AlertDialog.Builder(Starting.this);
                    b.setCancelable(false);
                    b.setTitle("Privacy Policy");
                    b.setMessage(Html.fromHtml("Do you accept our privacy policy?"));
                    b.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    });
                    b.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getSharedPreferences("PREFERENCE",MODE_PRIVATE).edit().putBoolean("isFirstRun",false).apply();
                            startActivity(new Intent(Starting.this,Student_login.class));
                        }
                    });
                    b.setNeutralButton("View privacy policy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent ii = new Intent(Starting.this,PrivacyPolicy.class);
                            startActivity(ii);
                        }
                    });
                    AlertDialog a = b.create();
                    a.show();
                    //((TextView)a.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                }
                else startActivity(new Intent(Starting.this,Student_login.class));
            }
        });
        stxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFirstRun = getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("isFirstRun",true);
                if (isFirstRun){
                    AlertDialog.Builder b = new AlertDialog.Builder(Starting.this);
                    b.setCancelable(false);
                    b.setTitle("Privacy Policy");
                    b.setMessage(Html.fromHtml("Do you accept our privacy policy?"));
                    b.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    });
                    b.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getSharedPreferences("PREFERENCE",MODE_PRIVATE).edit().putBoolean("isFirstRun",false).apply();
                            startActivity(new Intent(Starting.this,Student_login.class));
                        }
                    });
                    b.setNeutralButton("View privacy policy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent ii = new Intent(Starting.this,PrivacyPolicy.class);
                            startActivity(ii);
                        }
                    });
                    AlertDialog a = b.create();
                    a.show();
                    //((TextView)a.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                }
                else startActivity(new Intent(Starting.this,Student_login.class));
            }
        });

    }
}