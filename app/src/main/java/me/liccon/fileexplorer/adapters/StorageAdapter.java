package me.liccon.fileexplorer.adapters;

import android.content.Context;
import android.os.StatFs;
import android.view.View;
import android.widget.TextView;

import me.liccon.fileexplorer.R;
import me.liccon.fileexplorer.adapters.StorageAdapter.ViewHolder;
import me.liccon.fileexplorer.base.BaseListAdapter;
import me.liccon.fileexplorer.utils.SpaceFormatter;

public class StorageAdapter extends BaseListAdapter<String, ViewHolder>
{
    public StorageAdapter(Context context)
    {
        super(context, R.layout.row_storage);
    }

    @Override
    protected ViewHolder viewHolder(View view)
    {
        return new ViewHolder(view);
    }

    @Override
    protected void fillView(View rowView, ViewHolder viewHolder, String item)
    {
        viewHolder.name.setText(item);

        StatFs stat = new StatFs(item);
        long blockSize = (long) stat.getBlockSize();
        long totalSpace = blockSize * stat.getBlockCount();
        long availableSpace = blockSize * stat.getAvailableBlocks();

        SpaceFormatter spaceFormatter = new SpaceFormatter();

        String labelTotal = getContext().getString(R.string.space_total);
        String total = spaceFormatter.format(totalSpace);
        String labelAvailable = getContext().getString(R.string.space_available);
        String available = spaceFormatter.format(availableSpace);
        viewHolder.space.setText(String.format("%s: %s     %s: %s", labelTotal, total, labelAvailable, available));
    }

    protected static class ViewHolder
    {
        public final TextView name;
        public final TextView space;

        public ViewHolder(View view)
        {
            this.name = (TextView) view.findViewById(R.id.name);
            this.space = (TextView) view.findViewById(R.id.space);
        }
    }
}