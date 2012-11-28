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
