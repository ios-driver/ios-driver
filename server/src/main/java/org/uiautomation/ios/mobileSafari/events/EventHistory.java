/*
 * Copyright 2012 ios-driver committers.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uiautomation.ios.mobileSafari.events;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.mobileSafari.events.inserted.ChildIframeInserted;

public class EventHistory {
  private static final Logger log = Logger.getLogger(EventHistory.class.getName());
  private final List<Event> events = new CopyOnWriteArrayList<Event>();
  private static final long MAX_AGE = 10 * 1000;

  public EventHistory() {

  }

  public void add(Event e) {
    if (events.size() >= 50) {
      removeOldEvents();
    }
    events.add(e);
  }

  public void removeEvent(Event e) {
    events.remove(e);
  }

  private void removeOldEvents() {
    int removed = 0;
    for (Event e : events) {
      if (e.getAge() > MAX_AGE) {
        if (events.remove(e)) {
          removed++;
        }
      }
    }
    if (removed == 0) {
      log.warning("events history is growing too fast.");
    }
  }

  public List<ChildIframeInserted> getInsertedFrames(NodeId parent) {
    List<ChildIframeInserted> res = new ArrayList<ChildIframeInserted>();
    for (Event e : events) {
      if (e instanceof ChildIframeInserted && ((ChildIframeInserted) e).getParent().equals(parent)) {
        res.add((ChildIframeInserted) e);
      }
    }
    return res;
  }

}
