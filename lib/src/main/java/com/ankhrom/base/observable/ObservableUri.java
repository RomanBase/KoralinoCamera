package com.ankhrom.base.observable;

import android.databinding.ObservableField;
import android.net.Uri;

public class ObservableUri extends ObservableField<Uri> {

    public ObservableUri(Uri value) {
        super(value);
    }

    public ObservableUri() {
    }
}
