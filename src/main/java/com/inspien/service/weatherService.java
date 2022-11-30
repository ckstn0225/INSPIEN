package com.inspien.service;


import com.inspien.entity.region;
import com.inspien.entity.regionCode;
import com.inspien.repository.regionCodeRepository;
import com.inspien.repository.regionRepository;
import com.inspien.response.*;
import com.inspien.util.gpsTrans;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class weatherService {
    @Value("${ServiceKey}")
    private String serviceKey;

    private final gpsTrans gps;
    private final regionRepository repository;
    private final regionCodeRepository regionCodeRep;

    public weatherService(gpsTrans gps, regionRepository reg, regionCodeRepository regionCodeRep) {
        this.gps = gps;
        this.repository = reg;
        this.regionCodeRep = regionCodeRep;
    }

    //    현재 날씨 조회
    public responseDto<?> currentWeather(String city, String reg) throws IOException {

        region region = repository.findFirstByCityAndRegion(city, reg);
        if (region == null) {
            return new responseDto<>(true, "해당 도시를 조회 할 수 없습니다.");
        }
        int x = region.getXis();
        int y = region.getYis();


        JSONArray item = JsonParsing(currentUrl(x, y).toString());
        currentWeatherResponseDto currentWeatherResponseDto = new currentWeatherResponseDto(city + " " + reg, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm")));
        for (int i = 0; i < item.length(); i++) {
            switch (item.getJSONObject(i).get("category").toString()) {
                case "T1H" -> {
                    currentWeatherResponseDto.setTemperatures(item.getJSONObject(i).get("obsrValue").toString() + "도");
                }
                case "RN1" -> {
                    currentWeatherResponseDto.setPrecipitation(item.getJSONObject(i).get("obsrValue").toString() + "mm");
                }
                case "REH" -> {
                    currentWeatherResponseDto.setHumidity(item.getJSONObject(i).get("obsrValue").toString() + "%");
                }
                case "PTY" -> {
                    currentWeatherResponseDto.setRain(
                            ShortRain(item.getJSONObject(i).get("obsrValue").toString()));
                }
            }
        }
        return new responseDto<>(true, currentWeatherResponseDto);
    }
//    현재 날씨 위도 경도 검색
    public responseDto<?> currentWeather(double lat, double lng) throws IOException {
        gpsTrans.LatXLngY Xy = gps.convertGRID_GPS(0,lat,lng);

        region region = repository.findFirstByXisAndYis((int) Xy.getX()+1, (int) Xy.getY()+1);
        if (region == null) {
            return new responseDto<>(true, "해당 도시를 조회 할 수 없습니다.");
        }
        int x = region.getXis();
        int y = region.getYis();


        JSONArray item = JsonParsing(currentUrl(x, y).toString());
        currentWeatherResponseDto currentWeatherResponseDto = new currentWeatherResponseDto(region.getCity() + " " + region.getRegion(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm")));
        for (int i = 0; i < item.length(); i++) {
            switch (item.getJSONObject(i).get("category").toString()) {
                case "T1H" -> {
                    currentWeatherResponseDto.setTemperatures(item.getJSONObject(i).get("obsrValue").toString() + "도");
                }
                case "RN1" -> {
                    currentWeatherResponseDto.setPrecipitation(item.getJSONObject(i).get("obsrValue").toString() + "mm");
                }
                case "REH" -> {
                    currentWeatherResponseDto.setHumidity(item.getJSONObject(i).get("obsrValue").toString() + "%");
                }
                case "PTY" -> {
                    currentWeatherResponseDto.setRain(
                            ShortRain(item.getJSONObject(i).get("obsrValue").toString()));
                }
            }
        }
        return new responseDto<>(true, currentWeatherResponseDto);
    }

    //    오늘 날씨 조회
    public responseDto<?> todayWeather(String city, String reg) throws IOException {
        region region = repository.findFirstByCityAndRegion(city, reg);
        if (region == null) {
            return new responseDto<>(true, "해당 도시를 조회 할 수 없습니다.");
        }
        int x = region.getXis();
        int y = region.getYis();
        JSONArray item = JsonParsing(todayUrl(x, y).toString());

        List<todayWeatherResponseDto> list = new ArrayList<>();

        int jcount = 0;
        for (int i = 0; i < 24; i++) {
            todayWeatherResponseDto today = new todayWeatherResponseDto();
            int count = 0;

            for (; jcount < item.length(); jcount++) {
                switch (item.getJSONObject(jcount).get("category").toString()) {
                    case "TMP" -> {
                        today.setTemperatures(item.getJSONObject(jcount).get("fcstValue").toString() + "도");
                        count++;
                    }
                    case "REH" -> {
                        today.setHumidity(item.getJSONObject(jcount).get("fcstValue").toString() + "%");
                        count++;
                    }
                    case "POP" -> {
                        today.setRainProbability(item.getJSONObject(jcount).get("fcstValue").toString() + "%");
                        count++;
                    }
                    case "PTY" -> {
                        today.setRain(Rain(item.getJSONObject(jcount).get("fcstValue").toString()));
                        count++;
                    }
                    case "SKY" -> {
                        today.setCloud(sky(item.getJSONObject(jcount).get("fcstValue").toString()));
                        count++;
                    }
                    default -> {
                    }
                }
                if (count >= 5) {
                    today.setTime(item.getJSONObject(jcount).get("fcstTime").toString().substring(0, 2) + "시");
                    jcount++;
                    break;
                }
            }
            list.add(today);
        }
        return new responseDto<>(true, new todayWeatherListResponseDto(city + " " + reg, LocalDate.now().toString(), list));
    }
    //    오늘 날씨 조회
    public responseDto<?> todayWeather(double lat, double lng) throws IOException {
        gpsTrans.LatXLngY Xy = gps.convertGRID_GPS(0,lat,lng);
        region region = repository.findFirstByXisAndYis((int) Xy.getX()+1, (int) Xy.getY()+1);
        if (region == null) {
            return new responseDto<>(true, "해당 도시를 조회 할 수 없습니다.");
        }
        int x = region.getXis();
        int y = region.getYis();
        JSONArray item = JsonParsing(todayUrl(x, y).toString());

        List<todayWeatherResponseDto> list = new ArrayList<>();

        int jcount = 0;
        for (int i = 0; i < 24; i++) {
            todayWeatherResponseDto today = new todayWeatherResponseDto();
            int count = 0;

            for (; jcount < item.length(); jcount++) {
                switch (item.getJSONObject(jcount).get("category").toString()) {
                    case "TMP" -> {
                        today.setTemperatures(item.getJSONObject(jcount).get("fcstValue").toString() + "도");
                        count++;
                    }
                    case "REH" -> {
                        today.setHumidity(item.getJSONObject(jcount).get("fcstValue").toString() + "%");
                        count++;
                    }
                    case "POP" -> {
                        today.setRainProbability(item.getJSONObject(jcount).get("fcstValue").toString() + "%");
                        count++;
                    }
                    case "PTY" -> {
                        today.setRain(Rain(item.getJSONObject(jcount).get("fcstValue").toString()));
                        count++;
                    }
                    case "SKY" -> {
                        today.setCloud(sky(item.getJSONObject(jcount).get("fcstValue").toString()));
                        count++;
                    }
                    default -> {
                    }
                }
                if (count >= 5) {
                    today.setTime(item.getJSONObject(jcount).get("fcstTime").toString().substring(0, 2) + "시");
                    jcount++;
                    break;
                }
            }
            list.add(today);
        }
        return new responseDto<>(true, new todayWeatherListResponseDto(region.getCity() + " " + region.getRegion(), LocalDate.now().toString(), list));
    }





    //     3일간 기상 예보
    public responseDto<?> threeWeather(String region) throws IOException {
        regionCode code = regionCodeRep.findByRegion(region);
        if (code.getCode() == null) {
            return new responseDto<>(true, "해당 도시를 조회 할 수 없습니다.");
        }
        JSONArray item = JsonParsing(threeUrl(code.getCode()).toString());
        List<threeWeatherResponseDto> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            threeWeatherResponseDto three = new threeWeatherResponseDto(
                    weatherClock(item.getJSONObject(i).get("numEf").toString()),
                    item.getJSONObject(i).get("ta").toString() + "도",
                    item.getJSONObject(i).get("rnSt").toString() + "%",
                    item.getJSONObject(i).get("wf").toString(),
                    Rain(item.getJSONObject(i).get("rnYn").toString())

            );
            list.add(three);
        }
        return new responseDto<>(true, new threeWeatherListResponseDto(region, LocalDate.now().toString(), list));
    }

    //    기상 예보
    public responseDto<?> forecastWeather(String region) throws IOException {
        regionCode code = regionCodeRep.findByRegion(region);
        if (code.getCode() == null) {
            return new responseDto<>(true, "해당 지역을 조회 할 수 없습니다.");
        }
        JSONArray item = JsonParsing(forecastUrl(code.getCode()).toString());
        System.out.println(item);
        System.out.println("-------------------------------------------");
        for (int i = 0; i < item.length(); i++) {
            System.out.println(item.get(i));
        }
        return new responseDto<>(true, item.getJSONObject(0).get("wfSv1"));
    }

    //    Url 설정

    //현재 날씨 조회 [단기 예보] [초단기실황조회]
    public StringBuilder currentUrl(int x, int y) throws UnsupportedEncodingException {

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"); /*URL*/
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8")).append("=").append(serviceKey); /*Service Key*/
        urlBuilder.append("&").append(URLEncoder.encode("pageNo", "UTF-8")).append("=").append(URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8")).append("=").append(URLEncoder.encode("10", "UTF-8"));
        urlBuilder.append("&").append(URLEncoder.encode("dataType", "UTF-8")).append("=").append(URLEncoder.encode("JSON", "UTF-8"));
        urlBuilder.append("&").append(URLEncoder.encode("base_date", "UTF-8")).append("=").append(URLEncoder.encode(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE), "UTF-8"));
        urlBuilder.append("&").append(URLEncoder.encode("base_time", "UTF-8")).append("=").append(URLEncoder.encode(LocalTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("HHmm")), "UTF-8"));
        urlBuilder.append("&").append(URLEncoder.encode("nx", "UTF-8")).append("=").append(URLEncoder.encode(String.valueOf(x), "UTF-8"));
        urlBuilder.append("&").append(URLEncoder.encode("ny", "UTF-8")).append("=").append(URLEncoder.encode(String.valueOf(y), "UTF-8"));
        return urlBuilder;
    }

    // 시간별 날씨 조회 [단기 예보] [단기예보조회]
    public StringBuilder todayUrl(int x, int y) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8")).append("=").append(serviceKey); /*Service Key*/
        urlBuilder.append("&").append(URLEncoder.encode("pageNo", "UTF-8")).append("=").append(URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8")).append("=").append(URLEncoder.encode("300", "UTF-8"));
        urlBuilder.append("&").append(URLEncoder.encode("dataType", "UTF-8")).append("=").append(URLEncoder.encode("JSON", "UTF-8"));
        urlBuilder.append("&").append(URLEncoder.encode("base_date", "UTF-8")).append("=").append(URLEncoder.encode(LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE), "UTF-8"));
        urlBuilder.append("&").append(URLEncoder.encode("base_time", "UTF-8")).append("=").append(URLEncoder.encode("2300", "UTF-8"));
        urlBuilder.append("&").append(URLEncoder.encode("nx", "UTF-8")).append("=").append(URLEncoder.encode(String.valueOf(x), "UTF-8"));
        urlBuilder.append("&").append(URLEncoder.encode("ny", "UTF-8")).append("=").append(URLEncoder.encode(String.valueOf(y), "UTF-8"));
        return urlBuilder;
    }

    // 3일후 날씨 예상 [동네 예보] [육상예보조회]
    public StringBuilder threeUrl(String code) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst"); /*URL*/
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8")).append("=").append(serviceKey); /*Service Key*/
        urlBuilder.append("&").append(URLEncoder.encode("pageNo", "UTF-8")).append("=").append(URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8")).append("=").append(URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&").append(URLEncoder.encode("dataType", "UTF-8")).append("=").append(URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON)Default: XML*/
        urlBuilder.append("&").append(URLEncoder.encode("regId", "UTF-8")).append("=").append(URLEncoder.encode(code, "UTF-8")); /*108 전국, 109 서울, 인천, 경기도 등 (활용가이드 하단 참고자료 참조)*/
        return urlBuilder;
    }

    // 기상예보 [동네 예보] [기상개황조회]
    public StringBuilder forecastUrl(String code) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstMsgService/getWthrSituation"); /*URL*/
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8")).append("=").append(serviceKey); /*Service Key*/
        urlBuilder.append("&").append(URLEncoder.encode("pageNo", "UTF-8")).append("=").append(URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8")).append("=").append(URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&").append(URLEncoder.encode("dataType", "UTF-8")).append("=").append(URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON)Default: XML*/
        urlBuilder.append("&").append(URLEncoder.encode("stnId", "UTF-8")).append("=").append(URLEncoder.encode(code, "UTF-8")); /*108 전국, 109 서울, 인천, 경기도 등 (활용가이드 하단 참고자료 참조)*/
        return urlBuilder;
    }

    //  JsonArray 변경
    public JSONArray JsonParsing(String url) throws IOException {
        URL obj = new URL(url);
        BufferedReader bf;
        bf = new BufferedReader(new InputStreamReader(obj.openStream(), StandardCharsets.UTF_8));
        String result = bf.readLine();
        bf.close();
        JSONObject jsonObject = new JSONObject(result);
        return jsonObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");
    }

