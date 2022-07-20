# ppp


Ppp is a tool that helps users plan their next PC build.
It lets them easily browse and search through currently available components on the market and see their recent and historical prices.

![Image](https://i.imgur.com/ZfzN1EJ.png)

# Running locally
## Backend:
### Prerequisites:
- Java 17+
- A running MySQL instance set up with appropriate database and user, which can be specified in application.properties
### Start a local server:
```sh
git clone https://github.com/marko-tisma/ppp.git
cd ppp/backend
bash mvnw spring-boot:run
```
## Frontend:
### Prerequisites:
- npm version 8.11.0+
### Installation:
```sh
git clone https://github.com/marko-tisma/ppp.git
cd ppp/frontend
npm install
```
### Start a local dev server:
```sh
npm run dev
```
