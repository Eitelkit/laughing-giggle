package com.example.mxx62.findit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.R.color.white;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecyclerViewMain.OnFragmentInteractionListener
                    , BlackList.OnFragmentInteractionListener, FavoriteList.OnFragmentInteractionListener
                    ,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
                    ,RecyclerViewMain.voiceinferface{

    ImageView photoImage;
    private Toolbar MainPageBar;
    private Fragment RecyclerView;
    final private int CAMERA_FLAG = 100;
    final private int GALLERY_FLAG = 1;
    private GoogleApiClient mainGoogleApiClient;
    private Location mLastLocation;
    private ArrayList<ToggleButton> allButton;
    private SearchView my_search;



    private String myLocation;
    private String term="";
    //private final RestaurantData restaurantData = new RestaurantData();

    private DatabaseReference mDatabse;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabse = FirebaseDatabase.getInstance().getReference();

        if (mainGoogleApiClient == null){
            mainGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLastLocation = new Location("");//provider name is unnecessary
        mLastLocation.setLatitude(42.9979d);//your coords of course
        mLastLocation.setLongitude(-76.1288d);

        allButton = new ArrayList<ToggleButton>();
        allButton.add((ToggleButton) findViewById(R.id.KoreanFoodBtn));
        allButton.add((ToggleButton) findViewById(R.id.VietButton));
        allButton.add((ToggleButton) findViewById(R.id.ItalianButton));
        allButton.add((ToggleButton) findViewById(R.id.ChineseButton));
        allButton.add((ToggleButton) findViewById(R.id.BurgerButton));
        allButton.add((ToggleButton) findViewById(R.id.IndianButton));
        allButton.add((ToggleButton) findViewById(R.id.JapaneseButton));
        allButton.add((ToggleButton) findViewById(R.id.FastFood));
        findViewById(R.id.parentLayout).requestFocus();
        ToggleView();


        MainPageBar = (Toolbar)findViewById(R.id.toolbar_main_page);
        setSupportActionBar(MainPageBar);
        MainPageBar.setTitleTextColor(getColor(white));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, MainPageBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        photoImage = (ImageView) headerView.findViewById(R.id.imageView77);


        ///////////////////////////LOAD fRAGMENT////////////////////////////

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar bar = Snackbar.make(getCurrentFocus(), "Detecting...", Snackbar.LENGTH_LONG);
                bar.show();
                initeList();
            }
        });


    }

   ////////////////////////////////////////////////////////////////////////////////////////////////
    public void initeList(){
        term="";
        GetLocation();
        CategoryRequest();

        FragmentManager fm = getSupportFragmentManager();
        RecyclerView = (RecyclerViewMain)fm.findFragmentById(R.id.detailView);
        if (RecyclerView == null) {
            // RecyclerView = new RecyclerViewMain();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.finalContainer, RecyclerViewMain.newInstance(myLocation, term)).commit();
        }
    }

    private boolean openedView = true;
    public void ToggleFoodView(View v){
        ToggleView();
    }
    private void ToggleView(){
        openedView = !openedView;
        if (openedView){
           // findViewById(R.id.button).setVisibility(View.VISIBLE);
            findViewById(R.id.list1).setPivotY(0);
            findViewById(R.id.list2).setPivotY(0);
            findViewById(R.id.list1).animate().scaleY(1).setDuration(400);
            findViewById(R.id.list2).animate().scaleY(1).setDuration(400);
            findViewById(R.id.list1).setVisibility(View.VISIBLE);
            findViewById(R.id.list2).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.typeToggle)).setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_keyboard_arrow_up_black, 0);
        }
        else {
          //  findViewById(R.id.button).setVisibility(View.GONE);
            findViewById(R.id.list1).setPivotY(0);
            findViewById(R.id.list2).setPivotY(0);
            findViewById(R.id.list1).animate().scaleY(0).setDuration(400);
            findViewById(R.id.list2).animate().scaleY(0).setDuration(400);
            findViewById(R.id.list1).setVisibility(View.GONE);
            findViewById(R.id.list2).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.typeToggle)).setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_keyboard_arrow_down_black, 0);

            initeList();
        }
        findViewById(R.id.parentLayout).requestLayout();
    }

    private void CategoryRequest(){
        boolean first = true;
        if (((ToggleButton)findViewById(R.id.KoreanFoodBtn)).isChecked()){
                if (!first) term += ",";
                term += "korean";
                first = false;
        }

        if (((ToggleButton)findViewById(R.id.ChineseButton)).isChecked()){
            if (!first) term +=",";
            term += "chinese";
            first = false;
        }

        if (((ToggleButton)findViewById(R.id.BurgerButton)).isChecked()){
            if (!first) term +=",";
            term += "burgers,sandwiches";
            first = false;
        }

        if (((ToggleButton)findViewById(R.id.VietButton)).isChecked()){
            if (!first) term +=",";
            term += "vietnamese";
            first = false;
        }

        if (((ToggleButton)findViewById(R.id.IndianButton)).isChecked()){
            if (!first) term +=",";
            term += "indpak";
            first = false;
        }

        if (((ToggleButton)findViewById(R.id.ItalianButton)).isChecked()){
            if (!first) term +=",";
            term += "italian";
            first = false;
        }

        if (((ToggleButton)findViewById(R.id.JapaneseButton)).isChecked()){
            if (!first) term +=",";
            term += "japanese";
            first = false;
        }

        if (((ToggleButton)findViewById(R.id.FastFood)).isChecked()){
            if (!first) term +=",";
            term += "food_court,hotdogs";
            first = false;
        }


    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    private void GetLocation(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    this.checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
            }
        }

        boolean perm = true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            perm = this.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    this.checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        // if ok perms
        if(perm) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mainGoogleApiClient);

            if (mLastLocation != null) {
                String temp_lon = String.valueOf(mLastLocation.getLongitude());
                String temp_lat = String.valueOf(mLastLocation.getLatitude());
                myLocation ="&latitude="+temp_lat+"&longitude="+temp_lon;
            }

           // myLocation = "&latitude="+String.valueOf(mLastLocation.getLatitude()+"&longitude="+String.valueOf(mLastLocation.getLongitude()));
         //   myLocation ="&latitude="+temp_lat+"&longitude="+temp_lon;
        //    Log.d("!!!!!!!!!!!!@@@@", String.valueOf(mLastLocation));
           if (mLastLocation != null) {
              //  Toast.makeText(this, "Location detected", Toast.LENGTH_SHORT).show();
           }

        } else {
            //((Button)findViewById(R.id.LocationButton)).setEnabled(false);
            Log.i("Location", "no permission");
        }
    }




    ///////////////////////////////////////////////////////////////////////////////////////////

    // get the camera and set the image view content by click the image

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == CAMERA_FLAG && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            photoImage.setImageBitmap(bitmap);
        } else if (requestCode == GALLERY_FLAG && requestCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.d("my", uri.toString());
            Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor.moveToFirst()) {
                String ImgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Log.d("my", ImgPath);
                photoImage.setImageBitmap(BitmapFactory.decodeFile(ImgPath));
            }
        } else */if (requestCode == 0 && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String text = results.get(0);
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            my_search.setQuery(text,false);
        }
    }



    @Override
    protected void onStart() {
       mainGoogleApiClient.connect();

        super.onStart();
    }

    @Override
    protected void onStop() {
        mainGoogleApiClient.disconnect();
        super.onStop();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);

//        final SearchView my_search = (SearchView) menu.findItem(R.id.SearchView).getActionView();
//        my_search.setSubmitButtonEnabled(true);
//
//        if (my_search != null) {
//            my_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    return true;
//                }
//            });
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.account) {
            Intent intent = new Intent(MainActivity.this, AccountInfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.settings) {
            startActivity(new Intent(MainActivity.this, listActivity.class));

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            startActivity(new Intent(MainActivity.this, ContactActivity.class));
        } else if (id == R.id.nav_send) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();;
                            startActivity(new Intent(MainActivity.this, LogInActivity.class));
                        }
                    });

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(HashMap map) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("MovieDetail",map);
        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
          initeList();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void startvoice(SearchView s) {
        my_search = s;
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(i, 0);
    }
}
