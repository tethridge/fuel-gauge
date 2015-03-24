/*
 * Copyright (C) 2015 Unchained Software
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

package com.unchained.fuelgauge;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView textView = (TextView) findViewById(R.id.about_text_view);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(getString(R.string.copyright));
        ssb.setSpan(new RelativeSizeSpan(5f), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append("\n\n");
        ssb.append(getString(R.string.apache_text));
        ssb.append("\n");
        ssb.append("\n");
        ssb.append(getString(R.string.usage));
        ssb.append("\n\n");
        ssb.append(getString(R.string.trey_ethridge));
        ssb.append("\n");
        ssb.append(getString(R.string.unchained_email));

        textView.setText(ssb.toString());
    }

}
