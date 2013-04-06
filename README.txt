Adaptive Alarm Clock - Application 1

Sujay Narsale, Scott Nellenbach, and Mike Roda
CSC750-601 Spring 2013


Requirements:
1. Android 2.3.3

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

