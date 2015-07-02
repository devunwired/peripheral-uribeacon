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
package org.uribeacon.scan.testing;

import org.uribeacon.scan.util.Clock;

/**
 * A fake clock implementation, for testing.
 */
public class FakeClock implements Clock {

  private long nowMillis = 1000000000L;

  @Override public long currentTimeMillis() {
    return nowMillis;
  }
  
  public void advance(long millis) {
    nowMillis += millis;
  }

  @Override public long elapsedRealtimeNanos() {
    return nowMillis * 1000 * 1000;
  }
}
