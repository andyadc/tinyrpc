package io.tinyrpc.serialization;

public class SerializationFactory {

    private static Serialization hessianSerialization = new HessianSerialization();

    public static Serialization get(byte type) {
        switch (type & 0x7) {
            case 0x0:
                return hessianSerialization;
            default:
                return new HessianSerialization();
        }
    }
}
