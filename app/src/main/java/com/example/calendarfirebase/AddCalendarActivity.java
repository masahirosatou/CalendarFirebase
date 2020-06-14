package com.example.calendarfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.lang.ref.Reference;

public class AddCalendarActivity extends AppCompatActivity {
    private static final String TAG = "Add";
    private static final int REQUEST_IMAGE = 2;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference();
    private EditText titleEditText;
    private ImageView mAddMessageImageView;
    private TextView imageText;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private String title;
    // Firebase instance variables
//    CalendarDataにuid,keyなどしっかり入れないと起動しない。　重複したせいでならなかった！！
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mFirebaseAuth.getCurrentUser();
    String uid = user.getUid();
    String key = reference.push().getKey();
    CalendarData mCalendarData;
    Uri uri;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_calendar);

        titleEditText = (EditText) findViewById(R.id.title);

        //イメージ表示
        mAddMessageImageView = (ImageView) findViewById(R.id.add_ImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_IMAGE);
            }
        });



    }
    public void back(View v){
        finish();
    }
    public void save(View v) {
        title = titleEditText.getText().toString();

//引数のToDoDataの内容をデータベースに送る。 imageURI書き込み完了　LOADING_IMAGE_URLを写真ごとのデータにしないといけない
         mCalendarData = new CalendarData(key, title,LOADING_IMAGE_URL);

        putImageInStorage(storageReference, uri,key);
        reference.child("users").child(uid).child(key).setValue(mCalendarData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void v) {

                finish();
            }
        });

    }
    // startActivityForResultを受け取る mAddMessageImageViewに表示
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            try {
                 uri = data.getData();
                Log.d(TAG, "Uri: " + uri.toString());
                imageText = (TextView)findViewById(R.id.ImageText);
                imageText.setText("");

                BufferedInputStream inputStream = new BufferedInputStream(getContentResolver().openInputStream(data.getData()));
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                mAddMessageImageView.setImageBitmap(image);

                //DBにimageUrlを書き込む
//                mCalendarData = new CalendarData(key,title,LOADING_IMAGE_URL);
//                reference.child("users").child(uid).push().setValue(mCalendarData);



                //Fire Storage に追加する処理いる。 Saveでその処理をしたいが、ひとまずResult後に
                storageReference =
                        FirebaseStorage.getInstance()
                        //保存場所のパスを設定している。だから参照必ず作成するのか
                                .getReference(uid)
                                .child(key)
                                .child(uri.getLastPathSegment());
//                //画像をアップロードするメソッド呼び出し


            } catch (Exception e) {

            }
        }
    }

    //    画像をアップロードしてメッセージを更新　ユーザごとに分けることが必要uidを気にするべき
    private void putImageInStorage(StorageReference storageReference, Uri uri,final String key) {
        storageReference.putFile(uri).addOnCompleteListener(AddCalendarActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getMetadata().getReference().getDownloadUrl()
                                    .addOnCompleteListener(AddCalendarActivity.this,
                                            new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                         mCalendarData =
                                                                new CalendarData(key, title,
                                                                        task.getResult().toString());
                                                        reference.child("users").child(uid).child(key)
                                                                .setValue(mCalendarData);
                                                    }
                                                }
                                            });
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }
}
