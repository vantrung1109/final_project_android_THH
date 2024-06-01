package com.example.projectfinaltth.ui.adapter.Topic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.projectfinaltth.R;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



public class TopicAdapter extends ArrayAdapter<Topic> {


    public TopicAdapter(@NonNull Context context, int resource, @NonNull List<Topic> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic_selected, parent, false);
        TextView textView = convertView.findViewById(R.id.tv_topic_selected);


        Topic topic = getItem(position);
        if (topic != null) {
            textView.setText(topic.getName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        TextView textView = convertView.findViewById(R.id.tv_topic);

        Topic topic = getItem(position);
        if (topic != null) {
            textView.setText(topic.getName());
        }


        return convertView;
    }
}
