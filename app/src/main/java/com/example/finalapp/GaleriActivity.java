package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.util.NavUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GaleriActivity extends AppCompatActivity {
    String Girisyapan, begeniSTR, nbegeniSTR, selectedLabel;
    private DrawerLayout drawer;
    private Button btnOpenDrawer1;
    private FrameLayout addphoto, addlabel;
    private NavigationView navigationView;
    Spinner spin;
    LinearLayout galeriLinner;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> labelsList = new ArrayList<>();
    Set<String> uniqueLabelsSet = new HashSet<>();
    String[] labelsArray = uniqueLabelsSet.toArray(new String[0]);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeri);

        Intent intent = getIntent();
        if (intent != null){
            Girisyapan = intent.getStringExtra("useremail");
        }else{
            Log.e("IntentError","intent boş!");
        }

        drawer = findViewById(R.id.drawer_layout1);
        navigationView = findViewById(R.id.nav_view1);
        btnOpenDrawer1 = findViewById(R.id.drawerbtn1);
        addphoto = findViewById(R.id.addPhoto);
        addlabel = findViewById(R.id.addLabel);

        displayGaleri();
        spinView();

        NavUtil.init(this, addphoto, addlabel);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                NavUtil.handleNagivationSelected(menuItem, Girisyapan);
                menuItem.setChecked(true);
                drawer.closeDrawers();
                return true;
            }
        });

        btnOpenDrawer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(navigationView)) {
                    drawer.openDrawer(navigationView);
                }
            }
        });
    }
    private void displayGaleri(){
        galeriLinner =findViewById(R.id.galeriLinenr);

        db.collection("galeri")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            String user = document.getString("name");
                            String photoName = document.getString("foto");

                            begeniSTR =document.getString("begeni");
                            int begeni = (begeniSTR != null && !begeniSTR.isEmpty()) ? Integer.parseInt(begeniSTR) : 0;

                            nbegeniSTR = document.getString("nbegeni");
                            int nbegeni = (nbegeniSTR != null && !nbegeniSTR.isEmpty()) ? Integer.parseInt(nbegeniSTR) : 0;

                            String ID = document.getId();
                            Object objectLabel = document.get("labels");
                            if(objectLabel instanceof List<?>){
                                List<?> labelList = (List<?>) objectLabel;

                                for (Object item : labelList){
                                    if (item instanceof String){
                                        String label =(String) item;
                                        labelsList.add(label);
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

                            TextView textName = new TextView(this);
                            textName.setId(View.generateViewId());
                            textName.setText(user);

                            TextView textlabels = new TextView(this);
                            textlabels.setId(View.generateViewId());
                            textlabels.setText("Labels : \n" + labelsList);
                            labelsList.clear();

                            Button like = new Button(this);
                            like.setId(View.generateViewId());
                            like.setText("LİKE : " + begeniSTR);

                            Button nlike = new Button(this);
                            nlike.setId(View.generateViewId());
                            nlike.setText("DİSSLİKE : " + nbegeniSTR);

                            ImageView imageView = new ImageView(this);
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
                            constraintLayout.addView(nlike);

                            galeriLinner.addView(constraintLayout);

                            ConstraintSet constraintSet =new ConstraintSet();
                            constraintSet.clone(constraintLayout);

                            constraintSet.connect(imageView.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
                            constraintSet.connect(imageView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
                            constraintSet.connect(imageView.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);

                            constraintSet.connect(textName.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.TOP);
                            constraintSet.connect(textName.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);
                            constraintSet.setMargin(textName.getId(), ConstraintSet.TOP, 200);

                            constraintSet.connect(textlabels.getId(), ConstraintSet.TOP, textName.getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(textlabels.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);

                            constraintSet.connect(like.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(like.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
                            constraintSet.setMargin(like.getId(), ConstraintSet.START, 500);

                            constraintSet.connect(nlike.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(nlike.getId(), ConstraintSet.START, like.getId(), ConstraintSet.END);

                            constraintSet.applyTo(constraintLayout);

                            Space space = new Space(this);
                            space.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    20));
                            galeriLinner.addView(space);

                            int finalBegeni = begeni;
                            like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int inalBegeni = finalBegeni +1;
                                    DocumentReference docRef = db.collection("galeri").document(ID);
                                    docRef.update("begeni",String.valueOf(inalBegeni))
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("firestore","beğeni!!");
                                            })
                                            .addOnFailureListener(e -> Log.e("direstore","!!!begeni",e));
                                    yenile();
                                }
                            });

                            int finalnBegeni = nbegeni;
                            nlike.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int inalnBegeni = finalnBegeni +1;
                                    DocumentReference docRef = db.collection("galeri").document(ID);
                                    docRef.update("nbegeni",String.valueOf(inalnBegeni))
                                            .addOnSuccessListener(aVoid -> Log.d("firestore","nbeğeni!!"))
                                            .addOnFailureListener(e -> Log.e("direstore","!!!nbegeni",e));
                                    yenile();
                                }
                            });
                        }
                    }else {
                        Log.e("Firestore","veriler çekilmedi",task.getException());
                    }
                });
    }
    private void yenile(){
        galeriLinner.removeAllViews();
        displayGaleri();
    }
    private void spinView(){
        db.collection("galeri")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Set<String> uniqueLabelsSet = new HashSet<>();
                        uniqueLabelsSet.add("hepsi");
                        for (QueryDocumentSnapshot document : task.getResult()){
                            Object labelObject = document.get("labels");

                            if(labelObject instanceof List<?>){
                                List<?> labelList = (List<?>) labelObject;

                                for (Object item : labelList){
                                    if (item instanceof String){
                                        String label =(String) item;
                                        uniqueLabelsSet.add(label);
                                    }else {
                                        Log.e("FireStore","labels içi string değil!" +item);
                                    }
                                }
                            }
                            else {
                                Log.e("firestore", "labels liste değil" + labelObject);
                                continue;
                            }
                        }
                        String[] uniqueLabelsArray = uniqueLabelsSet.toArray(new String[0]);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(GaleriActivity.this, android.R.layout.simple_spinner_item,uniqueLabelsArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin =findViewById(R.id.spinner);
                        spin.setAdapter(adapter);
                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedLabel = (String) parent.getItemAtPosition(position);
                                if("hepsi".equals(selectedLabel)){
                                    galeriLinner.removeAllViews();
                                    displayGaleri();
                                }
                                galeriLinner.removeAllViews();
                                db.collection("galeri")
                                        .whereArrayContains("labels",selectedLabel)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot document : task.getResult()){
                                                    String user = document.getString("name");
                                                    String photoName = document.getString("foto");

                                                    begeniSTR =document.getString("begeni");
                                                    int begeni = (begeniSTR != null && !begeniSTR.isEmpty()) ? Integer.parseInt(begeniSTR) : 0;

                                                    nbegeniSTR = document.getString("nbegeni");
                                                    int nbegeni = (nbegeniSTR != null && !nbegeniSTR.isEmpty()) ? Integer.parseInt(nbegeniSTR) : 0;

                                                    String ID = document.getId();
                                                    Object objectLabel = document.get("labels");
                                                    if(objectLabel instanceof List<?>){
                                                        List<?> labelList = (List<?>) objectLabel;

                                                        for (Object item : labelList){
                                                            if (item instanceof String){
                                                                String label =(String) item;
                                                                labelsList.add(label);
                                                            }else {
                                                                Log.e("FireStore","labels içi string değil!" +item);
                                                            }
                                                        }
                                                    }
                                                    else {
                                                        Log.e("firestore", "labels liste değil" + objectLabel);
                                                        continue;
                                                    }
                                                    ConstraintLayout constraintLayout = new ConstraintLayout(GaleriActivity.this);

                                                    TextView textName = new TextView(GaleriActivity.this);
                                                    textName.setId(View.generateViewId());
                                                    textName.setText(user);

                                                    TextView textlabels = new TextView(GaleriActivity.this);
                                                    textlabels.setId(View.generateViewId());
                                                    textlabels.setText("Labels : \n" + labelsList);
                                                    labelsList.clear();

                                                    Button like = new Button(GaleriActivity.this);
                                                    like.setId(View.generateViewId());
                                                    like.setText("LİKE : " + begeniSTR);

                                                    Button nlike = new Button(GaleriActivity.this);
                                                    nlike.setId(View.generateViewId());
                                                    nlike.setText("DİSSLİKE : " + nbegeniSTR);

                                                    ImageView imageView = new ImageView(GaleriActivity.this);
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
                                                    constraintLayout.addView(nlike);

                                                    galeriLinner.addView(constraintLayout);

                                                    ConstraintSet constraintSet =new ConstraintSet();
                                                    constraintSet.clone(constraintLayout);

                                                    constraintSet.connect(imageView.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
                                                    constraintSet.connect(imageView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
                                                    constraintSet.connect(imageView.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);

                                                    constraintSet.connect(textName.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.TOP);
                                                    constraintSet.connect(textName.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);
                                                    constraintSet.setMargin(textName.getId(), ConstraintSet.TOP, 200);

                                                    constraintSet.connect(textlabels.getId(), ConstraintSet.TOP, textName.getId(), ConstraintSet.BOTTOM);
                                                    constraintSet.connect(textlabels.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);

                                                    constraintSet.connect(like.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM);
                                                    constraintSet.connect(like.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
                                                    constraintSet.setMargin(like.getId(), ConstraintSet.START, 500);

                                                    constraintSet.connect(nlike.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM);
                                                    constraintSet.connect(nlike.getId(), ConstraintSet.START, like.getId(), ConstraintSet.END);

                                                    constraintSet.applyTo(constraintLayout);

                                                    Space space = new Space(GaleriActivity.this);
                                                    space.setLayoutParams(new LinearLayout.LayoutParams(
                                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                                            20));
                                                    galeriLinner.addView(space);

                                                    int finalBegeni = begeni;
                                                    like.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            int inalBegeni = finalBegeni +1;
                                                            DocumentReference docRef = db.collection("galeri").document(ID);
                                                            docRef.update("begeni",String.valueOf(inalBegeni))
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        Log.d("firestore","beğeni!!");
                                                                    })
                                                                    .addOnFailureListener(e -> Log.e("direstore","!!!begeni",e));
                                                            yenile();
                                                        }
                                                    });

                                                    int finalnBegeni = nbegeni;
                                                    nlike.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            int inalnBegeni = finalnBegeni +1;
                                                            DocumentReference docRef = db.collection("galeri").document(ID);
                                                            docRef.update("nbegeni",String.valueOf(inalnBegeni))
                                                                    .addOnSuccessListener(aVoid -> Log.d("firestore","nbeğeni!!"))
                                                                    .addOnFailureListener(e -> Log.e("direstore","!!!nbegeni",e));
                                                            yenile();
                                                        }
                                                    });
                                                }
                                            }else {
                                                Log.e("Firestore","veriler çekilmedi",task.getException());
                                            }
                                        });
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }else {
                        Log.e("Fire", "no belge");
                    }
                });
    }
}