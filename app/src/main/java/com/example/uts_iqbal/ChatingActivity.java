package com.example.uts_iqbal;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_iqbal.adapter.UserAdapter;
import com.example.uts_iqbal.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ChatingActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    ImageView btnaddperson;
    List<User> list = new ArrayList<>();
    UserAdapter userAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);

        recyclerView = findViewById(R.id.rcvNews);
        btnaddperson = findViewById(R.id.addperson);

        progressDialog = new ProgressDialog(ChatingActivity.this);
        progressDialog.setTitle("Proses menampilkan data....");

        userAdapter = new UserAdapter(getApplicationContext(), list);
        userAdapter.setDialog(new UserAdapter.Dialog() {
            @Override
            public void onClick(int pos) {
                CharSequence[] optionAction = {"Edit", "Hapus"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatingActivity.this);
                dialog.setItems(optionAction, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent update = new Intent(ChatingActivity.this, CreateActivity.class);
                                update.putExtra("id", list.get(pos).getId());
                                update.putExtra("nama", list.get(pos).getName());
                                update.putExtra("desc", list.get(pos).getDesc());
                                update.putExtra("avatar", list.get(pos).getAvatar());
                                startActivity(update);

                                break;
                            case 1:
                                deleteData(list.get(pos).getId(), list.get(pos).getAvatar());
                                break;
                        }

                    }
                });
                dialog.show();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(userAdapter);

        btnaddperson.setOnClickListener(v -> {
            Intent toAddPage = (new Intent(getApplicationContext(), CreateActivity.class));
            startActivity(toAddPage);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    private void getData() {
        progressDialog.show();
        db.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    list.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = new User(document.getString("nama"),
                                document.getString("desc"), document.getString("avatar"));
                        user.setId(document.getId());
                        user.setName(document.getString("nama"));
                        user.setDesc(document.getString("desc"));
                        list.add(user);
                        Log.d("data", document.getId() + " => " + document.getData());
                    }
                    userAdapter.notifyDataSetChanged();
                } else {
//                            Toast.makeText(getApplicationContext(), "Data gagal ditampilkan", Toast.LENGTH_SHORT).show();
                    Log.w("data", "Error getting documents.", task.getException());
                }
                progressDialog.dismiss();
            }
        });
    }

    //    private void deleteData(String id, String avatar) {
//        db.collection("User").document(id)
//                .delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        getData();
//                        progressDialog.dismiss();
//                        Log.d("data", "DocumentSnapshot successfully deleted!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        getData();
//                        progressDialog.dismiss();
//                        Log.w("data", "Error deleting document", e);
//                    }
//                });
//    }
    private void deleteData(String id, String avatar) {
        db.collection("User").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Data gagal dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseStorage.getInstance().getReferenceFromUrl(avatar).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            getData();
                        }
                    });
                }
            }
        });
    }

}