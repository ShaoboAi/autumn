package com.pingnotes.server;

import com.google.protobuf.Message;
import com.pingnotes.proto.InternalPb;
import com.pingnotes.proto.PbUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.prometheus.client.Counter;

import java.util.Map;

/**
 * Created by shaobo
 */
public class DispatchingHandler extends ChannelInboundHandlerAdapter {
    static final Counter requests = Counter.build().name("requests_total").help("Total requests.").register();
    static final Counter failedRequests = Counter.build().name("requests_failed_total").help("Total failed requests.").register();

    private ServiceDefinitionContainer container = ServiceDefinitionContainer.getInstance();

    public DispatchingHandler() {
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            //todo metric switch
            requests.inc();
            if (msg instanceof InternalPb.InternalRequest) {
                InternalPb.InternalRequest pbRequest = (InternalPb.InternalRequest) msg;
                String mSign = PbUtils.methodSignature(pbRequest);
                CallMethod callMethod = container.get(mSign);
                if (callMethod == null) {
                    //todo throw exception
                }
                Message param = callMethod.getParameter().getParserForType().parseFrom(pbRequest.getParameter());

                Message resp = (Message) callMethod.getMethod().invoke(callMethod.getInstance(), param);

                InternalPb.InternalResponse response = InternalPb.InternalResponse
                        .newBuilder()
                        .setResponseId(pbRequest.getRequestId())
                        .setResponseContent(resp.toByteString()).build();
                ctx.write(response);
            } else {
                //todo this is a exception
            }
        } catch (Exception e) {
            failedRequests.inc();
            throw e;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
