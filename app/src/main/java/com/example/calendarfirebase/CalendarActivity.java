package com.example.calendarfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity implements ListView.OnItemLongClickListener {
    public FirebaseUser user;
    public String uid;

    public FirebaseAuth mAuth;

    public FirebaseDatabase database;
    public DatabaseReference reference;

    public CustomAdapter mCustomAdapter;
    public ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //ログイン情報を取得
        user = FirebaseAuth.getInstance().getCurrentUser();

        //user id = Uid を取得する
        uid = user.getUid();

        //データベース取得
        database = FirebaseDatabase.getInstance();
        //TODO referenceは参照という意味
        reference = database.getReference("users").child(uid);
        //変数リストにリストビューを入れる
        mListView = (ListView) findViewById(R.id.list_view);

        //CustomAdapterをセット
        mCustomAdapter = new CustomAdapter(getApplicationContext(), R.layout.calendar_view, new ArrayList<CalendarData>());
        mListView.setAdapter(mCustomAdapter);

        mListView.setOnItemLongClickListener(this);

        reference.addChildEventListener(new ChildEventListener() {
            //            データを読み込むときはイベントリスナーを登録して行う。
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                アイテムのリストを取得するか、アイテムのリストへの追加がないかリッスンします。
                CalendarData calendarData = dataSnapshot.getValue(CalendarData.class);
                mCustomAdapter.add(calendarData);
                mCustomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                リスト内のアイテムに対する変更がないかリッスンします。

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                リストから削除されるアイテムがないかリッスンします。
                Log.d("ToDoActivity", "onChildRemoved:" + dataSnapshot.getKey());
                CalendarData result = dataSnapshot.getValue(CalendarData.class);
                if (result == null) return;

                CalendarData item = mCustomAdapter.getCalendarDateKey(result.getFirebaseKey());

                mCustomAdapter.remove(item);
                mCustomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                並べ替えリストの項目順に変更がないかリッスンします。
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                ログを記録するなどError時の処理を記載する。
            }
        });

    }

    public void addButton(View v) {
        Intent intent = new Intent(this, AddCalendarActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final CalendarData calendarData = mCustomAdapter.getItem(position);
        uid = user.getUid();

        new AlertDialog.Builder(this)
                .setTitle("Done?")
                .setMessage("この項目を完了しましたか？")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // OK button pressed
                        reference.child(calendarData.getFirebaseKey()).removeValue();
                        mCustomAdapter.remove(calendarData);
                    }
                })
                .setNegativeButton("No", null)
                .show();

        return false;
    }

    public void logout(View v) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
        intent.putExtra("check", true);
        startActivity(intent);
        finish();
    }


}
