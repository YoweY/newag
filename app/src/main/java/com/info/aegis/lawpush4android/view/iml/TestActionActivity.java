package com.info.aegis.lawpush4android.view.iml;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TestActionActivity extends BaseActivity {
    ConnectionFactory factoryShowMode ;
    ExecutorService threadPoolExecutorTest;
    TextView textView;
    MyHandler handler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_action);
        AppManager.getAppManager().addActivity(TestActionActivity.this);
        factoryShowMode = new ConnectionFactory();
        threadPoolExecutorTest = Executors.newFixedThreadPool(1);
        textView = (TextView) findViewById(R.id.id_text);
        handler = new MyHandler();
        setupConnectionShowModeFactory();
        testActionMQ();
    }

    /**
     * rabbitMQ设置连接参数
     */
    private void setupConnectionShowModeFactory() {
        factoryShowMode.setHost("180.96.11.68");
        factoryShowMode.setPort(5672);
        factoryShowMode.setUsername("aegis");
        factoryShowMode.setPassword("shield");
    }

    class MyHandler extends  Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            textView.setText("接收到消息："+msg.obj);
        }
    }
    /**
     * 原先的人工处理平台
     */
    public void testActionMQ() {
        if (threadPoolExecutorTest != null)
            threadPoolExecutorTest.shutdownNow();
        threadPoolExecutorTest = Executors.newFixedThreadPool(1);
        threadPoolExecutorTest.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = factoryShowMode.newConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare("lawPush_motion_test", false, false, false, null);
                    //设置最大服务转发消息数量
                    int prefetchCount = 1;
                    channel.basicQos(prefetchCount);
                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    // 指定消费队列
                    boolean ack = false; //打开应答机制
                    channel.basicConsume("lawPush_motion_test", ack, consumer);
                    while (true) {

                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        String message = new String(delivery.getBody());
                        Log.e("lawPush","   message---"+message);

                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        String value = "{motions:" + message + "}";
                        robotPerformAction(new JSONObject(value));

                    }
                } catch (InterruptedException e) {
                    Log.e("lawPush", "rabbitMQ InterruptedException : " + e.toString());
                } catch (Exception e1) {
                    Log.e("lawPush", "rabbitMQ Exception : " + e1.toString());
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        AppManager.getAppManager().finishActivity(TestActionActivity.this);
        finish();
    }
}
