package com.pingnotes.proto;

/**
 * Created by shaobo.
 */
public final class PbUtils {
    public static String methodSignature(InternalPb.InternalRequest pb) {
        StringBuilder sbd = new StringBuilder(pb.getServiceName())
                .append(".")
                .append(pb.getMethodName())
                .append("(")
                .append(pb.getParamType())
                .append(")");
        return sbd.toString();
    }
}
