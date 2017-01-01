package com.heyongjian.application;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class RxJavaActivity extends AppCompatActivity {

    @BindView(R.id.button) Button mClick;
    @BindView(R.id.editText) EditText mEditText;

    private static final String TAG = "RxJava";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button)
    void onButtonClick() {
        simpleRx();
    }

    @OnClick(R.id.buttonMap)
    void onButtonClickMap() {
        simpleRxMap();
    }

    private void simpleRx() {

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello, rxJava");
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "subscribe onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "subscribe onError" + e.toString());
            }

            @Override
            public void onNext(String s) {
                mEditText.setText(mEditText.getText().append("\n").append(s));
                Log.d(TAG, "subscribe onNext" + s);
            }
        });
    }

    private void simpleRxMap() {

        Observable<String> observableA = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello, rxJava");
            }
        });

        Observable<String> observableB = observableA.map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s + " I'm map";
            }
        });

        Subscriber<String> subscriberA = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "subscribe onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "subscribe onError" + e.toString());
            }

            @Override
            public void onNext(String s) {
                mEditText.setText(mEditText.getText().append("\n").append(s));
                Log.d(TAG, "subscribe onNext" + s);
            }
        };
        observableB.subscribe(subscriberA);
    }

}
