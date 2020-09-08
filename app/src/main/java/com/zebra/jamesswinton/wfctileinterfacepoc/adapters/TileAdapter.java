package com.zebra.jamesswinton.wfctileinterfacepoc.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.jamesswinton.wfctileinterfacepoc.R;
import com.zebra.jamesswinton.wfctileinterfacepoc.data.Config;
import com.zebra.jamesswinton.wfctileinterfacepoc.databinding.LayoutTileBinding;

import java.util.List;

public class TileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Debugging
    private static final String TAG = "TileAdapter";

    // ViewHolders
    private static final int EMPTY_VIEW_HOLDER = 0;
    private static final int TILE_VIEW_HOLDER = 1;

    // Data
    private Context mContext;
    private List<Config.Tile> mTiles;
    private OnTileClickedListener mOnTileClickedListener;

    public TileAdapter(Context context, List<Config.Tile> tiles,
                       OnTileClickedListener onTileClickedListener) {
        this.mContext = context;
        this.mTiles = tiles;
        this.mOnTileClickedListener = onTileClickedListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case EMPTY_VIEW_HOLDER:
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tiles_empty, parent));
            case TILE_VIEW_HOLDER:
                return new TileViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.layout_tile, parent, false));
            default:
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tiles_empty, parent));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        // Get Current Tile
        Config.Tile tile = mTiles.get(position);

        if (viewHolder instanceof TileViewHolder) {
            // Cast
            TileViewHolder holder = (TileViewHolder) viewHolder;

            // Set Tile Values
            holder.mTileDataBinding.text.setText(tile.getUiConfig().getText());
            holder.mTileDataBinding.text.setTextSize(tile.getUiConfig().getTextSize());
            holder.mTileDataBinding.text.setTextColor(Color.parseColor(tile.getUiConfig().getTextColour()));
            holder.mTileDataBinding.baseView.setCardBackgroundColor(Color.parseColor(
                    tile.getUiConfig().getBackgroundColour()));
            holder.mTileDataBinding.baseView.setOnClickListener(view -> {
                if (validateTileIwgData(tile)) {
                    mOnTileClickedListener.onTileClicked(tile);
                } else {
                    Toast.makeText(mContext, "Tile has invalid IWG settings", Toast.LENGTH_LONG).show();
                }
            });
            if (tile.getIwgConfig().getAttachmentType() != null
                    && !TextUtils.isEmpty(tile.getIwgConfig().getAttachmentType())) {
                switch (tile.getIwgConfig().getAttachmentType()) {
                    case "AUDIO": {
                        holder.mTileDataBinding.icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_audio));
                        break;
                    }
                    case "VIDEO": {
                        holder.mTileDataBinding.icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_video));
                        break;
                    }
                    case "IMAGE": {
                        holder.mTileDataBinding.icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_image));
                        break;
                    }
                    default: {
                        holder.mTileDataBinding.icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_message));
                        break;
                    }
                }
            } else {
                holder.mTileDataBinding.icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_message));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mTiles.size() == 0 ? 1 : mTiles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTiles == null || mTiles.isEmpty() ? EMPTY_VIEW_HOLDER : TILE_VIEW_HOLDER;
    }

    public void updateTiles(List<Config.Tile> tiles) {
        this.mTiles = tiles;
        notifyDataSetChanged();
    }

    private boolean validateTileIwgData(Config.Tile tile) {
        if (tile.getIwgConfig() == null) {
            return false;
        }

        if (tile.getIwgConfig().getAttachmentType() != null &&
                (!tile.getIwgConfig().getAttachmentType().equalsIgnoreCase("AUDIO")
                        && !tile.getIwgConfig().getAttachmentType().equalsIgnoreCase("IMAGE")
                        && !tile.getIwgConfig().getAttachmentType().equalsIgnoreCase("VIDEO"))) {
            return false;
        }

        if (tile.getIwgConfig().getAttachmentType() != null
                && (tile.getIwgConfig().getAttachmentType().equalsIgnoreCase("AUDIO") ||
                tile.getIwgConfig().getAttachmentType().equalsIgnoreCase("IMAGE") ||
                tile.getIwgConfig().getAttachmentType().equalsIgnoreCase("VIDEO"))
                && (tile.getIwgConfig().getFilePath() == null
                || TextUtils.isEmpty(tile.getIwgConfig().getFilePath()))) {
            return false;
        }

        if (tile.getIwgConfig().getEid().length < 1) {
            return false;
        }

        if (tile.getIwgConfig().getMessage() == null) {
             return false;
        }

        return true;

    }

    public static class TileViewHolder extends RecyclerView.ViewHolder {
        public LayoutTileBinding mTileDataBinding;
        public TileViewHolder(@NonNull LayoutTileBinding itemView) {
            super(itemView.getRoot());
            mTileDataBinding = itemView;
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface OnTileClickedListener {
        void onTileClicked(Config.Tile tile);
    }

}
