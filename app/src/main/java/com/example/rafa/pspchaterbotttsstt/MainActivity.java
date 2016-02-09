package com.example.rafa.pspchaterbotttsstt;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rafa.pspchaterbotttsstt.ChaterBot.ChatterBot;
import com.example.rafa.pspchaterbotttsstt.ChaterBot.ChatterBotFactory;
import com.example.rafa.pspchaterbotttsstt.ChaterBot.ChatterBotSession;
import com.example.rafa.pspchaterbotttsstt.ChaterBot.ChatterBotType;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private TextView tv,tv2,tv3;
    private Button bt1;
    private TextToSpeech tts;
    private boolean speaking;
    ChatterBot bot;
    private static final int N1 = 1;
    private static final int N2 = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);
        bt1 = (Button) findViewById(R.id.button);

        init();
    }

    private void init(){
        Intent i = new Intent();
        i.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(i, N1);
    }

    public void hablar(View v) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora");
        i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        startActivityForResult(i, N2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == N1) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
                tts.setLanguage(new Locale("es", "ES"));
            } else {
                Intent intent = new Intent();
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }

        if (requestCode == N2) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> textos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                tv2.setText(" " + textos.get(0));
                tv.append(textos.get(0));
                BotSpeak botspeak = new BotSpeak();
                botspeak.execute(tv2.getText().toString());
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            speaking = true;
        } else {
            speaking = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    class BotSpeak extends AsyncTask<String, Integer, String> {

        BotSpeak(String... p) {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            ChatterBotFactory botFactory = new ChatterBotFactory();
            try {
                bot = botFactory.create(ChatterBotType.CLEVERBOT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ChatterBotSession sessionBot = bot.createSession();
            String param = params[0];

            try {
                return sessionBot.think(param);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (speaking == true) {
                tv3.setText(" " + s);
                tv.append("\n" + s + "\n");
                tts.setLanguage(new Locale("es", "ES"));
                tts.setPitch((float) 1.0);
                tts.speak(tv.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }
}

