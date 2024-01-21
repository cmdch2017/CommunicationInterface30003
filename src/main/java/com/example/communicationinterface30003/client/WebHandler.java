package com.example.communicationinterface30003.client;

import cn.hutool.core.util.HexUtil;
import com.example.communicationinterface30003.constant.Constants;
import com.example.communicationinterface30003.entity.Physics;
import com.example.communicationinterface30003.util.WebUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.communicationinterface30003.constant.Constants.*;
import static com.example.communicationinterface30003.excel.ExcelWriter.writeToExcel;

//5c5c5c5c445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABB75757575

/**
 * @author lst
 * @date 2023/12/20 14:22
 * @return null
 */
@Slf4j
public class WebHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private Map<Physics, byte[]> previousMessageFields = new LinkedHashMap<>();
    private long lastReceivedTimestamp = System.currentTimeMillis();

    private WebClient webClient;

    public WebHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("连接的Web服务器端地址:" + ctx.channel().remoteAddress());
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] bytes = new byte[Constants.MEG_LENGTH];
        msg.readBytes(bytes);
//        log.info("接收到转发服务原始报文 - {}", HexUtil.encodeHexStr(bytes));
        if (!WebUtil.validate(bytes)) {
            msg.clear();
            return;
        }
        //检测msgId异常情况
        if (!WebUtil.MsgIdCheck(bytes)) {
            log.error("Modbus总线通讯故障");
        }
        //当前接受报文
        Map<Physics, byte[]> currentFields = parseFields(bytes);
        // 记录接收时间戳
        long currentTimestamp = System.currentTimeMillis();
        //比较两次报文
        if (!previousMessageFields.isEmpty()) {
            List<Map<String, Object>> resultList=WebUtil.compareAndPrintChanges(previousMessageFields, currentFields);

            writeToExcel(resultList);
        }
        //更新报文
        previousMessageFields = currentFields;
        // 更新上一次接收的时间戳
        lastReceivedTimestamp = currentTimestamp;
    }

    private Map<Physics, byte[]> parseFields(byte[] bytes) {
        //解析报文放入map中
        Map<Physics, byte[]> map = new LinkedHashMap<>();
        // Head部分
        map.put(new Physics("HeadByte01", "HeadByte01", HEAD_BYTE_01_POS), WebUtil.generateTypeUInt8(bytes, HEAD_BYTE_01_POS));
        map.put(new Physics("HeadByte02", "HeadByte02", HEAD_BYTE_02_POS), WebUtil.generateTypeUInt8(bytes, HEAD_BYTE_02_POS));
        map.put(new Physics("HeadByte03", "HeadByte03", HEAD_BYTE_03_POS), WebUtil.generateTypeUInt8(bytes, HEAD_BYTE_03_POS));
        map.put(new Physics("HeadByte04", "HeadByte04", HEAD_BYTE_04_POS), WebUtil.generateTypeUInt8(bytes, HEAD_BYTE_04_POS));

// Frame部分
        //消息号无需比较
//        map.put(new Physics("MsgID", "消息帧号", MSG_ID_POS), WebUtil.generateTypeUInt32(bytes, MSG_ID_POS));

// St部分
        map.put(new Physics("BSysPowerUp", "开机按钮按下状态", BSYS_POWER_UP_POS), WebUtil.generateTypeUInt8(bytes, BSYS_POWER_UP_POS));
        map.put(new Physics("BCloseDoor", "（霍尔传感器1）到位状态", BCLOSE_DOOR_POS), WebUtil.generateTypeUInt8(bytes, BCLOSE_DOOR_POS));
        map.put(new Physics("BDynamicLevel", "（霍尔传感器2）到位状态", BDYNAMIC_LEVEL_POS), WebUtil.generateTypeUInt8(bytes, BDYNAMIC_LEVEL_POS));

// AI部分
        map.put(new Physics("Weight_LargeCell", "大量程称重传感器读数：单位g", WEIGHT_LARGE_CELL_POS, true), WebUtil.generateTypeInt32(bytes, WEIGHT_LARGE_CELL_POS));
        map.put(new Physics("Weight_SmallCell", "小量程称重传感器读数：单位g", WEIGHT_SMALL_CELL_POS, true), WebUtil.generateTypeInt32(bytes, WEIGHT_SMALL_CELL_POS));
        map.put(new Physics("Pressure", "压力传感器读数：单位pa", PRESSURE_POS), WebUtil.generateTypeInt32(bytes, PRESSURE_POS));
        map.put(new Physics("PowerVolt", "AC-DC输出端采样电压", POWER_VOLT_POS), WebUtil.generateTypeInt32(bytes, POWER_VOLT_POS));
        map.put(new Physics("BattVolt", "锂电池输出端采样电压", BATT_VOLT_POS), WebUtil.generateTypeInt32(bytes, BATT_VOLT_POS));
        map.put(new Physics("BattCurr", "锂电池输出端采样电流", BATT_CURR_POS), WebUtil.generateTypeInt32(bytes, BATT_CURR_POS));

// Temp部分
        map.put(new Physics("TempBag01", "热电偶通道1温度值", TEMP_BAG_01_POS), WebUtil.generateTypeUInt32(bytes, TEMP_BAG_01_POS));
        map.put(new Physics("TempBag02", "热电偶通道2温度值", TEMP_BAG_02_POS), WebUtil.generateTypeUInt32(bytes, TEMP_BAG_02_POS));
        map.put(new Physics("TempPan", "热电偶通道3温度值", TEMP_PAN_POS), WebUtil.generateTypeUInt32(bytes, TEMP_PAN_POS));
        map.put(new Physics("TempAmbient", "热电偶通道4温度值", TEMP_AMBIENT_POS), WebUtil.generateTypeUInt32(bytes, TEMP_AMBIENT_POS));

// UsrInfo_Head部分
        map.put(new Physics("RFID_MsgHead", "RFID_MsgHead", RFID_MSG_HEAD_POS), WebUtil.generateTypeUInt32(bytes, RFID_MSG_HEAD_POS));

// UsrInfo_Body部分
        map.put(new Physics("RFID_INFO", "RFID_INFO", RFID_INFO_POS), WebUtil.generateTypeULength(bytes, RFID_INFO_POS, 124));
//        map.put(new Physics("RFID_INFO02", "RFID_INFO02", RFID_INFO_POS+4), WebUtil.generateTypeUInt8(bytes, RFID_INFO_POS));

// Enc部分
        map.put(new Physics("Encoder01", "Encoder01", ENCODER_01_POS), WebUtil.generateTypeInt32(bytes, ENCODER_01_POS));
        map.put(new Physics("Encoder02", "Encoder02", ENCODER_02_POS), WebUtil.generateTypeInt32(bytes, ENCODER_02_POS));
        map.put(new Physics("Encoder03", "Encoder03", ENCODER_03_POS), WebUtil.generateTypeInt32(bytes, ENCODER_03_POS));
        map.put(new Physics("Encoder04", "Encoder04", ENCODER_04_POS), WebUtil.generateTypeInt32(bytes, ENCODER_04_POS));
        map.put(new Physics("Encoder05", "Encoder05", ENCODER_05_POS), WebUtil.generateTypeInt32(bytes, ENCODER_05_POS));
        map.put(new Physics("Encoder06", "Encoder06", ENCODER_06_POS), WebUtil.generateTypeInt32(bytes, ENCODER_06_POS));
        map.put(new Physics("Encoder07", "Encoder07", ENCODER_07_POS), WebUtil.generateTypeInt32(bytes, ENCODER_07_POS));
        map.put(new Physics("Encoder08", "Encoder08", ENCODER_08_POS), WebUtil.generateTypeInt32(bytes, ENCODER_08_POS));

// Ext部分
        map.put(new Physics("BDevCommOK_TempCoupler01", "电偶01#设备通讯连接", BDEV_COMM_OK_TEMP_COUPLER_01_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_TEMP_COUPLER_01_POS));
        map.put(new Physics("BDevFdbkOK_TempCoupler01", "电偶01#设备通讯报文内容", BDEV_FDBK_OK_TEMP_COUPLER_01_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_TEMP_COUPLER_01_POS));

        map.put(new Physics("BDevCommOK_TempCoupler02", "电偶02#设备通讯连接", BDEV_COMM_OK_TEMP_COUPLER_02_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_TEMP_COUPLER_02_POS));
        map.put(new Physics("BDevFdbkOK_TempCoupler02", "电偶02#设备通讯报文内容", BDEV_FDBK_OK_TEMP_COUPLER_02_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_TEMP_COUPLER_02_POS));

        map.put(new Physics("BDevCommOK_TempCoupler03", "电偶03#设备通讯连接", BDEV_COMM_OK_TEMP_COUPLER_03_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_TEMP_COUPLER_03_POS));
        map.put(new Physics("BDevFdbkOK_TempCoupler03", "电偶03#设备通讯报文内容", BDEV_FDBK_OK_TEMP_COUPLER_03_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_TEMP_COUPLER_03_POS));

        map.put(new Physics("BDevCommOK_TempCoupler04", "电偶04#设备通讯连接", BDEV_COMM_OK_TEMP_COUPLER_04_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_TEMP_COUPLER_04_POS));
        map.put(new Physics("BDevFdbkOK_TempCoupler04", "电偶04#设备通讯报文内容", BDEV_FDBK_OK_TEMP_COUPLER_04_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_TEMP_COUPLER_04_POS));

        map.put(new Physics("BDevCommOK_ExtRelay01", "外部继电器模组01#设备通讯连接", BDEV_COMM_OK_EXT_RELAY_01_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_EXT_RELAY_01_POS));
        map.put(new Physics("BDevFdbkOK_ExtRelay01", "外部继电器模组01#设备通讯报文内容", BDEV_FDBK_OK_EXT_RELAY_01_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_EXT_RELAY_01_POS));

        map.put(new Physics("BDevCommOK_ExtRelay02", "外部继电器模组02#设备通讯连接", BDEV_COMM_OK_EXT_RELAY_02_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_EXT_RELAY_02_POS));
        map.put(new Physics("BDevFdbkOK_ExtRelay02", "外部继电器模组02#设备通讯报文内容", BDEV_FDBK_OK_EXT_RELAY_02_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_EXT_RELAY_02_POS));

        map.put(new Physics("BDevCommOK_Encoder01", "编码器采集模组01#设备通讯连接", BDEV_COMM_OK_ENCODER_01_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_ENCODER_01_POS));
        map.put(new Physics("BDevFdbkOK_Encoder01", "编码器采集模组01#设备通讯报文内容", BDEV_FDBK_OK_ENCODER_01_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_ENCODER_01_POS));

        map.put(new Physics("BDevCommOK_Encoder02", "编码器采集模组02#设备通讯连接", BDEV_COMM_OK_ENCODER_02_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_ENCODER_02_POS));
        map.put(new Physics("BDevFdbkOK_Encoder02", "编码器采集模组02#设备通讯报文内容", BDEV_FDBK_OK_ENCODER_02_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_ENCODER_02_POS));

        map.put(new Physics("BDevCommOK_RFID", "RFID模组设备通讯连接", BDEV_COMM_OK_RFID_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_RFID_POS));
        map.put(new Physics("BDevFdbkOK_RFID", "RFID模组设备通讯报文内容", BDEV_FDBK_OK_RFID_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_RFID_POS));

        map.put(new Physics("BDevCommOK_MultiFuncBoard", "多功能采集板通讯连接正常", BDEV_COMM_OK_MULTI_FUNC_BOARD_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_MULTI_FUNC_BOARD_POS));
        map.put(new Physics("BDevFdbkOK_MultiFuncBoard", "多功能采集板通讯报文内容正常", BDEV_FDBK_OK_MULTI_FUNC_BOARD_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_MULTI_FUNC_BOARD_POS));

        map.put(new Physics("BDevCommOK_LargeScale", "大天平变送器通讯连接正常", BDEV_COMM_OK_LARGE_SCALE_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_LARGE_SCALE_POS));
        map.put(new Physics("BDevFdbkOK_LargeScale", "大天平变送器通讯报文内容正常", BDEV_FDBK_OK_LARGE_SCALE_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_LARGE_SCALE_POS));

        map.put(new Physics("BDevCommOK_SmallScale", "小天平变送器通讯连接正常", BDEV_COMM_OK_SMALL_SCALE_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_SMALL_SCALE_POS));
        map.put(new Physics("BDevFdbkOK_SmallScale", "小天平变送器通讯报文内容正常", BDEV_FDBK_OK_SMALL_SCALE_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_SMALL_SCALE_POS));

        map.put(new Physics("BDevCommOK_Pressure", "压力传感器通讯连接正常", BDEV_COMM_OK_PRESSURE_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_COMM_OK_PRESSURE_POS));
        map.put(new Physics("BDevFdbkOK_Pressure", "压力传感器通讯报文内容正常", BDEV_FDBK_OK_PRESSURE_POS, false, true), WebUtil.generateTypeUInt8(bytes, BDEV_FDBK_OK_PRESSURE_POS));

// Tail部分
        map.put(new Physics("TailByte01", "TailByte01", TAIL_BYTE_01_POS), WebUtil.generateTypeUInt8(bytes, TAIL_BYTE_01_POS));
        map.put(new Physics("TailByte02", "TailByte02", TAIL_BYTE_02_POS), WebUtil.generateTypeUInt8(bytes, TAIL_BYTE_02_POS));
        map.put(new Physics("TailByte03", "TailByte03", TAIL_BYTE_03_POS), WebUtil.generateTypeUInt8(bytes, TAIL_BYTE_03_POS));
        map.put(new Physics("TailByte04", "TailByte04", TAIL_BYTE_04_POS), WebUtil.generateTypeUInt8(bytes, TAIL_BYTE_04_POS));

        return map;

    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("运行中与Web服务器断开，重连...");
        // 重连
        webClient.connect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage());
        ctx.close();
    }
}
