/*
 * ice4j, the OpenSource Java Solution for NAT and Firewall Traversal.
 * Maintained by the SIP Communicator community (http://sip-communicator.org).
 *
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.ice4j.stunclient;

import org.ice4j.StunException;
import org.ice4j.StunMessageEvent;
import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.attribute.Attribute;
import org.ice4j.attribute.MappedAddressAttribute;
import org.ice4j.attribute.XorMappedAddressAttribute;
import org.ice4j.message.Message;
import org.ice4j.message.MessageFactory;
import org.ice4j.message.Response;
import org.ice4j.socket.IceSocketWrapper;
import org.ice4j.stack.StunStack;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class provides basic means of discovering a public IP address. All it
 * does is send a binding request through a specified port and return the
 * mapped address it got back or <tt>null</tt> if there was no response.
 *
 * @author Emil Ivov
 */
public class SimpleAddressDetector
{
    /**
     * Our class logger.
     */
    private static final Logger logger
        = Logger.getLogger(SimpleAddressDetector.class.getName());

    /**
     * The stack to use for STUN communication.
     */
    private StunStack stunStack = null;

    /**
     * The address of the stun server
     */
    private TransportAddress serverAddress = null;

    /**
     * A utility used to flatten the multi-threaded architecture of the Stack
     * and execute the discovery process in a synchronized manner
     */
    private BlockingRequestSender requestSender = null;

    /**
     * Creates a StunAddressDiscoverer. In order to use it one must start the
     * discoverer.
     * @param serverAddress the address of the server to interrogate.
     */
    public SimpleAddressDetector(TransportAddress serverAddress)
    {
        this.serverAddress = serverAddress;
    }

    /**
     * Returns the server address that this detector is using to run stun
     * queries.
     *
     * @return StunAddress the address of the stun server that we are running
     * stun queries against.
     */
    public TransportAddress getServerAddress()
    {
        return serverAddress;
    }

    /**
     * Shuts down the underlying stack and prepares the object for garbage
     * collection.
     */
    public void shutDown()
    {
        stunStack = null;
        requestSender = null;
    }

    /**
     * Puts the discoverer into an operational state.
     */
    public void start()
    {
        stunStack = new StunStack();
    }

    /**
     * Creates a listening point for the specified socket and attempts to
     * discover how its local address is NAT mapped.
     * @param socket the socket whose address needs to be resolved.
     * @return a StunAddress object containing the mapped address or null if
     * discovery failed.
     *
     * @throws java.io.IOException if something fails along the way.
     * @throws java.net.BindException if we cannot bind the socket.
     */
    public TransportAddress getMappingFor(IceSocketWrapper socket)
       throws IOException, BindException
    {
         TransportAddress localAddress = new TransportAddress(
              (InetSocketAddress)socket.getLocalSocketAddress(), Transport.UDP);

         stunStack.addSocket(socket);

         requestSender = new BlockingRequestSender(stunStack, localAddress);
         StunMessageEvent evt = null;
         try
         {
             evt = requestSender.sendRequestAndWaitForResponse(
                 MessageFactory.createBindingRequest(), serverAddress);
         }
         catch(StunException exc)
         {
             //this shouldn't be happening since we are the one that constructed
             //the request, so let's catch it here and not oblige users to
             //handle exception they are not responsible for.
             logger.log(Level.SEVERE, "Internal Error. We apparently "
                        +"constructed a faulty request.", exc);
             return null;
         }
         finally
         {
             stunStack.removeSocket(localAddress);
         }

        if(evt != null)
        {
            Response res = (Response)evt.getMessage();

             /* in classic STUN, the response contains a MAPPED-ADDRESS */
             MappedAddressAttribute maAtt = (MappedAddressAttribute)
                                 res.getAttribute(Attribute.MAPPED_ADDRESS);
             if(maAtt != null)
             {
                  return maAtt.getAddress();
             }

             /* in STUN bis, the response contains a XOR-MAPPED-ADDRESS */
             XorMappedAddressAttribute xorAtt = (XorMappedAddressAttribute)res
                 .getAttribute(Attribute.XOR_MAPPED_ADDRESS);
             if(xorAtt != null)
             {
               byte xoring[] = new byte[16];

               System.arraycopy(Message.MAGIC_COOKIE, 0, xoring, 0, 4);
               System.arraycopy(res.getTransactionID(), 0, xoring, 4, 12);

              return xorAtt.applyXor(xoring);
            }
       }

       return null;
   }
}
