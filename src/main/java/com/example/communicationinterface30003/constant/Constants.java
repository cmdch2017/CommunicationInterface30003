/**
 * Constants.java
 * Created at 2022-08-26
 * Created by chenyuxiang
 * Copyright (C) 2022 WEGO Group, All rights reserved.
 */
package com.example.communicationinterface30003.constant;

/**
 * <p>ClassName: Constants</p>
 * <p>Description: 常量信息  </p>
 *
 * @author chenyuxiang
 * @date 2022-08-26
 */
public class Constants {
    //空白行 65-187
    public static final int PIANYILIANG = 123;

    /**
     * 报文长度256个字节
     */
    public static final int MEG_LENGTH = 133 + PIANYILIANG;
    /**
     * 报文首尾分隔符位置 默认0x5c
     */
    public static final String HeadByte01 = "HeadByte01";
    public static final String HeadByte02 = "HeadByte02";
    public static final String HeadByte03 = "HeadByte03";
    public static final String HeadByte04 = "HeadByte04";
    public static final String MsgID = "MsgID";


    public static final int HEAD_BYTE_01_POS = 0;
    public static final int HEAD_BYTE_02_POS = 1;
    public static final int HEAD_BYTE_03_POS = 2;
    public static final int HEAD_BYTE_04_POS = 3;
    /**
     * 报文首尾分隔符位置 默认0x75
     */
    public static final int MEG_SEPARATION_TAIL_POS1 = 252;
    public static final int MEG_SEPARATION_TAIL_POS2 = 253;
    public static final int MEG_SEPARATION_TAIL_POS3 = 254;
    public static final int MEG_SEPARATION_TAIL_POS4 = 255;
    /**
     * 报文首尾分隔符
     */
    public static final byte MEG_SEPARATION_HEAD = (byte) 0x5c;
    public static final byte MEG_SEPARATION_TAIL = (byte) 0x75;

    /**
     * MsgID消息帧号字段开始位置
     */
    public static final int MSG_ID_POS = 4;
    /**
     * 开机按钮按下状态，1：开机键被按下；0：开机键未被按下（多功能板采集的IO）
     */
    public static final int BSYS_POWER_UP_POS = 8;
    public static final int BCLOSE_DOOR_POS = 9;
    public static final int BDYNAMIC_LEVEL_POS = 10;

    public static final int WEIGHT_LARGE_CELL_POS = 20;
    public static final int WEIGHT_SMALL_CELL_POS = 24;
    public static final int PRESSURE_POS = 28;
    public static final int POWER_VOLT_POS = 32;
    public static final int BATT_VOLT_POS = 36;
    public static final int BATT_CURR_POS = 40;

    public static final int TEMP_BAG_01_POS = 44;
    public static final int TEMP_BAG_02_POS = 48;
    public static final int TEMP_PAN_POS = 52;
    public static final int TEMP_AMBIENT_POS = 56;

    public static final int RFID_MSG_HEAD_POS = 60;

    public static final int RFID_INFO_POS = 64;

    public static final int ENCODER_01_POS = 188;
    public static final int ENCODER_02_POS = 192;
    public static final int ENCODER_03_POS = 196;
    public static final int ENCODER_04_POS = 200;
    public static final int ENCODER_05_POS = 204;
    public static final int ENCODER_06_POS = 208;
    public static final int ENCODER_07_POS = 212;
    public static final int ENCODER_08_POS = 216;


    public static final int BDEV_COMM_OK_TEMP_COUPLER_01_POS = 220;
    public static final int BDEV_FDBK_OK_TEMP_COUPLER_01_POS = 221;

    public static final int BDEV_COMM_OK_TEMP_COUPLER_02_POS = 222;
    public static final int BDEV_FDBK_OK_TEMP_COUPLER_02_POS = 223;

    public static final int BDEV_COMM_OK_TEMP_COUPLER_03_POS = 224;
    public static final int BDEV_FDBK_OK_TEMP_COUPLER_03_POS = 225;

    public static final int BDEV_COMM_OK_TEMP_COUPLER_04_POS = 226;
    public static final int BDEV_FDBK_OK_TEMP_COUPLER_04_POS = 227;

    public static final int BDEV_COMM_OK_EXT_RELAY_01_POS = 228;
    public static final int BDEV_FDBK_OK_EXT_RELAY_01_POS = 229;

    public static final int BDEV_COMM_OK_EXT_RELAY_02_POS = 230;
    public static final int BDEV_FDBK_OK_EXT_RELAY_02_POS = 231;

    public static final int BDEV_COMM_OK_ENCODER_01_POS = 232;
    public static final int BDEV_FDBK_OK_ENCODER_01_POS = 233;

    public static final int BDEV_COMM_OK_ENCODER_02_POS = 234;
    public static final int BDEV_FDBK_OK_ENCODER_02_POS = 235;

    public static final int BDEV_COMM_OK_RFID_POS = 236;
    public static final int BDEV_FDBK_OK_RFID_POS = 237;

    public static final int BDEV_COMM_OK_MULTI_FUNC_BOARD_POS = 238;
    public static final int BDEV_FDBK_OK_MULTI_FUNC_BOARD_POS = 239;

    public static final int BDEV_COMM_OK_LARGE_SCALE_POS = 240;
    public static final int BDEV_FDBK_OK_LARGE_SCALE_POS = 241;

    public static final int BDEV_COMM_OK_SMALL_SCALE_POS = 242;
    public static final int BDEV_FDBK_OK_SMALL_SCALE_POS = 243;

    public static final int BDEV_COMM_OK_PRESSURE_POS = 244;
    public static final int BDEV_FDBK_OK_PRESSURE_POS = 245;


    public static final int TAIL_BYTE_01_POS = 252;
    public static final int TAIL_BYTE_02_POS = 253;
    public static final int TAIL_BYTE_03_POS = 254;
    public static final int TAIL_BYTE_04_POS = 255;

    public static final String SYSTEM_ERROR = "系统错误，请联系管理员";
}
