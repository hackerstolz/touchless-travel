# touchless-travel
Stuttgart Hackathon 2015 Project - Team Hackerstolz and Friends

### Elevator Pitch
Create a real pay-as-you go solution: No more tickets, no more terminals, no more queues.
Touchless Travei is the worlds first approach that leverages iBeacon technology to track public transport rides. Your phone recognizes the vehicle when you enter and leave, thus being able to infere the distance traveled.
At the end of the month users receive a summed bill that is automatically optimized based on the 
public transport usage. I.e. the actually used tickets are selected afterwards.

### Technology 

1. iBeacons and end user smartphone App - Native iOS App, detecting entering and leaving of trains/taxies. The app also displays 
2. Cloud infrastructure - Scala and play framework based cloud server hosted on Heroku. It stores rides data and provides an API to integrate further systems. E.g. the public transport providers ERP systems for billing.
3. Ticket inspectors App - Scan QR codes to verify that users checked in correctly.
4. Analytics webapp - HTML5/CSS/JS webapp for public service providers to monitor the usage of vehicles in realtime.

The following figure visualizes the systems architecture:
![architecture](/readme_images/architecture.png)
