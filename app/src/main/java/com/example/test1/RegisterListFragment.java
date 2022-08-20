package com.example.test1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yalantis.phoenix.PullToRefreshView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class RegisterListFragment extends Fragment {

    private RecyclerView mylistRecyclerView;
    private RecyclerView otherlistRecyclerView;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private PullToRefreshView pullToRefreshView;
    private PullToRefreshView pullToRefreshView2;
    private ArrayList<RegisterListData> myListData = new ArrayList<>();
    private ArrayList<RegisterListData> otherListData = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_list, container, false);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        mylistRecyclerView = view.findViewById(R.id.my_reg_list_layout);
        otherlistRecyclerView = view.findViewById(R.id.other_reg_list_layout);
        pullToRefreshView = view.findViewById(R.id.reg_list_pull_to_refresh);
        pullToRefreshView2 = view.findViewById(R.id.reg_list_pull_to_refresh2);

        mylistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        otherlistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RegisterListRecyclerViewAdapter myAdapter = new RegisterListRecyclerViewAdapter(myListData);
        RegisterListRecyclerViewAdapter otherAdapter = new RegisterListRecyclerViewAdapter(otherListData);

        mylistRecyclerView.setAdapter(myAdapter);
        otherlistRecyclerView.setAdapter(otherAdapter);

        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMyListdata();
                        pullToRefreshView.setRefreshing(false);
                    }
                }, 500);
            }
        });
        pullToRefreshView2.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefreshView2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadOtherListData();
                        pullToRefreshView2.setRefreshing(false);
                    }
                }, 500);
            }
        });

        myAdapter.setOnItemClickListener(new RegisterListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {


            }

        });

        otherAdapter.setOnItemClickListener(new RegisterListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("참가 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("facilityRegisterList")
                                        .document(otherListData.get(pos).uid+otherListData.get(pos).loc)
                                        .update("cur_count", FieldValue.increment(1));
                                Toast.makeText(getActivity(), "등록 완료!", Toast.LENGTH_SHORT).show();
                            }
            })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                            }
                        }).show();

            }

        });
        return view;
    }

    public void loadMyListdata() {

            myListData.clear();
            db.collection("facilityRegisterList").whereEqualTo("uid", user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                for(QueryDocumentSnapshot document : task.getResult()) {
                                    RegisterListData data = new RegisterListData();
                                    data.title = document.get("title").toString();
                                    data.date = document.get("date").toString();
                                    data.loc = document.get("fName").toString();
                                    data.max_count = Integer.parseInt(document.get("count").toString());
                                    data.cur_count = Integer.parseInt(document.get("cur_count").toString());
                                    data.imgsrc = getImgSrc(document.get("type").toString());
                                    data.uid = document.get("uid").toString();


                                    myListData.add(data);
                                }
                            }
                        }
                    });
        }

    public void loadOtherListData() {
        otherListData.clear();
        db.collection("facilityRegisterList").whereGreaterThan("uid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                RegisterListData data = new RegisterListData();
                                data.title = document.get("title").toString();
                                data.date = document.get("date").toString();
                                data.loc = document.get("fName").toString();
                                data.max_count = Integer.parseInt(document.get("count").toString());
                                data.cur_count = Integer.parseInt(document.get("cur_count").toString());
                                data.imgsrc = getImgSrc(document.get("type").toString());
                                data.uid = document.get("uid").toString();




                                otherListData.add(data);
                            }
                        }
                    }
                });

        db.collection("facilityRegisterList").whereLessThan("uid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                RegisterListData data = new RegisterListData();
                                data.title = document.get("title").toString();
                                data.date = document.get("date").toString();
                                data.loc = document.get("fName").toString();
                                data.max_count = Integer.parseInt(document.get("count").toString());
                                data.cur_count = Integer.parseInt(document.get("cur_count").toString());
                                data.imgsrc = getImgSrc(document.get("type").toString());
                                data.uid = document.get("uid").toString();



                                otherListData.add(data);
                            }
                        }
                    }
                });
    }

    int getImgSrc(String type) {
        switch(type) {
            case "런닝" :
                return R.drawable.ic_directions_run_black_24dp;

            case "사이클" :
                return R.drawable.ic_directions_bike_black_24dp;

            default :
                return R.drawable.ic_directions_run_black_24dp;
        }
    }



}
