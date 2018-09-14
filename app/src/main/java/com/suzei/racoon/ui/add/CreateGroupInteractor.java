package com.suzei.racoon.ui.add;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suzei.racoon.R;
import com.suzei.racoon.model.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CreateGroupInteractor {

    private Context context;

    private CreateGroupContract.CreateGroupListener createGroupListener;

    CreateGroupInteractor(Context context, CreateGroupContract.CreateGroupListener createGroupListener) {
        this.createGroupListener = createGroupListener;
        this.context = context;
    }

    public void createGroup(ArrayList<Users> members) {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("groups");
        String groupId = groupRef.push().getKey();
        String currentUserId = FirebaseAuth.getInstance().getUid();
        String defaultImage = context.getResources().getString(R.string.default_user_image);

        HashMap<String, Object> groupDetailsMap = new HashMap<>();
        groupDetailsMap.put("name", getDefaultName());
        groupDetailsMap.put("admin/" + currentUserId, true);
        groupDetailsMap.put("description", "Newly created group");
        groupDetailsMap.put("image", defaultImage);
        groupDetailsMap.put("members", getMembers(currentUserId, members));

        groupRef.child(groupId).updateChildren(groupDetailsMap).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                createGroupListener.onCreateSuccess(groupId);
            } else {
                //TODO Handle errors
            }

        });
    }

    private String getDefaultName() {
        Random random = new Random(System.currentTimeMillis());
        int randonNumber = 10000 + random.nextInt(20000);
        return "Group" + randonNumber;
    }

    private HashMap<String, Object> getMembers(String currentUserId, ArrayList<Users> members) {
        HashMap<String, Object> membersMap = new HashMap<>();
        for (int i = 0; i < members.size(); i++) {
            Users users = members.get(i);
            membersMap.put(users.getUid(), true);
        }
        membersMap.put(currentUserId, true);
        return membersMap;
    }
}
