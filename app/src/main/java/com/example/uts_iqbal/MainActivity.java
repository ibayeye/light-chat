package com.example.uts_iqbal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uts_iqbal.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity {

    // inisialisasi objek
    ActivityMainBinding binding;
    TextView daftar;
    EditText inputemail, inputpassword;
    String email, password;
    Button btn_login, btn_lanjutkan_google;
    private FirebaseAuth mAuth;
    private GoogleSignInClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        // inisialisasi objek menggunakan findViewById
        daftar = findViewById(R.id.buatakun);
        inputemail = findViewById(R.id.user_login);
        inputpassword = findViewById(R.id.user_password);
        btn_login = findViewById(R.id.btn_masuk);
        btn_lanjutkan_google = findViewById(R.id.btn_lanjutkangoogle);



        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekLogin();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(this, gso);
        btn_lanjutkan_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultLauncher.launch(new Intent(client.getSignInIntent()));
            }
        });
    }
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

             if (result.getResultCode() == Activity.RESULT_OK) {
                 Intent intent = result.getData();
                 Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                 try {
                     GoogleSignInAccount account = task.getResult(ApiException.class);

                     AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                     FirebaseAuth.getInstance().signInWithCredential(credential)
                             .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                     if(task.isSuccessful()){
                                         Intent intent = new Intent(getApplicationContext(),ChatingActivity.class);
                                         startActivity(intent);
                                     }else{
                                         Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT);
                                     }
                                 }
                             });
                 } catch (ApiException e) {
                     e.printStackTrace();
                 }
             }
        }
    });

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//       if(requestCode == 500) {
//           Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//           try {
//               GoogleSignInAccount account = task.getResult(ApiException.class);
//
//               AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//               FirebaseAuth.getInstance().signInWithCredential(credential)
//                       .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                           @Override
//                           public void onComplete(@NonNull Task<AuthResult> task) {
//                               if(task.isSuccessful()){
//                                    Intent intent = new Intent(getApplicationContext(),ChatingActivity.class);
//                                    startActivity(intent);
//                               }else{
//                                   Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT);
//                               }
//                           }
//                       });
//           } catch (ApiException e) {
//               e.printStackTrace();
//           }
//       }
//    }
    private void cekLogin() {
        email = inputemail.getText().toString();
        password = inputpassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            Toast.makeText(MainActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, ChatingActivity.class));
                        } else {
                            // If sign in fails, display a message to the User.
                            Toast.makeText(MainActivity.this, "Login Gagal, email atau password anda salah!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
