package com.example.disc;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disc.activities.SpecificUser;
import com.example.disc.models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private static FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    final private ArrayList<Users> mArrayOfUsers = new ArrayList<>();
    private  Users users;
    public UsersAdapter(Context context){
        DatabaseReference ref = mFirebaseDatabase.getReference("user_details");
        Query query  = ref.orderByChild("mFullName");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot ds: children) {
                    users = ds.getValue(Users.class);
                    users.setmId(ds.getKey());
                    assert users != null;
                    Log.d(TAG, "onDataChange: "+ users.getmFullName()+users.getmDOB());
                    mArrayOfUsers.add(users);
                }
                notifyItemInserted(mArrayOfUsers.size()-1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    @NonNull
    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_layout,parent,false);
        Log.d(TAG,"****************************onCreateViewHolder Called********************************");
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UserViewHolder holder, int position) {
        Users users = mArrayOfUsers.get(position);
        holder.bind(users,holder);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mArrayOfUsers.size());
        return mArrayOfUsers.size();
    }

    public class UserViewHolder  extends  RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userName;
        TextView userEmail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.name_field);
            userEmail = itemView.findViewById(R.id.email_field);
            itemView.setOnClickListener(this); }

        public void bind(Users user, UserViewHolder holder) {
            String name = user.getmFullName();
            userName.setText(name);
            userEmail.setText(user.getmEmail());
            Log.d("Bind Method","********************On Bind Method Started***************************");
        }

        @Override
        public void onClick(View view) {
            //Context context = view.getContext();
            int position = getAdapterPosition();
            Log.d(TAG, "onClick: "+ position);
            Users selectedGuesses  = mArrayOfUsers.get(position);
            Intent intent = new Intent(view.getContext(), SpecificUser.class);
            intent.putExtra("users",selectedGuesses);
            view.getContext().startActivity(intent);

        }
    }
}
