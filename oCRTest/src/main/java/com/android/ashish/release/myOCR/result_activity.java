/*
 * Copyright 2015 Ashish Ranjan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.ashish.release.myOCR;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import com.android.ashish.release.myOCR.language.TranslateAsyncTask;


public class result_activity extends Activity {

    OcrResult ocrResult;
    TextToSpeech textToSpeech;
    ProgressDialog dialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_activity);
        //ocrResult = ((ocrRes)getIntent().getSerializableExtra("ocr_res")).getRes();
        //ocrResult = ((ocrRes)getIntent().getExtras().getSerializable("ocr_res")).getRes();
        try{ocrResult = MainActivity.getOcrRes;
        textToSpeech = MainActivity.textToSpeech;
            if(textToSpeech==null){
                textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            textToSpeech.setLanguage(Locale.US);
                            read_text(ocrResult.getText());
                        }
                    }
                });
            }
        final TextView ocrResultView = (TextView) findViewById(R.id.ocr_result_text_view);
        registerForContextMenu(ocrResultView);
        TextView translationView = (TextView) findViewById(R.id.translation_text_view);
        registerForContextMenu(translationView);

        ((Button)(findViewById(R.id.read_button))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.stop();
                String textToSpeak = ocrResultView.getText().toString();
                textToSpeak = textToSpeak.subSequence(17, textToSpeak.length()).toString();
                read_text(textToSpeak);
            }
        });

        View progressView = (View) findViewById(R.id.indeterminate_progress_indicator_view);
        String sourceLanguageReadable = "English";
        ImageView bitmapImageView = (ImageView) findViewById(R.id.image_view);
        Bitmap lastBitmap = ocrResult.getBitmap();
        if (lastBitmap == null) {
            bitmapImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        } else {
            bitmapImageView.setImageBitmap(lastBitmap);
        }

        TextView sourceLanguageTextView = (TextView) findViewById(R.id.source_language_text_view);
        sourceLanguageTextView.setText(sourceLanguageReadable);
        TextView ocrResultTextView = (TextView) findViewById(R.id.ocr_result_text_view);
        ocrResultTextView.setText("Detected text : \n"+ocrResult.getText());
        int scaledSize = Math.max(22, 32 - ocrResult.getText().length() / 4);
        ocrResultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);

        TextView translationLanguageLabelTextView = (TextView) findViewById(R.id.translation_language_label_text_view);
        TextView translationLanguageTextView = (TextView) findViewById(R.id.translation_language_text_view);
        TextView translationTextView = (TextView) findViewById(R.id.translation_text_view);
        //if (true) {
            translationLanguageLabelTextView.setVisibility(View.VISIBLE);
            translationLanguageTextView.setText("Hindi");
            translationLanguageTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL), Typeface.NORMAL);
            translationLanguageTextView.setVisibility(View.VISIBLE);

            translationTextView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
            setProgressBarVisibility(true);

            new TranslateAsyncTask(this, "eng", "hi",
                    ocrResult.getText()).execute();
        /*} else {
            translationLanguageLabelTextView.setVisibility(View.GONE);
            translationLanguageTextView.setVisibility(View.GONE);
            translationTextView.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);
            setProgressBarVisibility(false);
        }*/
        read_text(ocrResult.getText());
        }
        catch(Exception e){
            TextView ocrResultTextView = (TextView) findViewById(R.id.ocr_result_text_view);
            ocrResultTextView.setText(e + " " + e.getMessage());
        }
    }
    void read_text(String text)
    {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void stop_read(View view){
        if(textToSpeech != null && textToSpeech.isSpeaking())
            textToSpeech.stop();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();
    }
}
