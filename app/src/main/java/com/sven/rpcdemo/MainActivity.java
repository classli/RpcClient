package com.sven.rpcdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sven.api.HelloService;
import com.sven.client.RpcProxy;

public class MainActivity extends AppCompatActivity {
    private final static String TAG ="MainActivity";
    private EditText editText;
    private EditText input;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.btn);
        final SharedPreferences sharedPreferences = getSharedPreferences("test_sp", Context.MODE_PRIVATE);
        final String oldIp = sharedPreferences.getString("ip", null);
        editText = (EditText) findViewById(R.id.edt);
        input = (EditText) findViewById(R.id.input);
        if (oldIp != null) {
            editText.setText(oldIp);
        }
        button.setFocusableInTouchMode(true);
        button.setFocusable(true);
        button.requestFocus();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String newIp = editText.getText().toString();
                            if (newIp != null && !newIp.equals(oldIp)) {
                                sharedPreferences.edit().putString("ip", newIp).apply();
                            }
                            RpcProxy proxy = new RpcProxy(newIp + ":8000");
                            HelloService helloService = proxy.create(HelloService.class);
                            final String resp = helloService.hello(input.getText().toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, resp, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Throwable t) {
                            Log.e(TAG, t.toString());
                        }
                    }
                });
                thread.start();
            }
        });
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
