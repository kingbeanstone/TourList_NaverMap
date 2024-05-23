package com.example.tourlist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;



import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;



import com.naver.maps.map.util.FusedLocationSource;








import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;




import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naver.maps.map.util.FusedLocationSource;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Frag3_NaverMap extends Fragment implements OnMapReadyCallback {


    private View view;

    MapView mapView;
    private NaverMap mMap;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;





    private Button btnCurrentLocation;
    private Location currentLocation;

    private static final String TAG = "Frag3_GoogleMap";


    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private LatLng selectedLocation;


    private Marker currentMarker;
    private Marker selectedMarker;
    private Marker poiMarker;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3_navermap,container,false);



        //지도 출력
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);




        //즐겨찾기 추가 버튼
        Button favoriteButton = view.findViewById(R.id.btn_fav);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMarker != null) {
                    // 데이터베이스에 위도 경도 추가 함수...
                    addFavoriteLocation(selectedMarker.getCaptionText(),selectedMarker.getPosition().latitude,selectedMarker.getPosition().longitude);
                } else {
                    Toast.makeText(getContext(), "먼저 마커를 클릭하여 위치를 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });



/*

        //예림 코드. 이거 뭔지 몰겠 jhj
        //loadSelectedPlaces();
//        getCurrentLocation();

        //현재 위치로 버튼.
        Button btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation);
        btnCurrentLocation.setOnClickListener(v -> {
            if (currentLocation != null) {
                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        });
        //

        // jhj...
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //로그인 안했으면 익명 로그인
        if (user == null) {

            mAuth.signInAnonymously().addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    // 익명 로그인 성공
                    Toast.makeText(getContext(), "게스트 로그인 성공", Toast.LENGTH_SHORT).show();
                } else {
                    // 익명 로그인 실패
                    Toast.makeText(getContext(), "게스트 로그인 실패", Toast.LENGTH_SHORT).show();
                }
            });

        }
        //

        //버튼들

        //즐겨찾기 추가 버튼
        Button favoriteButton = view.findViewById(R.id.btn_fav);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMarker != null) {
                    // 데이터베이스에 위도 경도 추가 함수...
                    addFavoriteLocation(selectedMarker.getCaptionText(),selectedMarker.getPosition().latitude,selectedMarker.getPosition().longitude);
                } else {
                    Toast.makeText(getContext(), "먼저 마커를 클릭하여 위치를 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        return view;

    }



        @Override
    public void onMapReady(@NonNull NaverMap map){

        mMap = map;

        Log.d(TAG, "GoogleMap is ready");

        /// 이거 왜 권한 요청 안뜨냐?/?
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        //지도 시작시, 현재 위치로.

        map.setLocationSource(locationSource);
        map.setLocationTrackingMode(LocationTrackingMode.Follow);

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        // POI 설정
        setupPOI();



        // 권한확인. 결과는 onRequestPermissionsResult 콜백 매서드 호출



        mMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                // 이전에 생성된 마커가 있으면 제거
                if (currentMarker != null && selectedMarker!=currentMarker) {
                    currentMarker.setMap(null);
                }

                // 이전에 생성된 POI 마커가 있다면 제거합니다.
                if (poiMarker != null) {
                    poiMarker.setMap(null);
                }

                //선택한 위치에 파란 마커 생성및, 다시 클릭시 '선택된 위치'라고 박스 뜸.

                currentMarker=new Marker();
                currentMarker.setPosition(latLng);
                currentMarker.setCaptionText("선택된 위치");
                currentMarker.setIconTintColor(0x478EEC);
                currentMarker.setMap(mMap);



            }



        });



        // 전체 마커 클릭 리스너를 설정
/*        mMap.setOnClickListener(new NaverMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    selectedMarker = marker;

                    if(currentMarker!=null ){
                        String name=currentMarker.getTitle();
                        if(!selectedMarker.getTitle().equals(name)) {
                            currentMarker.remove();
                            currentMarker
                        }
                    }
                    // 이전에 생성된 POI 마커가 있다면 제거합니다.
//                    if (poiMarker != null) {
//                        poiMarker.remove();
//                    }


                    String name = marker.getTitle();
//                    LatLng position = marker.getPosition();
//                    double latitude = position.latitude;
//                    double longitude = position.longitude;
//
//
//                    selectedLocation=position;



                    Toast.makeText(getContext(), "Selected Marker : " + name, Toast.LENGTH_SHORT).show();
                    return false;
                }
        });*/

        // POI 클릭 리스너 설정
        /*mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
                @Override
                public void onPoiClick(PointOfInterest poi) {
                    // 이전에 생성된 POI 마커가 있다면 제거합니다.
                    if (poiMarker != null ) {
                        if(!poiMarker.getTitle().equals(poi.name)) {
                            poiMarker.remove();
                        }
                    }

                    if (currentMarker != null) {
                        currentMarker.remove();
                    }

                    {
                        // 새로운 POI 마커를 추가합니다.
                        poiMarker = mMap.addMarker(new MarkerOptions()
                                .position(poi.latLng)
                                .title(poi.name)
                                .icon(BitmapDescriptorFactory.defaultMarker(200)).alpha(0.9f));
                        Toast.makeText(getContext(), "Clicked: " + poi.name, Toast.LENGTH_SHORT).show();
                    }

                }
        });*/




        //구글맵 내위치.
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(false);


        //맵 시작 될떄.   현재 위치 currentLocation에 저장.  그리고 현재 위치로 카메라.
        /*fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
            if (location != null) {
                currentLocation = location;
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//                mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

//                addCurrentLocationMarker(currentLatLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                // 선택된 장소에 마커 추가
//                addMarkers();
            } else {
                Log.d(TAG, "Current location is null");
            }
        });*/




        // 공공데이터로부터 관광지 정보 받아오기
//        loadTouristPlaces();

    }

    //???




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void setupPOI() {
        // 예제 POI 추가
        LatLng poiLocation = new LatLng(37.5665, 126.9780); // 예제 좌표 (서울특별시청)

        // 마커 추가
        Marker marker = new Marker();
        marker.setPosition(poiLocation);
        marker.setMap(mMap);

        // 정보 창 추가
        InfoWindow infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getContext()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return "서울특별시청"; // 예제 POI 이름
            }
        });
        infoWindow.open(marker);
    }



    /*
    private void loadSelectedPlaces() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("selectedPlaces", Context.MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet("selectedPlaces", null);
        if (set != null) {
            selectedPlaces = new ArrayList<>(set);
            showToast("Selected places loaded");
        }
    }

    */


    //현재위치
    /*private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        Task<Location> locationResult = fusedLocationClient.getLastLocation();
        locationResult.addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                Location lastKnownLocation = task.getResult();
                if (lastKnownLocation != null) {
                    currentLocation = lastKnownLocation;

                    // 현재 위치 마킹
//                    addCurrentLocationMarker(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

                    // 현재 위치를 기준으로 방향 받아오기
                    double latitude = lastKnownLocation.getLatitude();
                    double longitude = lastKnownLocation.getLongitude();
                    getDirections(latitude, longitude);
                    showToast("Current location retrieved");
                } else {
                    Log.d(TAG, "Last known location is null");
                }
            } else {
                Log.d(TAG, "Task to get last location failed");
            }
        });
    }*/

    private void getDirections(double latitude, double longitude) {
        // 방향 정보를 가져와서 지도에 표시
        // 이전에 작성한 getDirections() 메서드의 내용을 여기에 복사해서 사용합니다.
    }


    /*private void addCurrentLocationMarker(LatLng currentLatLng) {
        try {
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
            Log.d(TAG, "Current location marker added at: " + currentLatLng.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error adding current location marker: ", e);
        }
    }*/

    /*private void addMarkers() {
        for (String place : selectedPlaces) {
            // 장소 이름을 위도와 경도로 변환하여 마커 추가
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(place, 1);
                assert addresses != null;
                if (!addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(place));
                    Log.d(TAG, "Marker added for place: " + place + " at: " + latLng.toString());
                } else {
                    Log.d(TAG, "No addresses found for place: " + place);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error adding marker for place: " + place, e);
            }
        }
    }*/


