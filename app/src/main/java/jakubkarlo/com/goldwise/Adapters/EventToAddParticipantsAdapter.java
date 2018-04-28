package jakubkarlo.com.goldwise.Adapters;

import android.content.res.ColorStateList;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jakubkarlo.com.goldwise.Models.Person;
import jakubkarlo.com.goldwise.R;

/**
 * Created by Jakub on 18.01.2018.
 */

public class EventToAddParticipantsAdapter extends RecyclerView.Adapter {

    private ArrayList<Person> people;
    private RecyclerView participantsRecyclerView;


    private class ParticipantViewHolder extends RecyclerView.ViewHolder{

        TextView name, phoneNumber;
        LinearLayout background;

        public ParticipantViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.participantNameTextView);
            phoneNumber = (TextView) itemView.findViewById(R.id.participantPhoneTextView);
            background = (LinearLayout) itemView.findViewById(R.id.pariticpantsItemBg);
        }
    }


    public EventToAddParticipantsAdapter(ArrayList<Person> people, RecyclerView recyclerView) {
        this.people = people;
        this.participantsRecyclerView = recyclerView;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participants_list_item, parent, false);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int position = participantsRecyclerView.getChildAdapterPosition(v);
                        people.remove(position);
                        notifyItemRemoved(position);

                    }
                });

        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ParticipantViewHolder) holder).name.setText(people.get(position).getName());
        ((ParticipantViewHolder) holder). phoneNumber.setText(people.get(position).getPhoneNumber());
        ViewCompat.setBackgroundTintList(((ParticipantViewHolder) holder).background, (ColorStateList.valueOf(people.get(position).getColor())));
    }

    @Override
    public int getItemCount() {
        return people.size();
    }
}
