package me.liccon.fileexplorer.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import me.liccon.fileexplorer.R;
import me.liccon.fileexplorer.fragments.FolderFragment;
import me.liccon.fileexplorer.fragments.StorageFragment;
import me.liccon.fileexplorer.models.ButtonBar;
import me.liccon.fileexplorer.models.Clipboard;
import me.liccon.fileexplorer.models.FileInfo;
import me.liccon.fileexplorer.models.ToolBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ToolBar toolBar;
    private ButtonBar buttonBar;
    private StorageFragment storageFragment = null;
    private final Stack<FolderFragment> fragments = new Stack<>();
    private final Clipboard clipboard = new Clipboard();

    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.toolBar = new ToolBar((TextView) findViewById(R.id.folderName));
        this.buttonBar = new ButtonBar(findViewById(R.id.buttonBar), fragments);


        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        FolderFragment folderFragment = FolderFragment.newInstance(root);

        addFragment(folderFragment, false);

        this.findViewById(R.id.app).setOnClickListener(this);
        this.findViewById(R.id.sd).setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
            {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    if (storageFragment != null)
                    {
                        storageFragment.reload();
                    }
                    else
                    {
                        FolderFragment folderFragment = fragments.peek();
                        folderFragment.refreshFolder();
                    }
                }
                else
                {
                    finish();
                }
            }
        }
    }

    public void addFragment(final FolderFragment fragment, final boolean addToBackStack)
    {
        fragments.push(fragment);

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (addToBackStack)
        {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        }

        transaction.add(R.id.fragmentContainer, fragment);

        if (addToBackStack)
        {
            transaction.addToBackStack(null);
        }

        transaction.commitAllowingStateLoss();

        toolBar.update(fragment);
    }

    private void removeFragment(final FolderFragment fragment)
    {
        fragments.pop();

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();

        if (!fragments.isEmpty())
        {
            FolderFragment topFragment = fragments.peek();
            topFragment.refreshFolder();

            toolBar.update(topFragment);
        }
    }

    public Clipboard clipboard()
    {
        return clipboard;
    }

    public ButtonBar buttonBar()
    {
        return buttonBar;
    }

    @Override
    public void onBackPressed()
    {
        if (fragments.size() > 0)
        {
            FolderFragment fragment = fragments.peek();

            if (fragment.onBackPressed())
            {
                if (storageFragment == null)
                {
                    if (fragments.size() > 1)
                    {
                        removeFragment(fragment);
                    }
                    else
                    {
                        finish();
                    }
                }
                else
                {
                    removeFragment(fragment);

                    if (fragments.isEmpty())
                    {
                        toolBar.update(getString(R.string.app_name));
                        buttonBar.displayButtons(0, false, false, false, false);
                    }
                }
            }
        }
        else
        {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        // no call for super(). Bug on API Level > 11.
    }
    private void openFolder(String filepath)
    {
        FolderFragment folderFragment = FolderFragment.newInstance(filepath);

        addFragment(folderFragment, true);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if (id==R.id.app){
            String root = this.getFilesDir().getParent();//.getAbsolutePath();
            openFolder(root);
        }else if (id==R.id.sd){
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            openFolder(root);
            //String root = this.getDataDir().getAbsolutePath();
           // openFolder(root);
        }
    }
}