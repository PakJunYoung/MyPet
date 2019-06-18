package com.inhatc.mypet;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Iterator;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class join extends AppCompatActivity {
    private TextView user_password = null;
    private TextView password_ch = null;
    private String email_code =null;
    private TextView user_name = null;
    private TextView user_id = null;
    private TextView email_tv = null;
    private TextView email_id = null;
    private TextView email_num = null;
    private TextView alert_1;
    private TextView alert_2;

    private Button id_chbtn = null;
    private Button email_btn = null;
    private Button email_numCh = null;
    private  Button sign_btn = null;
    private boolean id_overlapch = false;
    private boolean final_ch=true;

    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        user_name = (TextView) findViewById(R.id.join_name);
        user_id = (TextView) findViewById(R.id.join_id);
        alert_1 = (TextView) findViewById(R.id.join_alert1);
        alert_2 = (TextView) findViewById(R.id.join_alert2);
        email_tv = (TextView) findViewById(R.id.textView5);
        email_id = (TextView) findViewById(R.id.email_txt); //받는 사람의 이메일
        email_num = (TextView) findViewById(R.id.join_emailnum); // 인증번호 적는 칸
        id_chbtn = (Button) findViewById(R.id.join_id_chBtn); //중복검사 버튼
        email_numCh = (Button) findViewById(R.id.join_emailnum_chBtn); // 인증번호 체크버튼
        email_btn = (Button) findViewById(R.id.join_emailBtn); // 인증번호 이메일보내는 버튼
        sign_btn = (Button) findViewById(R.id.join_signBtn);
        db = FirebaseDatabase.getInstance().getReference("user-list");


        id_chbtn.setOnClickListener(new View.OnClickListener(){//중복검사 버튼
            @Override
            public void onClick(View v) {
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child =dataSnapshot.getChildren().iterator();
                        if(user_id.length() == 0){
                            Toast.makeText(getApplicationContext(), "ID를 입력하세요", Toast.LENGTH_SHORT).show();
                        }else{
                            while (child.hasNext()){
                                if(user_id.getText().toString().replaceAll("\\p{Z}","").equals(child.next().getKey())){
                                    Toast.makeText(getApplicationContext(), "이미 존재하는 ID입니다.", Toast.LENGTH_SHORT).show();
                                    db.removeEventListener(this);
                                    id_overlapch = true;
                                    break;
                                }else{
                                    id_overlapch=false;
                                }
                            }
                            if(!id_overlapch){
                                Toast.makeText(getApplicationContext(), "사용가능한 ID입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });
        user_id.addTextChangedListener(new TextWatcher() {//중복체크후 id 재입력받을때
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                id_overlapch = true;
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        user_password = (TextView) findViewById(R.id.join_pw);
        password_ch = (TextView) findViewById(R.id.join_pw_ch);
        user_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //user_password 입력중
                if (user_password.getText().toString().equals(password_ch.getText().toString()) && user_password.length()!=0) {
                    alert_1.setText("PW 일치");
                    alert_1.setTextColor(Color.GREEN);
                }else if(user_password.length()==0){
                    alert_1.setText("");
                }else {
                    alert_1.setText("PW 불일치");
                    alert_1.setTextColor(Color.RED);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //user_password 입력전
            }
            @Override
            public void afterTextChanged(Editable s) {
                //user_password 입력후
            }
        });
                password_ch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //password_ch 입력 중
                        if (user_password.getText().toString().equals(password_ch.getText().toString()) && password_ch.length()!=0) {
                            alert_1.setText("PW 일치");
                            alert_1.setTextColor(Color.GREEN);
                        }else if(password_ch.length()==0){
                            alert_1.setText("");
                        }else {
                            alert_1.setText("PW 불일치");
                            alert_1.setTextColor(Color.RED);
                        }
                    }
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        //password_ch 입력전
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        //password_ch 입력후
                    }
                });

                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .permitDiskReads()
                        .permitDiskWrites()
                        .permitNetwork().build());



        email_btn.setOnClickListener(new View.OnClickListener() { //이메일 보내는 버튼 이벤트
            @Override
            public void onClick(View v) { //이메일보내는 버튼 기능
                try {
                    GmailSender gMailSender = new GmailSender("ikingpark95@gmail.com", "xcbobtbotfvjclhg");
                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
                    String newcode = gMailSender.createEmailCode();
                    email_code = newcode;
                    gMailSender.sendMail("MyPat 인증번호 입니다.", "인증번호는\n"+newcode+"\n입니다.", email_id.getText().toString());
                    Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                    email_btn.setText("재전송");
                    email_num.setVisibility(View.VISIBLE);
                    email_numCh.setVisibility(View.VISIBLE);
                } catch (SendFailedException e) {
                    Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (MessagingException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        email_numCh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //인증번호 체크 버튼
                try{
                    if(email_code.equals(email_num.getText().toString())){
                        email_tv.setVisibility(View.GONE);
                        email_id.setVisibility(View.GONE);
                        email_btn.setVisibility(View.GONE);
                        email_num.setVisibility(View.GONE);
                        email_numCh.setVisibility(View.GONE);
                        alert_2.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "인증되었습니다.", Toast.LENGTH_SHORT).show();
                        final_ch=true;
                    }else if(email_num.getText().equals("") || email_num.length() == 0){
                        alert_2.setText("인증번호를 입력하세요.");
                    }else{
                        alert_2.setText("인증번호가 일치하지 않습니다.");
                    }
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "에러에러", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        sign_btn.setOnClickListener(new View.OnClickListener(){
            AlertDialog.Builder dialog = new AlertDialog.Builder(join.this);
            @Override
            public void onClick(View v) {
                if(user_name.length()==0){
                    user_name.requestFocus();
                    Toast.makeText(getApplicationContext(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                }else if(user_id.length()==0){
                    user_id.requestFocus();
                    Toast.makeText(getApplicationContext(), "ID를 입력하세요.", Toast.LENGTH_SHORT).show();
                }else if(id_overlapch){
                    Toast.makeText(getApplicationContext(), "ID중복체크를 하세요.", Toast.LENGTH_SHORT).show();
                }else if(alert_1.getText().equals("PW 불일치")){
                    user_password.requestFocus();
                    Toast.makeText(getApplicationContext(), "패스워드를 확인하세요.", Toast.LENGTH_SHORT).show();
                }else if(email_id.length()==0){
                    Toast.makeText(getApplicationContext(), "email을 입력하세요.", Toast.LENGTH_SHORT).show();
                    email_id.requestFocus();
                }else if(!final_ch){
                    Toast.makeText(getApplicationContext(), "email 인증을 하세요.", Toast.LENGTH_SHORT).show();
                }else if(final_ch&&!id_overlapch){

                    dialog.setTitle("알림");
                    dialog.setMessage("회원가입 되었습니다.");
                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(),login.class);
                            startActivity(intent);
                        }
                    });
                    addUser(user_name.getText().toString(),user_id.getText().toString(),user_password.getText().toString(),email_id.getText().toString());
                    dialog.show();
                }
            }
        });
    }
    private void addUser(String name,String id,String pw, String email){
        User user = new User(name ,pw ,email);
        db.child(id).setValue(user);
    }
}
class User{
    public String name;
    public String pw;
    public String email;
    public User(String name,String pw,String email){
        this.name=name;
        this.pw=pw;
        this.email=email;
    }
}

