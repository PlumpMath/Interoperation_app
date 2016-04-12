package ntu.selab.iot.interoperationapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ntu.selab.iot.interoperationapp.activity.MainActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

/**
 * This helper class download images from the Internet and binds those with the provided ImageView.
 *
 * <p>It requires the INTERNET permission, which should be added to your application's manifest
 * file.</p>
 *
 * A local cache of downloaded images is maintained internally to improve performance.
 */
public class ImageDownloader {
    private static final String LOG_TAG = "ImageDownloader";
    private static ImageDownloader imageDownloader = null;
    public enum Mode { NO_ASYNC_TASK, NO_DOWNLOADED_DRAWABLE, CORRECT }
    private Mode mode = Mode.CORRECT;
    
    // For implementing the Singleton design pattern
    private ImageDownloader(){    	
    
    }
    
    public static ImageDownloader getNewInstance(){
    	if(imageDownloader == null) imageDownloader = new ImageDownloader();
    	return imageDownloader;
    }
    
    /**
     * Download the specified image from the Internet and binds it to the provided ImageView. The
     * binding is immediate if the image is found in the cache and will be done asynchronously
     * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
     *
     * @param url The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public Bitmap download(String url){
    	System.out.println(url);
    	//url = "http://140.115.113.164:8080/wardrobe/wardrobedata/pic/15b0cfe6f85d45b9b09d6cc4f8d139b749f1c58c4c8e4ec6982d843116b70aca-R.PNG";
    	Bitmap bitmap = null;
    	bitmap = downloadBitmap(url);
    	if(bitmap == null)
    	System.out.print("bitmap is null!!");
    	addBitmapToCache(url, bitmap);
    	return bitmap;
    }
    public void download(String url, ImageView imageView) {
        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(url);

        if (bitmap == null) {
            forceDownload(url, imageView);
        } else {
            cancelPotentialDownload(url, imageView);
            imageView.setImageBitmap(bitmap);
        }
    }
    
    public void download(String url, ImageSwitcher imageSwitcher) {
    	//resetPurgeTimer();
    	Bitmap bitmap = getBitmapFromCache(url);
    	if (bitmap == null) {
            //do nothing
    		//forceDownload(url, imageSwitcher);
        } else {
            //cancelPotentialDownload(url, imageSwitcher);            
            BitmapDrawable drawable = new BitmapDrawable(imageSwitcher.getResources(), bitmap);
            drawable.setAntiAlias(true);
        	imageSwitcher.setImageDrawable(drawable);            
        }
    }
    /*
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
       private void forceDownload(String url, ImageView view) {
          forceDownload(url, view, null);
       }
     */

    /**
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
     */
    private void forceDownload(String url, ImageView imageView) {
        // State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
        if (url == null) {
            imageView.setImageDrawable(null);
            return;
        }

        if (cancelPotentialDownload(url, imageView)) {
            switch (mode) {
                case NO_ASYNC_TASK:
                    Bitmap bitmap = downloadBitmap(url);
                    addBitmapToCache(url, bitmap);
                    imageView.setImageBitmap(bitmap);
                    break;

                case NO_DOWNLOADED_DRAWABLE:
                    imageView.setMinimumHeight(156);
                    BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
                    task.execute(url);
                    break;

                case CORRECT:
                    task = new BitmapDownloaderTask(imageView);
                    DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
                    imageView.setImageDrawable(downloadedDrawable);
                    //imageView.setMinimumHeight(156);
                    task.execute(url);
                    break;
            }
        }
    }

