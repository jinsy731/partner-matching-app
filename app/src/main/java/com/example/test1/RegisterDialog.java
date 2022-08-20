package com.example.test1;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Calendar;

public class RegisterDialog extends Dialog {
    protected Context mContext;
    protected LinearLayout topLayout;
    protected EditText edit_title;
    protected EditText edit_people_number;
    protected EditText edit_type;
    protected Button btn_date;
    protected Button btn_time;
    protected Button btn_register;
    protected TextView text_time;
    protected TextView text_date;
    protected String reg_title = null, reg_type = null, reg_time = null, reg_date = null, reg_count = null;

    public RegisterDialog(Context context, int layoutId ) {
        super( context );
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView( layoutId );
        this.mContext = context;
        hideNavigationBar();

        topLayout = findViewById(R.id.register_top_layout);
        edit_title = findViewById(R.id.edit_reg_title);
        edit_type = findViewById(R.id.edit_reg_type);
        edit_people_number = findViewById(R.id.edit_reg_max_number);
        btn_date = findViewById(R.id.btn_date_select);
        btn_time = findViewById(R.id.btn_time_select);
        btn_register = findViewById(R.id.btn_reg_register);
        text_date = findViewById(R.id.text_reg_date);
        text_time = findViewById(R.id.text_reg_time);

        GradientDrawable drawable = (GradientDrawable)getContext().getDrawable(R.drawable.dialog_background_shape);
        topLayout.setBackground(drawable);

        Calendar cal = Calendar.getInstance();
        text_date.setText(cal.get(Calendar.YEAR) + "년 " + (cal.get(Calendar.MONTH)+1) + "월 " + cal.get(Calendar.DATE)+ "일");


        DatePickerDialog.OnDateSetListener mDateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        text_date.setText(year + "년 " + (month + 1) + "월 " + dayOfMonth + "일");
                    }
                };

        TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        text_time.setText(hourOfDay + "시 " + minute + "분");
                    }
                };



        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(context, mDateSetListener,
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                dialog.show();

                //context 부분에 getApplicationContext()가 아닌 RegisterActivity.this를 넣어주면 오류 없이 작동
            }
        });

        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar, mTimeSetListener, 0, 0, false);
                timePickerDialog.setTitle("Choose hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });

        setCancelable( true );
        setCanceledOnTouchOutside( true );

        Window window = getWindow();
        if( window != null ) {
            // 백그라운드 투명
            //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

            WindowManager.LayoutParams params = window.getAttributes();
            // 화면에 가득 차도록
//            params.width         = topLayout.getWidth();
//            params.height        = WindowManager.LayoutParams.MATCH_PARENT;

            // 열기&닫기 시 애니메이션 설정
            params.windowAnimations = R.style.AnimationPopupStyle;
            window.setAttributes( params );
            // UI 하단 정렬
            window.setGravity( Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        }


    }

    public void hideNavigationBar() {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.d("myTag", "Turning immersive mode mode off. ");
        } else {
            Log.d("myTag", "Turning immersive mode mode on.");
        }
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private void sendToActivity(Context context) {
        Intent intent = new Intent(context, NavigationActivity.class);

        intent.putExtra("type", "register"  );
        intent.putExtra("reg_title", edit_title.getText().toString());
        intent.putExtra("reg_type", edit_type.getText().toString());
        intent.putExtra("reg_date", text_date.toString());
        intent.putExtra("reg_time", text_time.toString());
        intent.putExtra("reg_count", edit_people_number.getText().toString());

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}