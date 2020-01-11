package com.zendesk.adtapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zendesk.adtapp.R;

public class InteractiveSecurityActivity extends AppCompatActivity {
    public static final String SERVICE_URL = "https://www.adt.com.ar/seguridad-interactiva/producto";
    public static final String PREGUNTAS_URL = "https://adtargentina.zendesk.com/hc/es/categories/360002271551-ADT-Interactive-Security";

    Button preguntas_frecuentes, acceso_app, contratar_el_servicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_security);
        contratar_el_servicio = (Button) findViewById(R.id.contratar_el_servicio_btn);
        acceso_app = (Button) findViewById(R.id.acceso_app_btn);
        preguntas_frecuentes = (Button) findViewById(R.id.preguntas_frecuentes_btn);
        contratar_el_servicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("",SERVICE_URL);
            }
        });

        acceso_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppLink("com.alarm.alarmmobile.android.adt");
            }
        });

        preguntas_frecuentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("",PREGUNTAS_URL);
            }
        });
    }

    public void openLink(String title, String url) {
        Intent intent = new Intent(InteractiveSecurityActivity.this, WebActivity.class);
        intent.putExtra(WebActivity.KEY_TITLE, title);
        intent.putExtra(WebActivity.KEY_URL, url);
        startActivity(intent);
    }

    public void openAppLink(String link) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + link));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (android.content.ActivityNotFoundException anfe) {

        }
    }
}