//    코드 해석
    public String ShortRain(String rain) {
        switch (rain) {
            case "1" -> {
                return "비";
            }
            case "2" -> {
                return "비/눈";
            }
            case "3" -> {
                return "눈";
            }
            case "5" -> {
                return "빗방울";
            }
            case "6" -> {
                return "빗방울눈날림";
            }
            case "7" -> {
                return "눈날림";
            }
            default -> {
                return "맑음";
            }
        }
    }

    public String Rain(String rain) {
        switch (rain) {
            case "1" -> {
                return "비";
            }
            case "2" -> {
                return "비/눈";
            }
            case "3" -> {
                return "눈";
            }
            case "4" -> {
                return "소나기";
            }
            default -> {
                return "비없음";
            }
        }
    }

    public String sky(String sky) {
        switch (sky) {
            case "3" -> {
                return "구름많음";
            }
            case "4" -> {
                return "흐림";
            }
            default -> {
                return "맑음";
            }
        }
    }

    public String weatherClock(String num) {
        if (LocalTime.of(5, 0).isAfter(LocalTime.now()) && LocalTime.of(11, 0).isBefore(LocalTime.now())) {
            switch (num) {
                case "0" -> {
                    return "오늘오전";
                }
                case "1" -> {
                    return "오늘오후";
                }
                case "2" -> {
                    return "내일오전";
                }
                case "3" -> {
                    return "내일오후";
                }
                case "4" -> {
                    return "모레오전";
                }
                default -> {
                    return "";
                }
            }
        } else {
            switch (num) {
                case "0" -> {
                    return "오늘오후";
                }
                case "1" -> {
                    return "내일오전";
                }
                case "2" -> {
                    return "내일오후";
                }
                case "3" -> {
                    return "모레오전";
                }
                case "4" -> {
                    return "모레오후";
                }
                default -> {
                    return "";
                }
            }
        }
    }

}
