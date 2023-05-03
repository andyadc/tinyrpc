package io.tinyrpc.protocol.test;

import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.enumeration.RpcType;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.protocol.header.RpcHeaderFactory;
import io.tinyrpc.protocol.request.RpcRequest;

public class Tests {

	public static RpcProtocol<RpcRequest> getRpcProtocol() {
		RpcHeader header = RpcHeaderFactory.getRequestHeader("jdk", RpcType.REQUEST.getType());
		RpcRequest body = new RpcRequest();
		body.setOneway(false);
		body.setAsync(false);
		body.setClassName("io.tinyrpc.demo.RpcProtocol");
		body.setMethodName("hello");
		body.setGroup("adc-g-1");
		body.setParameters(new Object[]{"andyadc"});
		body.setParameterTypes(new Class[]{String.class});
		body.setVersion("1.0.0");
		RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
		protocol.setBody(body);
		protocol.setHeader(header);
		return protocol;
	}
}
