package com.example.app_server;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ListActivity extends AppCompatActivity {
  private TextView nicknameTV;
  private EditText msgET;
  private Button logoutBT, msgBT;
  private UsersModal user;
  private final String BASE_URL = "http://192.249.18.196";
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
    
    if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
      finish();
      startActivity(new Intent(this, MainActivity.class));
    }
    
    user = SharedPrefManager.getInstance(this).getUser();
    nicknameTV = findViewById(R.id.nicknameTV);
    logoutBT = findViewById(R.id.logoutBT);
    msgBT = findViewById(R.id.msgBT);
    msgET = findViewById(R.id.msgET);
    
    nicknameTV.setText(user.getNickname() + "!");
    
    logoutBT.setOnClickListener(v -> {
      SharedPrefManager.getInstance(this).logout();
    });
  
    final Socket socket = IO.socket(URI.create(BASE_URL));
    socket.connect();
    Log.d(null, "socket created!");
    socket.emit("hello", user.getNickname());
    Log.d(null, "socket emitted!");
    
    String username = user.getNickname().toString();
    String room = "ROOM0";
    Object temp = new String[]{
        username, room
    };
    JSONObject new_user = new JSONObject();
    try {
      new_user.put("username", username);
      new_user.put("room", room);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    socket.emit("joinRoom", new_user);
//    socket.on("roomUsers", new Emitter.Listener() {
//      @Override
//      public void call(Object... args) {
//        outputRoomName(args[0].toString());
//        outputUsers(args[1].toString());
//      }
//    });
    socket.on("message", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        outputMessage(args[0].toString());
      }
    });
    
    msgBT.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        socket.emit("chatMessage", msgET.getText().toString());
        msgET.setText("");
      }
    });
  }
  
  private void outputMessage(String message) {
//    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    Log.d(null, message);
  }
  
//  private void outputRoomName(String room) {
//    Toast.makeText(getApplicationContext(), "You are currently in room " + room, Toast.LENGTH_SHORT).show();
//  }
  
//  private void outputUsers(String users) {
//    Toast.makeText(getApplicationContext(), "Current users: " + users, Toast.LENGTH_SHORT).show();
//  }
}