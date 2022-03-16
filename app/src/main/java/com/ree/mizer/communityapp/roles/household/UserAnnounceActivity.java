package com.ree.mizer.communityapp.roles.household;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.javaMail.JavaMailAPI;
import com.ree.mizer.communityapp.pojos.MyNotifications;
import com.ree.mizer.communityapp.pojos.admin.Funeral;
import com.ree.mizer.communityapp.pojos.admin.GeneralAnnouncement;
import com.ree.mizer.communityapp.pojos.fcm.FcmNotificationsSender;
import com.ree.mizer.communityapp.pojos.whatsApp.PrefManager;
import com.ree.mizer.communityapp.pojos.whatsApp.StaticConfig;
import com.ree.mizer.communityapp.pojos.whatsApp.WhatsAppAccessibilityService;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UserAnnounceActivity extends AppCompatActivity {
    EditText edtName, edtAddress, edtWhenBurial, edtContacts, edtExtras, edtHeading, edtDescription;
    DatePicker datePicker;
    String whenHappened;
    Button btnTest,btnTestWhatsApp;
    PrefManager prefManager;

    //firebase
    DatabaseReference funeralReference, generalReference;

    private ProgressDialog loadingBar;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_announce);

        funeralReference = FirebaseDatabase.getInstance().getReference("Funeral announcements");
        generalReference = FirebaseDatabase.getInstance().getReference("General announcements");

        edtName = findViewById(R.id.user_text_funeral_name);
        edtAddress = findViewById(R.id.user_text_funeral_address);
        datePicker = findViewById(R.id.user_datePickerFuneral);
        edtWhenBurial = findViewById(R.id.user_text_funeral_when_burial);
        edtExtras = findViewById(R.id.user_text_funeral_extras);
        edtContacts = findViewById(R.id.user_text_funeral_contact_number);
        edtHeading = findViewById(R.id.user_text_general_heading);
        edtDescription = findViewById(R.id.user_text_general_announcement);
        btnTest = findViewById(R.id.user_btn_general_test);
        btnTestWhatsApp = findViewById(R.id.user_btn_whatsapp_group_test);

        loadingBar = new ProgressDialog(this);

        prefManager = new PrefManager(getApplicationContext());

        //btnTestWhatsApp.setOnClickListener(v -> {whatsAppAnnounceGeneral();});

        //Check if whatsapp accessibility is on
        if(!isAccessibilityOn(getApplicationContext())){
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        //current date
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date = currentDate;

        /*if(!isAccessibilityOn(getApplicationContext())){
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }*/
        //Notifications
        //FirebaseMessaging.getInstance().subscribeToTopic("all");
        

    }

    public void backFromUserAnnounce(View view){
        this.finish();
    }

    public void userAnnounceFuneral(View view){
        //loading bar
        loadingBar.setTitle("Announcing funeral");
        loadingBar.setMessage("Please wait ...");
        loadingBar.show();

        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage("Funeral Announcement updated to the group");
        builder.setIcon(R.drawable.info_icon);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        //end of alert dialog

        //validations
        if(edtName.getText().toString().trim().equals("") || edtName.getText().toString().trim().isEmpty()){
            edtName.setError("Enter deceased name");
            loadingBar.dismiss();
        }else if(edtAddress.getText().toString().trim().equals("") || edtAddress.getText().toString().trim().isEmpty()){
            edtAddress.setError("Enter deceased address");
            loadingBar.dismiss();
        }else if(edtWhenBurial.getText().toString().trim().equals("") || edtWhenBurial.getText().toString().trim().isEmpty()){
            edtWhenBurial.setText("No date");
            loadingBar.dismiss();
        }else if(edtContacts.getText().toString().trim().equals("") || edtContacts.getText().toString().trim().isEmpty()){
            edtContacts.setError("Who to contact?");
            loadingBar.dismiss();
        }else if(edtExtras.getText().toString().trim().equals("") || edtExtras.getText().toString().trim().isEmpty()){
            edtExtras.setText("nothing");
            loadingBar.dismiss();
        }else{
            String name = edtName.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            whenHappened = datePicker.getDayOfMonth()+"/"+ (datePicker.getMonth() + 1)+"/"+datePicker.getYear();
            String whenBurial = edtWhenBurial.getText().toString().trim();
            String contacts = edtContacts.getText().toString().trim();
            String extras = edtExtras.getText().toString().trim();

            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            String funeralID = rootNode.getReference("Funeral announcements").push().getKey();
            funeralReference = rootNode.getReference("Funeral announcements").child(funeralID);

            Funeral funeral = new Funeral(funeralID, FirebaseAuth.getInstance().getCurrentUser().getUid(),name,address,whenHappened,whenBurial,contacts,extras,"admin");
            funeralReference.setValue(funeral).addOnCompleteListener(v->{
                loadingBar.dismiss();
                alertDialog.show();
                clearView();

                String reporter = HouseHoldDashActivity.LOGGED_IN_USER.getFullNames();
                //push notification
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/news","Funeral report", reporter+" reported new funeral, log in for more details"
                        ,getApplicationContext(),UserAnnounceActivity.this);
                notificationsSender.SendNotifications();

            }).addOnFailureListener(v-> Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show());
        }
    }



    public void whatsAppAnnounceGeneral(View view){
        StaticConfig.group_name = "Leola";
        prefManager.setIsOn(true);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.setPackage("com.whatsapp");
        share.putExtra(Intent.EXTRA_SUBJECT, "Title of the post");
        share.putExtra(Intent.EXTRA_TEXT, "Test message sent from the Community app id:"+funeralReference.getRef().toString());
        startActivity(Intent.createChooser(share,"Share link"));
    }

    public void setWhatsappPrefOff(){
        prefManager.setIsOn(false);
    }

    public void userAnnounceGeneral(View view){
        //loading bar
        loadingBar.setTitle("Announcing");
        loadingBar.setMessage("Please wait ...");
        loadingBar.show();

        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage("General Announcement submitted to the group");
        builder.setIcon(R.drawable.info_icon);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        //end of alert dialog

        //validations
        if(edtHeading.getText().toString().trim().equals("") || edtHeading.getText().toString().trim().isEmpty()){
            edtHeading.setError("Enter heading");
            loadingBar.dismiss();
        }else if(edtDescription.getText().toString().trim().equals("") || edtDescription.getText().toString().trim().isEmpty()){
            edtDescription.setError("Enter description");
            loadingBar.dismiss();
        }else{
            String heading = edtHeading.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            String generalID = rootNode.getReference("General announcements").push().getKey();
            generalReference = rootNode.getReference("General announcements").child(generalID);

            GeneralAnnouncement generalAnnouncement = new GeneralAnnouncement(generalID,date,heading,description,"admin");
            generalReference.setValue(generalAnnouncement).addOnCompleteListener(v->{
                loadingBar.dismiss();
                alertDialog.show();
                clearViewGeneral();

                String reporter = HouseHoldDashActivity.LOGGED_IN_USER.getFullNames();

                //push notification
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/news","General announcement", reporter+" posted an announcement, log in for more details"
                        ,getApplicationContext(),UserAnnounceActivity.this);
                notificationsSender.SendNotifications();

            }).addOnFailureListener(v-> Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show());
        }
    }

    private void clearView(){
        edtName.setText("");
        edtAddress.setText("");
        edtWhenBurial.setText("");
        edtContacts.setText("");
        edtExtras.setText("");
    }

    private void clearViewGeneral(){
        edtDescription.setText("");
        edtHeading.setText("");
    }

    /*public void whatsApp(View view){
        String number = "+27640604847";
        String message = "Text message from the community app ";
        try{
            PackageManager packageManager = getApplicationContext().getPackageManager();

            String url = "https://api.whatsapp.com?phone="+number+"&text="+ URLEncoder.encode(message,"UFT-8");
            Intent whatsAppIntent = new Intent(Intent.ACTION_VIEW);
            whatsAppIntent.setPackage("com.whatsapp");
            whatsAppIntent.setData(Uri.parse(url));
            whatsAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(whatsAppIntent.resolveActivity(packageManager) !=null){
                getApplicationContext().startActivity(whatsAppIntent);
                Thread.sleep(5000);

            }else{
                Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
            }


        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/

    //sends to a group
    public void whatsApp(View view){
        String msg = "Hi Good Afternoon Captain.";
        Uri uri = Uri.parse("https://chat.whatsapp.com/KaL2o4aKTS4DL9XzguYVSP");
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
        /*sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi Good Afternoon Captain.");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);*/
    }

    /*
    This one works
    public void whatsApp(View view){
        try {
            String mobile = "+27640604847";
            String msg = "Hi Good Afternoon Captain.";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + mobile + "&text=" + msg)));
        }catch (Exception e){
            //whatsapp app not install
        }
    }*/

    /*public void whatsApp(View view){
        boolean isWhatsAppInstalled = whatsAppInstalledOrNot("com.whatsapp");
        if (isWhatsAppInstalled) {
            Uri uri = Uri.parse("smsto:" + "+27640604847");
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi Good Afternoon Captain.");
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } else {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);

        }
    }*/

    /**
     * This is for whatsapp notifications
     *
     *
     */
    private boolean whatsAppInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private boolean isAccessibilityOn(Context context){
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + WhatsAppAccessibilityService.class.getCanonicalName();
        try{
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.NAME);
        }catch (Settings.SettingNotFoundException iggnored){

        }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');

        if(accessibilityEnabled == 1){
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),Settings.Secure.NAME);
            if(settingValue != null){
                colonSplitter.setString(settingValue);
                while(colonSplitter.hasNext()){
                     String accessibilityService = colonSplitter.next();

                     if(accessibilityService.equalsIgnoreCase(service)){
                         return true;
                     }
                }
            }
        }

        return false;
    }
}