package jakubkarlo.com.goldwise.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import jakubkarlo.com.goldwise.Activities.SpecificEventActivity;
import jakubkarlo.com.goldwise.R;

/**
 * Created by Jakub on 07.11.2017.
 */

public class EventAdapter extends RecyclerView.Adapter {
    private List<ParseObject> events;
    private Bitmap eventImage;
    private RecyclerView eventsRecyclerView;

    private class EventViewHolder extends RecyclerView.ViewHolder{

        ImageView eventImageView;
        TextView eventTitle;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventImageView = (ImageView) itemView.findViewById(R.id.grid_item_image);
            eventTitle = (TextView) itemView.findViewById(R.id.grid_item_label);
        }
    }


    public EventAdapter(List<ParseObject> events, RecyclerView recyclerView) {
        this.eventsRecyclerView = recyclerView;
        this.events = events;

    }

//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View gridView;
//
//
//        if (convertView == null) {
//
//            // get layout from mobile.xml
//            gridView = inflater.inflate(R.layout.events_grid_item, null);
//
//            // set value into textview
//            TextView textView = (TextView) gridView
//                    .findViewById(R.id.grid_item_label);
//            textView.setText(events.get(position).get("title").toString());
//
//            // set image based on selected text
//            final ImageView imageView = (ImageView) gridView
//                    .findViewById(R.id.grid_item_image);
//
//            // code to obtain image YOU'LL NEED PHOTO COMPRESSION
//            ParseFile file = (ParseFile)events.get(position).get("image");
//
//            file.getDataInBackground(new GetDataCallback() {
//                @Override
//                public void done(byte[] data, ParseException e) {
//
//                    if (e == null & data != null){
//
//                        eventImage = BitmapFactory.decodeByteArray(data,0, data.length);
//                        imageView.setImageBitmap(eventImage);
//
//                    }
//
//                }
//            });
//
//
//
//        } else {
//            gridView = (View) convertView;
//        }
//
//        return gridView;
//    }

//    @Override
//    public int getCount() {
//        return events.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return events.get(position);
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.events_grid_item, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ParseObject event = events.get(position);
        ((EventViewHolder) holder).eventTitle.setText(event.getString("title"));
        ParseFile file = (ParseFile)events.get(position).get("image");

        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {

                if (e == null & data != null){

                    eventImage = BitmapFactory.decodeByteArray(data,0, data.length);
                    ((EventViewHolder) holder).eventImageView.setImageBitmap(eventImage);

                }

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SpecificEventActivity.class);
                intent.putExtra("eventID", events.get(eventsRecyclerView.getChildAdapterPosition(v)).getObjectId());
                v.getContext().startActivity(intent);
            }

        });

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

}