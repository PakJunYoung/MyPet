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

public class ListViewAdapter2 extends BaseAdapter{

    public ListViewAdapter2 mContext;
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
            convertView = inflater.inflate(R.layout.activity_pet_info_list, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        ImageView iv_img = (ImageView) convertView.findViewById(R.id.imageView1) ;

        TextView tv_kind = (TextView) convertView.findViewById(R.id.kind) ;
        final TextView tv_time = (TextView) convertView.findViewById(R.id.time) ;

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        listview_item myItem = getItem(position);

        iv_img.setImageDrawable(myItem.getIcon());
        tv_kind.setText(myItem.getTitle());
        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference("user-list").child(myItem.getid()).child("pet");
        db.child(myItem.getName()).child("info").child(myItem.getTitle()).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String time=dataSnapshot.getValue(String.class);
                tv_time.setText(time);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */



        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */


        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(Drawable icon, String title,String name,String id) {

        listview_item mItem = new listview_item();

        /* MyItem에 아이템을 setting한다. */
        mItem.setIcon(icon);
        mItem.setTitle(title);
        mItem.setName( name );
        mItem.setid( id );

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);
    }
}

