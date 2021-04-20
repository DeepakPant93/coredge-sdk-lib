//package com.admin.coredge.Services;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.TaskStackBuilder;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.core.app.NotificationCompat;
//
//import org.eclipse.paho.android.service.MqttAndroidClient;
//import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
//import org.eclipse.paho.client.mqttv3.IMqttActionListener;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.IMqttToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//
//import java.util.Random;
//
//public class MqttHelper {
//    private static final String MQTT_TOPIC = "test";
//    private static final String MQTT_URL = "ssl://mqttdemo.tk:8883";
//    private static boolean published;
//    private static MqttAndroidClient client;
//    private static final String TAG = MqttHelper.class.getName();
//
//
//    public static MqttAndroidClient getClient(Context context){
//        if(client == null){
//            String clientId = MqttClient.generateClientId();
//            client =  new MqttAndroidClient(context, MQTT_URL, clientId);
//        }
//        if(!client.isConnected())
//            connect(context);
//        return client;
//    }
//
//    private static void connect(Context context){
//        final String username = "root";
//        final String password = "ADmin@123a";
//        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//        mqttConnectOptions.setCleanSession(true);
//        mqttConnectOptions.setKeepAliveInterval(30);
//        mqttConnectOptions.setUserName(username);
//        mqttConnectOptions.setPassword(password.toCharArray());
//
//        try{
//            client.connect(mqttConnectOptions, null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    Log.d(TAG, "onSuccess"+ asyncActionToken);
//                    subscribeToTopic();
//                }
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    Log.d(TAG, "onFailure. Exception when connecting: " + exception);
//                }
//            });
//
//        }catch (Exception e) {
//            Log.e(TAG, "Error while connecting to Mqtt broker : " + e);
//            e.printStackTrace();
//        }
//        client.setCallback(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable cause) {
//
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//
//                Log.d("tag", "message>>" + new String(message.getPayload()));
//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(context, new String(message.getPayload()), duration);
//                toast.show();
//
//
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });
//    }
//
//    public static void publishMessage(final String payload){
//        published = false;
//        try {
//            byte[] encodedpayload = payload.getBytes();
//            MqttMessage message = new MqttMessage(encodedpayload);
//            client.publish(MQTT_TOPIC, message);
//            published = true;
//            Log.i(TAG, "message successfully published : " + payload);
//        } catch (Exception e) {
//            Log.e(TAG, "Error when publishing message : " + e);
//            e.printStackTrace();
//        }
//    }
//
//    public static void close(){
//        if(client != null) {
//            client.unregisterResources();
//            client.close();
//        }
//    }
////    public MqttAndroidClient mqttAndroidClient;
////
////    final String serverUri = "tcp://mqttdemo.tk:8883";
////
////    final String clientId = " ";
////    final String subscriptionTopic = "testopic";
////
////    final String username = "sammy";
////    final String password = "password";
////
////    public MqttHelper(Context context){
////        client.setCallback(new MqttCallbackExtended() {
////            @Override
////            public void connectComplete(boolean b, String s) {
////                Log.w("mqtt", s);
////            }
////
////            @Override
////            public void connectionLost(Throwable throwable) {
////
////            }
////
////            @Override
////            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
////                Log.w("Mqtt123", mqttMessage.toString());
////            }
////
////            @Override
////            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
////
////            }
////        });
////        connect();
////    }
////
////    public void setCallback(MqttCallbackExtended callback) {
////        client.setCallback(callback);
////    }
////
////    private void connect(){
////        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
////        mqttConnectOptions.setAutomaticReconnect(true);
////        mqttConnectOptions.setCleanSession(false);
////        mqttConnectOptions.setUserName(username);
////        mqttConnectOptions.setPassword(password.toCharArray());
////
////        try {
////
////            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
////                @Override
////                public void onSuccess(IMqttToken asyncActionToken) {
////
////                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
////                    disconnectedBufferOptions.setBufferEnabled(true);
////                    disconnectedBufferOptions.setBufferSize(100);
////                    disconnectedBufferOptions.setPersistBuffer(false);
////                    disconnectedBufferOptions.setDeleteOldestMessages(false);
////                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
////                    subscribeToTopic();
////                }
////
////                @Override
////                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
////                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString());
////                }
////            });
////
////
////        } catch (MqttException ex){
////            ex.printStackTrace();
////        }
////    }
////
////
//    private static void subscribeToTopic() {
//        try {
//            client.subscribe(MQTT_TOPIC, 0, null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    Log.w("Mqtt","Subscribed!");
//                  //  subscribeMqttChannel(MQTT_TOPIC);
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    Log.w("Mqtt", "Subscribed fail!");
//                }
//            });
//
//        } catch (MqttException ex) {
//            System.err.println("Exceptionst subscribing");
//            ex.printStackTrace();
//        }
//    }
//}
//
//
