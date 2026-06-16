package com.example.smartfood.network;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiClient {
    // 真机演示时使用电脑在同一热点/局域网下的 IPv4 地址；模拟器可改回 10.0.2.2。
    private static final String BASE_URL = "http://192.168.43.37:8081/api";
    private static final int TIMEOUT_MS = 8000;

    public ApiResponse login(String username, String password) {
        return postUser("/user/login", username, password, null);
    }

    public ApiResponse register(String username, String password, String nickname) {
        return postUser("/user/register", username, password, nickname);
    }

    private ApiResponse postUser(String path, String username, String password, String nickname) {
        HttpURLConnection connection = null;
        try {
            JSONObject request = new JSONObject();
            request.put("username", username);
            request.put("password", password);
            if (nickname != null) {
                request.put("nickname", nickname);
            }

            URL url = new URL(BASE_URL + path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
            writer.write(request.toString());
            writer.flush();
            writer.close();

            int code = connection.getResponseCode();
            InputStream stream = code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
            JSONObject response = new JSONObject(readText(stream));
            return new ApiResponse(
                    response.optBoolean("success", false),
                    response.optString("message", code >= 200 && code < 300 ? "操作成功" : "服务器请求失败"),
                    response.optLong("userId", 0),
                    response.optString("username", username),
                    response.optString("nickname", username)
            );
        } catch (Exception e) {
            return new ApiResponse(false, "无法连接后端服务，请确认 Spring Boot 已启动", 0, username, username);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readText(InputStream stream) throws Exception {
        if (stream == null) {
            return "{}";
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }

    public static class ApiResponse {
        public final boolean success;
        public final String message;
        public final long userId;
        public final String username;
        public final String nickname;

        public ApiResponse(boolean success, String message, long userId, String username, String nickname) {
            this.success = success;
            this.message = message;
            this.userId = userId;
            this.username = username;
            this.nickname = nickname;
        }
    }
}
