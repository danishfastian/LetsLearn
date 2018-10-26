package com.learning.course.letslearn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

public class MainApp extends AppCompatActivity {

    private String mode;
    boolean isAlphabetMode;
    private TextView lblAlphabets;
    private TextView lblNumeric;
    private ImageView imgView;
    private Button[] btnOptions = new Button[6];
    private Button btnStart;
    private Button btnReplaySound;
    private TextView txtScore;
    private TextView txtBest;
    private int maxOption;

    private int score=0;
    private int attempt=0;
    private int bestScore=0;
    private int bestAttempts=0;
    private boolean applicationStarted = false;

    private String[] alphabets = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    private String[] numerics = {"0","1","2","3","4","5","6","7","8","9"};

    private int[] options = new int[btnOptions.length];
    private int correctOptionIndex;
    private double bestPercentage = 0;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        Intent intent = getIntent();
        mode = intent.getStringExtra("MODE");

        InitializeUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("score",score);
        outState.putInt("attempt",attempt);
        outState.putInt("bestScore",bestScore);
        outState.putInt("bestAttempts",bestAttempts);
        outState.putBoolean("applicationStarted",applicationStarted);
        outState.putSerializable("options",options);
        outState.putInt("correctOptionIndex",correctOptionIndex);
    }

    protected void onRestoreInstanceState( Bundle inState) {
        super.onRestoreInstanceState(inState);
        if( inState != null ){
            score = inState.getInt("score");
            attempt = inState.getInt("attempt");
            bestScore = inState.getInt("bestScore");
            bestAttempts = inState.getInt("bestAttempts");
            applicationStarted = inState.getBoolean("applicationStarted",applicationStarted);
            options = (int[]) inState.getSerializable("options");
            correctOptionIndex = inState.getInt("correctOptionIndex");

            if(applicationStarted){
                InitializeUIOnStart(); //Initialize UI with restored state
                showScore();//Showrestored score
            }

        }
    }


    //This will initialize user interface based on users selection
    private void InitializeUI(){
        //Check if its an alphabet mode. As application has only 2 modes therefore we assume that it is numeric mode otherwise
        isAlphabetMode = ( mode.equals(Const.APP_MODE_ALPHABETS) );

        lblAlphabets = ( TextView ) findViewById(R.id.lblAlphabets );
        lblNumeric = ( TextView ) findViewById(R.id.lblNumbers );
        imgView = (ImageView) findViewById( R.id.imgHeader );

        //We set title using by displaying approriate heading and hiding the other one
        if( isAlphabetMode )
        {
            lblAlphabets.setVisibility( View.VISIBLE );
            lblNumeric.setVisibility( View.INVISIBLE );
            imgView.setImageResource( R.drawable.alpha );
        }
        else
        {
            lblAlphabets.setVisibility( View.INVISIBLE );
            lblNumeric.setVisibility( View.VISIBLE );
            imgView.setImageResource( R.drawable.number );
        }

        //Initialize the option buttons
        btnOptions[0] = (Button) findViewById( R.id.btnButton1);
        btnOptions[1] = (Button) findViewById( R.id.btnButton2);
        btnOptions[2] = (Button) findViewById( R.id.btnButton3);
        btnOptions[3] = (Button) findViewById( R.id.btnButton4);
        btnOptions[4] = (Button) findViewById( R.id.btnButton5);
        btnOptions[5] = (Button) findViewById( R.id.btnButton6);

        //Initialize other buttons
        btnReplaySound = (Button) findViewById(R.id.btnReplaySound);
        btnStart = (Button) findViewById( R.id.btnStart );

        //Initialize score labels
        txtScore = (TextView) findViewById(R.id.txtScore);
        txtBest = (TextView) findViewById(R.id.txtBest);

        //We will use it later for initialization
        maxOption = btnOptions.length;
        //Log.d( "InitializeUI", "Max Option:" + maxOption );

        //Hide options and replay sound button. Will be available to user after start
        setOptionButtonVisibility( View.INVISIBLE );
        btnReplaySound.setVisibility( View.INVISIBLE );

        //Initialize best score
        SharedPreferences prefBestScore = getSharedPreferences("BestScore", Context.MODE_PRIVATE);

        if( prefBestScore.contains("bestScore") )
            bestScore = prefBestScore.getInt( "bestScore", 0 );

        if( prefBestScore.contains("bestAttempts") )
            bestAttempts = prefBestScore.getInt( "bestAttempts", 0 );

        if( bestAttempts !=0 )
        {
            bestPercentage = bestScore/bestAttempts;
        }
        //Display score
        showScore();
    }

    private void saveBestScore(){

        //Check if score is greater then best score
        double perc = 0;
        if( attempt !=0)
            perc = score/attempt;

        if( perc > bestPercentage || ( perc == bestPercentage && attempt > bestAttempts ) )
        {
            bestPercentage = perc;
            bestScore = score;
            bestAttempts = attempt;

            SharedPreferences prefBestScore = getSharedPreferences("BestScore", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = prefBestScore.edit();
            editor.putInt( "bestScore", bestScore );
            editor.putInt( "bestAttempts", bestAttempts );
            editor.commit();
        }
    }

    private void showScore(){
        txtScore.setText( "Score: " + score + " out of " + attempt + " attempts");
        txtBest.setText("Best: " + bestScore + " out of " + bestAttempts + " attempts");
    }


    private void setOptionButtonVisibility( int visibility ){
        for( int index=0; index < maxOption; index ++ ){
            btnOptions[index].setVisibility( visibility );
        }
    }

    public void onSelectOption( View vw )
    {
        int index = 0;
        for ( Button btnOpt : btnOptions ) {
            if( btnOpt.equals( vw ) ) {
                if( index == correctOptionIndex )
                {
                    score ++;
                    attempt++;
                    Toast.makeText( vw.getContext(),"You selected correct option", Toast.LENGTH_SHORT ).show();
                    //Re initialize user interface with next random number.
                    showScore();
                    onStart( vw );
                    return;
                }
            }
            index++;
        }
        attempt++;
        Toast.makeText( vw.getContext(),"Please select correct option", Toast.LENGTH_SHORT ).show();
        showScore();
    }

    public void onStart( View vw )
    {
        applicationStarted = true;
        //Calculating random options to display
        Random random = new Random();
        int max = isAlphabetMode ? alphabets.length : numerics.length;

        //Initialize array
        for( int index = 0; index < options.length; index++ )
        {
            options[index] = -1;
        }

        //Assign random index
        for( int index = 0; index < options.length;  )
        {
            int tmpRand = random.nextInt( max );
            boolean usedEarlier = false;
            //Check if its not used earlier
            for( int val : options )
            {
                if( val == tmpRand )
                {
                    usedEarlier = true;
                    continue;
                }
            }

            if( !usedEarlier) {
                options[index] = tmpRand;
                index++;
            }
        }

        //randomly select correct option to be played and selected
        correctOptionIndex = random.nextInt(options.length);
        //Display Options
        InitializeUIOnStart();
        //This will play sound file corresponding to the correct option
        PlayOption();
    }

    private void InitializeUIOnStart()
    {
        //Display Options
        for( int index = 0; index < options.length; index++ )
        {
            //Set text of the button as per selected index earlier
            btnOptions[index].setText( isAlphabetMode ? alphabets[options[index]] :numerics[options[index]]  );
        }
        setOptionButtonVisibility( View.VISIBLE );
        btnReplaySound.setVisibility( View.VISIBLE );
        btnStart.setVisibility( View.INVISIBLE );
    }

    private void PlayOption()
    {
        if( mediaPlayer != null ) {
            if( mediaPlayer.isPlaying()) //Dont do anything if previous command is not finished
                return;

            mediaPlayer.release(); //Release media player
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create( getApplicationContext(),GetSelectedOption() );
        mediaPlayer.start(); //Start playing the sound
    }

    @Override
    protected void onStop(){
        super.onStop();
        if( mediaPlayer!= null ){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void onReplay( View vw )
    {
        PlayOption();
    }

    public void onQuit( View vw )
    {
        //Check if score is greater then best score
        saveBestScore();

        if( mediaPlayer != null  && mediaPlayer.isPlaying()) //Dont do anything if previous command is not finished
            return;

        if( mediaPlayer != null ) {
            mediaPlayer.release(); //Release media player
            mediaPlayer = null;
        }

        finish();
    }


    private int GetSelectedOption()
    {
        if( isAlphabetMode )
        {
            String correctOption = alphabets[options[correctOptionIndex]];

            if( correctOption.equals("A")){
                return R.raw.alpha;
            }
            else if( correctOption.equals("B")){
                return R.raw.alphb;
            }
            else if( correctOption.equals("C")){
                return R.raw.alphc;
            }
            else if( correctOption.equals("D")){
                return R.raw.alphd;
            }
            else if( correctOption.equals("E")){
                return R.raw.alphe;
            }
            else if( correctOption.equals("F")){
                return R.raw.alphf;
            }
            else if( correctOption.equals("G")){
                return R.raw.alphg;
            }
            else if( correctOption.equals("H")){
                return R.raw.alphh;
            }
            else if( correctOption.equals("I")){
                return R.raw.alphi;
            }
            else if( correctOption.equals("J")){
                return R.raw.alphj;
            }
            else if( correctOption.equals("K")){
                return R.raw.alphk;
            }
            else if( correctOption.equals("L")){
                return R.raw.alphl;
            }
            else if( correctOption.equals("M")){
                return R.raw.alphm;
            }
            else if( correctOption.equals("N")){
                return R.raw.alphn;
            }
            else if( correctOption.equals("O")){
                return R.raw.alpho;
            }
            else if( correctOption.equals("P")){
                return R.raw.alphp;
            }
            else if( correctOption.equals("Q")){
                return R.raw.alphq;
            }
            else if( correctOption.equals("R")){
                return R.raw.alphr;
            }
            else if( correctOption.equals("S")){
                return R.raw.alphs;
            }
            else if( correctOption.equals("T")){
                return R.raw.alpht;
            }
            else if( correctOption.equals("U")){
                return R.raw.alphu;
            }
            else if( correctOption.equals("V")){
                return R.raw.alphv;
            }
            else if( correctOption.equals("W")){
                return R.raw.alphw;
            }
            else if( correctOption.equals("X")){
                return R.raw.alphx;
            }
            else if( correctOption.equals("Y")){
                return R.raw.alphy;
            }
            else if( correctOption.equals("Z")){
                return R.raw.alphz;
            }
        }
        else
        {
            String correctOption = numerics[options[correctOptionIndex]];

            if( correctOption.equals("1")){
                return R.raw.num1;
            }
            else if( correctOption.equals("2")){
                return R.raw.num2;
            }
            else if( correctOption.equals("3")){
                return R.raw.num3;
            }
            else if( correctOption.equals("4")){
                return R.raw.num4;
            }
            else if( correctOption.equals("5")){
                return R.raw.num5;
            }
            else if( correctOption.equals("6")){
                return R.raw.num6;
            }
            else if( correctOption.equals("7")){
                return R.raw.num7;
            }
            else if( correctOption.equals("8")){
                return R.raw.num8;
            }
            else if( correctOption.equals("9")){
                return R.raw.num9;
            }
            else if( correctOption.equals("0")){
                return R.raw.num0;
            }
        }

        return R.raw.alpha;
    }

}
