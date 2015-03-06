package com.example.sshah.gilt_android;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;


public class GiltSalesListActivity extends ActionBarActivity {

    private GiltSalesListFragment salesListFragment;
    private ViewPager mViewPager;
    private SalesListPageAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gilt_sales_list);


        pagerAdapter =
                new SalesListPageAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });

        this.setTitle("Gilt");

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };

        actionBar.addTab(actionBar.newTab().setText("Women").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Men").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Baby & Kids").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Home").setTabListener(tabListener));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gilt_sales_list, menu);
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

    public class SalesListPageAdapter extends android.support.v4.app.FragmentPagerAdapter
    {
        public SalesListPageAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new GiltSalesListFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt("sale_type", i);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }



    }

    public static class GiltSalesListFragment extends ListFragment {

        private ListView listView;
        private ProgressBar spinner;

        private int saleType;

        public void onCreate(Bundle icicle) {
            setRetainInstance(true);
            super.onCreate(icicle);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
            View view = inflater.inflate(R.layout.fragment_sales_list, container, false);
            spinner = (ProgressBar) view.findViewById(R.id.progressBar);
            listView = (ListView) view.findViewById(android.R.id.list);

            Bundle args = getArguments();
            saleType = args.getInt("sale_type");
            getSales();
            return view;
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            GiltSale sale = (GiltSale) this.getListAdapter().getItem(position);
            Intent showProducts = new Intent(this.getActivity(), GiltProductsListActivity.class);
            showProducts.putExtra(GiltSale.TAG, sale);
            getActivity().startActivity(showProducts);
            // TODO make better animation
            // getActivity().overridePendingTransition(R.animator.slide_in_right,0);
        }

        private void getSales() {
            listView.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.VISIBLE);
            GiltLog.d("Getting Sales");
            GiltSale.getSales(new GiltSale.GetSalesResponseHandler() {
                @Override
                public void onCompletion(ArrayList<GiltSale> sales) {
                    if (sales.size() == 0) {
                        showError();
                    } else {

                        ArrayList<GiltSale> storeSales;

                        switch(saleType) {
                            case 0:
                                storeSales = getSubSetOfSales("women", sales);
                                break;
                            case 1:
                                storeSales = getSubSetOfSales("men", sales);
                                break;
                            case 2:
                                storeSales = getSubSetOfSales("kids", sales);
                                break;
                            default:
                                storeSales = getSubSetOfSales("home", sales);
                        }

                        GiltSalesListAdapter adapater = new GiltSalesListAdapter(GiltSalesListFragment.this.getActivity(), storeSales);
                        GiltSalesListFragment.this.setListAdapter(adapater);
                        spinner.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        private ArrayList<GiltSale> getSubSetOfSales(String store, ArrayList<GiltSale> sales)
        {
            ArrayList<GiltSale> retVal = new ArrayList<GiltSale>();
            for(int x = 0; x < sales.size(); x++){

                GiltSale sale = sales.get(x);

                if(sale.getStore().equalsIgnoreCase(store)) {
                    retVal.add(sales.get(x));
                }
            }

            return retVal;
        }

        private void showError() {

            // TODO show an alert when no sales
        }
    }
}
