//package com.example.test1;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.firestore.SetOptions;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//public class RegisteredListActivity extends Activity {
//
//    private final String TAG = "MainActivity";
//    private FirebaseUser user;
//    private FirebaseFirestore db;
//    private String uid;
//    private RecyclerView recyclerView;
//    private Button btn_register;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_registeredlist);
//
//        btn_register = findViewById(R.id.btn_list_register);
//
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        uid = user.getUid();
//
//        db = FirebaseFirestore.getInstance();
//        final ArrayList<ListData> list = new ArrayList<>();
//
//        /* 리사이클러 뷰 설정 */
//        recyclerView = findViewById(R.id.view_list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        final MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(list);
//        recyclerView.setAdapter(adapter);
//
//        Intent intent = getIntent();
//
//        db.collection("registlist").whereEqualTo("faciName", intent.getStringExtra("fName"))
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()) {
//                            for(DocumentSnapshot document : task.getResult()) {
//                                ListData data = new ListData();
//                                data.setTitle(document.get("title").toString());
//                                data.setDate(document.get("date").toString());
//                                data.setType(document.get("type").toString());
//                                list.add(data);
//                            }
//                        }
//                        else {
//                            Log.d("log", "list loading failed!");
//                        }
//                    }
//                });
//
//        btn_register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Intent outIntent = new Intent(getApplicationContext(), RegisterActivity.class);
//                //시설 마커 클릭 시 나오는 리스트 화면에서 등록하는 기능부터 구현
//            }
//        });
//
//    }
//}