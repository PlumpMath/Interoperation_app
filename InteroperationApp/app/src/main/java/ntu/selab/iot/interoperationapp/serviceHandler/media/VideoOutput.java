package ntu.selab.iot.interoperationapp.serviceHandler.media;

import java.io.IOException;
import java.nio.ByteBuffer;

import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.model.ScenarioModel;
import ntu.selab.iot.interoperationapp.serviceHandler.IPCameraControlHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.IPCameraQosHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.p2p.IPCameraStreamingHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.MediaInfo;
import ntu.selab.iot.interoperationapp.utils.Tuple;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

public class VideoOutput{
	private static final String TAG = "VideoOutput";
    private ScenarioModel scenarioModel;
	private MediaCodec decoder=null;
	private Surface surface=null;
    // size of a frame, in pixels
	private MediaInfo mediaInfo;
    private String uuid;
//    private int mWidth = 1280;
//    private int mHeight = 720;
    private int mWidth = 640;
    private int mHeight = 480;
//  private int mWidth = 800;
//  private int mHeight = 600;
//    private Buffer input=null;
//    private Queue<Tuple<Byte[], Long, Integer>> inputBuffer;

	private static String H_264 = "video/avc";
    public boolean decoding = true;

	
	private ByteBuffer[] inputBuffers;
	private ByteBuffer[] outputBuffers;
	
	public VideoOutput(ScenarioModel scenarioModel, String uuid, MediaInfo mediaInfo) {
		Log.d(TAG,"v-create");
        this.scenarioModel=scenarioModel;
		this.mediaInfo=mediaInfo;
        this.uuid=uuid;
	}

    private MediaFormat initMediaFormat(String type){
        MediaFormat format = MediaFormat.createVideoFormat(type, mWidth, mHeight);
        byte[] sps=addSeperation(mediaInfo.getSPS());
        byte[] pps=addSeperation(mediaInfo.getPPS());
        format.setByteBuffer("csd-0", ByteBuffer.wrap(sps));
        format.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
        return format;
    }

