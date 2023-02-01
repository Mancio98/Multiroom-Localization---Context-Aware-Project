package com.cas.multiroom.server.messages.localization;


import com.cas.multiroom.server.messages.Message;


public class MessageStartScan extends Message {
    private boolean start = false;

    public MessageStartScan(boolean start) {
        super("START_SCAN");
        this.start = start;
    }

    public boolean getStart() {
        return this.start;
    }
}