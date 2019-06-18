package com.inhatc.mypet;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListViewAdapter3 extends BaseAdapter{

    public ListViewAdapter3 mContext;
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
            convertView = inflater.inflate(R.layout.activity_pet_group_list, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */

        final TextView sign_name = (TextView) convertView.findViewById(R.id.sign_name) ;
        final TextView sign_id = (TextView) convertView.findViewById(R.id.sign_id) ;

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        listview_item myItem = getItem(position);


        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference("user-list").child(myItem.getTitle()).child("group").child("alert");
        db.child(myItem.getid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sign_name.setText(dataSnapshot.getValue(String.class));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
        sign_id.setText(myItem.getid());

        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addsign_id(String id,String title) {

        listview_item mItem = new listview_item();

        /* MyItem에 아이템을 setting한다. */
        mItem.setid( id );
        mItem.setTitle(title);
        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);
    }

}

