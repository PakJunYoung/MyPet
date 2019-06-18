package com.inhatc.mypet;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.InternalTokenResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;

public class pet_upd extends AppCompatActivity {
    String get_pet_name;

    ImageView image;
    TextView pet_name;
    TextView pet_birth;
    TextView pet_type;
    TextView pet_gender;
    TableRow row_name;
    TableRow row_birth;
    TableRow row_type;
    TableRow row_gender;
    String user_id;
    Button btn;
    DatabaseReference db;
    StorageReference mStorageRef;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pet_upd );

        Intent getIntent =getIntent();
        get_pet_name=getIntent.getStringExtra("pet_name");
        Toast.makeText(getApplicationContext(), get_pet_name, Toast.LENGTH_SHORT).show();
        user_id = ((main)main.mContext).data();

        image=(ImageView)findViewById(R.id.imageView);
        pet_name=(TextView)findViewById( R.id.pet_name );
        pet_birth = (TextView)findViewById(R.id.pet_birth);
        pet_type = (TextView)findViewById(R.id.pet_type);
        pet_gender = (TextView)findViewById(R.id.pet_gender);
        row_name = (TableRow) findViewById(R.id.row_name);
        row_birth = (TableRow)findViewById( R.id.row_birth);
        row_gender = (TableRow)findViewById( R.id.row_gender);
        row_type = (TableRow)findViewById( R.id.row_type);
        btn = (Button)findViewById(R.id.btn);
        db = FirebaseDatabase.getInstance().getReference("user-list");
        petsetting();
        image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType( MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,1);
            }
        } );

        row_birth.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.DialogFragment newFragment = new DatePickerFragment2();   //DatePickerFragment 객체 생성
                newFragment.show(getSupportFragmentManager(), "datePicker");                //프래그먼트 매니저를 이용하여 프래그먼트 보여주기
            }
        } );

        row_gender.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(pet_upd.this);
                dialog.setTitle("이름 입력");
                final String [] items = {"수컷", "수컷(중성화)", "암컷", "암컷(중성화)"};
                final int[] value={0};
                dialog.setSingleChoiceItems( items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        value[0] = which;
                    }
                } );
                dialog.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pet_gender.setText(items[value[0]]);
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton( "취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        } );
        row_type.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(pet_upd.this);
                dialog.setTitle("이름 입력");
                final String [] items = {"개", "고양이", "토끼","고슴도치","기니피그","햄스터","기타"};
                final int[] value={0};
                dialog.setSingleChoiceItems( items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        value[0] = which;
                    }
                } );
                dialog.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pet_type.setText(items[value[0]]);
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton( "취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        } );

        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String birth = pet_birth.getText().toString();
                String gender= pet_gender.getText().toString();
                String type = pet_type.getText().toString();
                String id = user_id;
                if(birth.length()==0){
                    Toast.makeText(getApplicationContext(), "생일을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(gender.length()==0){
                    Toast.makeText(getApplicationContext(), "성별을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(type.length()==0){
                    Toast.makeText(getApplicationContext(), "종별을 입력하세요.", Toast.LENGTH_SHORT).show();
                }else{
                    addPet(id,get_pet_name,birth,gender,type);
                    mStorageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference riversRef = mStorageRef.child(user_id).child("("+get_pet_name+")_image.jpg");
                    if(selectedImage != null){
                        riversRef.putFile(selectedImage)
                                .addOnCompleteListener( new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    }
                                } )
                                .addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                    }
                                } );
                    }
                    finish();
                }


            }
        } );
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==1&&resultCode==RESULT_OK&&data!=null&data.getData()!=null){
            selectedImage = data.getData();
            image.setImageURI(selectedImage);
        }
    }

    private void addPet(String id,String name,String birth, String gender,String type){
        upd_info pet_info = new upd_info(gender,birth ,type);
        db.child(id).child("pet").child(name).setValue(pet_info);
    }

    private void petsetting(){
        mStorageRef = FirebaseStorage.getInstance().getReference().child(user_id).child("("+get_pet_name+")_image.jpg");
        mStorageRef.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(pet_upd.this).load(uri).into(image);
            }
        } );
        mStorageRef.getDownloadUrl().addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                image.setImageDrawable( ContextCompat.getDrawable(getApplicationContext(), R.drawable.add_image) );
            }
        } );
        pet_name.setText(get_pet_name+" 수정 불가 ");
        db.child(user_id).child("pet").child(get_pet_name).child("birth").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String set_birth =dataSnapshot.getValue(String.class);
                pet_birth.setText(set_birth);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
        db.child(user_id).child("pet").child(get_pet_name).child("gender").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String set_gender =dataSnapshot.getValue(String.class);
                pet_gender.setText(set_gender);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
        db.child(user_id).child("pet").child(get_pet_name).child("type").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String set_type =dataSnapshot.getValue(String.class);
                pet_type.setText(set_type);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );

    }

}

