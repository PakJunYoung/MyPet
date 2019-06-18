package com.inhatc.mypet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class login extends AppCompatActivity {

    private DatabaseReference db;
    private TextView user_id=null;
    private TextView user_password=null;
    private Button login_btn = null;
    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_btn = (Button) findViewById(R.id.login_btn);
        user_id = (TextView) findViewById(R.id.login_ID);
        user_password = (TextView) findViewById(R.id.login_PW);
        db = FirebaseDatabase.getInstance().getReference("user-list");
        login_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child =dataSnapshot.getChildren().iterator();
                        check = false;
                        while (child.hasNext()){
                            if(user_id.getText().toString().equals(child.next().getKey())){
                                check = true;
                                db.child(user_id.getText().toString()).child("pw").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Iterator<DataSnapshot> child =dataSnapshot.getChildren().iterator();
                                       String psswd = dataSnapshot.getValue(String.class);
                                        if(user_password.getText().toString().equals(psswd)){
                                            Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                                            db.removeEventListener(this);
                                            Intent intent = new Intent(login.this, main.class);
                                            intent.putExtra("id",user_id.getText().toString());
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(getApplicationContext(), "password가 틀립니다.", Toast.LENGTH_SHORT).show();
                                            db.removeEventListener(this);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                        if(user_id.length()==0){
                            Toast.makeText(getApplicationContext(), "ID를 입력하세요", Toast.LENGTH_SHORT).show();
                        }else if(!check){
                            Toast.makeText(getApplicationContext(), "ID가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        Button start_join = findViewById(R.id.start_join);
        start_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, join.class);
                startActivity(intent);
            }
        });


    }
}
