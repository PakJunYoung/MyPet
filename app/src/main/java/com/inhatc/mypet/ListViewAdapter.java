package com.inhatc.mypet;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter{

    public ListViewAdapter mContext;
    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<listview_item> mItems = new ArrayList<>();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public listview_item getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();
        mContext=this;

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_listview_item, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        final ImageView iv_img = (ImageView) convertView.findViewById(R.id.imageView1) ;
        final ImageView img_gender = (ImageView) convertView.findViewById(R.id.imageView2);
        ImageView img_birth = (ImageView) convertView.findViewById(R.id.imageView3);
        final ImageView img_type = (ImageView) convertView.findViewById(R.id.imageView4);


        TextView tv_name = (TextView) convertView.findViewById(R.id.pet_name) ;
        final TextView tv_birth = (TextView) convertView.findViewById(R.id.pet_birth) ;
        final TextView tv_type=(TextView) convertView.findViewById( R.id.pet_type );

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        final listview_item myItem = getItem(position);


        myItem.getUri().getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(pet.mContext).load(uri).into(iv_img);
            }
        } );
        myItem.getUri().getDownloadUrl().addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iv_img.setImageDrawable( ContextCompat.getDrawable(context, R.drawable.add_image) );
            }
        } );
        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        tv_name.setText(myItem.getName());
        img_birth.setImageResource( R.drawable.img_birth );
        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference("user-list").child(myItem.getid()).child("pet");
        db.child(myItem.getName()).child("birth").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String birth=dataSnapshot.getValue(String.class);
                tv_birth.setText(birth);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );

        db.child(myItem.getName()).child("type").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String type=dataSnapshot.getValue(String.class);
                tv_type.setText(type);
                if(type.equals("개")){
                    img_type.setImageResource(R.drawable.img_dog);
                }else if(type.equals("고양이")){
                    img_type.setImageResource(R.drawable.img_cat);
                }else if(type.equals("토끼")){
                    img_type.setImageResource(R.drawable.img_rabbit);
                }else if(type.equals("고슴도치")){
                    img_type.setImageResource(R.drawable.img_gosm);
                }else if(type.equals("기니피그")){
                    img_type.setImageResource(R.drawable.img_gunie);
                }else if(type.equals("햄스터")) {
                    img_type.setImageResource(R.drawable.img_hams);
                }else{
                    img_type.setImageResource(R.drawable.ic_test);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );

        db.child(myItem.getName()).child("gender").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gender=dataSnapshot.getValue(String.class);
                if(gender.equals("암컷")){
                    img_gender.setImageResource(R.drawable.gender_w);
                }else if(gender.equals("암컷(중성화)")){
                    img_gender.setImageResource(R.drawable.gender_w);
                }else if(gender.equals("수컷")){
                    img_gender.setImageResource(R.drawable.gender_m);
                }else if(gender.equals("수컷(중성화)")){
                    img_gender.setImageResource(R.drawable.gender_m);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */


        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(StorageReference uri, String name,String id) {

        listview_item mItem = new listview_item();

        /* MyItem에 아이템을 setting한다. */
        mItem.setUri(uri);
        mItem.setName(name);
        mItem.setid(id);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);

    }
}

