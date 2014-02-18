/*
 * Copyright 2012-2014 JetBrains s.r.o
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.jetpad.base;

public class Interval {
  private int myLowerBound;
  private int myUpperBound;

  public Interval(int lowerBound, int upperBound) {
    if (lowerBound > upperBound) throw new IllegalArgumentException("Lower bound is greater than upper: lower bound=" + lowerBound + ", upper bound=" + upperBound);
    myLowerBound = lowerBound;
    myUpperBound = upperBound;
  }

  public int getLowerBound() {
    return myLowerBound;
  }

  public int getUpperBound() {
    return myUpperBound;
  }

  @Override
  public String toString() {
    return "[" + myLowerBound + ", " + myUpperBound + "]";
  }
}