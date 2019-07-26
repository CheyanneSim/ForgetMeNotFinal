package com.example.forgetMeNot.expiry;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.forgetMeNot.R;

import java.util.HashMap;
import java.util.List;

public class ExpiryAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<String> dates;
    private HashMap<String, List<String>> data;

    public ExpiryAdapter(Context mContext, List<String> dates, HashMap<String, List<String>> data) {
        this.mContext = mContext;
        this.dates = dates;
        this.data = data;
    }

    @Override
    public int getGroupCount() {
        return this.dates.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.data.get(this.dates.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.dates.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.data.get(this.dates.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // Getting header title
        String headerTitle = (String) getGroup(groupPosition);

        // Inflating header layout and setting text
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expiry_date_groups, parent, false);
        }

        //set content for the parent views
        TextView header_text = (TextView) convertView.findViewById(R.id.date_header);
        header_text.setText(headerTitle);

        // If group is expanded then change the text into bold and change the
        // icon
        if (isExpanded) {
            header_text.setTypeface(null, Typeface.BOLD);
            header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up, 0);
        } else {
            // If group is not expanded then change the text back into normal
            // and change the icon

            header_text.setTypeface(null, Typeface.NORMAL);
            header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down, 0);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // Getting child text
        final String childText = (String) getChild(groupPosition, childPosition);
        // Inflating child layout and setting textview
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expiry_items, parent, false);
        }

        //set content in the child views
        TextView child_text = (TextView) convertView.findViewById(R.id.item_tv);

        child_text.setText(childText);
        child_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit, 0);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
