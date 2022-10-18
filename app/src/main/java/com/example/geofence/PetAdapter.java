package com.example.geofence;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    List<Pet> petList;
    Context context;
    String IP;

    UserApplication userApplication;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    // Delete Alert Dialog
    AlertDialog.Builder confirmDelete;

    public PetAdapter(List<Pet> petList, Context context) {
        this.petList = petList;
        this.context = context;
        // Delete Alert Dialog
        confirmDelete = new AlertDialog.Builder(context);
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_layout_view, parent, false);
        PetViewHolder holder = new PetViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        /* Add image from database */
        holder.petPic.setImageResource(R.drawable.ic_baseline_pets_24);
        holder.petName.setText(petList.get(position).getPetName());
        IP = petList.get(position).getPetCameraIP();
        holder.petIP.setText(IP);
//        databaseReference.child("Trackers").child(petList.get(position).getPetTrackerID().toString()).child("isActive").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.getValue(Boolean.class)){
//                    holder.petStatus.setText("Active");
//                }
//                else{
//                    holder.petStatus.setText("Inactive");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public class PetViewHolder extends RecyclerView.ViewHolder{
        ImageView petPic;
        TextView petName;
        TextView petIP;
        // Button bRequestFeed;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);

            petPic = itemView.findViewById(R.id.imageView);
            petName = itemView.findViewById(R.id.pName_Account);
            petIP = itemView.findViewById(R.id.pIPAddress);
            itemView.findViewById(R.id.pCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context.getApplicationContext(), "Go to activity to make request", Toast.LENGTH_SHORT).show();
                    Log.i("Yo", "Pet IP: " + petIP.getText());
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("http://"+ petIP.getText()));
                    context.startActivity(intent);
                }
            });
            itemView.findViewById(R.id.adapter_CloseButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Toast.makeText(context.getApplicationContext(), "Delete from database", Toast.LENGTH_SHORT).show();
                    //Log.i("Yo", "PetRef: " + databaseReference.child("Users").child(mUser.getUid()).child("Pets").orderByChild("petName").equalTo(petName.getText().toString()).getRef().getRef());
                    //Log.i("Yo", "PetRef: " + petName.getText().toString());

                    // Alert Dialog
                    confirmDelete.setTitle("Delete " + petName.getText() + "?");
                    confirmDelete.setMessage("Are you sure you want to delete your pet " + petName.getText() + "? You will not be able to undo this action.");
                    confirmDelete.setCancelable(true);

                    confirmDelete.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Delete pet
                                    databaseReference.child("Users").child(mUser.getUid()).child("Pets").orderByChild("petName").equalTo(petName.getText().toString()).getRef()
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        Log.i("Yo", dataSnapshot.child("petName").getValue().toString());
                                                        if(dataSnapshot.child("petName").getValue().toString().equals(petName.getText().toString())) {
                                                            dataSnapshot.getRef().removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                    dialogInterface.cancel();
                                }
                            }
                    );

                    confirmDelete.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }
                    );

                    AlertDialog alertDelete = confirmDelete.create();
                    alertDelete.show();
                }
            });
        }
    }
}
