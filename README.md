# touchless-travel
Stuttgart Hackathon 2015 Project - Team Hackerstolz and Friends

### Elevator Pitch
Create a real pay-as-you go solution: No more tickets, no more terminals, no more queues.
Touchless Travel is the world's first approach that leverages iBeacon technology to track public transport rides. 
With the help of iBeacons your phone recognizes the vehicle when you enter and leave, thus being able to infere the distance traveled.
At the end of the month you receive a summed bill that is automatically optimized based on the 
public transport usage. I.e. the actually used tickets are selected afterwards.

### Facts

- ~ 10 billion (10^9) passengers per year use public transport (ÖPNV) in Germany.
- ~ 60.000 buses and trains (S-Bahnen) in german public transport.
- Costs per iBeacon < 1$ when bought on a larger scale.


### Target Group

B2C: Everybody who uses public transport

B2B: Local public transport providers, taxi companies, train operators


### Technology 

1. iBeacons and end user smartphone App - Native iOS App, detecting entering and leaving of trains/taxies. The app also displays 
2. Cloud infrastructure - Scala and play framework based cloud server hosted on Heroku. It stores rides data and provides an API to integrate further systems. E.g. the public transport providers ERP systems for billing.
3. Ticket inspectors App - Scan QR codes to verify that users checked in correctly.
4. Analytics webapp - HTML5/CSS/JS webapp for public service providers to monitor the usage of vehicles in realtime.

The following figure visualizes the systems architecture:
![architecture](/readme_images/architecture.png)


### Show me something!

[Intro](https://touchless-travel.herokuapp.com/webapp/intro.html)

[Live Demo](https://touchless-travel.herokuapp.com/webapp/index.html)

![screenshot](/public/screenshot.png)


### Related Git Repositories

[iOS APP](https://github.com/settl/TouchlessTravelApp)


### Team

- Theresa Best
- Michael Dell
- Jascha Quintern
- Sophie Ettl
- Daniel Zaske
- Philip Zaske
- Norman Weisenburger

