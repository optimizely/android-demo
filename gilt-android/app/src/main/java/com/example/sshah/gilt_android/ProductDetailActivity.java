package com.example.sshah.gilt_android;

import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimizely.Optimizely;
import com.squareup.picasso.Picasso;


public class ProductDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ProductDetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ProductDetailFragment extends Fragment {

        private GiltProduct product;
        private TextView textViewBrand, textViewProductName, textViewMSRP, textViewSalePrice, textViewDescription;
        private ImageView productImageView;
        private Button addToCartButton;

        public ProductDetailFragment() {
        }

        @Override
        public void onCreate(Bundle icicle) {
            setRetainInstance(true);
            super.onCreate(icicle);

            Bundle bundle = getActivity().getIntent().getExtras();
            this.product = bundle.getParcelable(GiltProduct.TAG);
            getActivity().setTitle(product.getBrand());
            AppRater.didSignificantEvent(this.getActivity());
        }

        private View.OnClickListener addToCartListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Optimizely.trackEvent("addToCart");
            }
        };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);


            textViewBrand = (TextView)rootView.findViewById(R.id.textViewBrand);
            textViewProductName = (TextView)rootView.findViewById(R.id.textViewProductName);
            textViewMSRP = (TextView)rootView.findViewById(R.id.textViewMSRP);
            textViewSalePrice = (TextView)rootView.findViewById(R.id.textViewSalePrice);
            textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
            addToCartButton = (Button)rootView.findViewById(R.id.addToCartButton);
            addToCartButton.setOnClickListener(addToCartListener);

            textViewMSRP.setPaintFlags(textViewMSRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


            productImageView = (ImageView)rootView.findViewById(R.id.productImageView);

            textViewBrand.setText(product.getBrand());
            textViewProductName.setText(product.getName());
            textViewMSRP.setText("$" + product.getMaxSalesRetailPrice());
            textViewSalePrice.setText("$" + product.getSalePrice());
            textViewDescription.setText(product.getDescription());


            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;

            Picasso.with(ProductDetailFragment.this.getActivity())
                    .load(product.getImageURL())
                    .resize(0,750)
                    .into(productImageView);

            return rootView;
        }
    }
}
