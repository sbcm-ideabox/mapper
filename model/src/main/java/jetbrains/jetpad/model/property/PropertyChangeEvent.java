/*
 * Copyright 2012-2013 JetBrains s.r.o
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
package jetbrains.jetpad.model.property;

public class PropertyChangeEvent<ValueT> {
  private ValueT myOldValue;
  private ValueT myNewValue;

  public PropertyChangeEvent(ValueT oldValue, ValueT newValue) {
    myOldValue = oldValue;
    myNewValue = newValue;
  }

  public ValueT getOldValue() {
    return myOldValue;
  }

  public ValueT getNewValue() {
    return myNewValue;
  }

  @Override
  public String toString() {
    return myOldValue + " -> " + myNewValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PropertyChangeEvent that = (PropertyChangeEvent) o;

    if (myNewValue != null ? !myNewValue.equals(that.myNewValue) : that.myNewValue != null) return false;
    if (myOldValue != null ? !myOldValue.equals(that.myOldValue) : that.myOldValue != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = myOldValue != null ? myOldValue.hashCode() : 0;
    result = 31 * result + (myNewValue != null ? myNewValue.hashCode() : 0);
    return result;
  }
}
