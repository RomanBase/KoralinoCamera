package com.ankhrom.base.common.statics;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.provider.Settings;
import android.util.Xml;

import java.io.UnsupportedEncodingException;

import com.ankhrom.base.Base;

public class NfcHelper {

    public static NfcAdapter getAdapter(Context context) {

        return NfcAdapter.getDefaultAdapter(context);
    }

    public static void openSettings(Context context) {

        if (Build.VERSION.SDK_INT > 15) {
            AppsHelper.openIntent(context, Settings.ACTION_NFC_SETTINGS);
        } else {
            AppsHelper.openIntent(context, Settings.ACTION_SETTINGS);
        }
    }

    public static String getTextNDEF(Intent intent) {

        String nfcData = null;

        if (intent != null) {
            String action = intent.getAction();

            if (ObjectHelper.equals(action, NfcAdapter.ACTION_NDEF_DISCOVERED)) {

                String type = intent.getType();
                if (ObjectHelper.equals(type, "text/plain")) {

                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                    Ndef def = Ndef.get(tag);
                    if (def == null) {
                        Base.logE("NFC", "bad tag");
                        return null;
                    }

                    NdefMessage message = def.getCachedNdefMessage();
                    NdefRecord[] records = message.getRecords();
                    for (NdefRecord record : records) {
                        if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {

                            byte[] payload = record.getPayload();
                            String textEncoding = ((payload[0] & 128) == 0) ? Xml.Encoding.UTF_8.name() : Xml.Encoding.UTF_16.name();
                            int languageCodeLength = payload[0] & 0x0063;

                            try {
                                nfcData = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }
        }

        return nfcData;
    }
}
