package com.example.stickynotes;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.LinkedList;

public class SectionAdapter extends
    RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {

    public NoteData DH;
    private static SQLiteDatabase db;

    private LinkedList<String> titleList;
    private LinkedList<String> contentList;
    private LayoutInflater sectionInflater;
    private Context context;
    private String mColor = "Light";

    // view holder

    class SectionViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnCreateContextMenuListener {
        public TextView sectionTitleText;
        public TextView sectionContentText;
        final SectionAdapter sectionAdapter;

        public SectionViewHolder(View itemView, SectionAdapter adapter) {
            super(itemView);
            sectionTitleText = itemView.findViewById(R.id.sectionTitleText);
            sectionContentText = itemView.findViewById(R.id.sectionContentText);
            if(adapter.getColor() == "Dark"){
                sectionTitleText.setBackgroundColor(context.getResources().getColor(R.color.dColorSection));
                sectionContentText.setBackgroundColor(context.getResources().getColor(R.color.dColorSection));
                sectionTitleText.setTextColor(context.getResources().getColor(R.color.dColorPrimaryDark));
                sectionContentText.setTextColor(context.getResources().getColor(R.color.dColorPrimaryDark));
            }
            this.sectionAdapter = adapter;
            itemView.setOnClickListener(this);
            // itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            String currentTitle = titleList.get(getAdapterPosition());
            String currentContent = contentList.get(getAdapterPosition());
            Intent noteIntent = new Intent(context, NoteActivity.class);
            noteIntent.putExtra("title", currentTitle);
            noteIntent.putExtra("content", currentContent);
            noteIntent.putExtra("color", mColor);
            noteIntent.putExtra("dbmode", "update");
            noteIntent.putExtra("id", String.valueOf(getLayoutPosition()));
            context.startActivity(noteIntent);
        }

        // @Override
        // public boolean onLongClick(View view){
        //     String id = String.valueOf(getLayoutPosition());
        //     itemView.setTag("del");
        //     DH.delete(id);
        //     sectionAdapter.remove(getLayoutPosition());
        //     return true;
        // }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem delete = menu.add(0, 0, 0, "刪除");
            delete.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 0:
                        String id = String.valueOf(getLayoutPosition());
                        DH.delete(id);
                        sectionAdapter.remove(getLayoutPosition());
                        break;
                }
                return true;
            }
        };
    }

    // adapter

    public SectionAdapter(Context context, LinkedList<String> titleList, LinkedList<String> contentList, String color){
        db = DH.getDatabase(context);
        sectionInflater = LayoutInflater.from(context);
        this.titleList = titleList;
        this.contentList = contentList;
        this.context = context;
        mColor = color;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View sectionView = sectionInflater.inflate(R.layout.note_section, viewGroup, false);
        return new SectionViewHolder(sectionView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder sectionViewHolder, int i) {
        String currentTitle = titleList.get(i);
        String currentContent = contentList.get(i);

        sectionViewHolder.sectionTitleText.setText(currentTitle);
        sectionViewHolder.sectionContentText.setText(currentContent);
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public String getColor(){
        return mColor;
    }

    public void remove(int position) {
        titleList.remove(position);
        contentList.remove(position);

        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
}
