package com.example.tourlist;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//확인용
public class MainActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Frag1_Login frag1_login;
    private Frag1_Register frag1_register;
    private Frag2_FavoriteList frag2_favoriteList;

    private Frag3_NaverMap frag3_NaverMap;
    private Frag4_Empty frag4_Empty;

    private FirebaseAuth mAuth;

    private boolean isUserInteraction = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // 최하단 네비게이션 바. 색상
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.darkblue));
        //

        frag1_login = new Frag1_Login();
        frag1_register = new Frag1_Register();
        frag2_favoriteList = new Frag2_FavoriteList();
        frag3_NaverMap = new Frag3_NaverMap();
        frag4_Empty = new Frag4_Empty();


        bottomNavigationView = findViewById((R.id.bottomNavi));
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                isUserInteraction = true;

                int n = menuItem.getItemId();
                if(n==R.id.action_account)
                        setFrag(0);

                if(n==R.id.action_memory)
                    setFrag(1);

                if(n==R.id.action_map)
                        setFrag(2);

                if(n==R.id.action_empty)
                        setFrag(3);

                return true;




            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (isUserInteraction) {
                    isUserInteraction = false;  // 플래그 초기화
                    return;  // 사용자가 직접 프래그먼트를 전환한 경우, 이후 처리를 생략
                }

                String tag=null;


                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_frame);

                if (currentFragment != null) {
                    // 프래그먼트 타입에 따라 캐스팅 후 커스텀 메서드 호출
                    if (currentFragment instanceof Frag1_Login) {
//                        Frag1_Login frag1 = (Frag1_Login) currentFragment;
//                        tag=frag1.getFragmentTag();
                        tag="Login";
                    } else if (currentFragment instanceof Frag2_FavoriteList) {
//                        Frag2_FavoriteList frag2= (Frag2_FavoriteList) currentFragment;
//                        frag2.customMethod2();
                        tag="Favorite";
                    } else if (currentFragment instanceof Frag3_NaverMap) {
//                        Frag3 frag3 = (Frag3) currentFragment;
//                        frag3.customMethod3();
                        tag="NaverMap";
                    }
                    else if (currentFragment instanceof Frag4_Empty) {
//                        Frag3 frag3 = (Frag3) currentFragment;
//                        frag3.customMethod3();
                        tag="Empty";
                    }


                    if (tag != null) {
                        if(tag.equals("Login")){
                            bottomNavigationView.setSelectedItemId(R.id.action_account);
                            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
                            Log.d("BackStack", "Current Back Stack1 Entry Count: " + backStackEntryCount);

                        }
                        if(tag.equals("Favorite")){
                            bottomNavigationView.setSelectedItemId(R.id.action_memory);
                            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
                            Log.d("BackStack", "Current Back Stack2 Entry Count: " + backStackEntryCount);



                        }
                        if(tag.equals("NaverMap")){
                            bottomNavigationView.setSelectedItemId(R.id.action_map);

                            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
                            Log.d("BackStack", "Current Back Stack 3Entry Count: " + backStackEntryCount);

                        }
                        if(tag.equals("Empty")){
                            bottomNavigationView.setSelectedItemId(R.id.action_empty);

                            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
                            Log.d("BackStack", "Current Back Stack4 Entry Count: " + backStackEntryCount);

                        }


                    }
                }
            }
        });
        if (savedInstanceState == null) {
//            ft.addToBackStack("Favorite");
        }



        setFrag(0); //첫 프래그먼트 화면을 무엇으로 지정해줄 것인지 선택
    }

    //프래그먼트 교체가 일어나는 실행문이다.
    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch(n){
            case 0:
                ft.replace(R.id.main_frame,frag1_login);
//                ft.replace(R.id.main_frame,frag1_login,"Login");
                // 백 스택에 추가합니다.
//                ft.addToBackStack(null);
//                ft.addToBackStack("Login");
                ft.commit();
                break;

            case 1:
                ft.replace(R.id.main_frame,frag2_favoriteList);
//                ft.replace(R.id.main_frame,frag2_favoriteList,"Favorite");
                // 백 스택에 추가합니다.
                ft.addToBackStack(null);
//                ft.addToBackStack("Favorite");
                ft.commit();
                break;


            case 2:
                ft.replace(R.id.main_frame, frag3_NaverMap);
//                ft.replace(R.id.main_frame, frag3_NaverMap,"NaverMap");
                // 백 스택에 추가합니다.
                ft.addToBackStack(null);
//                ft.addToBackStack("NaverMap");
                ft.commit();
                break;

            case 3:
                ft.replace(R.id.main_frame, frag4_Empty);
//                ft.replace(R.id.main_frame, frag4_Empty,"Empty");
                // 백 스택에 추가합니다.
                ft.addToBackStack(null);
//                ft.addToBackStack("Empty");
                ft.commit();
                break;
        }

    }







    @Override
    protected void onDestroy() {
        super.onDestroy();

        //로그인 상태면 앱 종료 시 로그아웃
        // 익명 로그인 상태면 그대로 둠.



        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && !user.isAnonymous()) {
            mAuth.signOut(); // 익명 계정이 아닌 경우에만 로그아웃
        }
    }




}