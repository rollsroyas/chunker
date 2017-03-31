package net.chunker.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Special Thanks to http://stackoverflow.com/questions/11659396/using-a-jprogressbar-with-saxparser
 * A class that monitors the read progress of an input stream.
 *
 * @author Hermia Yeung "Sheepy"
 * @since 2012-04-05 18:42
 */
public class MonitoredInputStream extends FilterInputStream {
   private volatile long mark = 0;
   private volatile long lastTriggeredLocation = 0;
   private volatile long location = 0;
   private final int threshold;


   /**
    * Creates a MonitoredInputStream over an underlying input stream.
    * @param in Underlying input stream, should be non-null because of no public setter
    * @param threshold Min. position change (in byte) to trigger change event.
    */
   public MonitoredInputStream(InputStream in, int threshold) {
      super(in);
      this.threshold = threshold;
   }

   /**
    * Creates a MonitoredInputStream over an underlying input stream.
    * Default threshold is 16KB, small threshold may impact performance impact on larger streams.
    * @param in Underlying input stream, should be non-null because of no public setter
    */
   public MonitoredInputStream(InputStream in) {
      super(in);
      this.threshold = 1024*16;
   }

   public long getProgress() { return location; }

   protected void triggerChanged( final long location ) {
      if ( threshold > 0 && Math.abs( location-lastTriggeredLocation ) < threshold ) return;
      lastTriggeredLocation = location;
   }


   @Override public int read() throws IOException {
      final int i = super.read();
      if ( i != -1 ) triggerChanged( location++ );
      return i;
   }

   @Override public int read(byte[] b, int off, int len) throws IOException {
      final int i = super.read(b, off, len);
      if ( i > 0 ) triggerChanged( location += i );
      return i;
   }

   @Override public long skip(long n) throws IOException {
      final long i = super.skip(n);
      if ( i > 0 ) triggerChanged( location += i );
      return i;
   }

   @Override public void mark(int readlimit) {
      super.mark(readlimit);
      mark = location;
   }

   @Override public void reset() throws IOException {
      super.reset();
      if ( location != mark ) triggerChanged( location = mark );
   }
}
