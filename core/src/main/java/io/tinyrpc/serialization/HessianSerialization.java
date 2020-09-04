package io.tinyrpc.serialization;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerialization implements Serialization {

    @Override
    public <T> byte[] serialize(T t) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput out = new HessianOutput(os);
        out.writeObject(t);
        return os.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        HessianInput input = new HessianInput(is);
        return (T) input.readObject(clazz);
    }
}