    public boolean initDecoeder(String type){
        try {
            decoder = MediaCodec.createDecoderByType(type);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void confDecoder(MediaFormat format){
        decoder.configure(format, surface,null, 0);
        decoder.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        decoder.start();
        inputBuffers = decoder.getInputBuffers();
        outputBuffers = decoder.getOutputBuffers();
    }

    public void init(){
        MediaFormat format = initMediaFormat(H_264);
        initDecoeder(H_264);
        confDecoder(format);
    }



	
	public void setSurface(Surface surface){
		Log.d(TAG,"v-setSurface");
		this.surface = surface;
	}

    public Surface getSurface(){
        return surface;
    }

    /**
     * Sets the desired frame size and bit rate.
     */
    public void setParameters(int width, int height) {
        if ((width % 16) != 0 || (height % 16) != 0) {
            Log.w(TAG, "WARNING: width or height not multiple of 16");
        }
        mWidth = width;
        mHeight = height;
    }

    
    public void setMediaInfo(MediaInfo mediaInfo){
    	this.mediaInfo=mediaInfo;
    } 
    
    public MediaInfo getMediaInfo(){
    	return mediaInfo;
    } 



	public void setNalu(Tuple<Byte[], Long, Integer> naluInfo){
//		if (play == true) {

        if (naluInfo._1() == null) {
            Log.e(TAG, "nalu._1 is null ");
            return;
        }

        byte[] nalu = new byte[naluInfo._1().length];
        for (int i = 0; i < naluInfo._1().length; i++) {
            //          Log.i(TAG,"nalu: " + IPCameraStreamingHandler.bytesToHex(nalu._1()));
            if (naluInfo._1()[i] == null) {
                Log.e(TAG, "nalu._1 " + i + " is null ");
                return;
            }
            nalu[i] = naluInfo._1()[i];
        }
        Log.d(TAG, "v-Decoding_nalu_timestamp" + naluInfo._2());
        Log.d(TAG, "v-Docoding_nalu_seqnum" + naluInfo._3());
        Log.d(TAG, "v-Decoding_nalu_length" + naluInfo._1().length);
        if(decoder!=null) {
            decode(nalu);
        }
	}

    private void decode( byte[] nalu){
//        Log.e(TAG,"decode");
        decoding=true;
        BufferInfo info = new BufferInfo();
        long startMs = System.currentTimeMillis();
        int inIndex = getAvailableInputBufferIndex();

//        Log.e(TAG, "InputBuffer index " + inIndex);
        ByteBuffer buffer = inputBuffers[inIndex];
        buffer.clear();


        //For HTC
        nalu = addSeperation(nalu);

        buffer.put(nalu);
        int sampleSize = nalu.length;
        Log.d(TAG, "naluSize: " + sampleSize);
        if (sampleSize < 0) {
            // We shouldn't stop the playback at this point, just pass the EOS
            // flag to decoder, we will get it again from the
            // dequeueOutputBuffer
            Log.d(TAG, "I-InputBuffer BUFFER_FLAG_END_OF_STREAM");
            decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
        } else {
            Log.d(TAG, "I-queueInputBuffer");
            //			decoder.queueInputBuffer(inIndex, 0, sampleSize, naluInfo._2(), 0);
            decoder.queueInputBuffer(inIndex, 0, sampleSize, 0, 0);
        }


        int outIndex = decoder.dequeueOutputBuffer(info, 10000);
        switch (outIndex) {
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                Log.e(TAG, "I-INFO_OUTPUT_BUFFERS_CHANGED");
                outputBuffers = decoder.getOutputBuffers();
                break;
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                Log.e(TAG, "New format " + decoder.getOutputFormat());
                break;
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                Log.e(TAG, "dequeueOutputBuffer timed out!");
                break;
            default:
                Log.d(TAG, "I-decode successed!");
                while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                decoder.releaseOutputBuffer(outIndex, true);
                break;
        }
        decoding = false;
    }


    public int getAvailableInputBufferIndex(){
        int inIndex;
        try {
            inIndex = decoder.dequeueInputBuffer(10000);
            int i = 0;
            while (inIndex < 0) {
                inIndex = decoder.dequeueInputBuffer(10000);
                Log.e(TAG, "No available InputBuffer times:" + ++i);
            }
        }catch(IllegalStateException e){
            Log.e(TAG,"IllegalStateException occur when doing dequeueInputBuffer.");
            MediaFormat format = initMediaFormat(H_264);
            decoder.reset();
            confDecoder(format);
            inIndex = getAvailableInputBufferIndex();
        }
        return inIndex;
    }


    public void shutDown(){

        Log.d(TAG, "shutDown");
        decoder.stop();
        decoder.release();
        decoder=null;
    }


    public void stopDecode(){
        IPCameraStreamingHandler ipCameraStreamingHandler = (IPCameraStreamingHandler)(((GatewayModel)scenarioModel).getVideos(uuid)[0]);
        ipCameraStreamingHandler.stopRead();
    }
    public void startDecode(){
        IPCameraStreamingHandler ipCameraStreamingHandler = (IPCameraStreamingHandler)(((GatewayModel)scenarioModel).getVideos(uuid)[0]);
        ipCameraStreamingHandler.startRead();
    }

    public void flushDepacketizer(){
        IPCameraStreamingHandler ipCameraStreamingHandler = (IPCameraStreamingHandler)(((GatewayModel)scenarioModel).getVideos(uuid)[0]);
        ipCameraStreamingHandler.flushDepacketizer();
    }

    public void stopSendPak(GatewayModel gatewayModel, String uuid){
//        IPCameraControlHandler ipCameraControlHandler = (IPCameraControlHandler)(((GatewayModel)scenarioModel).getVideos(uuid)[0]);
//        ipCameraControlHandler.stopSend();


    }
    public void startSendPak(GatewayModel gatewayModel, String uuid){
        IPCameraControlHandler ipCameraControlHandler = (IPCameraControlHandler)(((GatewayModel)scenarioModel).getVideos(uuid)[0]);
        ipCameraControlHandler.startSend();
    }

    public void stopSendRTCP(){
        IPCameraQosHandler ipCameraQosHandler = (IPCameraQosHandler)(((GatewayModel)scenarioModel).getVideos(uuid)[2]);
        ipCameraQosHandler.stopTrasmite();
    }
    public void startSendRTCP(){
        IPCameraQosHandler ipCameraQosHandler = (IPCameraQosHandler)(((GatewayModel)scenarioModel).getVideos(uuid)[2]);
        ipCameraQosHandler.startTrasmite();
    }
	
    private byte[] addSeperation(byte[] header){
    	byte[] seperation = {0,0,0,1};
    	byte[] b = new byte[seperation.length+header.length];
    	System.arraycopy(seperation,0,b,0,seperation.length);
		System.arraycopy(header,0,b,seperation.length,header.length);
		return b;
    }


}

