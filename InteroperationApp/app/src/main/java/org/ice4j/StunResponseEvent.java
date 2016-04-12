/*
 * ice4j, the OpenSource Java Solution for NAT and Firewall Traversal.
 * Maintained by the SIP Communicator community (http://sip-communicator.org).
 *
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.ice4j;

import org.ice4j.message.Request;
import org.ice4j.message.Response;
import org.ice4j.stack.RawMessage;
import org.ice4j.stack.StunStack;
import org.ice4j.stack.TransactionID;

/**
 * The class is used to dispatch incoming STUN {@link org.ice4j.message.Response}s. Apart from
 * the {@link org.ice4j.message.Response} itself this event also carries a reference to the
 * {@link org.ice4j.message.Request} that started the corresponding transaction as well as other
 * useful things.
 *
 * @author Emil Ivov
 */
public class StunResponseEvent
    extends StunMessageEvent
{
    /**
     * Serial version UID for this Serializable class.
     */
    private static final long serialVersionUID = -1L;

    /**
     * The original {@link org.ice4j.message.Request} that started the client transaction that
     * the {@link org.ice4j.message.Response} carried in this event belongs to.
     */
    private final Request request;

    /**
     * Creates a new instance of this event.
     *
     * @param stunStack the <tt>StunStack</tt> to be associated with the new
     * instance
     * @param rawMessage the crude message we got off the wire.
     * @param response the STUN {@link org.ice4j.message.Response} that we've just received.
     * @param request  the message itself
     * @param transactionID a reference to the exact {@link org.ice4j.stack.TransactionID}
     * instance that represents the corresponding client transaction.
     */
    public StunResponseEvent(
            StunStack stunStack,
            RawMessage rawMessage,
            Response response,
            Request request,
            TransactionID transactionID)
    {
        super(stunStack, rawMessage, response);
        this.request = request;
        super.setTransactionID(transactionID);
    }

    /**
     * Returns the {@link org.ice4j.message.Request} that started the transaction that this
     * {@link org.ice4j.message.Response} has just arrived in.
     *
     * @return the {@link org.ice4j.message.Request} that started the transaction that this
     * {@link org.ice4j.message.Response} has just arrived in.
     */
    public Request getRequest()
    {
        return request;
    }

    /**
     * Returns the {@link org.ice4j.message.Response} that has just arrived and that caused this
     * event.
     *
     * @return  the {@link org.ice4j.message.Response} that has just arrived and that caused this
     * event.
     */
    public Response getResponse()
    {
        return (Response)getMessage();
    }
}
