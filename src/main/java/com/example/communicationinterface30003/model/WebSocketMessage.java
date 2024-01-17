package com.example.communicationinterface30003.model;

import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * @author tkj
 */
@Data
public class WebSocketMessage {
    private Integer code;

    private JSONObject bodyMessage;

    private String msg;

    private String token;
}
