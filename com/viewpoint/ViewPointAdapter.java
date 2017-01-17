package com.viewpoint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.le.mobile.R;

/**
 * 视点图视图适配器
 * An array adapter that knows how to render views when given CustomData classes
 */
public class ViewPointAdapter extends ArrayAdapter<Byte> {
    private LayoutInflater mInflater;

    public ViewPointAdapter(Context context, Byte[] values) {
        super(context, R.layout.player_full_view_points_item_layout, values);
        this.mInflater = (LayoutInflater) this.getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public ViewPointAdapter(Context context, Byte[] values, int normalWidth,
            int normalHeight) {
        this(context, values);

        this.mNormalWidth = normalWidth;
        this.mNormalHeight = normalHeight;
    }

    /*
     * @Override public int getCount() { return 2; }
     */

    int mNormalWidth = 0, mNormalHeight = 0;
    String TAG = "CustomArrayAdapter";

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        // Log.e(TAG, "positon " + position);
        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = this.mInflater.inflate(
                    R.layout.player_full_view_points_item_layout, parent,
                    false);

            // Create and save off the holder in the tag so we get quick access
            // to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.imageView = (ImageView) convertView
                    .findViewById(R.id.id_index_gallery_item_image);
            holder.textView = (TextView) convertView
                    .findViewById(R.id.id_index_gallery_item_text);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.imageView.setImageResource(R.drawable.default_img);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                this.mNormalWidth, this.mNormalHeight);
        holder.imageView.setLayoutParams(lp);
        // Populate the text
        // holder.textView.setText(getItem(position).getText());

        // Set the color
        // convertView.setBackgroundColor(getItem(position).getBackgroundColor());

        return convertView;
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public ImageView imageView;
        public TextView textView;
    }
}
