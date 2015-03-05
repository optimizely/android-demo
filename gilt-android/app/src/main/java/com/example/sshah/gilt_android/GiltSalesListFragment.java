package com.example.sshah.gilt_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by sshah on 3/3/15.
 */
public class GiltSalesListFragment extends ListFragment {

    private ListView listView;
    private ProgressBar spinner;

    public void onCreate(Bundle icicle) {
        setRetainInstance(true);
        super.onCreate(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View view = inflater.inflate(R.layout.fragment_sales_list, container, false);
        spinner = (ProgressBar)view.findViewById(R.id.progressBar);
        listView = (ListView)view.findViewById(android.R.id.list);
        getSales();
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        GiltSale sale = (GiltSale)this.getListAdapter().getItem(position);
        Intent showProducts = new Intent(this.getActivity(), GiltProductsListActivity.class);
        showProducts.putExtra(GiltSale.TAG,sale);
        getActivity().startActivity(showProducts);
       // TODO make better animation
       // getActivity().overridePendingTransition(R.animator.slide_in_right,0);
    }

    private void getSales()
    {
        listView.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);
        GiltLog.d("Getting Sales");
        GiltSale.getSales(new GiltSale.GetSalesResponseHandler() {
            @Override
            public void onCompletion(ArrayList<GiltSale> sales) {
                if(sales.size() == 0) {
                    showError();
                } else {
                    GiltSalesListAdapter adapater = new GiltSalesListAdapter(GiltSalesListFragment.this.getActivity(), sales);
                    GiltSalesListFragment.this.setListAdapter(adapater);
                    spinner.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showError() {

        // TODO show an alert when no sales
    }


}
