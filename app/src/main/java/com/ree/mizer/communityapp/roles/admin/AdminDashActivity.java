package com.ree.mizer.communityapp.roles.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.User;
import com.ree.mizer.communityapp.pojos.admin.Contributions;
import com.ree.mizer.communityapp.pojos.admin.Fine;
import com.ree.mizer.communityapp.pojos.admin.Group;
import com.ree.mizer.communityapp.pojos.admin.Inventory;
import com.ree.mizer.communityapp.pojos.user.Incident;
import com.ree.mizer.communityapp.roles.admin.groups.GroupsActivity;

import java.util.ArrayList;

public class AdminDashActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    static final float END_SCALE = 0.7f;
    public static Context context;

    MenuItem menuUsers, menuGroups, menuFines, menuContributions, menuReports, menuAnnounce;

    //inventory
    public static int POTS,PLATES,CUPS,CHAIRS,TABLES,TENS,SPADES,CLOTH;

    //drawer menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout contentLinearLayout;

    public static DatabaseReference FUNERAL_REFERENCE, USER_REFERENCE, FINES_REFERENCE,GENERAL_REFERENCE,GROUPS_REFERENCE, INCIDENT_REFERENCE,INVENTORY_REFERENCE,FINANCIAL_CONTRIBUTIONS;

    static ArrayList<User> SEARCHED_USER_LIST;
    public static ArrayList<Group> groupsArrayList;
    public static ArrayList<Incident> incidentArrayList;
    public static ArrayList<Fine> fineArrayList;
    public static ArrayList<Contributions> contributionsArrayList;

    public static User LOGGED_IN_ADMIN;
    public static String  LOGGED_IN_ADMIN_GROUP;

    ImageView menuIcon;

    //bottom navigation bar
    ChipNavigationBar chipNavigationBar;

    DatabaseReference assetsReference;
    public static ArrayList<Inventory> inventoryArrayList;
    Inventory inventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash);

        drawerLayout = findViewById(R.id.admin_drawer_layout);
        navigationView = findViewById(R.id.admin_navigation_view);
        menuIcon = findViewById(R.id.menu_drawer_icon);
        contentLinearLayout = findViewById(R.id.content);
        chipNavigationBar = findViewById(R.id.admin_bottom_nav_menu);

        getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container,new AdminDashFragment()).commit();
        chipNavigationBar.setItemSelected(R.id.bottom_nav_admin_dashboard,true);

        context = this;
        //My menus
        menuUsers = findViewById(R.id.nav_users);

        FUNERAL_REFERENCE = FirebaseDatabase.getInstance().getReference("Funeral announcements");
        GENERAL_REFERENCE = FirebaseDatabase.getInstance().getReference("General announcements");
        USER_REFERENCE = FirebaseDatabase.getInstance().getReference("Users");
        FINES_REFERENCE = FirebaseDatabase.getInstance().getReference("Fines");
        GROUPS_REFERENCE = FirebaseDatabase.getInstance().getReference("Groups");
        INCIDENT_REFERENCE = FirebaseDatabase.getInstance().getReference("Incident reports");
        INVENTORY_REFERENCE = FirebaseDatabase.getInstance().getReference("Inventory");
        FINANCIAL_CONTRIBUTIONS = FirebaseDatabase.getInstance().getReference("Financial contributions");


        getAllUser();
        getLoggedInAdmin();
        getAllGroups();
        getAllIncidents();
        calculateInventory();
        openNavigationDrawer();
        getAllFines();
        bottomMenu();


    }

    private void getLoggedInAdmin(){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        USER_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {

                    User u = dataSnapshot1.getValue(User.class);
                    String uid = dataSnapshot1.getKey();

                    if(UID.equals(uid)){
                        LOGGED_IN_ADMIN = u;
                        LOGGED_IN_ADMIN_GROUP = u.getGroup();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void calculateInventory(){
        assetsReference = FirebaseDatabase.getInstance().getReference("Inventory");
        assetsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inventoryArrayList = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    inventory = dataSnapshot1.getValue(Inventory.class);
                    inventoryArrayList.add(inventory);
                    if(inventory.getDescription().equals("Pots"))
                        POTS = inventory.getTotal();

                    if(inventory.getDescription().equals("Plates"))
                        PLATES = inventory.getTotal();

                    if(inventory.getDescription().equals("Cups"))
                        CUPS = inventory.getTotal();

                    if(inventory.getDescription().equals("Chairs"))
                        CHAIRS = inventory.getTotal();

                    if(inventory.getDescription().equals("Tables"))
                        TABLES = inventory.getTotal();

                    if(inventory.getDescription().equals("Tents"))
                        TENS = inventory.getTotal();

                    if(inventory.getDescription().equals("Spades"))
                        SPADES = inventory.getTotal();

                    if(inventory.getDescription().equals("White cloths"))
                        CLOTH = inventory.getTotal();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //-------Start of Navigation Drawer -------------------//
    private void openNavigationDrawer() {
        //navigation drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(v->{
            if (drawerLayout.isDrawerVisible(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else drawerLayout.openDrawer(GravityCompat.START);
        });

        //menuUsers.setTitle("jgchgchgcv  hgv ");

        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //Scale the view base on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentLinearLayout.setScaleX(offsetScale);
                contentLinearLayout.setScaleY(offsetScale);

                //translate the view according to the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentLinearLayout.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentLinearLayout.setTranslationX(xTranslation);

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }


            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    //must not close the app when drawer is opened
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    //items on the drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.nav_logout){
            this.finish();
        }

        if (item.getItemId() == R.id.nav_users) {
            menuUsers();
        }

        if (item.getItemId() == R.id.nav_group)
            menuGroups();

        if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(AdminDashActivity.this, AdminProfileActivity.class));
        }

        if(item.getItemId() == R.id.nav_incidents){
            menuIncidents();
        }

        if(item.getItemId() == R.id.nav_rate_us){

            try{
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.ree.mizer.communityapp")));
            }catch (ActivityNotFoundException e){
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.mamailanetwork.co.za/bohwa/")));

            }
        }

        if (item.getItemId() == R.id.nav_share){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String body = "Download this App";
            String downloadLink = "https://play.google.com/store/apps/details?id=com.ree.mizer.communityapp";
            shareIntent.putExtra(Intent.EXTRA_TEXT, body);
            shareIntent.putExtra(Intent.EXTRA_TEXT,downloadLink);
            startActivity(Intent.createChooser(shareIntent, "Share using"));

        }

        if(item.getItemId() == R.id.nav_announce){
            if(AdminDashFragment.userAnnouncements > 0){
                item.setTitle("Announcements ("+AdminDashFragment.userAnnouncements+")");
            }
        }

        if (item.getItemId() == R.id.nav_assets) {
            startActivity(new Intent(AdminDashActivity.this, InventoryActivity.class));
        }

        if(item.getItemId() == R.id.nav_fines){
            menuFines();
        }

        return true;
    }

    //-------End of Navigation Drawer -------------------//

    //-------start of bottom menu bar -------------------//
    private void bottomMenu() {

        chipNavigationBar.setOnItemSelectedListener(i -> {
            Fragment fragment = null;
            switch (i){
                case R.id.bottom_nav_admin_dashboard:
                    fragment = new AdminDashFragment();
                    break;
                case R.id.bottom_nav_admin_financial:
                    fragment = new AdminFinanceFragment();
                    break;
                case R.id.bottom_nav_admin_fines:
                    fragment = new AdminFinesFragment();
                    break;
                case R.id.bottom_nav_admin_assets:
                    fragment = new AdminAssetsFragment();
                    break;
                case R.id.bottom_nav_admin_users:
                    fragment = new AdminUserFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container,fragment).commit();
        });
    }
    //-------end of bottom menu bar -------------------//

    //Get all users
    public void getAllUser() {
        USER_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SEARCHED_USER_LIST = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    SEARCHED_USER_LIST.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminDashActivity.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //get all groups
    public void getAllGroups() {
        GROUPS_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupsArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Group group = dataSnapshot1.getValue(Group.class);
                    groupsArrayList.add(group);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminDashActivity.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get all incidents
    public void getAllIncidents() {
        INCIDENT_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                incidentArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Incident incident = dataSnapshot1.getValue(Incident.class);
                    if(incident.getStatus().equals("reported")){
                        incidentArrayList.add(incident);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminDashActivity.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get all fines
    public void getAllFines() {
        FINES_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fineArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Fine fine = dataSnapshot1.getValue(Fine.class);
                    if(fine.getStatus().equals("active")){
                        fineArrayList.add(fine);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminDashActivity.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //methods for menu item
    public void menuUsers(){

        int inactiveUsers = calculateInactiveUsers();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Users");
        builder.setMessage("Registered users: "+SEARCHED_USER_LIST.size()+"\nInactive users: "+inactiveUsers);
        builder.setPositiveButton("Manage users", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container,new AdminUserFragment()).commit();
                chipNavigationBar.setItemSelected(R.id.bottom_nav_admin_users,true);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void menuGroups(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Groups");
        builder.setMessage("The are 8 group:\n \n1.Thoto\n2.Mampheko\n3.Monokaneng\n4.Sethaseng\n5.Mabokeng\n6.Naledi\n7.Mabemane\n8.Mokgoloti");
        builder.setPositiveButton("Manage groups", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(AdminDashActivity.this, GroupsActivity.class));
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void menuIncidents(){
        int incidentReport = incidentArrayList.size();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Incident report");
        builder.setMessage("Currently there are: "+incidentReport+" pending incident reports");
        builder.setPositiveButton("Manage reports", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(AdminDashActivity.this, IncidentActivity.class));
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void menuFines(){
        int activeFines = fineArrayList.size();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Total fines");
        builder.setMessage("Currently there are: "+activeFines+" members fined");
        builder.setPositiveButton("Manage fines", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment fragment = new AdminFinesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container,fragment).commit();
                chipNavigationBar.setItemSelected(R.id.bottom_nav_admin_fines,true);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //Calculate number of users
    private int calculateInactiveUsers(){
        int count=0;

        try{
            for(int x=0;x<SEARCHED_USER_LIST.size(); x++){
                if(SEARCHED_USER_LIST.get(x).getStatus().equalsIgnoreCase("not active")){
                    count++;

                }
            }
        }catch (Exception e){
            Toast.makeText(context, "Error 101: Some users' status are blank please contact admin", Toast.LENGTH_SHORT).show();
        }


        return count;
    }




}