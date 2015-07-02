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
//
// Changes:
//   Added an '@Suppress unused' to the inputs of onScanFailed().
//   Added a bit more javadoc to the onScanResult() method.

package org.uribeacon.scan.compat;

import java.util.List;

/**
 * Bluetooth LE scan callbacks. Scan results are reported using these callbacks.
 *
 * @see BluetoothLeScannerCompat#startScan
 */
public abstract class ScanCallback {
    /**
     * Fails to start scan as BLE scan with the same settings is already started by the app.
     */
    public static final int SCAN_FAILED_ALREADY_STARTED = 1;

    /**
     * Fails to start scan as app cannot be registered.
     */
    public static final int SCAN_FAILED_APPLICATION_REGISTRATION_FAILED = 2;

    /**
     * Fails to start scan due an internal error
     */
    public static final int SCAN_FAILED_INTERNAL_ERROR = 3;

    /**
     * Fails to start power optimized scan as this feature is not supported.
     */
    public static final int SCAN_FAILED_FEATURE_UNSUPPORTED = 4;

    /**
     * Callback when a BLE advertisement has been found.
     *
     * @param callbackType Determines why this callback was triggered:
     *   ScanSettings.CALLBACK_TYPE_FIRST_MATCH: the first (recent) scan result for this beacon
     *   ScanSettings.CALLBACK_TYPE_ALL_MATCHES: a regular scan result
     *   ScanSettings.CALLBACK_TYPE_MATCH_LOST: a lost match indication
     * @param result A Bluetooth LE scan result.
     */
    public void onScanResult(int callbackType, ScanResult result) {
    }

    /**
     * Callback when batch results are delivered.
     *
     * @param results List of scan results that are previously scanned.
     */
    public void onBatchScanResults(List<ScanResult> results) {
    }

    /**
     * Callback when scan could not be started.
     * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
     */
    public void onScanFailed(@SuppressWarnings("unused") int errorCode) {
    }
}