    /**
     * Returns true if the current download has been canceled or if there was no download in
     * progress on this image view.
     * Returns false if the download in progress deals with the same url. The download is not
     * stopped in that case.
     */
    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active download task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    private Bitmap downloadBitmap(String url) {
        //If the cache directory not exist, create the directory.
    	File bufferFolder = new File(MainActivity.image_cache_dir);
		if(!bufferFolder.exists()) bufferFolder.mkdirs();
    	//If it is in the local file, get it from there.
    	File imageFile = new File(MainActivity.image_cache_dir + getFileName(url));
		if(imageFile.exists()) {
			System.out.println("get image from " + imageFile.getAbsolutePath());
			return BitmapFactory.decodeFile(imageFile.getAbsolutePath());						
		}else{
			System.out.println("image " + imageFile.getAbsolutePath() + " doesn't exist!");
		}
    
    	final int IO_BUFFER_SIZE = 4 * 1024;

        // AndroidHttpClient is not allowed to be used from the main thread
//        final HttpClient client = (mode == Mode.NO_ASYNC_TASK) ? new DefaultHttpClient() :
//            AndroidHttpClient.newInstance("Android");
//        final HttpGet getRequest = new HttpGet(url);
         HttpClient client = new DefaultHttpClient();
         HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url);
                return null;
            }else{
            	Log.w("ImageDownloader", "HttpStatus.SC_OK " + statusCode +
                        " while retrieving bitmap from " + url);
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    // return BitmapFactory.decodeStream(inputStream);
                    // Bug on slow connections, fixed in future release.
                    
                    FlushedInputStream flushedInputStream = new FlushedInputStream(inputStream);
                    // Need to be fixed: Save the loaded picture directly and transfer into bitmap then.
                    FileOutputStream fout = null;
            		try {
        				fout = new FileOutputStream(imageFile);				
        				 /*
                         * Read bytes to the Buffer until there is nothing more to read(-1).
                         */
        				ByteArrayBuffer baf = new ByteArrayBuffer(512);
                        int current = 0;
                        while ((current = flushedInputStream.read()) != -1) {
                                baf.append((byte) current);
                        }
                        fout.write(baf.toByteArray());                      
        				//System.out.println("bitmap.compress " + imageFile.getAbsolutePath());
            		} catch (FileNotFoundException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}finally{
        				if(fout != null){
        					try {
        						fout.flush();
        						fout.close();
        					} catch (IOException e) {						
        						e.printStackTrace();
        					}					
        				}	           
        			}  		                      
                    //return BitmapFactory.decodeStream(flushedInputStream);
        			return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            getRequest.abort();
            Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
            getRequest.abort();
            Log.w(LOG_TAG, "Incorrect URL: " + url);
        } catch (Exception e) {
            getRequest.abort();
            Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return null;
    }

    /*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    /**
     * The actual AsyncTask that will asynchronously download the image.
     */
    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            return downloadBitmap(url);
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            addBitmapToCache(url, bitmap);
            
            // write to cache file at the same time
            // Defect: Because transfer into bitmap, we can't save as GIF with transparent background
            // saveBitmapToCacheFile(url, bitmap);
            
            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                // Change bitmap only if this process is still associated with it
                // Or if we don't use any bitmap to task association (NO_DOWNLOADED_DRAWABLE mode)
                if ((this == bitmapDownloaderTask) || (mode != Mode.CORRECT)) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }


    /**
     * A fake Drawable that will be attached to the imageView while the download is in progress.
     *
     * <p>Contains a reference to the actual download task, so that a download task can be stopped
     * if a new binding is required, and makes sure that only the last started download process can
     * bind its result, independently of the download finish order.</p>
     */
    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.BLACK);
            bitmapDownloaderTaskReference =
                new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        clearCache();
    }

    
    /*
     * Cache-related fields and methods.
     * 
     * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the
     * Garbage Collector.
     */
    
    private static final int HARD_CACHE_CAPACITY = 20;
    private static final int DELAY_BEFORE_PURGE = 300 * 1000; // in milliseconds

    // Hard cache, with a fixed maximum capacity and a life duration
    private static final HashMap<String, Bitmap> sHardBitmapCache =
        new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) { // initial capacity = total capacity / 2 
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
        	// Log.w("ImageDownloader", "removeEldestEntry() has been invoked: " + eldest.getKey() + " size()=" + size());
        	// Log.w("ImageDownloader", "Entries are transferred to soft reference cache");           
        	if (size() > HARD_CACHE_CAPACITY) {
        		Log.w("ImageDownloader", "Entries are transferred to soft reference cache");
                // Entries push-out of hard reference cache are transferred to soft reference cache
                sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                return true;
            } else
            	Log.w("ImageDownloader", "Entries are Not transferred to soft reference cache");
                return false;
        }
    };

    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
        new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);

    private final static Handler purgeHandler = new Handler();

    private final static Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };

    /**
     * Adds this bitmap to the cache.
     * @param bitmap The newly downloaded bitmap.
     */
    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, bitmap);
            }
        }
    }

    /**
     * Saves the bitmap to local disk file as a cache.
     * @param url
     * @param bitmap
     */
    private void saveBitmapToCacheFile(String url, Bitmap bitmap){
    	
    	if(bitmap != null){
    		File bufferFolder = new File(MainActivity.image_cache_dir);
    		if(!bufferFolder.exists()) bufferFolder.mkdirs();
    					
    		File imageFile = new File(MainActivity.image_cache_dir + getFileName(url));
    		//System.out.println("saveBitmapToCacheFile " + imageFile.getAbsolutePath());
    		if(imageFile.exists()) {
    			//If the file already exist, don't save it again.
    			return;
    			//imageFile.delete();
    		}
    		FileOutputStream fout = null;
    		try {
				fout = new FileOutputStream(imageFile);				
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);	
				//System.out.println("bitmap.compress " + imageFile.getAbsolutePath());
    		} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(fout != null){
					try {
						fout.flush();
						fout.close();
					} catch (IOException e) {						
						e.printStackTrace();
					}					
				}	           
			}  		    		
    	}
    }
    
    public static String getFileName(String path) {
        //path: "http://photosaaaaa.net/photos-ak-snc1/v315/224/13/659629384/s659629384_752969_4472.jpg"
        String filename = "";
             
        //Checks for both forward and/or backslash 
        //NOTE:**While backslashes are not supported in URL's 
        //most browsers will autoreplace them with forward slashes
        //So technically if you're parsing an html page you could run into 
        //a backslash , so i'm accounting for them here;
        String[] pathContents = path.split("[\\\\/]");
        if(pathContents != null){
                int pathContentsLength = pathContents.length;
                //System.out.println("Path Contents Length: " + pathContentsLength);
                for (int i = 0; i < pathContents.length; i++) {
                        System.out.println("Path " + i + ": " + pathContents[i]);
                }
                //lastPart: s659629384_752969_4472.jpg
                String lastPart = pathContents[pathContentsLength-1];
                String[] lastPartContents = lastPart.split("\\.");
                if(lastPartContents != null && lastPartContents.length > 1){
                        int lastPartContentLength = lastPartContents.length;
                        //System.out.println("Last Part Length: " + lastPartContentLength);
                        //filenames can contain . , so we assume everything before
                        //the last . is the name, everything after the last . is the 
                        //extension
                        String name = "";
                        for (int i = 0; i < lastPartContentLength; i++) {
                                //System.out.println("Last Part " + i + ": "+ lastPartContents[i]);
                                if(i < (lastPartContents.length -1)){
                                        name += lastPartContents[i] ;
                                        if(i < (lastPartContentLength -2)){
                                                name += ".";
                                        }
                                }
                        }
                        String extension = lastPartContents[lastPartContentLength -1];
                        filename = name + "." +extension;
                        //System.out.println("Name: " + name);
                        //System.out.println("Extension: " + extension);
                        //System.out.println("Filename: " + filename);
                }
        }
        return filename;
    }
    /**
     * @param url The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    public Bitmap getBitmapFromCache(String url) {
        // First try the hard reference cache
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(url);
            if (bitmap != null) {
            	//Log.w("ImageDownloader", "Bitmap found in hard cache " + " while retrieving bitmap from " + url);
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
            	// This maybe is not correct. Because when building sHardBitmapCache, the accessOrder is set to true. This means that the ordering should be done based on the last access
                //sHardBitmapCache.remove(url);
                //sHardBitmapCache.put(url, bitmap);
  
            	return bitmap;
            }else{
            	//Log.w("ImageDownloader", "Bitmap not found in hard cache " + " while retrieving bitmap from " + url);
            }
        }

        // Then try the soft reference cache
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
            	//Log.w("ImageDownloader", "Bitmap found in soft cache " + " while retrieving bitmap from " + url);
            	// Bitmap found in soft cache
                return bitmap;
            } else {
                // Soft reference has been Garbage Collected
            	//Log.w("ImageDownloader", "Bitmap not found in soft cache " +  " while retrieving bitmap from " + url);
                sSoftBitmapCache.remove(url);
            }
        }

        return null;
    }
 
    /**
     * Clears the image cache used internally to improve performance. Note that for memory
     * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
     */
    public static void clearCache() {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }

    /**
     * Allow a new delay before the automatic cache clear is done.
     */
    private void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }
}
