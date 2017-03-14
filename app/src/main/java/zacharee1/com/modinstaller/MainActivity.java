package zacharee1.com.modinstaller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.Transition;
import android.support.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity
        /*implements NavigationView.OnNavigationItemSelectedListener*/ {

    public SharedPreferences sharedPrefs;
    public boolean firstStartRoot;

    public Button installMod;
    public Button installSysUI;
    public Button installSB;
    public Button installMinRes;

    public Button makeSysApp;
    public Button playStore;
    public Button XDA;

    public Switch chooseMods;

    public String SUI;
    public String SB;
    public String minRes;

    public String suiAPK;
    public String sbAPK;
    public String minZIP;

    public static final int WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPrefs = getSharedPreferences("com.zacharee1.modcontrol", MODE_PRIVATE);

        if (sharedPrefs.getBoolean("firststart", true)) {
            firstStartRoot = true;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.donate_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button kill_sysserver = (Button) findViewById(R.id.kill_systemserver);
        kill_sysserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sudo("killall system_server");
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        });

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        installMod = (Button) findViewById(R.id.install_mod);
        installSysUI = (Button) findViewById(R.id.install_sysui);
        installSB = (Button) findViewById(R.id.install_sb);
        installMinRes = (Button) findViewById(R.id.install_minit);

        makeSysApp = (Button) findViewById(R.id.make_sysapp);
        playStore = (Button) findViewById(R.id.play_store);
        XDA = (Button) findViewById(R.id.xda);

        chooseMods = (Switch) findViewById(R.id.switch_aio);

        SUI = "install";
        SB = "install";
        minRes = "MinitResources";

        suiAPK = "LGSystemUI";
        sbAPK = "LGSignBoard";
        minZIP = "MinitResources";

        findViewById(R.id.install_aio).setVisibility(View.VISIBLE);
        findViewById(R.id.install_choose).setVisibility(View.INVISIBLE);

        try {
            if (firstStartRoot) firstStart();
            buttons();
            reqPerms();
            switches();
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

    public void reqPerms() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE);
        }
    }

    public void firstStart() throws IOException {
        Runtime.getRuntime().exec(new String[]{"su", "-", "root"});
        firstStartRoot = false;
        SharedPreferences.Editor editor = getSharedPreferences("com.zacharee1.modcontrol", MODE_PRIVATE).edit();
        editor.putBoolean("firststart", false);
        editor.apply();
    }

    public void switches() throws IOException {
        chooseMods.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int animTime = 300;
                final LinearLayout layout1 = (LinearLayout) findViewById(R.id.install_aio);
                final LinearLayout layout2 = (LinearLayout) findViewById(R.id.install_choose);
                if (isChecked) {
                    fadeInOutLayout(layout2, layout1, animTime);
                    layout2.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.GONE);
                } else {
                    fadeInOutLayout(layout1, layout2, animTime);
                    layout2.setVisibility(View.INVISIBLE);
                    layout1.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void buttons() throws IOException {

        installMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            copyAPK(suiAPK);
                            copyFile2(SUI, suiAPK);

                            copyAPK(sbAPK);
                            copyFile2(SB, sbAPK);

                            copyZIP(minZIP);
                            copyFile2(minRes, minZIP);

                            runScript(minRes, minZIP);
                            runScript(SUI, suiAPK);
                            runScript(SB, sbAPK);
                        } catch (Exception e) {
                            Log.e("error", e.getMessage());
                        }
                    }
                }).start();
            }
        });

        installSysUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            copyAPK(suiAPK);
                            copyFile2(SUI, suiAPK);

                            runScript(SUI, suiAPK);
                        } catch (Exception e) {
                            Log.e("error", e.getMessage()); //commit problems...
                        }
                    }
                }).start();
            }
        });

        installSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            copyAPK(sbAPK);
                            copyFile2(SB, sbAPK);

                            runScript(SB, sbAPK);
                        } catch (Exception e) {
                            Log.e("error", e.getMessage());
                        }
                    }
                }).start();
            }
        });

        installMinRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            copyZIP(minZIP);
                            copyFile2(minRes, minZIP);

                            runScript(minRes, minZIP);
                        } catch (Exception e) {
                            Log.e("error", e.getMessage());
                        }
                    }
                }).start();
            }
        });

        makeSysApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            sudo("mount -o rw,remount /system");
                            sudo("mount -o rw,remount /data");
                            sudo("mkdir /system/priv-app/ModControl");
                            sudo("cp /data/app/com.zacharee1.modcontrol-*/base.apk /system/priv-app/ModControl/ModControl.apk");
                            sudo("chmod 0755 /system/priv-app/ModControl/");
                            sudo("chmod 0644 /system/priv-app/ModControl/ModControl.apk");
                        } catch (Exception e) {
                            Log.e("error", e.getMessage());
                        }
                    }
                }).start();
            }
        });

        playStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=com.zacharee1.modcontrol"));
                        startActivity(intent);
                    }
                }).start();
            }
        });

        XDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://forum.xda-developers.com/v20/themes/mod-aosp-signal-bars-t3551350"));
                        startActivity(intent);
                    }
                }).start();
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    public void sudo(String...strings) throws IOException {
        try{
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            for (String s : strings) {
                outputStream.writeBytes(s+"\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            outputStream.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void copyAPK(String targetFile) throws IOException {
        String targetDirectory = Environment.getExternalStorageDirectory().toString() + "/Zacharee1Mods/";
        AssetManager assetManager = getAssets();
        File modFolder = new File(targetDirectory);

        if (!modFolder.isDirectory()) {
            boolean result = modFolder.mkdir();
            if (!result) {
                throw new IOException("Could not create nonexistent mod folder. Abort.");
            }
        }
        try (
                InputStream in = assetManager.open(targetFile + ".apk");
                OutputStream out = new FileOutputStream(targetDirectory + targetFile + ".apk")
        ) {
            copyFile(in, out);
        }
    }

    public void copyZIP(String targetFile) throws IOException {
        String targetDirectory = Environment.getExternalStorageDirectory().toString() + "/Zacharee1Mods/";
        AssetManager assetManager = getAssets();
        File modFolder = new File(targetDirectory);

        if (!modFolder.isDirectory()) {
            boolean result = modFolder.mkdir();
            if (!result) {
                throw new IOException("Could not create nonexistent mod folder. Abort.");
            }
        }
        try (
                InputStream in = assetManager.open(targetFile + ".zip");
                OutputStream out = new FileOutputStream(targetDirectory + targetFile + ".zip")
        ) {
            copyFile(in, out);
        }
    }

    public void copyFile2(final String targetFile, final String zipFile) throws IOException {
        final String targetDirectory = Environment.getExternalStorageDirectory().toString() + "/Zacharee1Mods/";
        final AssetManager assetManager = getAssets();
        File modFolder = new File(targetDirectory);

        if (!modFolder.isDirectory()) {
            boolean result = modFolder.mkdir();
            if (!result) {
                throw new IOException("Could not create nonexistent mod folder. Abort.");
            }
        }

        try (
                InputStream in = assetManager.open(targetFile);
                OutputStream out = new FileOutputStream(targetDirectory + targetFile)
        ) {
            copyFile(in, out);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

    public void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[10240];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public void runScript(final String targetFile, final String zip) throws IOException{
        try {
            sudo("chmod +x /data/media/0/Zacharee1Mods/" + targetFile);
            sudo("chmod 777 /data/media/0/Zacharee1Mods/" + targetFile);
            sudo("sh /data/media/0/Zacharee1Mods/" + targetFile + " " + zip);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

    private void fadeInOutLayout(final LinearLayout layout1, final LinearLayout layout2, final int animTime) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        Animation fadeOut = new AlphaAnimation(1, 0);

        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeOut.setInterpolator(new AccelerateInterpolator());

        fadeIn.setDuration(animTime);
        fadeOut.setDuration(animTime);

        fadeIn.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
//                layout1.setVisibility(View.VISIBLE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
//                layout2.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        layout1.startAnimation(fadeIn);
        layout2.startAnimation(fadeOut);
    }
}
