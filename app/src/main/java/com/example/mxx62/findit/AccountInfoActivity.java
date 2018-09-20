package com.example.mxx62.findit;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit.http.POST;

public class AccountInfoActivity extends AppCompatActivity implements ReviewlistFrag.OnFragmentInteractionListener{

    FloatingActionButton reviewfab;
    FrameLayout first, second;
    FragmentManager fragmentManager;

    ImageView image_face;
    TextView useremail;

    DatabaseReference reference;
    FirebaseAuth firebaseAuth;
    final private int CAMERA_FLAG = 0;
    final private int GALLERY_FLAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info);

        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        first = (FrameLayout)findViewById(R.id.accountholder);
        second = (FrameLayout)findViewById(R.id.reviewholder);

        reviewfab = (FloatingActionButton)findViewById(R.id.fab_review);

        fragmentManager =getSupportFragmentManager();
        reviewfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first.setVisibility(View.GONE);
                second.setVisibility(View.VISIBLE);
                ReviewlistFrag frag = new ReviewlistFrag();
                fragmentManager.beginTransaction()
                        .replace(R.id.reviewholder, frag)
                        .addToBackStack(null).commit();

            }
        });

        image_face = (RoundImageView)findViewById(R.id.singleCompLogo);

        DatabaseReference refer = reference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("imageUrl").getRef();
        refer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String file = (String)dataSnapshot.getValue();
                if (file != null) {
                    try {
                        Bitmap bitmap = decodeFromFirebaseBase64(file);
                        image_face.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("sdfsdf", file);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        image_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
            }
        });

        useremail = (TextView)findViewById(R.id.useremail);
        String email = firebaseAuth.getCurrentUser().getEmail();
        if (email != null){
            useremail.setText(email);
        }
        else {
            return;
        }
    }

    public void initView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_menu_camera)
                .setTitle("Choose Image")
                //2 items
                .setItems(new String[]{"Camera", "Gallery"}, new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case CAMERA_FLAG:
                                pickPicFromCam();
                                break;
                            case GALLERY_FLAG:
                                pickPicFromPic();
                                break;
                            default:
                                break;
                        }

                    }
                });
        builder.create().show();

    }

    private void pickPicFromCam() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_FLAG);
    }


    private void pickPicFromPic() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_FLAG);
    }


    // get the camera and set the image view content by click the image

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_FLAG && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            image_face.setImageBitmap(bitmap);
            encodeBitmapAndSaveToFirebase(bitmap);
        } else if (requestCode == GALLERY_FLAG && requestCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.d("my", uri.toString());
            Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor.moveToFirst()) {
                String ImgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Log.d("my", ImgPath);
                image_face.setImageBitmap(BitmapFactory.decodeFile(ImgPath));
            }
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        DatabaseReference databaseReference = reference.child("users")
                .child(firebaseAuth.getCurrentUser().getUid())
                .child("imageUrl");
        databaseReference.setValue(imageEncoded);
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public void onBackPressed() {
        first.setVisibility(View.VISIBLE);
        second.setVisibility(View.GONE);
        super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}