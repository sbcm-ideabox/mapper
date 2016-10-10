/*
 * Copyright 2012-2016 JetBrains s.r.o
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
package jetbrains.jetpad.model.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Event Handler which logs all events for test purposes
 */
public class LoggingEventHandler<EventT> implements EventHandler<EventT> {
  private List<EventT> myEvents = new ArrayList<>();

  @Override
  public void onEvent(EventT event) {
    myEvents.add(event);
  }

  public List<EventT> getEvents() {
    return Collections.unmodifiableList(myEvents);
  }
}