package hu.bme.aut.mixit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import hu.bme.aut.mixit.init_activities.InitApplicationSettingsActivity;
import hu.bme.aut.mixit.menu.MenuActivity;

public class SplashActivity extends AppCompatActivity {

    Intent intent;
    ImageView logo;
    ImageView logotext;

    Animation zoom_in_rotate_animation;
    Animation rotate_back_40_animation;
    Animation fade_in_animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        zoom_in_rotate_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_rotate_animation);
        rotate_back_40_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_back_40_animation);
        fade_in_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);


        final ImageView startbutton = (ImageView) findViewById(R.id.start);
        logo = (ImageView) findViewById(R.id.splashlogo);
        logotext = (ImageView) findViewById(R.id.mixit);
        startbutton.setVisibility(ImageView.INVISIBLE);
        logotext.setVisibility(ImageView.INVISIBLE);


        zoom_in_rotate_animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0)
            {
                logo.startAnimation(rotate_back_40_animation);
            }
        });

        rotate_back_40_animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0)
            {
                logotext.startAnimation(fade_in_animation);
                startbutton.startAnimation(fade_in_animation);
            }
        });

        fade_in_animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0)
            {
                logotext.setVisibility(ImageView.VISIBLE);
                startbutton.setVisibility(ImageView.VISIBLE);

            }
        });


        logo.startAnimation(zoom_in_rotate_animation);

        startbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences sharedPrefs = getSharedPreferences("user_settings_sp1.0", MODE_PRIVATE);

                //checking if the application has recently been opened or not
                if(!sharedPrefs.contains("initialized"))
                {
                    intent = new Intent(getApplicationContext(), InitApplicationSettingsActivity.class);
                    startActivity(intent);

                }
                else
                    {
                        intent =new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(intent);

                    }
            }
        });



    }














}
