package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    FrameLayout splashlayout, loginlayout;
    String girisMail, girisPass;
    String kayitname, kayitsurname, kayitmail, kayitpass;
    FirebaseFirestore db= FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        splashlayout =findViewById(R.id.splashlayout);
        loginlayout =findViewById(R.id.loginlayout);

        Button splashtologin =findViewById(R.id.login);
        Button splashtosignin = findViewById(R.id.singin);
        Button logintosignin =findViewById(R.id.loginsingin);
        Button kayit = findViewById(R.id.singinkayit);
        Button signintologin =findViewById(R.id.singinlogin);
        Button giris =findViewById(R.id.logingiris);

        giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginIslemi();
            }
        });
        kayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SinginIslemi();
            }
        });
        logintosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splashlayout.setVisibility(View.INVISIBLE);
                loginlayout.setVisibility(View.INVISIBLE);
            }
        });
        signintologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splashlayout.setVisibility(View.INVISIBLE);
                loginlayout.setVisibility(View.VISIBLE);
            }
        });
        splashtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splashlayout.setVisibility(View.INVISIBLE);
                loginlayout.setVisibility(View.VISIBLE);
            }
        });
        splashtosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splashlayout.setVisibility(View.INVISIBLE);
                loginlayout.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void LoginIslemi(){
        girisMail = ((EditText)findViewById(R.id.loginmail)).getText().toString();
        girisPass = ((EditText)findViewById(R.id.loginpass)).getText().toString();
        if (girisMail.isEmpty() || girisPass.isEmpty()){
            Toast.makeText(MainActivity.this,"Alanlar Boş Bırakılamaz!",Toast.LENGTH_SHORT).show();
        }else {
            CollectionReference userRef = db.collection("users");
            Query query = userRef.whereEqualTo("email",girisMail);

            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String firePass = document.getString("password");
                        if(firePass != null && firePass.equals(girisPass)){
                            Toast.makeText(MainActivity.this,"Giriş yapılıyor..",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
                            intent.putExtra("useremail",document.getString("email"));
                            intent.putExtra("name",document.getString("name"));
                            this.startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(MainActivity.this," Şifre hatalı!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
    private void SinginIslemi(){
        User newuser =new User();
        newuser.setName(((EditText)findViewById(R.id.singinname)).getText().toString());
        newuser.setUsername(((EditText)findViewById(R.id.singinsurname)).getText().toString());
        newuser.setEmail(((EditText)findViewById(R.id.singinmail)).getText().toString());
        newuser.setPassword(((EditText)findViewById(R.id.singinpass)).getText().toString());
        if (newuser.getName().isEmpty() || newuser.getPassword().isEmpty() || newuser.getUsername().isEmpty() || newuser.getEmail().isEmpty()){
            Toast.makeText(MainActivity.this,"Alanlar Boş Bırakılamaz!",Toast.LENGTH_SHORT).show();
        }else {
            DocumentReference userDocumentRef =db.collection("users").document(newuser.getEmail());

            userDocumentRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot document =task.getResult();
                    if (document.exists()){
                        Toast.makeText(MainActivity.this,"Böyle bir kayıt var!",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        db.collection("users")
                                .document(newuser.getEmail())
                                .set(newuser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MainActivity.this,"Kayıt başarılı bir şekilde oluştu!",Toast.LENGTH_SHORT).show();
                                        splashlayout.setVisibility(View.INVISIBLE);
                                        loginlayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                }
            });
        }
    }
}
class User {
    private String name;
    private String username;
    private String email;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
