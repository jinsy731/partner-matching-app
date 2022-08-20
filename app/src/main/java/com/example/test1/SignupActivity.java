package com.example.test1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends HideBottomBarActivity {

    Button btn_signup;
    EditText etext_id, etext_pw;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private final String TAG = "SignupActivity";
    private boolean signupCheck;
    private String id, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btn_signup = (Button)findViewById(R.id.btn_signup);
        etext_id = (EditText)findViewById(R.id.etext_signup_id);
        etext_pw = (EditText)findViewById(R.id.etext_signup_pw);
        db = FirebaseFirestore.getInstance();
        hideNavigationBar();


        signupCheck = false;
        mAuth = FirebaseAuth.getInstance();

        final CollectionReference collection_user = db.collection("users");


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //User user = new User(etext_id.getText().toString(), etext_pw.getText().toString());
                id = etext_id.getText().toString();
                password = etext_pw.getText().toString();

                createAccount(id, password);

//                else {
//                    new AlertDialog.Builder(SignupActivity.this)
//                            .setMessage("계정 생성 실패!")
//                            .setNeutralButton("닫기", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //
//                                }
//                            });
               // }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Map<String, Object> data = new HashMap<>();
                            data.put("id", etext_id.getText().toString());

                            db.collection("users")
                                    .document(user.getUid())
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "add success!");
                                            Intent outIntent = new Intent(getApplicationContext(), MainActivity.class);
                                            outIntent.putExtra("email", id);
                                            outIntent.putExtra("pw", password);
                                            setResult(RESULT_OK, outIntent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "add failure!");
                                        }
                                    });
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "등록 실패!",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }

}
