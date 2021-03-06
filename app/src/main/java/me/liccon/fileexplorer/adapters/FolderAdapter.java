package me.liccon.fileexplorer.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.liccon.fileexplorer.R;
import me.liccon.fileexplorer.adapters.FolderAdapter.ViewHolder;
import me.liccon.fileexplorer.base.BaseListAdapter;
import me.liccon.fileexplorer.models.FileInfo;
import me.liccon.fileexplorer.utils.ThumbnailLoader;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends BaseListAdapter<FileInfo, ViewHolder>
{
    private int itemsSelected = 0;
    private final ThumbnailLoader thumbnailLoader;

    public FolderAdapter(Context context)
    {
        super(context, R.layout.row_file);

        this.thumbnailLoader = new ThumbnailLoader(context.getResources());
    }

    @Override
    protected ViewHolder viewHolder(View view)
    {
        return new ViewHolder(view);
    }

    @Override
    protected void fillView(View rowView, ViewHolder viewHolder, FileInfo fileInfo)
    {
        viewHolder.name.setText(fileInfo.name());

        if (fileInfo.isDirectory())
        {
            int numberOfChildren = fileInfo.numberOfChildren();

            viewHolder.size.setText(getContext().getResources().getQuantityString(R.plurals.itemAmount, numberOfChildren, numberOfChildren));

            viewHolder.icon.setImageResource(R.drawable.ic_folder);
            viewHolder.extension.setText(null);
            viewHolder.extension.setBackgroundResource(android.R.color.transparent);
        }
        else
        {
            viewHolder.size.setText(fileInfo.size());

            if (fileInfo.isImage())
            {
                thumbnailLoader.load(fileInfo, viewHolder.icon);
                viewHolder.extension.setText(null);
                viewHolder.extension.setBackgroundResource(android.R.color.transparent);
            }
            else if (fileInfo.isPdf())
            {
                viewHolder.icon.setImageResource(R.drawable.ic_pdf);
                viewHolder.extension.setText(null);
                viewHolder.extension.setBackgroundResource(android.R.color.transparent);
            }
            else if (fileInfo.isAudio())
            {
                viewHolder.icon.setImageResource(R.drawable.ic_audio);
                viewHolder.extension.setText(null);
                viewHolder.extension.setBackgroundResource(android.R.color.transparent);
            }
            else if (fileInfo.isVideo())
            {
                viewHolder.icon.setImageResource(R.drawable.ic_video);
                viewHolder.extension.setText(null);
                viewHolder.extension.setBackgroundResource(android.R.color.transparent);
            }
            else
            {
                viewHolder.icon.setImageResource(R.drawable.ic_file);

                String extension = fileInfo.extension();

                if (!extension.isEmpty())
                {
                    viewHolder.extension.setText(extension);
                    viewHolder.extension.setBackgroundResource(R.drawable.extension_border);

                    if (extension.length() <= 3)
                    {
                        viewHolder.extension.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
                    }
                    else
                    {
                        viewHolder.extension.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
                    }
                }
                else
                {
                    viewHolder.extension.setText(null);
                    viewHolder.extension.setBackgroundResource(android.R.color.transparent);
                }
            }
        }

        if (fileInfo.isSelected())
        {
            rowView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray4));
        }
        else
        {
            rowView.setBackgroundColor(0);
        }
    }

    public void updateSelection(boolean itemAdded)
    {
        notifyDataSetChanged();

        itemsSelected += itemAdded ? 1 : -1;
    }

    public void setData(List<FileInfo> list)
    {
        update(list);
        unselectAll();
    }

    public void unselectAll()
    {
        for (int i = 0; i < getCount(); i++)
        {
            getItem(i).select(false);
        }

        itemsSelected = 0;
        notifyDataSetChanged();
    }

    public void selectAll()
    {
        for (int i = 0; i < getCount(); i++)
        {
            getItem(i).select(true);
        }

        itemsSelected = getCount();
        notifyDataSetChanged();
    }

    public boolean isSelectionMode()
    {
        return itemsSelected > 0;
    }

    public int itemsSelected()
    {
        return itemsSelected;
    }

    public boolean allItemsSelected()
    {
        return itemsSelected == getCount();
    }

    public List<FileInfo> selectedItems(boolean onlyFiles)
    {
        List<FileInfo> list = new ArrayList<>();

        for (int i = 0; i < getCount(); i++)
        {
            FileInfo fileInfo = getItem(i);

            if (fileInfo.isSelected())
            {
                if (onlyFiles)
                {
                    list.addAll(fileInfo.files());
                }
                else
                {
                    list.add(fileInfo);
                }
            }
        }

        return list;
    }

    public boolean hasFiles()
    {
        for (int i = 0; i < getCount(); i++)
        {
            FileInfo fileInfo = getItem(i);

            if (fileInfo.isSelected())
            {
                if (fileInfo.hasFiles())
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected static class ViewHolder
    {
        public final TextView name;
        public final TextView size;
        public final TextView extension;
        public final ImageView icon;

        public ViewHolder(View view)
        {
            this.name = (TextView) view.findViewById(R.id.name);
            this.size = (TextView) view.findViewById(R.id.size);
            this.extension = (TextView) view.findViewById(R.id.extension);
            this.icon = (ImageView) view.findViewById(R.id.icon);
        }
    }
}