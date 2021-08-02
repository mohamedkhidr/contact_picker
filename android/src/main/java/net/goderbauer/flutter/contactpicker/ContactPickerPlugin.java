// Copyright 2017 Michael Goderbauer. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package net.goderbauer.flutter.contactpicker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static android.app.Activity.RESULT_OK;

public class ContactPickerPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener {
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "contact_picker");
    ContactPickerPlugin instance = new ContactPickerPlugin(registrar.activity());
    registrar.addActivityResultListener(instance);
    channel.setMethodCallHandler(instance);
  }

    private ContactPickerPlugin(Activity activity) {
        this.activity = activity;
    }

  private static int PICK_CONTACT = 2015;

  private Activity activity;
  private Result pendingResult;

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("selectContact")) {
      if (pendingResult != null) {
        pendingResult.error("multiple_requests", "Cancelled by a second request.", null);
        pendingResult = null;
     
      }
      pendingResult = result;

      Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
      activity.startActivityForResult(i, PICK_CONTACT);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
   Log.e("taggggo",  "activityresult");
    if (requestCode != PICK_CONTACT) {
      return false;
    }
    if (resultCode != RESULT_OK) {
      pendingResult.success(null);
      pendingResult = null;
      Log.e("taggggo",  data.getComponent());
      Log.e("taggggo",  requestCode+"====="+resultCode);
     Log.e("taggggo",  "failed");
      return true;
    }
 
    String fullName = data.getStringExtra("name");
    String number = data.getStringExtra("number");
    
    Log.e("taggggo",  fullName+number+"");
   

    HashMap<String, Object> contact = new HashMap<>();
    contact.put("fullName", fullName);
    contact.put("phoneNumber", number);

    pendingResult.success(contact);
    pendingResult = null;
    return true;
  }
}
