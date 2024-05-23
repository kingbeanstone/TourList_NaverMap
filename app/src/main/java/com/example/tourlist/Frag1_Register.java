package com.example.tourlist;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Frag1_Register extends Fragment {

    private View view;


    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증.
    //이것만으로도 회원가입은 구현 가능. 근데 데이터베이스로 관리해야...

    private DatabaseReference mDatabaseReference; //실시간 데이터베이스. 서버연동.
    private EditText mEtEmail, mEtPwd;
    private Button mBtnRegister; //회원가입 버튼



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1_register,container,false);



        mFirebaseAuth=FirebaseAuth.getInstance();//google
        mDatabaseReference= FirebaseDatabase.getInstance().getReference("hongdroid");//실시간 데이터베이스.=Fireb
        //앱이름 보통 넣어준다. 근데 길어서 hongdroid로 넣어준다..

        mEtEmail=view.findViewById(R.id.et_email);
        mEtPwd=view.findViewById(R.id.et_pwd);
        mBtnRegister=view.findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 처리 시작
                String email = mEtEmail.getText().toString(); //사용자가 입력한 값을 가져온다. 문자열로 변환
                String pwd = mEtPwd.getText().toString();

                //Firebase Auth 진행. 회원가입.
                mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();//로그인 성공시. 현재의 유저로 가져온다.
                            UserAccount account = new UserAccount();

                            account.setIdToken(user.getUid());// 고유값이다.. 정도로 이해ㅣ.
                            account.setEmailId(user.getEmail()); // 로그인 성공했으니,email 적어도 되지 않나..?
                            account.setPassword(pwd);

                            //setValue: database에 insert삽입 행위.
                            mDatabaseReference.child("UserAccount").child(user.getUid()).setValue(account);

                            Toast.makeText(getContext(), "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();


                            sendEmailVerification(user);

                        } else {
                            Toast.makeText(getContext(), "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();

                        }
                    }


                });
            }
        });



        Button btn_login=view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Frag1_Login frag1_login = new Frag1_Login();
                //main_layout에 homeFragment로 transaction 한다.
                transaction.replace(R.id.main_frame, frag1_login);
                // 백 스택에 추가합니다.
                transaction.addToBackStack(null);

                //꼭 commit을 해줘야 바뀐다.
                transaction.commit();
            }
        });



        return view;
    }

    private void sendEmailVerification(FirebaseUser user){
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("TAG", "sendEmailVerification", task.getException());
                            Toast.makeText(getContext(),
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }


    }


}
