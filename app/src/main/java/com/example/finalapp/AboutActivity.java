package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.util.NavUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AboutActivity extends AppCompatActivity {
    String girisYapan, name;
    FrameLayout addlabelframe, addphotoframe;
    Button btnOpenDrawer, labelAdd;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LinearLayout labelsLineer, photoLinner;
    LinearLayout kisiLinearLayout;
    ImageView image;
    ArrayList<String> galeriList = new ArrayList<>();
    byte[] datafoto;
    ArrayList<String> secilenLabels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Intent intent = getIntent();
        if(intent != null){
            girisYapan = intent.getStringExtra("useremail");
            name = intent.getStringExtra("name");
        }else{
            Log.e("IntentError", "intent yok!");
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        displayLabels();
        displayPhotoLabels();
        displayGaleri();

        DrawerLayout drawer= findViewById(R.id.drawer_layout);
        NavigationView navigationView =findViewById(R.id.nav_view);
        addlabelframe = findViewById(R.id.addLabel);
        addphotoframe = findViewById(R.id.addPhoto);
        image = findViewById(R.id.imageView2);
        btnOpenDrawer = findViewById(R.id.drawerstate);
        labelAdd = findViewById(R.id.labelkayit);
        Button camera =findViewById(R.id.camera);
        Button save = findViewById(R.id.photosave);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String randomID = generatorRndId(20);
                StorageReference galeriRef = storageRef.child(randomID + ".jpg");
                UploadTask uploadTask = galeriRef.putBytes(datafoto);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AboutActivity.this,"Fotoğraf yüklenemedi!",Toast.LENGTH_SHORT).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        galeriRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                            }
                        });
                    }
                });

                Map<String, Object> galeriData = new HashMap<>();
                galeriData.put("labels",secilenLabels);
                galeriData.put("email",girisYapan);
                galeriData.put("begeni","0");
                galeriData.put("nbegeni","0");
                galeriData.put("foto",randomID);
                galeriData.put("name",name);

                CollectionReference galeriCollectionRef = db.collection("galeri");

                galeriCollectionRef.add(galeriData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AboutActivity.this,"Galeri Eklendi!",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AboutActivity.this,"Galeri yüklenemedi!",Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotoCek();
            }
        });

        labelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String labelIcerik =((EditText)findViewById(R.id.labelgiris)).getText().toString();
                String descriptionIcerik =((EditText)findViewById(R.id.desriptiongiris)).getText().toString();
                Map<String, Object> labelData = new HashMap<>();
                labelData.put("description", !descriptionIcerik.isEmpty() ? descriptionIcerik : "labels");
                labelData.put("label",labelIcerik);
                labelData.put("email",girisYapan);

                CollectionReference labelsCollectionRef = db.collection("labels");
                labelsCollectionRef.add(labelData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AboutActivity.this,"Kayıt Başarılı label eklendi!!",Toast.LENGTH_SHORT).show();
                                displayLabels();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AboutActivity.this,"Kayıt Başarısız!! label eklenmedi!!",Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

        addphotoframe.setVisibility(View.INVISIBLE);
        addlabelframe.setVisibility(View.INVISIBLE);

        NavUtil.init(this,addlabelframe,addphotoframe);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!drawer.isDrawerOpen(navigationView)){
                    drawer.openDrawer(navigationView);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                NavUtil.handleNagivationSelected(item,girisYapan);
                item.setChecked(true);
                displayLabels();
                displayPhotoLabels();
                displayGaleri();
                drawer.closeDrawers();
                return true;
            }
        });

    }
    private void displayLabels(){
        labelsLineer = findViewById(R.id.labelsview);
        labelsLineer.removeAllViews();
        db.collection("labels")
                .whereEqualTo("email",girisYapan)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            String labelDesc = document.getString("description");
                            String labelName = document.getString("label");

                            ConstraintLayout labelLayout = new ConstraintLayout(this);

                            TextView textView = new TextView(this);
                            textView.setId(View.generateViewId());
                            textView.setText("Label : " + labelName +"/nDescription : " +labelDesc);

                            Button deleteButon = new Button(this);
                            deleteButon.setId(View.generateViewId());
                            deleteButon.setText("Sil");
                            deleteButon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    labelSil(document.getId());
                                    labelsLineer.removeView(labelLayout);
                                }
                            });

                            labelLayout.addView(textView);
                            labelLayout.addView(deleteButon);
                            labelsLineer.addView(labelLayout);

                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(labelLayout);

                            constraintSet.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                            constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

                            constraintSet.connect(deleteButon.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                            constraintSet.connect(deleteButon.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                            constraintSet.applyTo(labelLayout);
                        }
                    }else {
                        Log.e("Firestore", "veri çeklmedi",task.getException());
                    }
                });
    }
    private void labelSil(String documentId){
        db.collection("labels")
                .document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AboutActivity.this," label Silindi!!",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AboutActivity.this," label Silinirken hata oluştu!!",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void fotoCek(){
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePicIntent, 33);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Sonucun kamera aktivitesinden gelip gelmediğini ve başarılı olup olmadığını kontrol et
        if (requestCode == 33 && resultCode == RESULT_OK) {
            // Verilerin extras'ından alınan çekilen resmi bir Bitmap olarak al
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            // ImageView'e çekilen resmi atama
            image.setImageBitmap(imageBitmap);

            image.setDrawingCacheEnabled(true);
            image.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            datafoto = baos.toByteArray();
        }
    }
    private void displayPhotoLabels(){
        photoLinner = findViewById(R.id.photolabelview);
        photoLinner.removeAllViews();

        db.collection("labels")
                .whereEqualTo("email",girisYapan)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            String labelDesc = document.getString("description");
                            String labelName = document.getString("label");

                            ConstraintLayout labelLayout = new ConstraintLayout(this);

                            TextView textView = new TextView(this);
                            textView.setId(View.generateViewId());
                            textView.setText("Label : " + labelName +"\nDescription : " +labelDesc);

                            CheckBox checkBox = new CheckBox(this);
                            checkBox.setId(View.generateViewId());
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if(isChecked){
                                        secilenLabels.add(labelName);
                                    }
                                }
                            });

                            labelLayout.addView(textView);
                            labelLayout.addView(checkBox);
                            photoLinner.addView(labelLayout);

                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(labelLayout);

                            constraintSet.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                            constraintSet.connect(textView.getId(), ConstraintSet.START, checkBox.getId(), ConstraintSet.END);

                            constraintSet.connect(checkBox.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                            constraintSet.connect(checkBox.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

                            constraintSet.applyTo(labelLayout);
                        }
                    }else {
                        Log.e("Firestore", "veri çeklmedi",task.getException());
                    }
                });
    }
    public static String generatorRndId(int length){
        String karakter = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz012345678";
        StringBuilder rndID = new StringBuilder();
        Random rnd = new Random();
        for (int i=0; i<length;i++){
            int index = rnd.nextInt(karakter.length());
            rndID.append(karakter.charAt(index));
        }
        return rndID.toString();
    }
    private void displayGaleri(){
        kisiLinearLayout =findViewById(R.id.kisiselLineer);
        kisiLinearLayout.removeAllViews();
        db.collection("galeri")
                .whereEqualTo("email",girisYapan)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            String user = document.getString("name");
                            String photoName = document.getString("foto");
                            String begeniSTR =document.getString("begeni");
                            String nbegeniSTR = document.getString("nbegeni");
                            String ID = document.getId();
                            Object objectLabel = document.get("labels");
                            if(objectLabel instanceof List<?>){
                                List<?> labelList = (List<?>) objectLabel;

                                for (Object item : labelList){
                                    if (item instanceof String){
                                        String label =(String) item;
                                        galeriList.add(label);
                                    }else {
                                        Log.e("FireStore","labels içi string değil!" +item);
                                    }
                                }
                            }
                            else {
                                Log.e("firestore", "labels liste değil" + objectLabel);
                                continue;
                            }
                            ConstraintLayout constraintLayout = new ConstraintLayout(this);

                            TextView textName = new TextView(AboutActivity.this);
                            textName.setId(View.generateViewId());
                            textName.setText(user);

                            TextView textlabels = new TextView(AboutActivity.this);
                            textlabels.setId(View.generateViewId());
                            textlabels.setText("Labels : \n" + galeriList);
                            galeriList.clear();

                            TextView like = new TextView(AboutActivity.this);
                            like.setId(View.generateViewId());
                            like.setText("LİKE : " + begeniSTR);

                            TextView nlike = new TextView(AboutActivity.this);
                            nlike.setId(View.generateViewId());
                            nlike.setText("DİSSLİKE : " + nbegeniSTR);

                            Button sil = new Button(AboutActivity.this);
                            sil.setId(View.generateViewId());
                            sil.setText("sil");
                            sil.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    galeriSil(document.getId());
                                    kisiLinearLayout.removeView(constraintLayout);
                                }
                            });

                            ImageView imageView = new ImageView(AboutActivity.this);
                            imageView.setId(View.generateViewId());
                            String photoUrl ="https://firebasestorage.googleapis.com/v0/b/mobilprojeapp-fa16c.appspot.com/o/" + photoName +".jpg?alt=media";
                            Picasso.get().load(photoUrl).into(imageView);
                            int width =700;
                            int height =700;

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width,height);
                            imageView.setLayoutParams(layoutParams);

                            constraintLayout.addView(imageView);
                            constraintLayout.addView(textName);
                            constraintLayout.addView(textlabels);
                            constraintLayout.addView(like);
                            constraintLayout.addView(sil);
                            constraintLayout.addView(nlike);

                            kisiLinearLayout.addView(constraintLayout);

                            ConstraintSet constraintSet =new ConstraintSet();
                            constraintSet.clone(constraintLayout);

                            constraintSet.connect(imageView.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
                            constraintSet.connect(imageView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
                            constraintSet.connect(imageView.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);

                            constraintSet.connect(textName.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.TOP);
                            constraintSet.connect(textName.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);
                            constraintSet.setMargin(textName.getId(), ConstraintSet.TOP, 100);

                            constraintSet.connect(textlabels.getId(), ConstraintSet.TOP, textName.getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(textlabels.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);

                            constraintSet.connect(like.getId(), ConstraintSet.TOP, textlabels.getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(like.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);

                            constraintSet.connect(nlike.getId(), ConstraintSet.TOP, like.getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(nlike.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);

                            constraintSet.connect(sil.getId(), ConstraintSet.TOP,nlike.getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(sil.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);

                            constraintSet.applyTo(constraintLayout);

                            Space space = new Space(AboutActivity.this);
                            space.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    20));
                            kisiLinearLayout.addView(space);
                        }
                    }else {
                        Log.e("Firestore","veriler çekilmedi",task.getException());
                    }
                });
    }

    private void galeriSil(String documentId){
        db.collection("galeri")
                .document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AboutActivity.this," galerim Silindi!!",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AboutActivity.this," label Silinirken hata oluştu!!",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}