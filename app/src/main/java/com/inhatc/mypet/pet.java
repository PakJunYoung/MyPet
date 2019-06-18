package com.inhatc.mypet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;


public class pet extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public SwipeRefreshLayout mSwipeRefreshLayout;
    private String user_id;
    private Animation fab_open,fab_close;
    private Boolean isFabOpen=false;
    private FloatingActionButton fab,fab_sub1,fab_sub2,fab_sub3;
    StorageReference mStorageRef;
    private ListView mListView;
    public static pet mContext;
    ArrayList a = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_pet,container,false);
        mListView = (ListView) v.findViewById(R.id.listView);
        user_id = ((main)getActivity()).data();
        /* 아이템 추가 및 어댑터 등록 */
        dataSetting();
        mContext = this;

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab_sub1 = (FloatingActionButton) v.findViewById(R.id.fab_add);
        fab_sub2 = (FloatingActionButton) v.findViewById(R.id.fab_del);
        fab_sub3 = (FloatingActionButton) v.findViewById(R.id.fab_update);
        fab_open = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);

        mSwipeRefreshLayout.setOnRefreshListener(this );
        fab.setOnClickListener(this);
        fab_sub1.setOnClickListener(this);
        fab_sub2.setOnClickListener(this);
        fab_sub3.setOnClickListener(this);
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final listview_item get_petinfo = (listview_item) parent.getItemAtPosition( position );
                Intent pet_infoIntent = new Intent(getActivity(),pet_info.class);
                pet_infoIntent.putExtra( "pet_id",get_petinfo.getid());
                pet_infoIntent.putExtra("pet_name",get_petinfo.getName());
                startActivity(pet_infoIntent);
            }
        } );

        return v;
    }
    @Override
    public void onRefresh() {
        // 새로고침 코드
        dataSetting();
        // 새로고침 완료
        mSwipeRefreshLayout.setRefreshing(false);
    }
    private void dataSetting(){
        final DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference("user-list").child(user_id);
        db.child("group").child("check").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ch = dataSnapshot.getValue(String.class);
                if(ch==null || ch.equals("F")){

                    db.child("pet").addListenerForSingleValueEvent( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> child =dataSnapshot.getChildren().iterator();
                            final ListViewAdapter mMyAdapter = new ListViewAdapter();
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
                }else{
                    group_petlist();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //mMyAdapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_tests), "우리" , "안녕" );
        /* 리스트뷰에 어댑터 등록 */
    }
    private void group_petlist(){
        final DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference();
        db.child("user-list").child(user_id).child("group").child("code").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String code = dataSnapshot.getValue(String.class);
                db.child("Group").child(code).addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child =dataSnapshot.getChildren().iterator();
                        a.clear();
                        while(true){
                            String id=child.next().getKey();
                            if(child.hasNext()){
                                array(id,1);
                            }else{
                                array(id,0);
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:
                anim();
                break;
            case R.id.fab_add:
                anim();
                Intent addIntent = new Intent(getActivity(),pet_add.class);
                startActivity(addIntent);
                break;
            case R.id.fab_del:
                anim();
                Intent delIntent = new Intent(getActivity(),pet_del.class);
                startActivity(delIntent);
                break;
            case R.id.fab_update:
                anim();
                Intent upd_selectIntent = new Intent(getActivity(),pet_upd_select.class);
                startActivity(upd_selectIntent);
                break;
        }
    }
    public void array(final String id, final int ch){

        DatabaseReference db;
        final ListViewAdapter mMyAdapter = new ListViewAdapter();
        db = FirebaseDatabase.getInstance().getReference("user-list").child(id);
        db.child("pet").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child =dataSnapshot.getChildren().iterator();

                while(child.hasNext()){
                    String name = child.next().getKey();
                    a.add(id+"/"+name);
                }
                if(ch == 0){
                    for(int i=0;i<a.size();i++){
                        String arr = a.get(i).toString();
                        int idx = arr.indexOf("/");
                        String name = arr.substring(idx+1);
                        String id=arr.substring(0,idx);
                        mStorageRef = FirebaseStorage.getInstance().getReference().child(id).child("("+name+")_image.jpg");
                        mMyAdapter.addItem(mStorageRef,name,id);
                    }
                    mListView.setAdapter(mMyAdapter);
                    //Toast.makeText(getActivity(), test.substring(test.indexOf("/")+1), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );



    }
    public void anim(){
        if(isFabOpen){
            fab_sub1.startAnimation(fab_close);
            fab_sub2.startAnimation(fab_close);
            fab_sub3.startAnimation(fab_close);
            fab_sub1.setClickable(false);
            fab_sub2.setClickable(false);
            fab_sub3.setClickable(false);
            isFabOpen=false;
        }else{
            fab_sub1.startAnimation(fab_open);
            fab_sub2.startAnimation(fab_open);
            fab_sub3.startAnimation(fab_open);
            fab_sub1.setClickable(true);
            fab_sub2.setClickable(true);
            fab_sub3.setClickable(true);
            isFabOpen=true;
        }
    }
}
