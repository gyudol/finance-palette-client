package com.example.fin_palette;

//  API Endpoint value 수정하면 전체 클래스 수정됨
public class ApiServerManager {
    private String ApiEndpoint = "http://";    // 여기 수정

    public String getApiEndpoint() {
        return ApiEndpoint;
    }
}