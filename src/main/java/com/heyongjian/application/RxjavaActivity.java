package com.heyongjian.application;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxJavaActivity extends AppCompatActivity {

    @BindView(R.id.button) Button mClick;
    @BindView(R.id.editText) EditText mEditText;
    @BindView(R.id.switch1) Switch mSwithPrint;
    @BindView(R.id.switchObservableOn) Switch mSwitchObservableOn;

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

    @OnClick(R.id.subscribeOn)
    void onButtonSubscribeOn() {
        schedulerSubscribeOn();
    }

    @OnClick(R.id.clean)
    void onButtonClean() {
        printClean();
    }

    private String printSchedule(String where) {
        String log = null;
        if (mSwithPrint.isChecked()){
            log = "----" + where + " || " + Thread.currentThread().getName();
            Log.i(TAG, log);
        }
        return log;
    }

    private void printClean() {
        mEditText.setText("");
    }

    private void printLog(String s) {
        mEditText.setText( mEditText.getText().append("\n").append(s));
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

    private void schedulerSubscribeOn() {
        Observable<String> observableA = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                printSchedule("observableA call ");
                subscriber.onNext("Hello, rxJava");
            }
        });

        Observable<String> observableB = observableA.subscribeOn(Schedulers.io());
        if (mSwitchObservableOn.isChecked())
            observableB = observableB.observeOn(AndroidSchedulers.mainThread());

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
                String threadLog = printSchedule("subscriberA onNext ");
                if (mSwitchObservableOn.isChecked() && threadLog != null)
                    printLog(threadLog);
                Log.d(TAG, "subscribeA onNext " + s);
                printLog("subscribeA onNext " + s);
            }
        };

        observableB.subscribe(subscriberA);
    }


}
