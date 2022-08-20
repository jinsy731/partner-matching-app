package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.yalantis.phoenix.PullToRefreshView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.NavigableMap;

public class RecommendListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PullToRefreshView pullToRefreshView;
    public ArrayList<RecommendListData> list = new ArrayList<>();
    private int[] arrSrc = {R.drawable.park1, R.drawable.park2, R.drawable.park3, R.drawable.park4, R.drawable.park5, R.drawable.park6, R.drawable.park7};
    private String[] arrName = {"북한산도시자연공원", "새말공원", "도시공원", "오동근린공원"
        , "벽운근린공원", "맑은숲", "잠원근린공원" };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        for(int i = 0; i < Array.getLength(arrSrc); i++) {
            RecommendListData data = new RecommendListData();
            data.setTitle(arrName[i]);
            data.setImgSrc(arrSrc[i]);
            list.add(data);
        }

        View view = inflater.inflate(R.layout.fragment_recommendlist, container, false);

        pullToRefreshView = view.findViewById(R.id.pull_to_refresh);
        recyclerView = view.findViewById(R.id.list_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final RecommendListRecyclerViewAdapter adapter = new RecommendListRecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);

        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshView.setRefreshing(false);
                    }
                }, 500);
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        adapter.setOnItemClickListener(new RecommendListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {

                db.collection("facility").document(arrName[pos]).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()) {
                                        GeoPoint coord = document.getGeoPoint("fgeoPoint");
                                        Log.d("myTag", ""+coord.getLongitude()+"" +coord.getLongitude()) ;
                                        Intent intent = new Intent(getActivity(), NavigationActivity.class);
                                        intent.putExtra("latitude", coord.getLatitude());
                                        intent.putExtra("longitude", coord.getLongitude() );
                                        intent.putExtra("fName", arrName[pos]);
                                        intent.putExtra("type", "rec_list_item_click");

                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getContext().startActivity(intent);
                                }
                                    else
                                        Log.d("myTag", "cannot find arrName");
                                }
                            }
                        });


            }
        });

        return view;

    }
}
