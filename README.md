# Live Flight Radar ✈️

A real-time flight tracking web app focused on Polish airspace. The backend fetches live data from the OpenSky Network API and streams it directly to a browser client using WebSockets.

## 📸 Screenshots
*Main radar view using the CartoDB Dark Matter base map.*
<img width="1906" height="948" alt="1" src="https://github.com/user-attachments/assets/817eb266-244c-41f0-8b22-bd1821ad8460" />

*Flight telemetry (altitude, speed, ICAO24) displayed on click.*
<img width="197" height="136" alt="2" src="https://github.com/user-attachments/assets/28fdb9a7-bad3-46f7-9ae0-f9f84553d8e9" />

## ✨ Features

* **Real-time updates:** Polls the OpenSky API every 3 seconds for active flights.
* **WebSocket streaming (STOMP):** Pushes data to connected clients without page reloads.
* **Dynamic map markers:** SVG plane icons automatically rotate to match their actual heading (`trueTrack`). Planes are added, updated, or removed from the map on the fly.
* **OAuth2 Authentication:** Automatically handles token negotiation and refresh for the OpenSky API.

## 🛠 Tech Stack

**Backend:**
* Java / Spring Boot
* Spring WebFlux (`WebClient` for async REST calls)
* Spring WebSocket / STOMP (Message Broker)
* Thymeleaf (serves the entry point)

**Frontend:**
* Vanilla JS, HTML5, CSS3
* Leaflet.js (map rendering)
* SockJS + STOMP.js (WebSocket connection)

## 🚀 Running locally

### Prerequisites
* Java 17+
* Gradle
* Active [OpenSky Network](https://opensky-network.org/) API credentials

### How it works under the hood
1. A Spring @Scheduled task queries the OpenSky API for a specific bounding box (Poland).
2. The authorization service checks the OAuth2 token and refreshes it if necessary.
3. The raw JSON is parsed, filtered, and immediately pushed to the local STOMP message broker.
4. The server broadcasts the payload via WebSockets to all connected browsers.
5. The frontend script receives the data, calculates deltas (identifies new, existing, and removed aircraft based on ICAO24), and updates the Leaflet map accordingly.

### Setup & Run

1. Clone the repository:
```bash
git clone <repo-url>
```
2. Set your OpenSky API credentials in src/main/resources/application.properties:
```Properties
opensky.clientid=YOUR_CLIENT_ID
opensky.clientsecret=YOUR_CLIENT_SECRET
```
3. Run the application using gradle:
```bash
./gradlew bootRun
```
4. Open your browser and go to http://localhost:8080.
