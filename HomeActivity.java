package com.ideaplunge.efoodguru;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ideaplunge.efoodguru.constants.ApiList;
import com.ideaplunge.efoodguru.constants.SharedPref;
import com.ideaplunge.efoodguru.constants.SharConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


        private static final String TAG = "efoodguru.HomeActivity";
        ListView mDrawerList;
        RelativeLayout mDrawerPane;
        private ActionBarDrawerToggle mDrawerToggle;
        private DrawerLayout mDrawerLayout;
        private Toolbar mToolbar;


        private List<String> s = new ArrayList<>();
        private ArrayAdapter<String> adapter = null;

        public static int exitCount = 0;


        ArrayList<NavigationItem> mNavItems = new ArrayList<NavigationItem>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);


            mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            setSupportActionBar(mToolbar);


            RelativeLayout profileRelativeLayout = (RelativeLayout) findViewById(R.id.profileBox);
            profileRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    setTitle("Profile");

                    exitCount = 0;
                    Log.e(TAG, "clicked");
                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            mNavItems.add(new NavigationItem("Search", "Get Product Report", R.drawable.home));

            mNavItems.add(new NavigationItem("My Profile", "Your food avoidance list", R.drawable.profile));

            mNavItems.add(new NavigationItem("Logout", "", R.drawable.logout));


            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);


            mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
            mDrawerList = (ListView) findViewById(R.id.navList);
            selectItemFromDrawer(0);


            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                    R.string.drawer_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);

                    invalidateOptionsMenu();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    Log.d("homeActivity", "onDrawerClosed: " + getTitle());

                    invalidateOptionsMenu();
                }
            };

            getSupportActionBar().setHomeButtonEnabled(true);


            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            mDrawerLayout.setDrawerListener(mDrawerToggle);
            DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
            mDrawerList.setAdapter(adapter);


            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectItemFromDrawer(position);
                }
            });
        }

        @Override
        public void onBackPressed() {
            Log.e(TAG, "back"+exitCount);
            if(exitCount == 0)
            {
                Toast.makeText(getApplicationContext(), "Press Back again to Exit the application", Toast.LENGTH_LONG).show();
                exitCount = exitCount + 1;
            }
            else
            {
                //super.onBackPressed();
                this.finishAffinity();

            }
        }
    private void selectItemFromDrawer(int position)
    {
        exitCount = 0;
        Log.e(TAG, position+"");
        if(position == 1)     //Profile Activity
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("firsttime", false);
            startActivity(intent);
        }

        else if(position == 2)
        {
            logoutTask();
            HomeActivity.this.finish();
        }

        else{
            Fragment fragment = new HomeFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();

            mDrawerList.setItemChecked(position, true);
            setTitle(mNavItems.get(position).mTitle);

        }
        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id  = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavigationItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavigationItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText( mNavItems.get(position).mTitle );
            subtitleView.setText( mNavItems.get(position).mSubtitle );
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }

    public void logoutTask(){
        RequestQueue singleQueue = Volley.newRequestQueue(getApplicationContext());
        String date_time1 = SharedPref.getData(getApplicationContext(), SharConstants.date_time);
        JSONObject dateObject = new JSONObject();
        try {
            dateObject.put("date_time",date_time1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiList.userLogout, dateObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("response");
                            if(jsonObject.getInt("success")==1){
                                SharedPref.setData(getApplicationContext(), "login", "false");
                                SharedPref.setData(getApplicationContext(), "username", null);
                                SharedPref.setData(getApplicationContext(), "image", null);
                                SharedPref.setData(getApplicationContext(),"date_time",null);
                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }

        );
        singleQueue.add(jsonObjectRequest);

    }



}





