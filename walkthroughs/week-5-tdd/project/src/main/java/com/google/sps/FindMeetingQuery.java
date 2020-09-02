// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import com.google.sps.TimeRange;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.lang.Math;

public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> availableSlots = new ArrayList<>(); 
    List<TimeRange> occupiedSlots = new ArrayList<>();
    Collection<String> requestAttendees = request.getAttendees();

    // If duration time is longer then a day - no option
    if(request.getDuration() > TimeRange.WHOLE_DAY.duration()){
        return new ArrayList<TimeRange>();
    }

    // Add to occupiedSlots only event slots that include people who attend meeting request
    for(Event event : events){
        Set<String> eventAttendees = event.getAttendees();
        if(!Collections.disjoint(eventAttendees, requestAttendees)){
            occupiedSlots.add(event.getWhen());
        }
    }

    // If none of the events have attendees that is in the request, all day is free
    if(occupiedSlots.size() == 0){
        availableSlots.add(TimeRange.WHOLE_DAY);
        return availableSlots;
    }

    // Sort the events by start time
    Collections.sort(occupiedSlots, TimeRange.ORDER_BY_START);

    // If there is enough free time in the morning, add that slot
    TimeRange currentEventTime = occupiedSlots.get(0);
    int freeSlotStart = TimeRange.START_OF_DAY;
    int freeSlotEnd = currentEventTime.start();
    checkPlusAddSlot(availableSlots, request, freeSlotStart, freeSlotEnd, false);

    // initialize the free slot candidate to be at the end of first event
    freeSlotStart = currentEventTime.end();
    int i = 0;
    int j = 1;
    while(i < occupiedSlots.size()){
        currentEventTime = occupiedSlots.get(i);

        // While relevent events overlap with current event, we don't have a free slot
        while((i+j) < occupiedSlots.size() && currentEventTime.overlaps(occupiedSlots.get(i+j))){
            // If the meetings ovelap, the free slot will begin only after the one that ends later
            freeSlotStart = Math.max(freeSlotStart, occupiedSlots.get(i+j).end());
            j++;
        }

        // If we reached the end of the events, then the slot will end at end of the day
        if((i+j) == occupiedSlots.size()){
            checkPlusAddSlot(availableSlots, request, freeSlotStart, TimeRange.END_OF_DAY, true);
            return availableSlots;
        }
        // Else, the event that doesn't overlap with current event, but we need to check whether there is an empty slot
        else{
            TimeRange nextEventTime = occupiedSlots.get(i+j);
            if((nextEventTime.start() - freeSlotStart) > 0){
                freeSlotEnd = nextEventTime.start();
                checkPlusAddSlot(availableSlots, request, freeSlotStart, freeSlotEnd, false);
                freeSlotStart = nextEventTime.end();
            }
            // Else, The event overlaps with an event that overlaped with current event (and freeSlotStart doesn't change), like:
            // |----|
            //   |-----------|
            //          |---|            
        }
        // If j == 1 then there was no overlapping and we move to next event
        // If j > 1 then we already added the slot after the event that ended last in the overlapping events, so we don't need to go throw them again
        i += j;
        j = 1;
    }
    return availableSlots;
  }

  private void checkPlusAddSlot(List<TimeRange> availableSlots, MeetingRequest request, int start, int end, boolean isInclusive){
      if(request.getDuration() <= (end - start)){
        availableSlots.add(TimeRange.fromStartEnd(start, end, isInclusive));
      }
  }
}