/*
    private void loadTouristPlaces() {
        // 공공 데이터 API 엔드포인트
        String url = "https://www.data.go.kr/download/15021141/standard.do";
        String apiKey = "M4q3CWc0OP6VctrSKmKMdcNJAY3CWOj5XmhvM7WF2GkyXgdKb2IpCrGO8LRWl9Wl9986gSB%2Bi6t29viXcyV58g%3D%3D"; // 여기에 공공 데이터에서 발급한 API 키를 입력합니다.
        String requestUrl = url + "?dataType=xml&ServiceKey=" + apiKey + "&pageNo=1&numOfRows=100";


        // API 요청
        StringRequest request = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 응답을 로그로 출력하여 확인
                        Log.d(TAG, "API Response: " + response);

                        // 응답의 시작 부분을 확인하여 XML 형식인지 확인
                        if (response.trim().startsWith("<")) {
                            // XML 응답을 처리하는 메서드 호출
                            processXmlResponse(response);
                            showToast("Tourist places loaded successfully");
                        } else {
                            showToast("Received non-XML response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 오류 처리
                        Toast.makeText(getContext(), "Error loading tourist places", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading tourist places", error);
                    }
                });

        // 요청을 큐에 추가
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        queue.add(request);
    }

    private void processXmlResponse(String response) {
        try {
            // XML 파싱
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(response));

            // XML 문서를 읽으면서 관광지 정보를 추출하고 지도에 마커를 추가
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("record")) {
                    // 각 record 태그마다 관광지 정보 추출
                    String placeName = "";
                    double latitude = 0.0;
                    double longitude = 0.0;

                    while (!(eventType == XmlPullParser.END_TAG && parser.getName().equals("record"))) {
                        if (eventType == XmlPullParser.START_TAG) {
                            String tagName = parser.getName();
                            switch (tagName) {
                                case "관광지명":
                                    placeName = parser.nextText();
                                    break;
                                case "위도":
                                    latitude = Double.parseDouble(parser.nextText());
                                    break;
                                case "경도":
                                    longitude = Double.parseDouble(parser.nextText());
                                    break;
                                default:
                                    break;
                            }
                        }
                        eventType = parser.next();
                    }

                    // 마커 추가
                    LatLng latLng = new LatLng(latitude, longitude);
                    try {
                        mMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
                        Log.d(TAG, "Tourist place marker added for: " + placeName + " at: " + latLng.toString());
                    } catch (Exception e) {
                        Log.e(TAG, "Error adding marker for tourist place: " + placeName, e);
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            // XML 파싱 예외 처리
            Log.e(TAG, "Error parsing XML response", e);
            showToast("Error parsing XML response");
        } catch (IOException e) {
            // IO 예외 처리
            Log.e(TAG, "IO Exception occurred", e);
            showToast("IO Exception occurred");
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
    */

    //파이어베이스에.  위도 경도 문자열 추가.
    private void addFavoriteLocation(String place_name, double latitude, double longitude) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            // 해당 유저 계정에 해당하는 데이터베이스 받아옴.

            String userId = user.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");


            // 받아온 데이터 베이스에서 키 받고,  그 키를 통해  favoriteLocation(위도 경도) 등록.
            String key = mDatabase.push().getKey();
//            FavoriteLocation favoriteLocation = new FavoriteLocation(location.place_name, location.latitude, location.longitude);
            FavoriteLocation favoriteLocation = new FavoriteLocation(place_name,latitude, longitude);
            mDatabase.child(key).setValue(favoriteLocation).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "즐겨찾기 추가 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



}


class FavoriteLocation {
    public double latitude;
    public double longitude;
    private String place_name; // 장소 이름 추가

    private String key; // 추가된 필드

    public FavoriteLocation() {
        // Default constructor required for calls to DataSnapshot.getValue(FavoriteLocation.class)
    }

    public FavoriteLocation(String place_name,double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.place_name = place_name;
    }

    /*public FavoriteLocation(String place_name, double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.place_name = place_name;
    }*/


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public String getName() {
        return place_name;
    }

    public void setName(String name) {
        this.place_name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }



    @Override
    public String toString() {
        return place_name != null ? place_name : "Lat: " + latitude + ", Lng: " + longitude;
    }







}










