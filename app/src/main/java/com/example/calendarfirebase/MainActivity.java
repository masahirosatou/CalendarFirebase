package com.example.calendarfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText emailFromEditText, passwordFromEditText;
    public FirebaseAuth mAuth;
    public Intent data;
    //log用
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailFromEditText = (EditText) findViewById(R.id.email_log_in_edit_text);
        passwordFromEditText = (EditText) findViewById(R.id.password_log_in_edit_text);

        mAuth = FirebaseAuth.getInstance();

    }


//空をチェック
    public boolean checkEmpty() {
        if (TextUtils.isEmpty(emailFromEditText.getText())) {
            Log.d("MainActivity", "何も記入されていません");
            Toast.makeText(MainActivity.this, "メールアドレスが入力されていません。", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(passwordFromEditText.getText())) {
            Log.d("MainActivity", "何も記入されていません");
            Toast.makeText(MainActivity.this, "パスワードが入力されていません。", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void addMailButton(View v){
        createAccount(emailFromEditText.getText().toString(),passwordFromEditText.getText().toString());
        //ログインokで画面遷移する時のためのデータを送る
        setResult(RESULT_OK,data);
    }

    private void createAccount(String email,String password) {
        Log.d(TAG,"アカウント作成:"+email);
//空白かチェック空白だった場合、アカウント作成しない。
        if (!checkEmpty()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(MainActivity.this, "新規作成に成功しました！", Toast.LENGTH_SHORT).show();
                            changeActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "すでにそのアカウントはあります。",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //ログインボタンのクリックメソッド
    public void loginMailButton(View v) {
        signIn(emailFromEditText.getText().toString(), passwordFromEditText.getText().toString());
        setResult(RESULT_OK, data);
    }

    private void signIn(String email, String password) {
        Log.d(TAG,"ログイン:"+email);
//空白かチェック空白だった場合、アカウント作成しない。
        if (!checkEmpty()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            changeActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //Intentで遷移メソッド
    private void changeActivity() {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
        finish();
    }

}
