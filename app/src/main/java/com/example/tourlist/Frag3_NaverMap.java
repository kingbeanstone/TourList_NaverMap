package com.example.tourlist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
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


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.TimeoutError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
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


import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;








import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Frag3_NaverMap extends Fragment implements OnMapReadyCallback {
    private String fragmentTag="NaverMap";

    public void setFragmentTag(String tag) {
        this.fragmentTag = tag;
    }

    public String getFragmentTag() {
        return fragmentTag;
    }


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




/////////////////////////////////////////////////
    private ArrayList<String> selectedPlaces = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;






    private PlacesClient placesClient;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3_navermap,container,false);


        try {
            // Places SDK 초기화
            if (getActivity() != null) {
                Context context = getActivity().getApplicationContext();
                Places.initialize(context, "AIzaSyAkrFWzJeXx_k_k0g5vyuitlB8u6txNT98");
                placesClient = Places.createClient(context);
                Log.d(TAG, "Places SDK 초기화 성공");
            } else {
                Log.e(TAG, "getActivity()가 null입니다.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Places SDK 초기화 실패", e);
        }


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

        // 현재 위치로 버튼
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        // POI 설정
        setupPOI();





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

//                currentMarker=new Marker();
//                currentMarker.setPosition(latLng);
//                currentMarker.setCaptionText("선택된 위치");
//                currentMarker.setIconTintColor(0x478EEC);
//                currentMarker.setMap(mMap);

            }

        });













        // 공공데이터로부터 관광지 정보 받아오기
        loadTouristPlaces();

    }





    // 이거 권한 요청하는 거라는데 뭐 안 뜨는데??
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


    //테스트삼아 함 해봄.
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


    private void addMarkers() {
        for (String place : selectedPlaces) {
            // 장소 이름을 위도와 경도로 변환하여 마커 추가
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(place, 1);
                assert addresses != null;
                if (!addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    com.google.android.gms.maps.model.LatLng latLng = new com.google.android.gms.maps.model.LatLng(address.getLatitude(), address.getLongitude());
//                    mMap.addMarker(new MarkerOptions().position(latLng).title(place));// google

                    double lat=latLng.latitude;
                    double longt=latLng.longitude;

                    LatLng placeLocation = new LatLng(lat, longt);


                    // 마커 추가
                    Marker marker = new Marker();
                    marker.setPosition(placeLocation);
                    marker.setTag(place);
                    marker.setMap(mMap);



                    Log.d(TAG, "Marker added for place: " + place + " at: " + latLng.toString());
                } else {
                    Log.d(TAG, "No addresses found for place: " + place);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error adding marker for place: " + place, e);
            }
        }
    }




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
                        if (error instanceof TimeoutError) {
                            Log.e(TAG, "TimeoutError occurred while fetching tourist places.");
                        } else if (error instanceof NoConnectionError) {
                            Log.e(TAG, "NoConnectionError occurred while fetching tourist places.");
                        } else if (error instanceof ParseError) {
                            Log.e(TAG, "ParseError occurred while fetching tourist places.");
                        } else {
                            Log.e(TAG, "Unknown error occurred: " + error.getMessage());
                        }
                        showToast("Error fetching tourist places");
                    }
                });

        // 요청에 타임아웃 설정 추가
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10초
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // 요청을 큐에 추가
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        queue.add(request);
    }



