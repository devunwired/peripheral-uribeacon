/*
 * Copyright 2014 Google Inc. All rights reserved.
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
 */

// THIS IS MODIFIED COPY OF THE "L" PLATFORM CLASS. BE CAREFUL ABOUT EDITS.
// THIS CODE SHOULD FOLLOW ANDROID STYLE.


package org.uribeacon.scan.compat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Bluetooth LE scan settings are passed to {@link BluetoothLeScannerCompat#startScan}
 * to define the parameters for the scan.
 */
public final class ScanSettings implements Parcelable {
    /**
     * Perform Bluetooth LE scan in low power mode. This is the default scan mode as it consumes the
     * least power.
     */
    public static final int SCAN_MODE_LOW_POWER = 0;

    /**
     * Perform Bluetooth LE scan in balanced power mode. Scan results are returned at a rate that
     * provides a good trade-off between scan frequency and power consumption.
     */
    public static final int SCAN_MODE_BALANCED = 1;

    /**
     * Scan using highest duty cycle. It's recommended to only use this mode when the application is
     * running in the foreground.
     */
    public static final int SCAN_MODE_LOW_LATENCY = 2;

    /**
     * Trigger a callback for every Bluetooth advertisement found that matches the filter criteria.
     * If no filter is active, all advertisement packets are reported.
     */
    public static final int CALLBACK_TYPE_ALL_MATCHES = 1;

    /**
     * A result callback is only triggered for the first advertisement packet received that matches
     * the filter criteria.
     */
    public static final int CALLBACK_TYPE_FIRST_MATCH = 2;

    /**
     * Receive a callback when advertisements are no longer received from a device that has been
     * previously reported by a first match callback.
     */
    public static final int CALLBACK_TYPE_MATCH_LOST = 4;

    /**
     * Request full scan results which contain the device, rssi, advertising data, scan response as
     * well as the scan timestamp.
     */
    public static final int SCAN_RESULT_TYPE_FULL = 0;

    /**
     * Request abbreviated scan results which contain the device, rssi and scan timestamp.
     * <p>
     * <b>Note:</b> It is possible for an application to get more scan results than it asked for, if
     * there are multiple apps using this type.
     *
     * @hide
     */
    public static final int SCAN_RESULT_TYPE_ABBREVIATED = 1;

    // Bluetooth LE scan mode.
    private int mScanMode;

    // Bluetooth LE scan callback type
    private int mCallbackType;

    // Bluetooth LE scan result type
    private int mScanResultType;

    // Time of delay for reporting the scan result
    private long mReportDelayMillis;

    public int getScanMode() {
        return mScanMode;
    }

    public int getCallbackType() {
        return mCallbackType;
    }

    public int getScanResultType() {
        return mScanResultType;
    }

    /**
     * Returns report delay timestamp based on the device clock.
     */
    public long getReportDelayMillis() {
        return mReportDelayMillis;
    }

    private ScanSettings(int scanMode, int callbackType, int scanResultType,
            long reportDelayMillis) {
        mScanMode = scanMode;
        mCallbackType = callbackType;
        mScanResultType = scanResultType;
        mReportDelayMillis = reportDelayMillis;
    }

    private ScanSettings(Parcel in) {
        mScanMode = in.readInt();
        mCallbackType = in.readInt();
        mScanResultType = in.readInt();
        mReportDelayMillis = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mScanMode);
        dest.writeInt(mCallbackType);
        dest.writeInt(mScanResultType);
        dest.writeLong(mReportDelayMillis);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @hide
     */
    public static final Parcelable.Creator<ScanSettings>
            CREATOR = new Creator<ScanSettings>() {
                    @Override
                public ScanSettings[] newArray(int size) {
                    return new ScanSettings[size];
                }

                    @Override
                public ScanSettings createFromParcel(Parcel in) {
                    return new ScanSettings(in);
                }
            };

    /**
     * Builder for {@link ScanSettings}.
     */
    public static final class Builder {
        private int mScanMode = SCAN_MODE_LOW_POWER;
        private int mCallbackType = CALLBACK_TYPE_ALL_MATCHES;
        private int mScanResultType = SCAN_RESULT_TYPE_FULL;
        private long mReportDelayMillis = 0;

        /**
         * Set scan mode for Bluetooth LE scan.
         *
         * @param scanMode The scan mode can be one of {@link ScanSettings#SCAN_MODE_LOW_POWER},
         *            {@link ScanSettings#SCAN_MODE_BALANCED} or
         *            {@link ScanSettings#SCAN_MODE_LOW_LATENCY}.
         * @throws IllegalArgumentException If the {@code scanMode} is invalid.
         */
        public Builder setScanMode(int scanMode) {
            if (scanMode < SCAN_MODE_LOW_POWER || scanMode > SCAN_MODE_LOW_LATENCY) {
                throw new IllegalArgumentException("invalid scan mode " + scanMode);
            }
            mScanMode = scanMode;
            return this;
        }

        /**
         * Set callback type for Bluetooth LE scan.
         *
         * @param callbackType The callback type flags for the scan.
         * @throws IllegalArgumentException If the {@code callbackType} is invalid.
         */
        public Builder setCallbackType(int callbackType) {

            if (!isValidCallbackType(callbackType)) {
                throw new IllegalArgumentException("invalid callback type - " + callbackType);
            }
            mCallbackType = callbackType;
            return this;
        }

        // Returns true if the callbackType is valid.
        private boolean isValidCallbackType(int callbackType) {
            if (callbackType == CALLBACK_TYPE_ALL_MATCHES
                    || callbackType == CALLBACK_TYPE_FIRST_MATCH
                    || callbackType == CALLBACK_TYPE_MATCH_LOST) {
                return true;
            }
            return callbackType == (CALLBACK_TYPE_FIRST_MATCH | CALLBACK_TYPE_MATCH_LOST);
        }

        /**
         * Set scan result type for Bluetooth LE scan.
         *
         * @param scanResultType Type for scan result, could be either
         *            {@link ScanSettings#SCAN_RESULT_TYPE_FULL} or
         *            {@link ScanSettings#SCAN_RESULT_TYPE_ABBREVIATED}.
         * @throws IllegalArgumentException If the {@code scanResultType} is invalid.
         * @hide
         */
        public Builder setScanResultType(int scanResultType) {
            if (scanResultType < SCAN_RESULT_TYPE_FULL
                    || scanResultType > SCAN_RESULT_TYPE_ABBREVIATED) {
                throw new IllegalArgumentException(
                        "invalid scanResultType - " + scanResultType);
            }
            mScanResultType = scanResultType;
            return this;
        }

        /**
         * Set report delay timestamp for Bluetooth LE scan.
         *
         * @param reportDelayMillis Set to 0 to be notified of results immediately. Values &gt; 0
         *            causes the scan results to be queued up and delivered after the requested
         *            delay or when the internal buffers fill up.
         * @throws IllegalArgumentException If {@code reportDelayMillis} &lt; 0.
         */
        public Builder setReportDelayMillis(long reportDelayMillis) {
            if (reportDelayMillis < 0) {
                throw new IllegalArgumentException("reportDelayMillis must be > 0");
            }
            mReportDelayMillis = reportDelayMillis;
            return this;
        }

        /**
         * Build {@link ScanSettings}.
         */
        public ScanSettings build() {
            return new ScanSettings(mScanMode, mCallbackType, mScanResultType,
                    mReportDelayMillis);
        }
    }
}