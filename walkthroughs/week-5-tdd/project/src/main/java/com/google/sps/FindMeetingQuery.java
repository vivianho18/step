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

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import com.google.sps.TimeRange;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<String> allAttendees = new ArrayList<>();
    allAttendees.addAll(request.getAttendees());
    allAttendees.addAll(request.getOptionalAttendees());
    Collection<TimeRange> optionsConsideringAllAttendees = getAvailableTimes(events, request, allAttendees);
    if (optionsConsideringAllAttendees.size() > 0) {
      return optionsConsideringAllAttendees;
    } else {
      return getAvailableTimes(events, request, request.getAttendees());
    }
  }

  private Collection<TimeRange> getAvailableTimes(Collection<Event> events, MeetingRequest request, Collection<String> attendees) {
    // Store the times of events that attendees are already attending and sort by start time 
    ArrayList<TimeRange> unavailabilities = new ArrayList<>();
    HashSet<String> attendeesHash = new HashSet<String>(); 
    attendeesHash.addAll(attendees); 
    
    for (Event event : events) {
        for (String busyAttendee : event.getAttendees()) {
            if (attendeesHash.contains(busyAttendee)) {
                unavailabilities.add(event.getWhen()); 
            }
        }
    }

    // Use helper function to merge the unavailable time ranges 
    ArrayList<TimeRange> mergedUnavailabilities = mergeTimes(unavailabilities); 

    // Find the list of times available 
    ArrayList<TimeRange> availablities = new ArrayList<>(); 
    long duration = request.getDuration(); 
    int startMeeting = TimeRange.START_OF_DAY; 

    // Loop through the list of unavailable times, compare the time between startMeeting variable and start of each unavailable time range to find times that are greater than or equal to meeting request duration. Update startMeeting variable to equal the end of the unavailable time range after each iteration. 
    for (TimeRange range : mergedUnavailabilities) {
        if (range.start() - startMeeting >= duration) {
            TimeRange available = TimeRange.fromStartEnd(startMeeting, range.start(), false); 
            availablities.add(available); 
        }
        startMeeting = range.end(); 
    }
    if (TimeRange.END_OF_DAY - startMeeting >= duration) {
        TimeRange available = TimeRange.fromStartEnd(startMeeting, TimeRange.END_OF_DAY, true); 
        availablities.add(available); 
    }
    return availablities; 
  }

  // Helper function to merge times ranges given in an ArrayList
  // Returns an ArrayList of time ranges without any overlap in sorted order 
  private ArrayList<TimeRange> mergeTimes(ArrayList<TimeRange> ranges) {
    ranges.sort(TimeRange.ORDER_BY_START); 
    ArrayList<TimeRange> mergedTimes = new ArrayList<>(); 
    for (TimeRange range : ranges) {
        if (mergedTimes.size() == 0) {
            mergedTimes.add(range); 
        } else if (!mergedTimes.get(mergedTimes.size() - 1).overlaps(range)) {
            mergedTimes.add(range); 
        } else {
            TimeRange oldRange = mergedTimes.remove(mergedTimes.size() - 1); 
            int start = oldRange.start(); 
            int end = Math.max(oldRange.end(), range.end()); 
            TimeRange newRange = TimeRange.fromStartEnd(start, end, false); 
            mergedTimes.add(newRange);
        }
    }
    return mergedTimes; 
  }
}
