//package com.example.test1;
//
//import android.app.DatePickerDialog;
//import android.app.TimePickerDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.TimePicker;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.GeoPoint;
//import com.naver.maps.geometry.LatLng;
//
//import org.w3c.dom.Text;
//
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.Map;
//
//public class RegisterActivity extends AppCompatActivity {
//
//    private EditText edit_title, edit_type, edit_msg;
//    private Button btn_register, btn_datepicker, btn_timepicker;
//    private TextView text_date, text_time;
//
//    private FirebaseFirestore db;
//    private FirebaseUser user;
//
//    private final static String TAG = "RegisterActivity";
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        edit_title = findViewById(R.id.edit_reg_title);
//        edit_type = findViewById(R.id.edit_reg_type);
//        edit_msg = findViewById(R.id.edit_reg_message);
//        btn_register = findViewById(R.id.btn_register);
//        btn_datepicker = findViewById(R.id.btn_reg_datepick);
//        btn_timepicker = findViewById(R.id.btn_reg_timepick);
//        text_date = findViewById(R.id.text_reg_dateinfo);
//        text_time = findViewById(R.id.text_reg_timeinfo);
//
//        db = FirebaseFirestore.getInstance();
//        user = FirebaseAuth.getInstance().getCurrentUser();
//
//        Calendar cal = Calendar.getInstance();
////        text_date.setText(cal.get(Calendar.YEAR) + "년 " + (cal.get(Calendar.MONTH)+1) + "월 " + cal.get(Calendar.DATE)+ "일");
//
//        Intent intent = getIntent(); // 위치, 해당 지역 이름 또는 주소
//
//
//        btn_datepicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this, mDateSetListener,
//                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
//                dialog.show();
//
//                //context 부분에 getApplicationContext()가 아닌 RegisterActivity.this를 넣어주면 오류 없이 작동
//            }
//        });
//
//        btn_timepicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TimePickerDialog timePickerDialog = new TimePickerDialog(RegisterActivity.this,
//                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar, mTimeSetListener, 0, 0, false);
//                timePickerDialog.setTitle("Choose hour:");
//                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                timePickerDialog.show();
//            }
//        });
//
//        btn_register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (text_date.getText().toString().isEmpty() || text_time.getText().toString().isEmpty()
//                        || edit_title.getText().toString().isEmpty() || edit_type.getText().toString().isEmpty() || edit_msg.getText().toString().isEmpty()) {
//                    Toast.makeText(RegisterActivity.this, "입력하지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    ListData data = new ListData();
//                    data.setTitle(edit_title.getText().toString());
//                    data.setDate(text_date.getText().toString() + "  " + text_time.getText().toString());
//                    data.setType(edit_type.getText().toString());
//                    data.setRegistUid(user.getUid());
//                    data.setMessage(edit_msg.getText().toString());
//                    data.setGeoPoint(new GeoPoint(intent.getDoubleExtra("latitude",0),
//                            intent.getDoubleExtra("longitude", 0)));
//                    try{
//                            data.setFaciName(intent.getStringExtra("fName"));
//                    }
//                    catch(Exception e) {
//                        Log.d("log", "" + e);
//                    }
//
//                    db.collection("registlist").add(data)
//                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                @Override
//                                public void onSuccess(DocumentReference documentReference) {
//                                    Toast.makeText(RegisterActivity.this, "Register Success!", Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(RegisterActivity.this, "Register Failed!", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                    finish();
//                }
//
//            }
//        });
//
//    }
//
//    DatePickerDialog.OnDateSetListener mDateSetListener =
//            new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                    text_date.setText(year + "년 " + (month + 1) + "월 " + dayOfMonth + "일");
//                }
//            };
//
//    TimePickerDialog.OnTimeSetListener mTimeSetListener =
//            new TimePickerDialog.OnTimeSetListener() {
//                @Override
//                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                    text_time.setText(hourOfDay + "시 " + minute + "분");
//                }
//            };
//}
