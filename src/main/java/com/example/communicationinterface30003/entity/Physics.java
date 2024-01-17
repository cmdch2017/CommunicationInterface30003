package com.example.communicationinterface30003.entity;

import lombok.Data;

/**
 * @author lst
 * @date 2023年12月20日 17:11
 */
@Data
public class Physics {
    private String englishName;
    private String chineseName;
    private Integer index;
    /**
     * 使用符号位
     *
     * @author lst
     * @date 2023/12/25 11:01
     * @param null
     * @return null
     */
    private Boolean useSign = false;
    /**
     * 判断0不正常，1正常
     *
     * @author lst
     * @date 2023/12/27 10:57
     * @param null
     * @return null
     */
    private Boolean determine = false;

    public Physics(String englishName, String chineseName, Integer index) {
        this.englishName = englishName;
        this.chineseName = chineseName;
        this.index = index;
    }

    public Physics(String englishName, String chineseName, Integer index, Boolean useSign) {
        this.englishName = englishName;
        this.chineseName = chineseName;
        this.index = index;
        this.useSign = useSign;
    }

    public Physics(String englishName, String chineseName, Integer index, Boolean useSign, Boolean determine) {
        this.englishName = englishName;
        this.chineseName = chineseName;
        this.index = index;
        this.useSign = useSign;
        this.determine = determine;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + englishName + '\'' +
                ", 中文名称='" + chineseName +
                "'}";
    }

}
