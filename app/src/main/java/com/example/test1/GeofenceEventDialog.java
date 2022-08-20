package com.example.test1;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class GeofenceEventDialog extends Dialog {
    protected Context mContext;
    private ConstraintLayout dialogLayout;
    private Button btn_ok;
    private TextView text;
    public GeofenceEventDialog(Context context, int layoutId ) {
        super( context );
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView( layoutId );
        this.mContext = context;
        hideNavigationBar();
        dialogLayout = findViewById(R.id.geofence_event_dialog_layout);
        text = findViewById(R.id.text_event_description);
        setCancelable( true );
        setCanceledOnTouchOutside( true );

        Window window = getWindow();
        if( window != null ) {
            // 백그라운드 투명
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams params = window.getAttributes();
            // 화면에 가득 차도록
            params.width         = WindowManager.LayoutParams.MATCH_PARENT;
            params.height        = WindowManager.LayoutParams.MATCH_PARENT;

            // 열기&닫기 시 애니메이션 설정
            params.windowAnimations = R.style.AnimationPopupStyle;
            window.setAttributes( params );
            // UI 하단 정렬
            //window.setGravity( Gravity.BOTTOM );
        }

        // 화면 터치 시 다이얼로그 종료
        dialogLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });
    }

    public GeofenceEventDialog(Context context, int layoutId, String setText ) {
        super( context );
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView( layoutId );
        this.mContext = context;
        hideNavigationBar();
        dialogLayout = findViewById(R.id.geofence_event_dialog_layout);
        text = findViewById(R.id.text_event_description);
        setCancelable( true );
        setCanceledOnTouchOutside( true );

        Window window = getWindow();
        if( window != null ) {
            // 백그라운드 투명
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams params = window.getAttributes();
            // 화면에 가득 차도록
            params.width         = WindowManager.LayoutParams.MATCH_PARENT;
            params.height        = WindowManager.LayoutParams.MATCH_PARENT;

            // 열기&닫기 시 애니메이션 설정
            params.windowAnimations = R.style.AnimationPopupStyle;
            window.setAttributes( params );
            // UI 하단 정렬
            //window.setGravity( Gravity.BOTTOM );
        }

        // 화면 터치 시 다이얼로그 종료
        dialogLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });
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




}