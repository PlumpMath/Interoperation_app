/*******************************************************************************
 * Software Name : RCS IMS Stack
 *
 * Copyright (C) 2010 France Telecom S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package ntu.selab.iot.interoperationapp.protocol.rtp;

import java.io.IOException;

import android.util.Log;

/**
 * RTP packet receiver
 * 
 * @author jexa7410
 */
public class RtpPacketReceiver {
	private final static String TAG = "RtpPacketReceiver";

	/**
	 * Statistics
	 */
	private RtpStatisticsReceiver stats = new RtpStatisticsReceiver();

	/**
	 * RTCP Session
	 */
	private RtcpSession rtcpSession = null;

	public RtpPacketReceiver(RtcpSession rtcpSession) throws IOException {
		this.rtcpSession = rtcpSession;
	}

	/**
	 * Read a RTP packet (blocking method)
	 * 
	 * @return RTP packet
	 */
	public RtpPacket readRtpPacket(byte[] input) {
		try {

			byte[] data = input;
			// Log.d(TAG,"I-(readRtpPacket) input.length:"+data.length);
			// Parse the RTP packet
			RtpPacket pkt = parseRtpPacket(data);

			// Drop the keep-alive packets
			if ((pkt != null) && (pkt.payloadType != 20)) {
				// Update statistics
				stats.numPackets++;
				stats.numBytes += data.length;

				RtpSource s = rtcpSession.getMySource();
				s.activeSender = true;
				s.timeOfLastRTPArrival = rtcpSession.currentTime();
				s.updateSeq(pkt.seqnum);
				if (s.noOfRTPPacketsRcvd == 0)
					s.base_seq = pkt.seqnum;
				s.noOfRTPPacketsRcvd++;
				return pkt;
			} else {
				return null;
			}

		} catch (Exception e) {
			stats.numBadRtpPkts++;
			return null;
		}
	}

	/**
	 * Parse the RTP packet
	 * 
	 * @param data
	 *            RTP packet not yet parsed
	 * @return RTP packet
	 */
	private RtpPacket parseRtpPacket(byte[] data) {
		RtpPacket packet = new RtpPacket();
		try {
			// Read RTP packet length
			packet.length = data.length;

			// Set received timestamp
			packet.receivedAt = System.currentTimeMillis();

			// Read marker
			if ((byte) ((data[1] & 0xff) & 0x80) == (byte) 0x80) {
				packet.marker = 1;
			} else {
				packet.marker = 0;
			}

			// Read payload type
			packet.payloadType = (byte) ((data[1] & 0xff) & 0x7f);

			// Read seq number

			packet.seqnum = (((data[2] & 0xff) << 8) | (data[3] & 0xff));
			
			//Read timestamp
			packet.timestamp = (((long) data[4] & 0xff) << 24)
					| (((long) data[5] & 0xff) << 16)
					| (((long) data[6] & 0xff) << 8) | (((long) data[7] & 0xff));

			// Read SSRC
			packet.ssrc = (((data[8] & 0xff) << 24) | ((data[9] & 0xff) << 16)
					| ((data[10] & 0xff) << 8) | (data[11] & 0xff));

			// Read media data after the 12 byte header which is constant
			packet.payloadoffset = 12;
			packet.payloadlength = packet.length - packet.payloadoffset;
			packet.data = new byte[packet.payloadlength];
			System.arraycopy(data, packet.payloadoffset, packet.data, 0,
					packet.payloadlength);
		} catch (Exception e) {
			// if (logger.isActivated()) {
			// logger.error("RTP packet parsing error", e);
			// }
			return null;
		}
		return packet;
	}

	/**
	 * Returns the statistics of RTP reception
	 * 
	 * @return Statistics
	 */
	public RtpStatisticsReceiver getRtpReceptionStats() {
		return stats;
	}

	public static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}
}