package io.wyrmise.jumpmanga.adapters;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.wyrmise.jumpmanga.R;
import io.wyrmise.jumpmanga.activities.DetailActivity;
import io.wyrmise.jumpmanga.database.JumpDatabaseHelper;
import io.wyrmise.jumpmanga.manga24hbaseapi.DownloadUtils;
import io.wyrmise.jumpmanga.model.Chapter;
import io.wyrmise.jumpmanga.model.Page;
import io.wyrmise.jumpmanga.service.DownloaderService;
import io.wyrmise.jumpmanga.utils.FileDownloader;
import io.wyrmise.jumpmanga.utils.FileUtils;
import io.wyrmise.jumpmanga.utils.NotificationUtils;

public class ChapterAdapter extends ArrayAdapter<Chapter> implements Filterable {
    private ArrayList<Chapter> chapters;
    private ArrayList<Chapter> temp;
    private SparseBooleanArray selectedItems;
    private JumpDatabaseHelper db;
    private Context context;
    private FileUtils fileUtils;
    Holder holder;


    public ChapterAdapter(Context c, int textViewResourceId, ArrayList<Chapter> list) {
        super(c, textViewResourceId);
        chapters = list;
        context = c;
        temp = new ArrayList<>(chapters);
        db = new JumpDatabaseHelper(context);
        fileUtils = new FileUtils();
        selectedItems = new SparseBooleanArray();
    }


    @Override
    public int getCount() {
        return chapters.size();
    }

    @Override
    public Chapter getItem(int position) {
        return chapters.get(position);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        View convertView1 = convertView;
        LayoutInflater vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView1 == null) {
            holder = new Holder();
            convertView1 = vi.inflate(R.layout.chapter_list_item, container,
                    false);
            holder.chapter_name = (TextView) convertView1.findViewById(R.id.name);
            holder.read_status = (ImageView) convertView1.findViewById(R.id.status_read);
            holder.fav_status = (CheckBox) convertView1.findViewById(R.id.favorite_box);
            holder.download_btn = (ImageView) convertView1.findViewById(R.id.download);

            convertView1.setTag(holder);

        } else {
            holder = (Holder) convertView1.getTag();
        }

        final Chapter chapter = getItem(position);

        holder.chapter_name.setText(chapter.getName());

        holder.fav_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox c = (CheckBox) view;
                if (!c.isChecked()) {
                    chapter.setIsFav(false);
                    db.unfavChapter(chapter, chapter.getMangaName().replaceAll("'", "''"));
                } else {
                    chapter.setIsFav(true);
                    db.favChapter(chapter, chapter.getMangaName().replaceAll("'", "''"));
                }
            }
        });

        if (chapter.isRead())
            holder.read_status.setVisibility(ImageView.VISIBLE);
        else holder.read_status.setVisibility(ImageView.INVISIBLE);

        if (chapter.isFav())
            holder.fav_status.setChecked(true);
        else holder.fav_status.setChecked(false);

        if (fileUtils.isChapterDownloaded(chapter.getMangaName(), chapter.getName()))
            holder.download_btn.setVisibility(View.INVISIBLE);
        else {
            holder.download_btn.setVisibility(View.VISIBLE);
            holder.download_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(context instanceof DetailActivity) {
                        String image = ((DetailActivity)context).getManga().getImage();
                        Intent intent = new Intent(context, DownloaderService.class);
                        intent.putExtra("image",image);
                        intent.putExtra("mangaName", chapter.getMangaName());
                        intent.putExtra("chapterName", chapter.getName());
                        intent.putExtra("chapterUrl", chapter.getUrl());
                        context.startService(intent);
                    }
                    holder.download_btn.setOnClickListener(null);
                }
            });
        }

        return convertView1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    chapters = (ArrayList<Chapter>) results.values;
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = temp;
                    results.count = temp.size();
                } else {
                    ArrayList<Chapter> filteredRowItems = new ArrayList<>();
                    chapters = temp;
                    for (Chapter c : chapters) {
                        if (c.getName().trim().toLowerCase().contains(constraint.toString().trim().toLowerCase())) {
                            filteredRowItems.add(c);
                        }
                    }
                    results.values = filteredRowItems;
                    results.count = filteredRowItems.size();
                }
                return results;
            }
        };
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedItems.get(position));
    }

    public void removeSelection() {
        selectedItems = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            selectedItems.put(position, value);
        else
            selectedItems.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedItems.size();
    }

    public SparseBooleanArray getSelectedItems() {
        return selectedItems;
    }


    private class Holder {
        public TextView chapter_name;
        public ImageView read_status;
        public CheckBox fav_status;
        public ImageView download_btn;
    }


}
