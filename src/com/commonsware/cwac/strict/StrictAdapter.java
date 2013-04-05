/***
Copyright (c) 2012 CommonsWare, LLC

Licensed under the Apache License, Version 2.0 (the "License"); you may
not use this file except in compliance with the License. You may obtain
a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.commonsware.cwac.strict;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import com.commonsware.cwac.adapter.AdapterWrapper;

/**
 * Reports on slow-performing getView() calls. Pass the
 * regular ListAdapter to the StrictAdapter constructor, and
 * put the StrictAdapter in your AdapterView. Long-running
 * getView() calls will be logged as errors to LogCat, and
 * you can call dumpResultsToLog() to get a summary of your
 * adapter performance.
 * 
 */
public class StrictAdapter extends AdapterWrapper {
  private long calls=0L;
  private long total=0L;
  private long threshold=1000000L;
  private long penalizedCalls=0L;
  private boolean penaltyLog=true;
  private String logTag="StrictAdapter";

  /**
   * Constructor for StrictAdapter.
   * 
   * @param wrapped
   *          ListAdapter to profile
   */
  public StrictAdapter(ListAdapter wrapped) {
    super(wrapped);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.commonsware.cwac.adapter.AdapterWrapper#getView
   * (int, android.view.View, android.view.ViewGroup)
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    long start=System.nanoTime();
    View result=super.getView(position, convertView, parent);
    long delta=System.nanoTime() - start;

    calls+=1;
    total+=delta;

    if (delta > threshold) {
      applyPenalties(delta);
    }

    return(result);
  }

  /**
   * @return Number of getView() calls since construction or
   *         reset()
   */
  public long getCallCount() {
    return(calls);
  }

  /**
   * @return Total time in nanoseconds for all getView()
   *         calls since construction or reset()
   */
  public long getTotal() {
    return(total);
  }

  /**
   * Sets the time in nanoseconds to be used as the
   * threshold between OK and slow getView() calls. Defaults
   * to 1000000ns (1ms).
   * 
   * @param threshold
   *          Time in nanoseconds
   */
  public void setThreshold(long threshold) {
    this.threshold=threshold;
  }

  /**
   * Whether or not to log all getView() calls that exceed
   * the threshold. Defaults to true.
   * 
   * @param penaltyLog
   *          true if slow calls should be logged, else
   *          false
   */
  public void setPenaltyLog(boolean penaltyLog) {
    this.penaltyLog=penaltyLog;
  }

  /**
   * Sets the tag to use in all LogCat calls by this object.
   * Defaults to "StrictAdapter".
   * 
   * @param penaltyLog
   *          Tag to use.
   */
  public void setLogTag(String logTag) {
    this.logTag=logTag;
  }

  /**
   * Emits a report on the results to date to LogCat (debug
   * severity).
   */
  public void dumpResultsToLog() {
    Log.d(logTag, String.format("# calls = %d", calls));
    Log.d(logTag, String.format("# penalized calls = %d (%2.2f%%)",
                                penalizedCalls, 100.0f * penalizedCalls
                                    / calls));
    Log.d(logTag, String.format("total time = %d ns", total));

    if (calls > 0) {
      Log.d(logTag, String.format("mean time = %d ns", total / calls));
    }
  }

  /**
   * Clears the running counts. For example, you might
   * reset() in onResume() and dumpResultsToLog() in
   * onPause().
   */
  public void reset() {
    calls=0L;
    penalizedCalls=0L;
    total=0L;
  }

  /**
   * A slow call was detected, so apply all configured
   * penalties. At the moment, this just logs to LogCat if
   * so directed.
   * 
   * @param delta
   *          Time for the slow call to getView()
   */
  private void applyPenalties(long delta) {
    penalizedCalls+=1;

    if (penaltyLog) {
      Log.e(logTag,
            String.format("Call #%d, threshold = %d, actual time = %d",
                          calls, threshold, delta));
    }
  }
}
