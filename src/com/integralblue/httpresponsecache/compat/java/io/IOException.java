package com.integralblue.httpresponsecache.compat.java.io;

@SuppressWarnings("serial")
public class IOException extends java.io.IOException {

    public IOException() {
        super();
    }

    public IOException(String detailMessage) {
        super(detailMessage);
    }

    public IOException(Exception e) {
        super(e.toString());
    }
}
