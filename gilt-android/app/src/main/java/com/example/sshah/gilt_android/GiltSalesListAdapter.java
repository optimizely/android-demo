package com.example.sshah.gilt_android;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by sshah on 3/3/15.
 */
public class GiltSalesListAdapter extends ArrayAdapter<GiltSale>
{
    //private final Activity context;
    private final ArrayList<GiltSale> objects;

    public GiltSalesListAdapter(Activity ctx, ArrayList<GiltSale> sales)
    {
        super(ctx, 0, sales);
        this.objects = sales;
    }


    static class ViewHolder
    {
        public ImageView bgImageView;
        public TextView saleNameTextView;
        public TextView saleEndingTextView;
    }

    @Override
    public GiltSale getItem(int position)
    {
        return objects.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            rowView = inflater.inflate(R.layout.sale_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.saleNameTextView = (TextView) rowView.findViewById(R.id.sale_list_item_textView1);
            viewHolder.saleEndingTextView = (TextView) rowView.findViewById(R.id.sale_list_item_textView2);
            viewHolder.bgImageView = (ImageView) rowView.findViewById(R.id.saleListItemImageView);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        GiltSale sale = objects.get(position);
        holder.saleNameTextView.setText(sale.getName());
        holder.saleEndingTextView.setText(sale.getEndsInDaysString());
        Picasso.with(this.getContext()).load(sale.getImage_url()).into(holder.bgImageView);

        return rowView;
    }
}