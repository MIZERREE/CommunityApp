package com.ree.mizer.communityapp.pojos.whatsApp;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

public class WhatsAppAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(getRootInActiveWindow() == null){
            return;
        }

        PrefManager prefManager = new PrefManager(getApplicationContext());
       if(!prefManager.getIsOn()){
           return;
       }

       AccessibilityNodeInfoCompat rootNodeInfo = AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());

       List<AccessibilityNodeInfoCompat> sendMessageNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");
       if(sendMessageNodeList != null && !sendMessageNodeList.isEmpty()){
           AccessibilityNodeInfoCompat sendMessage = sendMessageNodeList.get(0);
           if(sendMessage.isVisibleToUser()){
               sendMessage.performAction(AccessibilityNodeInfo.ACTION_CLICK);
           }
       }else{
           Log.e("WhatsAppAccessibilityService","No send button");
       }

       //get search icon in whatsapp
        List<AccessibilityNodeInfoCompat> searchNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/menuitem_search");
       if(searchNodeList !=null && !searchNodeList.isEmpty()){
           AccessibilityNodeInfoCompat searchIcon = searchNodeList.get(0);
           if(searchIcon.isVisibleToUser()){
               //do nothing
               searchIcon.performAction(AccessibilityNodeInfo.ACTION_CLICK);

               List<AccessibilityNodeInfoCompat> searchTextNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/search_src_text");
               if(searchTextNodeList != null && !searchTextNodeList.isEmpty()){
                   AccessibilityNodeInfoCompat searchText = searchTextNodeList.get(0);
                   if(searchText != null && StaticConfig.group_name != null){
                       //set text of text search field
                       Bundle arguments = new Bundle();
                       arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, StaticConfig.group_name);
                       searchText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

                       //sleep until searching
                       try{
                           Thread.sleep(2000);
                       }catch (InterruptedException e){
                           e.printStackTrace();
                       }

                       List<AccessibilityNodeInfoCompat> contactPickerRowNode = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/contactpicker_row_name");
                       if(contactPickerRowNode != null && !contactPickerRowNode.isEmpty()){
                           //open search contact
                           AccessibilityNodeInfoCompat contactPickerRow = contactPickerRowNode.get(0);
                           if(contactPickerRow !=null){
                               if(contactPickerRow.getParent().isClickable()){
                                   contactPickerRow.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                               }else {
                                   performGlobalAction(GLOBAL_ACTION_BACK);
                               }
                           }else {
                               performGlobalAction(GLOBAL_ACTION_BACK);
                           }
                       }else {
                           performGlobalAction(GLOBAL_ACTION_BACK);
                       }
                   }else {
                       performGlobalAction(GLOBAL_ACTION_BACK);
                   }
               }else {
                   performGlobalAction(GLOBAL_ACTION_BACK);
               }
           }
           else {
               performGlobalAction(GLOBAL_ACTION_BACK);
           }
       }else {
           performGlobalAction(GLOBAL_ACTION_BACK);
       }

       try {
           Thread.sleep(2000);
       }catch (InterruptedException e){
           e.printStackTrace();
       }
       performGlobalAction(GLOBAL_ACTION_BACK);
    }

    @Override
    public void onInterrupt() {

    }
}
