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
import java.util.Arrays;
import java.lang.Math;

/** Class that finds available slots to hold a meeting */
public final class FindMeetingQuery {

  /** Returns all the available time slots to schedule the meeting */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> requiredOccupiedSlots = new ArrayList<>();
    List<TimeRange> occupiedSlotsWithOptional = new ArrayList<>();
    Collection<TimeRange> availableSlotsWithOptional;

    // If duration time is longer than a day - no option
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()){
        return new ArrayList<TimeRange>();
    }

    // Add to occupied slots only event slots that include people who attend meeting request
    for (Event event : events){
        Set<String> eventAttendees = event.getAttendees();
        if (!Collections.disjoint(eventAttendees, request.getAttendees())){
            requiredOccupiedSlots.add(event.getWhen());
        }
        if (!Collections.disjoint(eventAttendees, request.getOptionalAttendees())){
            occupiedSlotsWithOptional.add(event.getWhen());
        }
    }
    // Add to optional occupied slots the occupied slots of required attendees
    occupiedSlotsWithOptional.addAll(requiredOccupiedSlots);
    
    availableSlotsWithOptional = findAvailableSlots(occupiedSlotsWithOptional, request.getDuration());
    // If we can't find a slot with optional attendees, we try to find one without them
    if (availableSlotsWithOptional.isEmpty()){
        return findAvailableSlots(requiredOccupiedSlots, request.getDuration());
    }
    return availableSlotsWithOptional;
  }

  /** Gets a list of occupied slots and meeting duration and returns all the free slots available that have enough time */
  private Collection<TimeRange> findAvailableSlots(List<TimeRange> occupiedSlots, long duration){
    Collection<TimeRange> availableSlots = new ArrayList<>();

    // If none of the events have attendees or optional attendees that is in the request, all day is free
    if (occupiedSlots.isEmpty()){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    // Sort the events by start time
    Collections.sort(occupiedSlots, TimeRange.ORDER_BY_START);

    // If there is enough free time in the morning, add that slot
    TimeRange currentEventTime = occupiedSlots.get(0);
    int freeSlotStart = TimeRange.START_OF_DAY;
    int freeSlotEnd = currentEventTime.start();
    checkPlusAddSlot(availableSlots, duration, freeSlotStart, freeSlotEnd, false);

    // initialize the free slot candidate to be at the end of first event
    freeSlotStart = currentEventTime.end();
    int i = 0;
    while(i < occupiedSlots.size()){
        int j = 1;
        currentEventTime = occupiedSlots.get(i);

        // While relevent events overlap with current event, we don't have a free slot
        while((i+j) < occupiedSlots.size() && currentEventTime.overlaps(occupiedSlots.get(i+j))){
            // If the meetings overlap, the free slot will begin only after the one that ends later
            freeSlotStart = Math.max(freeSlotStart, occupiedSlots.get(i+j).end());
            j++;
        }

        // If we reached the end of the events, then the slot will end at end of the day
        if ((i+j) == occupiedSlots.size()){
            checkPlusAddSlot(availableSlots, duration, freeSlotStart, TimeRange.END_OF_DAY, true);
            return availableSlots;
        }
        // Else, the event that doesn't overlap with current event, but we need to check whether there is an empty slot between them
        else {
            TimeRange nextEventTime = occupiedSlots.get(i+j);
            freeSlotEnd = nextEventTime.start();
            checkPlusAddSlot(availableSlots, duration, freeSlotStart, freeSlotEnd, false);
            // Move the start of the free slot, if needed
            freeSlotStart = Math.max(freeSlotStart, nextEventTime.end());
        }
        // Because we already checked the overlapping events, we don't need to go through them again
        i += j;
    }
    return availableSlots;
  }
  
  /** Checks if the time slot is greater or equel to the duration required, and if so adds it to the available slots. */
  private void checkPlusAddSlot(Collection<TimeRange> availableSlots, long duration, int start, int end, boolean isInclusive){
      if (duration <= (end - start)){
        availableSlots.add(TimeRange.fromStartEnd(start, end, isInclusive));
      }
  }  
}
