package com.qiyue.qdmobile.ui.messages;

import android.os.Bundle;

import com.qiyue.qdmobile.BasFragmentActivity;

public class MessageActivity extends BasFragmentActivity implements MessageFragment.OnQuitListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBarTintManager();
        
        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            MessageFragment detailFragment = new MessageFragment();
            detailFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, detailFragment).commit();
            detailFragment.setOnQuitListener(this);
        }
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == Compatibility.getHomeMenuId()) {
//            finish();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
    @Override
    public void onQuit() {
        finish();
    }
}
