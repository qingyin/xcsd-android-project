package com.tuxing.sdk.utils;

import com.squareup.wire.Message;
import com.squareup.wire.Wire;

import java.io.IOException;

/**
 * Created by alan on 15/9/4.
 */
public class SerializeUtils {
    private static Wire wire = new Wire();

    public static <M extends Message> M parseFrom(byte[] bytes, Class<M> messageClass) throws IOException {
        return wire.parseFrom(bytes, messageClass);
    }

}
