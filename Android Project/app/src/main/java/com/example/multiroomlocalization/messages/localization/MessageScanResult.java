package com.example.multiroomlocalization.messages.localization;

import com.example.multiroomlocalization.localization.ScanResult;
import com.example.multiroomlocalization.messages.Message;


public class MessageScanResult extends Message {
	private final ScanResult[] fingerprint;

	public MessageScanResult(ScanResult[] fingerprint) {
		super("SCAN_INFO");
        this.fingerprint = fingerprint;
	}
    public ScanResult[] getFingerprint() {
        return this.fingerprint;
    }

}

