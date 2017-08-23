package me.liccon.fileexplorer.models;

import android.widget.TextView;

import me.liccon.fileexplorer.R;
import me.liccon.fileexplorer.fragments.FolderFragment;

public class ToolBar
{
    private final TextView folderName;

    public ToolBar(TextView textview)
    {
        this.folderName = textview;
    }

    public void update(FolderFragment fragment)
    {
        updateTitle(fragment.folderName());
    }

    public void update(String title)
    {
        updateTitle(title);
    }

    private void updateTitle(String text)
    {
        try
        {
            folderName.setText(text);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            folderName.setText(R.string.app_name);
        }
    }
}