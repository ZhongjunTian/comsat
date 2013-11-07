/*
 * COMSAT
 * Copyright (C) 2013, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.comsat.webactors;

import co.paralleluniverse.actors.ActorRef;
import java.nio.ByteBuffer;

public class WebDataMessage implements WebMessage {
    private final ActorRef<WebDataMessage> sender;
    private final String string;
    private final ByteBuffer byteBuffer;

    public WebDataMessage(ActorRef<? super WebDataMessage> from, String str) {
        this.sender = (ActorRef<WebDataMessage>)from;
        this.string = str;
        this.byteBuffer = null;
    }

    public WebDataMessage(ActorRef<? super WebDataMessage> from, ByteBuffer bb) {
        this.sender = (ActorRef<WebDataMessage>)from;
        this.string = null;
        this.byteBuffer = bb;
    }

    @Override
    public ActorRef<WebDataMessage> sender() {
        return sender;
    }

    public boolean isBinary() {
        return (byteBuffer != null);
    }

    @Override
    public String getStringBody() {
        return string;
    }

    @Override
    public ByteBuffer getByteBufferBody() {
        return byteBuffer;
    }
}