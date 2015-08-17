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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.android.ashish.release.myOCR.language.LanguageCodeHelper;


public class MainActivity extends Activity {
    private ProgressDialog dialog;
    static TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Loading components....");
        dialog.setCancelable(false);
        dialog.show();
        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    dialog.dismiss();
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==0){
            Intent intent = new Intent(this,settings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    String lang(){
        return PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(PreferencesActivity.KEY_SOURCE_LANGUAGE_PREFERENCE,
                        CaptureActivity.DEFAULT_SOURCE_LANGUAGE_CODE);
    }
    public void startOcr_click(View view)
    {
        try{
        Intent intent  = new Intent(this,CaptureActivity.class);
        startActivity(intent);}catch (Exception e){Toast.makeText(this,e+" "+e.getMessage(),Toast.LENGTH_LONG);}
    }

    public void pick_image(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected  void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri uri = data.getData();
                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Bitmap mBit = b.copy(Bitmap.Config.ARGB_8888, true);
                /*int width = b.getWidth();
                int height = b.getHeight();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Bitmap img = renderCroppedGreyscaleBitmap(width, height, byteArray);*/
                /*if(decode(b))
                {
                    //Class re = result_activity(ocrResult);
                    //
                    Toast.makeText(this, ocrResult.getText(),
                            Toast.LENGTH_LONG).show();
                }*/
                dialog = new ProgressDialog(this);
                dialog.setTitle("Please wait");
                dialog.setMessage("Reading text in the photo ("+ name(lang())+" mode) ....");
                dialog.setCancelable(false);
                dialog.show();
                new readFromPhoto().execute(mBit);
            }
            else {
                Toast.makeText(this, "Pick an Image to continue",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong and we're on it.Please try again", Toast.LENGTH_LONG)
                    .show();
        }
    }
    String name(String code){
        return LanguageCodeHelper.getOcrLanguageName(this,code);
    }
    public Bitmap renderGreyscaleBitmap(Bitmap b) {
        int height = b.getHeight();
        int width = b.getWidth();
        Bitmap grayScaleBitmap = b.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(grayScaleBitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(b,0,0,paint);
        return grayScaleBitmap;
    }
    private File getStorageDirectory() {
        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (RuntimeException e) {
            Toast.makeText(this, "Is the SD card visible?",Toast.LENGTH_SHORT).show();
        }

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            try {
                return getExternalFilesDir(Environment.MEDIA_MOUNTED);
            } catch (NullPointerException e) {
                Toast.makeText(this, "Looks like external storage(SD card) is full or not available.Please free some space!",Toast.LENGTH_SHORT).show();
            }

        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Toast.makeText(this, "Looks like external storage is read-only",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Looks like external storage is unavailable.Please plugin SD card!",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private TessBaseAPI baseApi;// = new TessBaseAPI();
    private OcrResult ocrResult;
    public static OcrResult getOcrRes;

    //private long timeRequired;
    private boolean decode(Bitmap bitmap){
        String textResult;
        boolean init_result = false;
        baseApi = new TessBaseAPI();
        try{
            init_result = baseApi.init(getStorageDirectory().toString() + File.separator, lang(), TessBaseAPI.OEM_TESSERACT_ONLY);
        }catch(Exception e){
            Toast.makeText(this,e+" "+e.getMessage(), Toast.LENGTH_LONG)
                .show();
        }
        if (init_result) {
            try {
                baseApi.setImage(ReadFile.readBitmap(bitmap));
                textResult = baseApi.getUTF8Text();
                if (textResult == null || textResult.equals("")) {
                    Toast.makeText(this, "No text found!", Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
                ocrResult = new OcrResult();
                ocrResult.setWordConfidences(baseApi.wordConfidences());
                ocrResult.setMeanConfidence(baseApi.meanConfidence());
                ocrResult.setRegionBoundingBoxes(baseApi.getRegions().getBoxRects());
                ocrResult.setTextlineBoundingBoxes(baseApi.getTextlines().getBoxRects());
                ocrResult.setWordBoundingBoxes(baseApi.getWords().getBoxRects());
                ocrResult.setStripBoundingBoxes(baseApi.getStrips().getBoxRects());
            } catch (RuntimeException e) {
                Log.e("OcrRecognizeAsyncTask", "Caught RuntimeException in request to Tesseract. Setting state to CONTINUOUS_STOPPED.");
                e.printStackTrace();
                try {
                    baseApi.clear();
                } catch (NullPointerException e1) {
                    // Continue
                }
                Toast.makeText(this, "Something went wrong! " + e + " msg: " + e.getMessage(), Toast.LENGTH_LONG)
                        .show();
                return false;
            }
            ocrResult.setBitmap(bitmap);
            ocrResult.setText(textResult);
            //ocrResult.setRecognitionTimeRequired(timeRequired);
            getOcrRes = ocrResult;
            return true;
        }
        Toast.makeText(this, "storage init prob", Toast.LENGTH_SHORT)
                .show();
        return false;
    }
    //public static final String source = "photo";
    void show_res(){
        try{
            Intent intent = new Intent(this,result_activity.class);
        //ocrRes res = new ocrRes(ocrResult);
        //intent.putExtra("ocr_res",res);
            startActivity(intent);
        }
        catch(Exception e){
            ((TextView)findViewById(R.id.temp_read_photo)).setText(e+" "+e.getMessage());
        }
    }
    void toastMaker(String s,int l){
        Toast.makeText(this,s,l).show();
    }
    Context getContext(){
        return this;
    }
    final class readFromPhoto extends AsyncTask<Bitmap, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Bitmap... arg0){
            boolean decode_success = false;
            try{
                decode_success= decode(arg0[0]);
            }catch(Exception e){
               // Toast.makeText(getParent(),e+" "+e.getMessage(),Toast.LENGTH_SHORT).show();
                //((TextView)findViewById(R.id.temp_read_photo)).setText("w");
            }
            if(decode_success)
                return true;
            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result)
                show_res();
            else
                toastMaker("Language data unavailable\nPlease select OCR mode to download language data for "+name(lang()),Toast.LENGTH_LONG);
            //    Toast.makeText(getParent(),"Unknown error occured",Toast.LENGTH_SHORT);
            //((TextView)findViewById(R.id.temp_read_photo)).setText(ocrResult.getText());
        }
    }
}
