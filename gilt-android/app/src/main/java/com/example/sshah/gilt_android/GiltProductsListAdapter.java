package com.example.sshah.gilt_android;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sshah.gilt_android.GiltProduct;
import com.example.sshah.gilt_android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by sshah on 3/4/15.
 */
public class GiltProductsListAdapter extends ArrayAdapter<GiltProduct> {

    //private final Activity context;
    private final ArrayList<GiltProduct> objects;

    public GiltProductsListAdapter(Activity ctx, ArrayList<GiltProduct> products)
    {
        super(ctx, 0, products);
        this.objects = products;
    }

    static class ViewHolder
    {
        public ImageView productImageView;
        public TextView brandTextView, productNameTextView, msrpTextView, salesPriceTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            rowView = inflater.inflate(R.layout.grid_item_gilt_product, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.brandTextView = (TextView) rowView.findViewById(R.id.textViewBrand);
            viewHolder.productNameTextView = (TextView) rowView.findViewById(R.id.textViewProductName);
            viewHolder.msrpTextView = (TextView) rowView.findViewById(R.id.textViewMSRP);
            viewHolder.salesPriceTextView = (TextView) rowView.findViewById(R.id.textViewSalePrice);
            viewHolder.productImageView = (ImageView) rowView.findViewById(R.id.productImageView);

            //Set strikethrough text
            viewHolder.msrpTextView.setPaintFlags(viewHolder.msrpTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        GiltProduct product = objects.get(position);

        holder.brandTextView.setText(product.getBrand());
        holder.productNameTextView.setText(product.getName());
        holder.msrpTextView.setText("$200");
        holder.salesPriceTextView.setText("$150");

        Picasso.with(this.getContext()).load(product.getImageURL()).into(holder.productImageView);

        return rowView;
    }
}
