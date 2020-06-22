package com.masahiro.calendarfirebase;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<CalendarData> {

    private List<CalendarData> mCards;

    public CustomAdapter(@NonNull Context context, int layoutResourceid, List<CalendarData> calendarData) {
        super(context, layoutResourceid, calendarData);
        this.mCards = calendarData;
    }

    @Override
    public int getCount() {
        return mCards.size();
    }

    @Nullable
    @Override
    public CalendarData getItem(int position) {
        return mCards.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView !=null){
            viewHolder = (ViewHolder) convertView.getTag();
        }else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.calendar_view, null);
            viewHolder = new ViewHolder();

            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.title_text_view);
            viewHolder.calendarImageView = (ImageView) convertView.findViewById(R.id.result_ImageView);
            convertView.setTag(viewHolder);

        }

        CalendarData calendarData = mCards.get(position);
        viewHolder.titleTextView.setText(calendarData.getTitle());

        //画像を取得Glideライブラリ使用
        String imageUrl = calendarData.getImageUrl();
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(imageUrl);
        storageReference.getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String downloadUrl = task.getResult().toString();
                            Glide.with(viewHolder.calendarImageView.getContext())
                                    .load(downloadUrl)
                                    .into(viewHolder.calendarImageView);
                        } else {
                            Log.w("TAG", "Getting download url was not successful.",
                                    task.getException());
                        }
                    }
                });



        return convertView;
    }

    public CalendarData getCalendarDateKey(String key) {
        for (CalendarData calendarData : mCards) {
            if (calendarData.getFirebaseKey().equals(key)) {
                return calendarData;
            }
        }

        return null;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView calendarImageView;
    }
}
