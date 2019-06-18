package com.inhatc.mypet;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class pet_group extends AppCompatActivity {

    Button btn_group;
    TextView txt_groupNum;
    EditText edt_groupNum;
    Button btn_group_join;
    String user_id;
    String user_name;
    DatabaseReference db;
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pet_group );

        user_id=((main)main.mContext).data();
        user_name=((main)main.mContext).User_name();
        btn_group = (Button)findViewById(R.id.btn_group);
        txt_groupNum=(TextView)findViewById(R.id.txt_groupNum);
        edt_groupNum=(EditText)findViewById(R.id.edt_groupNum);
        btn_group_join=(Button)findViewById(R.id.btn_group_join);

        db = FirebaseDatabase.getInstance().getReference();
        db.child("user-list").child(user_id).child("group").child("check").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ch = dataSnapshot.getValue(String.class);
                if(ch==null || ch.equals("F")){
                    btn_group.setText("그룹 만들기");
                }else{
                    db.child("user-list").child(user_id).child("group").child("code").addListenerForSingleValueEvent( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String code = dataSnapshot.getValue(String.class);
                            txt_groupNum.setText(code);
                            test();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    btn_group.setText("그룹 탈퇴");
                    edt_groupNum.setVisibility(View.INVISIBLE);
                    btn_group_join.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        btn_group.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCode = new String();
                if(btn_group.getText().equals("그룹 만들기")){
                    String[] str = {"1","2","3","4","5","6","7", "8", "9"
                            ,"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

                    for (int x = 0; x < 6; x++) {
                        int random = (int) (Math.random() * str.length);
                        newCode += str[random];
                    }
                    db.child("user-list").child(user_id).child("group").child("check").setValue("T");
                    db.child("user-list").child(user_id).child("group").child("code").setValue(newCode);
                    db.child("Group").child(newCode).child(user_id).setValue("Leader");
                    txt_groupNum.setText(newCode);
                    btn_group.setText("그룹 탈퇴");
                    edt_groupNum.setVisibility(View.INVISIBLE);
                    btn_group_join.setVisibility(View.INVISIBLE);
                }else{
                    db.child("user-list").child(user_id).child("group").child("check").setValue("F");
                    db.child("user-list").child(user_id).child("group").child("code").removeValue();
                    db.child("Group").child(txt_groupNum.getText().toString()).child(user_id).removeValue();
                    txt_groupNum.setText("그룹 코드");
                    btn_group.setText("그룹 만들기");
                    edt_groupNum.setVisibility(View.VISIBLE);
                    btn_group_join.setVisibility(View.VISIBLE);
                }
            }
        } );
        btn_group_join.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String code = edt_groupNum.getText().toString();
                db.child("Group").addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Iterator<DataSnapshot> child =dataSnapshot.getChildren().iterator();
                        while (child.hasNext()){
                            if(code.equals(child.next().getKey())){
                                db.child("Group").child(code).addListenerForSingleValueEvent( new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final Iterator<DataSnapshot> child =dataSnapshot.getChildren().iterator();
                                        while (child.hasNext()){
                                            final String leader_id= child.next().getKey();
                                            db.child("Group").child(code).child(leader_id).addListenerForSingleValueEvent( new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    String leader = dataSnapshot.getValue(String.class);
                                                    if(leader.equals("Leader")){
                                                        AlertDialog.Builder dialog = new AlertDialog.Builder(pet_group.this);
                                                        dialog.setTitle("그룹 가입");
                                                        dialog.setMessage("그룹장의 아이디: "+leader_id+"\n맞습니까?");
                                                        dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                db.child("user-list").child(leader_id).child("group").child("alert").child(user_id).setValue(user_name);
                                                                Toast.makeText(getApplicationContext(), "가입 신청을 전송했습니다.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                        dialog.setNegativeButton( "아니오", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                            }
                                                        });
                                                        dialog.show();
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            } );
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                } );
                            }else{
                                Toast.makeText(getApplicationContext(), "존재하지 않는 코드입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                } );
            }
        } );
    }
    private void test(){
        mListView =(ListView)findViewById(R.id.signlist);
        db.child("user-list").child(user_id).child("group").child("alert").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child =dataSnapshot.getChildren().iterator();
                ListViewAdapter3 mMyAdapter = new ListViewAdapter3();

                while(child.hasNext()){
                    String id=child.next().getKey();
                    mMyAdapter.addsign_id(id,user_id);
                }
                mListView.setAdapter(mMyAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final listview_item get_userinfo = (listview_item) parent.getItemAtPosition( position );
                AlertDialog.Builder dialog = new AlertDialog.Builder(pet_group.this);
                dialog.setTitle("그룹 가입");
                dialog.setMessage(get_userinfo.getid()+"님 을 가입 시키겠습니까?");
                dialog.setPositiveButton("수락", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.child("Group").child(txt_groupNum.getText().toString()).child(get_userinfo.getid()).setValue("mem");
                        db.child("user-list").child(user_id).child("group").child("alert").child(get_userinfo.getid()).removeValue();
                        db.child("user-list").child(get_userinfo.getid()).child("group").child("check").setValue("T");
                        db.child("user-list").child(get_userinfo.getid()).child("group").child("code").setValue(txt_groupNum.getText().toString());
                        Toast.makeText(getApplicationContext(), "수락 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNegativeButton( "거절", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.child("user-list").child(user_id).child("group").child("alert").child(get_userinfo.getid()).removeValue();
                    }
                });
                dialog.show();
            }
        } );
    }
}
