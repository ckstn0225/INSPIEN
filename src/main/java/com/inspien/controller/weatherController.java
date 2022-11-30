package com.inspien.controller;

import com.inspien.response.responseDto;
import com.inspien.service.weatherService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/weather")
public class weatherController {

    private final weatherService service;

    public weatherController(weatherService service) {
        this.service = service;
    }

    // 현재 날씨 조회 (주소 검색)
    @ApiOperation(value = "현재 날씨 조회 (주소 조회)",notes = "입력한 주소 기반의 현재 날씨를 출력한다 <br> ex) city = 서울시, region = 종로구")
    @GetMapping("/region")
    public responseDto<?> weather(@RequestParam(defaultValue = "서울특별시")@ApiParam("시/도") String city, @RequestParam(defaultValue = "종로구")@ApiParam("시/군/구/읍/면/동") String region)
            throws IOException {
        return service.currentWeather(city, region);
    }

    // 현재 날씨 조회 (위 경도 검색)
    @ApiOperation(value = "현재 날씨 조회 (위경도 조회)",notes = "입력한 위/경도 기반의 현재 날씨를 출력한다  <br> ex) lat = 37.56356944444444, lng = 126.98000833333333 ")
    @GetMapping("/xy")
    public responseDto<?> xyWeather(@RequestParam(defaultValue = "37.56356944444444")@ApiParam("위도") double lat, @RequestParam(defaultValue = "126.98000833333333")@ApiParam("경도") double lng)
            throws IOException {
        return service.currentWeather(lat, lng);
    }

    //  오늘 날씨 조회
    @ApiOperation(value = "오늘 시간대별 날씨 조회 (주소 조회)",notes = "입력한 주소 기반의 오늘 시간대별의 날씨를 출력한다 <br> ex) city = 서울시, region = 종로구")
    @GetMapping("/today/region")
    public responseDto<?> todayWeather(@RequestParam(defaultValue = "서울특별시")@ApiParam("시/도") String city, @RequestParam(defaultValue = "종로구")@ApiParam("시/군/구/읍/면/동") String region)
            throws IOException {
        return service.todayWeather(city, region);
    }
    //  오늘 날씨 조회 (위 경도 검색)
    @ApiOperation(value = "오늘 시간대별 날씨 조회 (위경도 조회)",notes = "입력한 위/경도 기반의 오늘 시간대별의 날씨를 출력한다  <br> ex) lat = 37.56356944444444, lng = 126.98000833333333 ")
    @GetMapping("/today/xy")
    public responseDto<?> xyTodayWeather(@RequestParam(defaultValue = "37.56356944444444")@ApiParam("위도") double lat, @RequestParam(defaultValue = "126.98000833333333")@ApiParam("경도") double lng)
            throws IOException {
        return service.todayWeather(lat, lng);
    }

    //    3일간 날씨 조회
    @ApiOperation(value = "내일 모레까지의 날씨 검색 (주소 조회)",notes = "입력한 주소 기반으로 내일모레까지의 날씨를 출력한다  <br> ex) region = 서울")
    @GetMapping("/later")
    public responseDto<?> threeWeather(@RequestParam(defaultValue = "서울") String region) throws IOException {
        return service.threeWeather(region);
    }

    //   기상전망
    @ApiOperation(value = "도별 기상전망 조회 (주소 조회)",notes = "입력한 도의 기상전망을 조회한다. <br> ex) region = 전국 or region = 수도권 or region = 경상남도 ")
    @GetMapping("/forecast")
    public responseDto<?> forecastWeather(@RequestParam(defaultValue = "전국") String region) throws IOException {
        return service.forecastWeather(region);
    }
}
