package com.example.rosdroid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ros_droid.ConnectionListener;
import com.example.ros_droid.Publisher;
import com.example.ros_droid.RosBridge;
import com.example.ros_droid.RosListenDelegate;
import com.example.ros_droid.SubscriptionRequestMsg;
import com.example.ros_droid.msgs.std_msgs.PrimitiveMsg;
import com.example.ros_droid.tools.MessageUnpacker;

public class MainActivity extends AppCompatActivity {

    private Button connectBtn, publishBtn, subscribeBtn;
    private TextView statusView, subscribeView;
    private EditText subscribeTopic, publishTopic, publishMessage;


    private RosBridge rosBridge;
    private boolean isConnected;
    private Publisher publisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // reference to the views component
        connectBtn = findViewById(R.id.connect_btn);
        statusView = findViewById(R.id.status_view);
        publishBtn = findViewById(R.id.publish_btn);
        subscribeBtn = findViewById(R.id.subscribe_btn);
        subscribeView = findViewById(R.id.subscribe_view);
        subscribeTopic = findViewById(R.id.subscribe_topic);
        publishTopic = findViewById(R.id.publish_topic);
        publishMessage = findViewById(R.id.publish_message);


        // instantiate a ros bridge reference
        rosBridge = new RosBridge();
        isConnected = false;


        // listen when a connection or disconnection event happens
        rosBridge.connectionListener.setListener(new ConnectionListener.ChangeListener() {
            @Override
            public void onChange() {
                if(rosBridge.connectionListener.getIsConnected()){
                    isConnected = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusView.setText("Status : Connected");
                            connectBtn.setText("Disconnect");
                        }
                    });
                } else{
                    isConnected = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusView.setText("Status: Disconnected");
                            connectBtn.setText("Connect");
                        }
                    });
                }
            }
        });


        // catch the click on the connect button
        // connect to a web socket server with a ros bridge server
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected){
                    statusView.setText("Status : Disconnecting . . .");
                    rosBridge.closeConnection();
                } else{
                    statusView.setText("Status : Connecting . . .");
                    rosBridge.connect("ws://192.168.0.166:9090", false);
                }
            }
        });


        // PUBLISH A MESSAGE ON A ROS TOPIC
        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if the client is connected with the ros server
                if(isConnected){
                    // Check that the user filled the topic field
                    if(!publishTopic.getText().equals("")){
                        // Check that the user filled the message field
                        if(!publishMessage.getText().equals("")){
                            // Publish the message on the topic
                            publisher = new Publisher(publishTopic.getText().toString(), "std_msgs/String", rosBridge);
                            publisher.publish(new PrimitiveMsg<String>(publishMessage.getText().toString()));
                        } else{
                            Toast.makeText(getApplicationContext(), "Please insert the message before publishing", Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        Toast.makeText(getApplicationContext(), "Please select the topic before publishing", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Please connect before publish a message", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // SUBSCRIBE TO A ROS TOPIC
        subscribeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if the client is connected with the ros server
                if(isConnected){
                    // check that the user filled the topic field
                    if(!subscribeTopic.getText().equals("")){
                        // Subscribe to a topic
                        rosBridge.subscribe(SubscriptionRequestMsg.generate(subscribeTopic.getText().toString())
                                .setType("std_msgs/String")
                                .setThrottleRate(1)
                                .setQueueLength(1), new RosListenDelegate() {
                            @Override
                            public void receive(com.fasterxml.jackson.databind.JsonNode data, String stringRep) {
                                MessageUnpacker<PrimitiveMsg<String>> unpacker = new MessageUnpacker<PrimitiveMsg<String>>(PrimitiveMsg.class);
                                PrimitiveMsg<String> msg = unpacker.unpackRosMessage(data);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        subscribeView.setText(subscribeView.getText().toString()+msg.data+"\n");
                                    }
                                });
                            }
                        });
                    } else{
                        Toast.makeText(getApplicationContext(), "Please select the topic before subscribing", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Please connect before subscribe to a topic", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}