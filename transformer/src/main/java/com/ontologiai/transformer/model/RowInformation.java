package com.ontologiai.transformer.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class RowInformation {
    String rev;
    Double nr;
    String outstation;
    String deviceTag;
    String pointDescriptor;
    String pidNr;
    String ioType;
    String description;
    String power;
    String signalType;
    String converter;
    String splitter;
    String rangeLow;
    String rangeHigh;
    String unit;
    String accuracy;
    String mfgr;
    String model;
    String calCertReq;
    String techDatasheetNr;
    String signalTypeHc900;
    String rangeLowHc900;
    String rangeHighHc900;
    String unitHc900;
    String remarkHc900;
    String ebiPointDescriptor;
    String ebiTag;
    String hcdTag;
    String remarks;
    String roomNumber;
    String slotNo;
    String moduleNo;
    String channelNo;
}