package com.example.tourlist;

/*

사용자 계정 정보 모델 클래스
*/
public class UserAccount {

//    getter, setter.... alt+insert.

    private String idToken; // Firebase Uid(고유 토큰정보)... 유일하게 가질 수있는 키값

    private String emailId; // 이메일 아이디
    private String password;// 비밀번호


    public UserAccount() {
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
//    빈 생성자 적엉줘야함.

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
