package com.example.tourlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Frag1_Login extends Fragment {

    private View view;

    private FirebaseAuth mAuth; //파이어베이스 인증.
    //이것만으로도 회원가입은 구현 가능. 근데 데이터베이스로 관리해야...

    private DatabaseReference mDatabaseReference; //실시간 데이터베이스. 서버연동.
    private EditText mEtEmail, mEtPwd; // 로그인 입력필드

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1_login,container,false);


        mAuth = FirebaseAuth.getInstance();//google
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("hongdroid");//실시간 데이터베이스.=Fireb
        // 앱이름 보통 넣어준다. 근데 길어서 hongdroid로 넣어준다..

        mEtEmail = view.findViewById(R.id.et_email);
        mEtPwd = view.findViewById(R.id.et_pwd);


        Button btn_login = view.findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 로그인 요청.
                String email = mEtEmail.getText().toString(); //사용자가 입력한 값을 가져온다. 문자열로 변환
                String pwd = mEtPwd.getText().toString();

                mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    //로그인 성공
//


                                    Toast.makeText(getContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
//

                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    Frag3_NaverMap frag3_NaverMap = new Frag3_NaverMap();
                                    //main_layout에 homeFragment로 transaction 한다.
                                    transaction.replace(R.id.main_frame, frag3_NaverMap);
                                    //꼭 commit을 해줘야 바뀐다.
                                    transaction.commit();


                                }
                                else{
                                    //로그인 실패
                                    Toast.makeText(getContext(), "로그인 실패", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                );

            }



        });

        Button btn_register=view.findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Frag1_Register frag1_register = new Frag1_Register();
                //main_layout에 homeFragment로 transaction 한다.
                transaction.replace(R.id.main_frame, frag1_register);
                //꼭 commit을 해줘야 바뀐다.
                transaction.commit();
            }
        });



        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }



}


