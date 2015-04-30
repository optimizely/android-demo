package com.example.sshah.gilt_android;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimizely.Optimizely;
import com.optimizely.Variable.LiveVariable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by sshah on 3/4/15.
 */
public class GiltProductsListAdapter extends ArrayAdapter<GiltProduct> {

    //private final Activity context;
    private final ArrayList<GiltProduct> objects;
    private static LiveVariable<Integer> msrpPriceColor = Optimizely.colorVariable("MSRPColor", Color.parseColor("#97000000"));
    private static LiveVariable<Boolean> shouldShowMsrp = Optimizely.booleanVariable("ShouldShowMSRP", true);

    public GiltProductsListAdapter(Activity ctx, ArrayList<GiltProduct> products)
    {
        super(ctx, 0, products);
        this.objects = products;
    }

    public GiltProduct getItem(int position)
    {
        return objects.get(position);
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
            viewHolder.msrpTextView.setTextColor(msrpPriceColor.get());
            viewHolder.msrpTextView.setVisibility(shouldShowMsrp.get() ? View.VISIBLE : View.INVISIBLE);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        GiltProduct product = objects.get(position);

        holder.brandTextView.setText(product.getBrand());
        holder.productNameTextView.setText(product.getName());
        holder.msrpTextView.setText("$" + product.getMaxSalesRetailPrice());
        holder.salesPriceTextView.setText("$" + product.getSalePrice());

        Picasso.with(this.getContext()).load(product.getImageURL()).into(holder.productImageView);

        return rowView;
    }
}
