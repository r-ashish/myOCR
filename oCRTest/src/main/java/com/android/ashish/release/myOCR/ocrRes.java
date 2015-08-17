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

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by Ashish on 18/07/2015.
 */
public class ocrRes implements Serializable {
    public OcrResult ocr_res;
    public ocrRes(OcrResult o){
        this.ocr_res = o;
    }
    public OcrResult getRes(){
        return this.ocr_res;
    }
}
