Adaptive Alarm Clock - Application 1

Sujay Narsale, Scott Nellenbach, and Mike Roda
CSC750-601 Spring 2013


Requirements:
1. Android 2.3.3

General features of the adaptive alarm application:

- Up to 4 independent alarms are supported
- Each alarm can be one of 3 types:
    1. A simple alarm for a specified time
    2. An alarm to catch a specified flight
    3. An alarm to drive to an event and arrive at a specific time
- Each alarm stores an optimum preparation time and a minimum preparation time.
  Hitting snooze reduces the available preparation time for an event and is allowed
  until the minimum preparation time is hit. If the minimum time is reached,
  snooze is inhibited and messages to wake up user are sent via sms or available bluetooth. 
  
Context information used:
- Flight time information via Flightaware api
- Drive time information via Bing maps api
- User activity level estimating need for sleep via accelerometer/screen activity
- Local friends to be contacted by bluetooth pairing  
    
This project covers 3 complete scenarios and utilizes 3 sources of contextual information:

Scenario 1. A sales professional is scheduled to make multiple business pitches in 
a single day tour of Chicago. The day before he estimates the times he would have to
leave each venue to get in time to the next. He set alarms for when he must wrap up 
at each talk. But he underestimates Chicago's traffic and leaves very little time for
travel.

  * Context: A travel time agent checks the estimated travel time to the event based 
  on traffic conditions and adjusts the alarm time based on the optimum/minimum 
  pre-travel time setup when the alarm was scheduled. This agent use a free REST API
  provided by Bing Maps to get traffic information. An API key is built into the agent. 

Scenario 2. A professor who has spent all night preparing a presentation, has an 
early morning flight to a conference. But the flight is delayed by two hours. He ends 
up losing sleep unnecessarily since the alarm is set for too early now.

  * Context: A flight agent continually checks the departure time of upcoming flights
    and updates the alarm time based on the optimum/minimum pre-flight time setup when
    the alarm was scheduled. This agent uses the Flightaware XML commercial API which
    requires a paying account. You must set the account information via the Preferences
    button. Please contact mwroda@ncsu.edu for the account information to test this out.

Scenario 3. A student living in a Hostel sets the alarm before an exam, but ends up 
repeatedly snoozing the alarm and reached late for the exam. This would hardly have
happened if there was somebody around to make sure she wakes up

  * Context: A user agent will send SMS text messages to nearby acquaintances when the
    snooze button on the alarm has been activated over limit. The agent uses bluetooth
    to locate people nearby and cross-references them with the contact information in 
    the phone (display name must be same as bluetooth name). Note, this feature 
    requires an actual bluetooth-capable device; when running on the emulator, it will
    SMS text a predefined number 555-123-4567.

Note: The fourth scenario (student wakes up late to catch a delayed bus) is not supported
    currently as query of bus transit information is not included.  However, this
    scenario would be supported in cases where the delay was due to a flight change
    or a delay in drive time due to traffic.