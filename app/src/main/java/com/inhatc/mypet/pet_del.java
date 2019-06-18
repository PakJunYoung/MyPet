package com.inhatc.mypet;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;

public class pet_del extends AppCompatActivity {

    private ListView mListView;
    String user_id;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pet_del );
        user_id = ((main)main.mContext).data();
        mListView = (ListView)findViewById(R.id.listView);
        dataSetting();
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(pet_del.this);
                final listview_item get_petinfo = (listview_item) parent.getItemAtPosition( position );
                dialog.setTitle("삭제");
                dialog.setMessage("펫 '"+get_petinfo.getName()+"' 정말 삭제 하시겠습니까?");
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference db;
                        db = FirebaseDatabase.getInstance().getReference("user-list").child(user_id).child("pet").child(get_petinfo.getName());
                        db.removeValue();
                        /*db.addListenerForSingleValueEvent( new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().removeValue();
                                finish();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        } );*/
                        FirebaseStorage.getInstance().getReference().child(user_id).child("("+get_petinfo.getName()+")_image.jpg").delete();
                    }
                });
                dialog.setNegativeButton( "취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        } );
    }
    private void dataSetting(){
        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference("user-list").child(user_id).child("pet");
        db.addValueEventListener( new ValueEventListener() {
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


        Intent backIntent = getIntent();
        backIntent.putExtra( "ch",true );
        setResult(RESULT_OK,backIntent);

        //mMyAdapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_tests), "우리" , "안녕" );

        /* 리스트뷰에 어댑터 등록 */

    }
}
