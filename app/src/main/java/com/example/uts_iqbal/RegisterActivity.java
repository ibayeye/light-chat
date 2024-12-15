package com.example.uts_iqbal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    TextView txt_masuk;
    ImageView back;
    EditText NamaLengkap, Email, KataSandi;
    String email, katasandi;
    Button btn_buatakun;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        txt_masuk = findViewById(R.id.txt_masuk);
        NamaLengkap = findViewById(R.id.NamaLengkap);
        Email = findViewById(R.id.Email);
        KataSandi = findViewById(R.id.KataSandi);
        btn_buatakun = findViewById(R.id.btn_buatakun);
        back = findViewById(R.id.btn_back);


        btn_buatakun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrasi();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });


        txt_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });
    }

    private void registrasi() {
        email = Email.getText().toString();
        katasandi = KataSandi.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, katasandi)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            Toast.makeText(RegisterActivity.this, "Daftar Berhasil", Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the User.
                            Toast.makeText(RegisterActivity.this, "Daftar Gagal", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}