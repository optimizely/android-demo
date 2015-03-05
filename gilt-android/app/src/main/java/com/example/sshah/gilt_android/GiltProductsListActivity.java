package com.example.sshah.gilt_android;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.ArrayList;


public class GiltProductsListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gilt_products_list);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new GiltProductsListFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gilt_products_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class GiltProductsListFragment extends Fragment {

        private GridView gridView;
        private GiltSale sale;
        private ProgressBar spinner;

        public GiltProductsListFragment() {

        }

        public void onCreate(Bundle icicle) {
            setRetainInstance(true);
            super.onCreate(icicle);

            Bundle bundle = getActivity().getIntent().getExtras();
            this.sale = bundle.getParcelable(GiltSale.TAG);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_gilt_products_list, container, false);

            gridView = (GridView)rootView.findViewById(R.id.gridView);
            spinner = (ProgressBar)rootView.findViewById(R.id.progressBar);
            getProducts();
            return rootView;
        }

        private void getProducts()
        {
            spinner.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.INVISIBLE);

            sale.getAllProducts(new GiltSale.GetProductsResponseHandler() {
                @Override
                public void onCompletion(ArrayList<GiltProduct> products) {
                    GiltProductsListAdapter adapter = new GiltProductsListAdapter(getActivity(), products);
                    gridView.setAdapter(adapter);
                    spinner.setVisibility(View.INVISIBLE);
                    gridView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
