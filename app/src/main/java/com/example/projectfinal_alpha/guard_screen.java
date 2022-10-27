package com.example.projectfinal_alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.0
 * @since 23/10/2022
 */
public class guard_screen extends AppCompatActivity {
    private CodeScanner codeScanner;
    TextView tv_result, tv_datetime;
    CodeScannerView scannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_screen);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},100);
        }
        Toast.makeText(guard_screen.this, "לחץ על המסך בשביל לסרוק", Toast.LENGTH_LONG).show();
        scannerView = findViewById(R.id.scanner_view);
        tv_result =findViewById(R.id.tv_result);
        tv_datetime =findViewById(R.id.tv_view_time);

        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Format dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateTimeString  = dateFormat.format(new Date());
                        Toast.makeText(guard_screen.this, result.getText(), Toast.LENGTH_SHORT).show();
                        tv_result.setText(result.getText());
                        tv_datetime.setText(dateTimeString);
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }
}