//    private void processXmlResponse(String response) {
//        try {
//            // XML 파싱
//            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//            XmlPullParser parser = factory.newPullParser();
//            parser.setInput(new StringReader(response));
//
//            // XML 문서를 읽으면서 관광지 정보를 추출하고 지도에 마커를 추가
//            int eventType = parser.getEventType();
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("record")) {
//                    // 각 record 태그마다 관광지 정보 추출
//                    String placeName = "";
//                    double latitude = 0.0;
//                    double longitude = 0.0;
//
//                    while (!(eventType == XmlPullParser.END_TAG && parser.getName().equals("record"))) {
//                        if (eventType == XmlPullParser.START_TAG) {
//                            String tagName = parser.getName();
//                            switch (tagName) {
//                                case "관광지명":
//                                    placeName = parser.nextText();
//                                    break;
//                                case "위도":
//                                    latitude = Double.parseDouble(parser.nextText());
//                                    break;
//                                case "경도":
//                                    longitude = Double.parseDouble(parser.nextText());
//                                    break;
//                                default:
//                                    break;
//                            }
//                        }
//                        eventType = parser.next();
//                    }
//
//                    // 마커 추가
//                    LatLng latLng = new LatLng(latitude, longitude);
//                    try {
//
//
//                        Marker tourMarker = new Marker();
//
//
//                        tourMarker.setPosition(latLng);
//                        tourMarker.setCaptionText(placeName);
//                        tourMarker.setTag(placeName);
//
//                        tourMarker.setMap(mMap);
//
//                        tourMarker.setOnClickListener(new Marker.OnClickListener() {
//
//
//                            @Override
//                            public boolean onClick(@NonNull Overlay overlay) {
//                                Toast.makeText(getContext(), "마커 클릭됨 "+tourMarker.getCaptionText(), Toast.LENGTH_SHORT).show();
//                                selectedMarker=tourMarker;
//
//                                TouristPlace place = (TouristPlace)tourMarker.getTag();
//
//                                Toast.makeText(getContext(),"asdfasd"+place,Toast.LENGTH_SHORT).show();
//
//                                if (place != null) {
////                                     장소 검색을 통해 placeId 가져오기
//
//                                    searchPlaceIdByName(place.getPlaceName(), place);
//                                }
//                                return true; // true로 설정하여 기본 마커 클릭 동작을 유지하지 않음
//
//
////                                return false;
//                            }
//
//
//                        });
//
//
//
//
//
//                        Log.d(TAG, "Tourist place marker added for: " + placeName + " at: " + latLng.toString());
//                    } catch (Exception e) {
//                        Log.e(TAG, "Error adding marker for tourist place: " + placeName, e);
//                    }
//                }
//                eventType = parser.next();
//            }
//        } catch (XmlPullParserException e) {
//            // XML 파싱 예외 처리
//            Log.e(TAG, "Error parsing XML response", e);
//            showToast("Error parsing XML response");
//        } catch (IOException e) {
//            // IO 예외 처리
//            Log.e(TAG, "IO Exception occurred", e);
//            showToast("IO Exception occurred");
//        }
//    }








    private void processXmlResponse(String response) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(response));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("record")) {
                    final String[] placeName = {""};
                    final double[] latitude = {0.0};
                    final double[] longitude = {0.0};
                    final String[] address = {""};
                    final String[] description = {""};
                    final String[] phone = {""};

                    while (!(eventType == XmlPullParser.END_TAG && parser.getName().equals("record"))) {
                        if (eventType == XmlPullParser.START_TAG) {
                            String tagName = parser.getName();
                            switch (tagName) {
                                case "관광지명":
                                    placeName[0] = parser.nextText();
                                    break;
                                case "위도":
                                    latitude[0] = Double.parseDouble(parser.nextText());
                                    break;
                                case "경도":
                                    longitude[0] = Double.parseDouble(parser.nextText());
                                    break;
                                case "소재지도로명주소":
                                    address[0] = parser.nextText();
                                    break;
                                case "관광지소개":
                                    description[0] = parser.nextText();
                                    break;
                                case "관리기관전화번호":
                                    phone[0] = parser.nextText();
                                    break;
                                default:
                                    break;
                            }
                        }
                        eventType = parser.next();
                    }

                    LatLng latLng = new LatLng(latitude[0], longitude[0]);


//
//                    Marker marker.setMap(new MarkerOptions()
//                            .position(latLng)
//                            .title(placeName[0])
//                            .snippet(description[0])); // Description을 snippet에 저장
//                    marker.setTag(new TouristPlace(placeName[0], latitude[0], longitude[0], address[0], description[0], phone[0])); // Tag에 객체 저장
//



                    try {


                        Marker tourMarker = new Marker();

                        tourMarker.setPosition(latLng);
                        tourMarker.setCaptionText(placeName[0]);



                        // 정보창 생성
                        InfoWindow infoWindow = new InfoWindow();
                        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getContext()) {
                            @NonNull
                            @Override
                            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                                return description[0];
                            }
                        });

