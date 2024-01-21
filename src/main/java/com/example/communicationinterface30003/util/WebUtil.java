package com.example.communicationinterface30003.util;

import cn.hutool.core.util.HexUtil;
import com.example.communicationinterface30003.constant.Constants;
import com.example.communicationinterface30003.entity.Physics;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author lst
 * @date 2023年12月20日 14:26
 */
@Slf4j
public class WebUtil {

    /**
     * 检查报文首尾是否正确
     *
     * @param message 请求报文
     * @return boolean
     * @author chenyuxiang
     */
    public static boolean validate(byte[] message) {
        if ((message[Constants.HEAD_BYTE_01_POS] == Constants.MEG_SEPARATION_HEAD) && (message[Constants.HEAD_BYTE_02_POS] == Constants.MEG_SEPARATION_HEAD) && (message[Constants.HEAD_BYTE_03_POS] == Constants.MEG_SEPARATION_HEAD) && (message[Constants.HEAD_BYTE_04_POS] == Constants.MEG_SEPARATION_HEAD)

                && (message[Constants.MEG_SEPARATION_TAIL_POS1] == Constants.MEG_SEPARATION_TAIL) && (message[Constants.MEG_SEPARATION_TAIL_POS2] == Constants.MEG_SEPARATION_TAIL) && (message[Constants.MEG_SEPARATION_TAIL_POS3] == Constants.MEG_SEPARATION_TAIL) && (message[Constants.MEG_SEPARATION_TAIL_POS4] == Constants.MEG_SEPARATION_TAIL)) {
            return true;
        }
        log.error("报文头尾出现问题请排查");
        return false;
    }

    public static boolean MsgIdCheck(byte[] message) {
        byte[] msgIdBytes = generateTypeInt32(message, Constants.MSG_ID_POS);
        if (HexUtil.encodeHexStr(msgIdBytes).equalsIgnoreCase("ffffffff")) {
            return false;
        }
        return true;
    }


    public static byte[] generateTypeUInt8(byte[] bytes, int index) {
        return Arrays.copyOfRange(bytes, index, index + 1);
    }

    public static byte[] generateTypeULength(byte[] bytes, int index, int length) {
        return Arrays.copyOfRange(bytes, index, index + length);
    }

    public static byte[] generateTypeInt32(byte[] bytes, int index) {
        return Arrays.copyOfRange(bytes, index, index + 4);
    }

    public static byte[] generateTypeUInt32(byte[] bytes, int index) {
        return Arrays.copyOfRange(bytes, index, index + 4);
    }

    /**
     * 比较内存中的报文与当前的报文区别
     *
     * @param previousMessageFields
     * @param currentMessageFields
     * @author lst
     * @date 2023/12/21 10:43
     */
    public static List<Map<String, Object>> compareAndPrintChanges(Map<Physics, byte[]> previousMessageFields, Map<Physics, byte[]> currentMessageFields) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<Physics, byte[]> entry : currentMessageFields.entrySet()) {
            String previousValueResult;
            String currentValueResult;
            Physics fieldName = entry.getKey();
            byte[] currentValue = entry.getValue();
            byte[] previousValue = previousMessageFields.get(fieldName);

            if (previousValue != null && !Arrays.equals(currentValue, previousValue)) {
                Map<String, Object> resultMap = new HashMap<>();
                String str = "";

                if (previousValue.length == 4) {
                    // 采用大端变化值
                    //TODO 处理符号位
                    previousValueResult = String.valueOf(bytesToInt(swap(previousValue)));
                    currentValueResult = String.valueOf(bytesToInt(swap(currentValue)));
                    str = String.format("%s ，变化前'%d' ，变化后'%d'%n", fieldName, bytesToInt(swap(previousValue)), bytesToInt(swap(currentValue)));
                } else {
                    if (fieldName.getDetermine()) {
                        if (HexUtil.encodeHexStr(currentValue).equals("00")) {
                            System.err.printf("%s不正常!!!%n", fieldName);
                        }
                    }
                    previousValueResult = HexUtil.encodeHexStr(previousValue) + "H";
                    currentValueResult = HexUtil.encodeHexStr(currentValue) + "H";
                    str = String.format("%s ，变化前'%sH' ，变化后'%sH'%n", fieldName, HexUtil.encodeHexStr(previousValue), HexUtil.encodeHexStr(currentValue));
                }

                resultMap.put("变化字段", fieldName.getChineseName());
                resultMap.put("变化前的值", previousValueResult);
                resultMap.put("变化后的值", currentValueResult);
                if (fieldName.getDetermine()) {
                    if (HexUtil.encodeHexStr(currentValue).equals("00")) {
                        resultMap.put("状态", "不正常");
                    } else {
                        resultMap.put("状态", "正常");
                    }
                }

                resultList.add(resultMap);
                System.out.printf(str);

                // Send message to all
                WebSocketMessageUtil.sendMessageToAll(300, resultMap);
            }
        }
        return resultList;
    }


    private static void printChangedIndex(byte[] currentValue, byte[] previousValue, Physics fieldName, String str) {
        int changedIndex = getChangedIndex(currentValue, previousValue);
        if (changedIndex != -1) {
            int currentIndex = changedIndex + fieldName.getIndex();
            str += String.format("变化的起始位置索引: %d, 相对位置：%d%n", currentIndex, changedIndex);
        }
    }


    /**
     * 大端读取算法
     *
     * @param previousValue
     * @return byte[]
     * @author lst
     * @date 2023/12/25 16:03
     */
    private static byte[] swap(byte[] previousValue) {
        byte[] bytes = new byte[4];
        bytes[0] = previousValue[3];
        bytes[1] = previousValue[2];
        bytes[2] = previousValue[1];
        bytes[3] = previousValue[0];
        return bytes;
    }

    private static int getChangedIndex(byte[] currentValue, byte[] previousValue) {
        int minLen = Math.min(currentValue.length, previousValue.length);
        for (int i = 0; i < minLen; i++) {
            if (currentValue[i] != previousValue[i]) {
                return i;
            }
        }
        return -1; // No change found
    }

    public static int bytesToInt(byte[] bytes) {
        int value;
        value = ((bytes[0] & 0xff) << 24) |
                ((bytes[1] & 0xff) << 16) |
                ((bytes[2] & 0xff) << 8) |
                (bytes[3] & 0xff);
        return value;
    }


}
