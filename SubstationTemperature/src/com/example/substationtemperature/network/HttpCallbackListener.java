package com.example.substationtemperature.network;

public interface HttpCallbackListener {
	void onFinish(String response);
    void onError(Exception e);
}
