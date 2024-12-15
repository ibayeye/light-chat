package com.example.uts_iqbal;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    private EditText name, desc;
    private Button simpanuser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id = "";
    ProgressDialog progressDialog;
    ImageView avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        name = findViewById(R.id.name);
        desc = findViewById(R.id.desc);
        simpanuser = findViewById(R.id.btn_simpan);
        avatar = findViewById(R.id.avatar);

        progressDialog = new ProgressDialog(CreateActivity.this);
        progressDialog.setTitle("Sedang diproses...");

        avatar.setOnClickListener(v -> {
            selectImage();
        });

        simpanuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().length() > 0 && desc.getText().length() > 0) {
                    upload(name.getText().toString(), desc.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Semua data harus diisi!", Toast.LENGTH_LONG).show();
                }
            }
        });
        Intent updateOption = getIntent();
        if (updateOption != null) {
            id = updateOption.getStringExtra("id");
            name.setText(updateOption.getStringExtra("nama"));
            desc.setText(updateOption.getStringExtra("desc"));
            Glide.with(getApplicationContext()).load(updateOption.getStringExtra("avatar")).into(avatar);
        }

    }

    private void selectImage() {
        CharSequence[] optionAction = {"Take Photo", "Choose from library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(optionAction, (dialogInterface, i) -> {
            if (optionAction[i].equals("Take Photo")) {
                Intent take = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(take, 10);
            } else if (optionAction[i].equals("Choose from library")) {
                Intent pick = new Intent(Intent.ACTION_PICK);
                pick.setType("image/*");
                startActivityForResult(Intent.createChooser(pick, "Select Image"), 20);
            } else {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == RESULT_OK && data != null) {
            final Uri path = data.getData();
            Thread thread = new Thread(() -> {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(path);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    avatar.post(() -> {
                        avatar.setImageBitmap(bitmap);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        if (requestCode == 10 && resultCode == RESULT_OK) {
            final Bundle extras = data.getExtras();
            Thread thread = new Thread(() -> {
                Bitmap bitmap = (Bitmap) extras.get("data");
                avatar.post(() -> {
                    avatar.setImageBitmap(bitmap);
                });
            });
            thread.start();
        }
    }

    private void upload(String name, String desc) {
        // Get the data from an ImageView as bytes
        progressDialog.show();
        avatar.setDrawingCacheEnabled(true);
        avatar.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference referenceStorage = storage.getReference("images").child("IMG"+new Date().getTime()+".jpeg ");
        UploadTask uploadTask = referenceStorage.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(), "Data gagal diupload", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.getResult() != null) {
                                    saveData(name, desc, task.getResult().toString());
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Data berhasil diupload", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Data berhasil diupload", Toast.LENGTH_LONG).show();
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Data berhasil diupload", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void saveData(String name, String desc, String avatar) {
        Map<String, Object> user = new HashMap<>();
        user.put("nama", name);
        user.put("desc", desc);
        user.put("avatar", avatar);


        progressDialog.show();
        if (id != null) {
            db.collection("User").document(id)
                    .set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Data Berhasil di simpan", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        } else {
            db.collection("User")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("data", "DocumentSnapshot added with ID: " + documentReference.getId());
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("data", "Error adding document", e);
                        }
                    });
        }
        progressDialog.dismiss();
    }
}