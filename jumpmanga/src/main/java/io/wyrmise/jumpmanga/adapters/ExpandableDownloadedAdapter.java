package io.wyrmise.jumpmanga.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.wyrmise.jumpmanga.R;
import io.wyrmise.jumpmanga.activities.DownloadedReadActivity;
import io.wyrmise.jumpmanga.model.Chapter;
import io.wyrmise.jumpmanga.model.Wrapper;

public class ExpandableDownloadedAdapter
        extends ExpandableRecyclerAdapter<ExpandableDownloadedAdapter.ParentViewHolder, ExpandableDownloadedAdapter.ChildViewHolder> {

    private LayoutInflater mInflater;

    public ExpandableDownloadedAdapter(Context context, List<ParentObject> itemList) {
        super(context, itemList);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.list_item_parent, viewGroup, false);
        return new ParentViewHolder(view);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.list_item_child, viewGroup, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(ParentViewHolder viewHolder, int i, Object o) {
        Wrapper wrapper = (Wrapper) o;
        viewHolder.mangaName.setText(wrapper.getName());
        Picasso.with(mContext).load(wrapper.getImagePath()).error(R.drawable.error).into(viewHolder.mangaThumbnail);
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder viewHolder, int i, Object o) {
        final Chapter chapter = (Chapter) o;
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DownloadedReadActivity.class);
                intent.putExtra("chapter_name", chapter.getName());
                intent.putStringArrayListExtra("image_path", chapter.getPath());
                mContext.startActivity(intent);
            }
        });
        viewHolder.chapterName.setText(chapter.getName());
    }


    public class ParentViewHolder extends com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder {
        public TextView mangaName;
        public ImageView mangaThumbnail;

        public ParentViewHolder(View view) {
            super(view);
            mangaName = (TextView) view.findViewById(R.id.mangaName);
            mangaThumbnail = (ImageView) view.findViewById(R.id.mangaThumbnail);
        }
    }

    public class ChildViewHolder extends com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder {
        public TextView chapterName;
        public View view;

        public ChildViewHolder(View view) {
            super(view);
            this.view = view;
            chapterName = (TextView) view.findViewById(R.id.chapterName);
        }
    }
}
