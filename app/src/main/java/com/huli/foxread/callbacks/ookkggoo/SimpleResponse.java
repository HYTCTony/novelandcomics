package com.huli.foxread.callbacks.ookkggoo;

import java.io.Serializable;

public class SimpleResponse implements Serializable {

    private static final long serialVersionUID = -1577609349345966116L;

    public int error_code;
    public String msg;

    public LzyResponse toLzyResponse() {
        LzyResponse lzyResponse = new LzyResponse();
        lzyResponse.error_code = error_code;
        lzyResponse.msg = msg;
        return lzyResponse;
    }
}