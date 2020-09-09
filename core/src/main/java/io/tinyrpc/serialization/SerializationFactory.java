package io.tinyrpc.serialization;

public class SerializationFactory {

    private static Serialization hessianSerialization = new HessianSerialization();

    public static Serialization getSerialization(short ext) {
        return hessianSerialization;
    }
}
