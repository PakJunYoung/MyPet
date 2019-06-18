package com.inhatc.mypet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Iterator;

public class pet_upd_select extends AppCompatActivity {

    private ListView mListView;
    String user_id;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pet_upd_select );
        user_id = ((main)main.mContext).data();
        mListView = (ListView)findViewById(R.id.listView);
        dataSetting();
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final listview_item get_petinfo = (listview_item) parent.getItemAtPosition( position );
                Intent updIntent = new Intent(pet_upd_select.this,pet_upd.class);
                updIntent.putExtra("pet_name",get_petinfo.getName());
                startActivityForResult(updIntent,1);
            }
        } );
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1){
            dataSetting();
        }
    }
    private void dataSetting(){

        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference("user-list").child(user_id).child("pet");
        db.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child =dataSnapshot.getChildren().iterator();
                ListViewAdapter mMyAdapter = new ListViewAdapter();

                while(child.hasNext()){
                    String name=child.next().getKey();
                    mStorageRef = FirebaseStorage.getInstance().getReference().child(user_id).child("("+name+")_image.jpg");
                    mMyAdapter.addItem(mStorageRef, name,user_id);
                }
                mListView.setAdapter(mMyAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );

        //mMyAdapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_tests), "우리" , "안녕" );

        /* 리스트뷰에 어댑터 등록 */

    }
}
