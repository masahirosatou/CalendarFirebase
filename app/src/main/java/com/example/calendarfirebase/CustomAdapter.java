package com.example.calendarfirebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
            convertView.setTag(viewHolder);

        }

        CalendarData calendarData = mCards.get(position);
        viewHolder.titleTextView.setText(calendarData.getTitle());

        return convertView;
    }

    public CalendarData getCalendarDateKey(String key) {
        for (CalendarData calendarData : mCards) {
            if (CalendarData.getFirebaseKey().equals(key)) {
                return calendarData;
            }
        }

        return null;
    }

    static class ViewHolder {
        TextView titleTextView;
    }
}
