package com.example.ros_droid.msgs.geometry_msgs;

/**
 * A Java Bean for the Vector3 ROS geometry_msgs/Twist message type. This can be used both for publishing Twist messages to
 * {@link com.example.ros_droid.RosBridge} and unpacking Twist messages received from {@link com.example.ros_droid.RosBridge} (see the {@link com.example.ros_droid.tools.MessageUnpacker}
 * documentation for how to easily unpack a ROS Bridge message into a Java object).
 * @author James MacGlashan.
 */
public class Twist {
    public Vector3 linear = new Vector3();
    public Vector3 angular = new Vector3();

    public Twist(){}

    public Twist(Vector3 linear, Vector3 angular) {
        this.linear = linear;
        this.angular = angular;
    }
}
