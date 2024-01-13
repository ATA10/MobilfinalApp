package com.example.finalapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import  com.example.finalapp.AboutActivity;
import com.example.finalapp.GaleriActivity;
import  com.example.finalapp.MainActivity;
public class NavUtil {
    private  static FrameLayout addphoto, addlabel;
    private  static Context context;
    private static  String visibilityState = "default";
    public  static void  init(Context context, FrameLayout addlabel, FrameLayout addphoto){
        NavUtil.context = context;
        NavUtil.addlabel = addlabel;
        NavUtil.addphoto = addphoto;
    }
    public  static void handleNagivationSelected(MenuItem menuItem, String giren){
        int itemId = menuItem.getItemId();
        if (itemId == getResourceId("nav_item1","id")){
            setVisibility(View.VISIBLE,View.INVISIBLE);
        }else if (itemId == getResourceId("nav_item2","id")){
            setVisibility(View.INVISIBLE,View.VISIBLE);
        }else if (itemId == getResourceId("nav_item3","id")){
            Intent intent = new Intent(context, GaleriActivity.class);
            intent.putExtra("useremail",giren);
            context.startActivity(intent);
        }else if (itemId == getResourceId("nav_item4","id")){
            Intent intent4 =new Intent(context,AboutActivity.class);
            intent4.putExtra("useremail",giren);
            context.startActivity(intent4);
            setVisibility(View.INVISIBLE,View.VISIBLE);
        }else if (itemId == getResourceId("nav_item5","id")){
            Intent intent5 =new Intent(context,MainActivity.class);
            context.startActivity(intent5);
        }
    }
    private  static int  getResourceId(String name, String type){
        Resources resources =context.getResources();
        return resources.getIdentifier(name, type, context.getPackageName());
    }
    private static void setVisibility(int addLabelVisibility, int addPhotovisibility){
        if(addlabel != null){
            addlabel.setVisibility(addLabelVisibility);
        }
        if(addphoto != null){
            addphoto.setVisibility(addPhotovisibility);
        }
    }
}
