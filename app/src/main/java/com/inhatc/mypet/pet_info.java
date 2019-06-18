package com.inhatc.mypet;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class pet_info extends AppCompatActivity {

    String user_id;
    String get_pet_name;
    TextView txt_name;
    TextView txt_gender;
    TextView txt_birth;
    TextView txt_type;

    ImageView img_main;
    ImageView img_gender;
    ImageView img_birth;
    ImageView img_type;
    DatabaseReference db;
    StorageReference mStorageRef;
    ListViewAdapter2 adapter2;
    ListView list ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pet_info );

        Intent getIntent =getIntent();
        user_id = getIntent.getStringExtra("pet_id");
        get_pet_name=getIntent.getStringExtra("pet_name");
        txt_name = (TextView)findViewById( R.id.txt_name );
        txt_gender = (TextView)findViewById( R.id.txt_gender );
        txt_birth = (TextView)findViewById( R.id.txt_birth );
        txt_type = (TextView)findViewById( R.id.txt_type );
        txt_name.setText(get_pet_name);

        img_main=(ImageView)findViewById( R.id.mainimg );
        img_gender=(ImageView)findViewById( R.id.img_gender );
        img_birth=(ImageView)findViewById( R.id.img_birth );
        img_type=(ImageView)findViewById( R.id.img_type );
        img_birth.setImageResource( R.drawable.img_birth );
        db = FirebaseDatabase.getInstance().getReference("user-list");
        adapter2 = new ListViewAdapter2();
        list =(ListView)findViewById( R.id.list);
        list.setAdapter(adapter2);
        datasetting();




        list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final listview_item get_petinfo = (listview_item) parent.getItemAtPosition( position );
                Calendar cal = Calendar.getInstance();
                TimePickerDialog dialog = new TimePickerDialog(pet_info.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {

                        String msg = String.format("%d 시 %d 분", hour, min);
                        Toast.makeText(pet_info.this, msg, Toast.LENGTH_SHORT).show();
                        db.child(user_id).child("pet").child(get_petinfo.getName()).child("info").child(get_petinfo.getTitle()).setValue(msg);
                    }
                }, cal.get( Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);  //마지막 boolean 값은 시간을 24시간으로 보일지 아닐지
                dialog.show();
            }
        } );

    }
    private void datasetting(){
        mStorageRef= FirebaseStorage.getInstance().getReference().child(user_id).child("("+get_pet_name+")_image.jpg");
        mStorageRef.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(pet_info.this).load(uri).into(img_main);
            }
        } );

        db.child(user_id).child("pet").child(get_pet_name).child("gender").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String set_gender =dataSnapshot.getValue(String.class);
                txt_gender.setText(set_gender);
                if(set_gender.equals("수컷")|| set_gender.equals("수컷(중성화)")){
                    img_gender.setImageResource(R.drawable.gender_m);
                }else{
                    img_gender.setImageResource(R.drawable.gender_w);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
        db.child(user_id).child("pet").child(get_pet_name).child("birth").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String set_birth =dataSnapshot.getValue(String.class);
                txt_birth.setText(set_birth);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
        db.child(user_id).child("pet").child(get_pet_name).child("type").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String set_type =dataSnapshot.getValue(String.class);
                txt_type.setText(set_type);
                if(set_type.equals("개")){
                    img_type.setImageResource(R.drawable.img_dog);
                }else if(set_type.equals("고양이")){
                    img_type.setImageResource(R.drawable.img_cat);
                }else if(set_type.equals("토끼")){
                    img_type.setImageResource(R.drawable.img_rabbit);
                }else if(set_type.equals("고슴도치")){
                    img_type.setImageResource(R.drawable.img_gosm);
                }else if(set_type.equals("기니피그")){
                    img_type.setImageResource(R.drawable.img_gunie);
                }else if(set_type.equals("햄스터")) {
                    img_type.setImageResource(R.drawable.img_hams);
                }else{
                    img_type.setImageResource(R.drawable.ic_test);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
        adapter2.addItem( ContextCompat.getDrawable( this,R.drawable.img_dish ),"식사",get_pet_name,user_id );
        adapter2.addItem( ContextCompat.getDrawable( this,R.drawable.img_snack ),"간식",get_pet_name,user_id);
        adapter2.addItem( ContextCompat.getDrawable( this,R.drawable.img_walk ),"산책",get_pet_name,user_id);
        adapter2.addItem( ContextCompat.getDrawable( this,R.drawable.img_shower ),"목욕",get_pet_name,user_id);


    }
}
