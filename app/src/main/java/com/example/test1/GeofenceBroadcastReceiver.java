package com.example.test1;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import static android.view.View.generateViewId;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ListenerRegistration registration;
    String requestID;
    boolean isInGeofence = false;
    boolean isInGeofencePeopleCheck = false;
    static Map<String, ListenerRegistration> map = new HashMap<>();

    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            //Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            Log.d("myTag", "geofence active");

            Iterator<Geofence> it = triggeringGeofences.iterator();
            while (it.hasNext()) {
                requestID = it.next().getRequestId();

                if (isInGeofence != true) {
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                        isInGeofence = true;
                        Toast.makeText(context, "Geofence 진입", Toast.LENGTH_SHORT).show();
                        // 해당 지오펜스에 구역에 대한 데이터 변경 리스너를 설정 (지오펜스 안에 인원이 얼마나 있나 체크)
                        map.put(requestID, db.collection("facility").document(requestID)
                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Log.d("myTag", "listner:error", e);
                                        }

                                        if (documentSnapshot != null && documentSnapshot.exists()) {
                                            //Toast.makeText(context, "데이터 변경 수신 : " +
                                                  //  documentSnapshot.getData().get("nInboundPeople"), Toast.LENGTH_SHORT).show();
                                            // 지오펜스 안에 있는 사람이 2명 이상이면 이벤트 발생
//                                            if (Integer.parseInt(documentSnapshot.getData().get("nInboundPeople").toString()) >= 2) {
//                                                isInGeofencePeopleCheck = true;
//                                                sendToActivity(context, "OtherPeopleInEvent", isInGeofencePeopleCheck);
//                                            }
//                                            // 지오펜스 안에 있는 사람이 2명 미만이면 이벤트 발생
//                                            else if (Integer.parseInt(documentSnapshot.getData().get("nInboundPeople").toString()) < 2) {
//                                                isInGeofencePeopleCheck = false;
//                                                sendToActivity(context, "OtherPeopleInEvent", isInGeofencePeopleCheck);
//                                            }
                                        }
                                    }
                                }));

                        db.collection("facility").
                                document(requestID).
                                update("nInboundPeople", FieldValue.increment(1));
                        sendToActivity(context, "MyInEvent", isInGeofence);


                    }

                } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                    isInGeofence = false;
                    isInGeofencePeopleCheck = false;

                    // 지오펜스에서 나가면 데이터 변경 리스너 삭제
                    if (!map.isEmpty()) {
                        map.get(requestID).remove();
                        map.remove(requestID);
                    }

                    Toast.makeText(context, "Geofence 나감", Toast.LENGTH_SHORT).show();

                    // 데이터베이스에서 지오펜스 안의 인원 수를 하나 줄임
                    db.collection("facility").
                            document(requestID).
                            update("nInboundPeople", FieldValue.increment(-1));

                    // 액티비티에 값 전달
                    sendToActivity(context, "MyInEvent", isInGeofence);

                }

            }


            // Get the transition details as a String.
//            String geofenceTransitionDetails = getGeofenceTransitionDetails(
//                    this,
//                    geofenceTransition,
//                    triggeringGeofences
//            );

            // Send notification and log the transition details.
            //sendNotification(geofenceTransitionDetails);
            //Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
//            Log.e(TAG, getString(R.string.geofence_transition_invalid_type,
//                    geofenceTransition));
        }
    }

    private void sendToActivity(Context context, String type, boolean isInGeofenceCheck) {
        Intent intent = new Intent(context, NavigationActivity.class);

        intent.putExtra("placeName", requestID);
        intent.putExtra("type", type);
        intent.putExtra("check", isInGeofenceCheck);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
