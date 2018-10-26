package com.learning.course.letslearn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_menu);
    }

    public void buttonClicked( View srcView ){

        String mode = "Alphabets";

        if( srcView.getId() == R.id.btnExit ) {
            finish();
            System.exit(0);
            return; //Return otherwise transfer control to main application (Activity)
        }

        else if( srcView.getId() == R.id.btnLearnAlphabets ) {
            mode = Const.APP_MODE_ALPHABETS;
        }

        else if( srcView.getId() == R.id.btnLearnNumbers ) {
            mode = Const.APP_MODE_NUMBERS;
        }
        //Create new Intent
        Intent mainApp = new Intent();
        //Setting main App class
        mainApp.setClass( this, MainApp.class );
        // setting mode as per selection of user
        mainApp.putExtra("MODE", mode);
        //start new activity
        startActivity( mainApp );
    }
}
