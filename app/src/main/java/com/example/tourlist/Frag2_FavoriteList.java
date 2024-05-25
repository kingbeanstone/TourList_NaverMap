package com.example.tourlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Frag2_FavoriteList extends Fragment {
    private String fragmentTag="Favorite";

    public void setFragmentTag(String tag) {
        this.fragmentTag = tag;
    }

    public String getFragmentTag() {
        return fragmentTag;
    }

    private View view;

    private ListView favoriteListView;
    private ArrayAdapter<String> adapter;
    private List<FavoriteLocation> favoriteLocations;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2_favorite_list,container,false);

        favoriteListView = view.findViewById(R.id.favorite_list);
        favoriteLocations = new ArrayList<>();
        List<String> favoriteList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, favoriteList);
        favoriteListView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");
            loadFavoriteLocations();
        } else {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            // 로그인하지 않은 경우 이전 프레그먼트로 돌아감. 사실상 즐겨찾기 목록 종료
        }

        // 항목을 클릭했을 때 수정
        favoriteListView.setOnItemClickListener((parent, view, position, id) -> {
            FavoriteLocation location = favoriteLocations.get(position);
            showEditDialog(location);
        });

        // 항목을 길게 눌렀을 때 삭제
        favoriteListView.setOnItemLongClickListener((parent, view, position, id) -> {
            FavoriteLocation location = favoriteLocations.get(position);
            removeFavoriteLocation(location);
            return true;
        });

        return view;
    }

    private void loadFavoriteLocations() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteLocations.clear();
                List<String> favoriteList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FavoriteLocation location = snapshot.getValue(FavoriteLocation.class);
                    if (location != null) {
                        location.setKey(snapshot.getKey());
                        favoriteLocations.add(location);
                        favoriteList.add(location.toString());
                    }
                }
                adapter.clear();
                adapter.addAll(favoriteList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void removeFavoriteLocation(FavoriteLocation location) {
        if (location.getKey() != null) {
            mDatabase.child(location.getKey()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "즐겨찾기에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private void showEditDialog(FavoriteLocation location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("장소 이름 수정");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(location.getName());
        builder.setView(input);

        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                location.setName(name);
                updateFavoriteLocation(location);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateFavoriteLocation(FavoriteLocation location) {
        if (location.getKey() != null) {
            mDatabase.child(location.getKey()).setValue(location).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "장소 이름이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    loadFavoriteLocations();
                } else {
                    Toast.makeText(getContext(), "수정 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}






