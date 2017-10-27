/*
 * Copyright (c) 2017 original authors and authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.syyllab.broke;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.syyllab.broke.channel.PipelineInitializer;

/**
 * The Server class used as an entry instance.
 * An example creation,
 * <code>
 *     Server srv = new Server(port, ctx).run();
 * </code>
 */
public class Server {

    private int port;

    private final ServerContext ctx;

    /**
     * The Server constructor construct a basic information of a Server.
     * @param port the binding port.
     * @param ctx the {@link SeverContext} instance for associated information.
     */
    public Server(int port, ServerContext ctx) {
        this.port = port;
        this.ctx = ctx;
    }

    /**
     * Run the server.
     * @throws Exception
     */
    public void run() throws Exception {
        // TODO: May move these codebase into another class for scalability.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap boots = new ServerBootstrap();

            boots.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new PipelineInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture after = boots.bind(port).sync();
            after.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Server is running at 0.0.0.0:8181");
        // TODO: Blocking currently.
        // TODO: Prefer to have a return binding for server as an interaction.
        new Server(8181, new ServerContext()).run();
    }
}