// 마커 클릭 리스너 설정
//                        marker.setOnClickListener(overlay -> {
//                            infoWindow.open(marker);
//                            return true;
//                        });




                        tourMarker.setTag(new TouristPlace(placeName[0], latitude[0], longitude[0], address[0], description[0], phone[0])); // Tag에 객체 저장

                        tourMarker.setMap(mMap);

                        tourMarker.setOnClickListener(new Marker.OnClickListener() {


                            @Override
                            public boolean onClick(@NonNull Overlay overlay) {
                                Toast.makeText(getContext(), "마커 클릭됨 "+tourMarker.getCaptionText(), Toast.LENGTH_SHORT).show();
                                selectedMarker=tourMarker;


//                                infoWindow.open(tourMarker);
                                TouristPlace place = (TouristPlace)tourMarker.getTag();

                                Toast.makeText(getContext(),"asdfasd"+place,Toast.LENGTH_SHORT).show();

                                if (place != null) {
//                                     장소 검색을 통해 placeId 가져오기

                                    searchPlaceIdByName(place.getPlaceName(), place);
                                }
                                return true; // true로 설정하여 기본 마커 클릭 동작을 유지하지 않음


//                                return false;
                            }


                        });





                        Log.d(TAG, "Tourist place marker added for: " + placeName + " at: " + latLng.toString());
                    } catch (Exception e) {
                        Log.e(TAG, "Error adding marker for tourist place: " + placeName, e);
                    }










                    Log.d(TAG, "Tourist place marker added for: " + placeName[0] + " at: " + latLng.toString());
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Log.e(TAG, "Error parsing XML response", e);
            showToast("Error parsing XML response");
        }
    }























































    private void searchPlaceIdByName(String placeName, TouristPlace place) {
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(placeName)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            if (!response.getAutocompletePredictions().isEmpty()) {
                AutocompletePrediction prediction = response.getAutocompletePredictions().get(0);
                String placeId = prediction.getPlaceId();
                Log.d(TAG, "Found placeId: " + placeId);
                // 장소 세부 정보 가져오기
                fetchPlaceDetails(placeId, place);
            } else {
                Log.d(TAG, "No predictions found for place: " + placeName);
            }
        }).addOnFailureListener((exception) -> {
            Log.e(TAG, "Error finding place predictions: " + exception.getMessage());
        });
    }

    private void fetchPlaceDetails(String placeId, TouristPlace place) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.PHOTO_METADATAS);

        // FetchPlaceRequest 객체 생성
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place fetchedPlace = response.getPlace();

            // 장소 이름 가져오기
            String name = fetchedPlace.getName();
            Log.d(TAG, "Place name: " + name);

            // 장소 사진 가져오기
            if (fetchedPlace.getPhotoMetadatas() != null && !fetchedPlace.getPhotoMetadatas().isEmpty()) {
                // 첫 번째 사진 메타데이터 가져오기
                PhotoMetadata photoMetadata = fetchedPlace.getPhotoMetadatas().get(0);

                // 사진을 가져오기 위한 요청
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(500) // 사진의 최대 너비
                        .setMaxHeight(300) // 사진의 최대 높이
                        .build();


                Toast.makeText(getContext(),"sibal",Toast.LENGTH_SHORT).show();

                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    if (bitmap != null) {
                        // 사진 URL을 TouristPlace 객체에 설정
                        place.setPhotoUrl(bitmapToBase64(bitmap));
                        Log.d(TAG, "Photo URL set for place: " + place.getPlaceName());
                    } else {
                        Log.d(TAG, "Bitmap is null");
                    }
                    // TouristPlaceDetailActivity로 이동
                    openTouristPlaceDetailActivity(place);
                }).addOnFailureListener((exception) -> {
                    Log.e(TAG, "Error fetching photo: " + exception.getMessage());
                    // 사진이 없을 경우에도 TouristPlaceDetailActivity로 이동
                    openTouristPlaceDetailActivity(place);
                });
            } else {
                Log.d(TAG, "No photo metadata available");
                // 사진이 없을 경우에도 TouristPlaceDetailActivity로 이동
                openTouristPlaceDetailActivity(place);
            }
        }).addOnFailureListener((exception) -> {
            Log.e(TAG, "Place not found: " + exception.getMessage());
        });
    }

    private void openTouristPlaceDetailActivity(TouristPlace place) {
        Toast.makeText(getContext(),"openTour~"+place.getPlaceName(),Toast.LENGTH_SHORT).show();
        TouristPlaceDataHolder.getInstance().setPlace(place);
        Intent intent = new Intent(getActivity(), TouristPlaceDetailActivity.class);
        startActivity(intent);
    }


    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Log.d(TAG, "Encoded bitmap to Base64: " + encoded);
        return encoded;
    }





    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }


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










