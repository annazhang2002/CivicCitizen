Voting App - Original App Design Project
===

# Vote4Action

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Users can find information about elections they can vote in and ways to stay civically involved. Based on their location, they can see local elections and local polling offices, as well as the candidates running. 

### App Evaluation
- **Category:** Education/Information
- **Mobile:** The mobile app would allow you to share and interact with other users while a web version could simply provide you with information about the elections/representatives in your area. 
- **Story:** Overall, I hope by creating this app, I can encourage people to stay civically engaged in order to empower them to vote for a government that best represents everyone. Voting rates are extremely low because information is not easily accessible, resulting in decreased representation, especially for low-income and minority communities. 
- **Market:** The market is for anyone who struggles to stay informed about elections and voting days, especially in local government. It is also a good way for people who want their voice to do more and vote in ways that push legislation they want.
- **Habit:** A crucial aspect of this app would be to send notifications to the user about elections after they enter their address. That way, people do not need to check the app everyday for new information about upcoming elections. 
- **Scope:** The very basic MVP is an app just provides notifications about elections the user can participate in as well as information about how to vote. Hopefully though, I can add more information about candidates running, and also other ways to engage civically like messaging representatives and voting on propositions. 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users can create a profile with their location
* Users can view a list of all elections they can participate in
* Users get notifications about upcoming deadlines and elections
* Users have access to information about how to vote, register to vote, absentee ballots, and voting by mail options
* Users can see a list of all of their local representatives (and contact information)
* Each election can be clicked on to show a detailed information screen which shows...
    * candidates running
    * a map of local polling locations
    * important links to vote


**Optional Nice-to-have Stories**

* Users can click on a candidate to learn about their basic policies and party alignment
* Users can send messages to their representatives within the app
* Users can save "civic actions" that they have done like voting, registering to vote, messaging representatives
    * They can also upload images with these actions too
* Users can share information about elections with their friends via message or social media

### 2. Screen Archetypes

* Upcoming Elections
   * Show list of all elections the user can participate in
   * when an election is clicked it opens the election details screen
* Election Details
   * Checklist for deadlines regarding the election (register by, send mailing ballot by etc)
   * Candidates on the ballot
   * Propositions on the ballot
   * List/Map of polling locations to vote at 
* Voting Information FAQs
   * steps for how to vote by state
   * List of FAQs in regards to how to vote
* Local Representatives
    * List of all representatives for the user with information about contact, districts and office location
* User Profile Page
    * Users can edit their location
    * Users can upload profile picture
    * Users can change notification settings
    * Shows list of actions the user has taken / saved
* Log-in page
    * Users can create a new profile
    * Users can log into their account

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Homepage (Election List)
* Representatives List
* Voting Information
* Profile Page

**Flow Navigation** (Screen to Screen)
* Election List
   * On item click -> Election Details Screen
* Election Details Screen
    * Can click on candidate -> Candidate information
    * click on polling location to open map
* Representative List
    * on representative click -> can email them in app
* Profile Page
    * on upload profile picture -> launch camera

## Wireframes
![](https://i.imgur.com/Zs3livW.jpg)

## Schema

### Models
#### User

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user (default field) |
   | profile       | File     | image that for the user's profile picture |
   | state        | String   | state user is located in |
   | zip           | Number   | zip code of where the user lives to find their voting district |
   | createdAt     | DateTime | date when user account is created (default field) |
   | updatedAt     | DateTime | date when user account is last updated (default field) |

#### Action
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the action (default field) |
   | profile       | File     | optional picture for the user to upload when completing that action |
   | category      | String   | string specifying what kind of action it was (i.e. register, vote, contactRepresentative) |
   | note         | String   | any note the user wants to write about that action to remember |
   | createdAt     | DateTime | date when user account is created (default field) |
   | updatedAt     | DateTime | date when user account is last updated (default field) |
   
### Networking
#### List of network requests by screen
   - User Profile Page
      - (Read/GET) Query information about the user for profile
      - (Update/PUT) Update the user's profile (location, picture etc)
   - Election List Screen
       - (Read/GET) Query all elections where the user is in the appropriate location
   - Election Details Screen
      - (Create/POST) Create a user civic action
      - (Delete) Delete an action
      - (Read/GET) Query all of the polling locations
#### [OPTIONAL:] Existing API Endpoints
##### Google Civic Information API
- Base URL - [https://www.googleapis.com/civicinfo/v2](https://www.googleapis.com/civicinfo/v2)

   HTTP Verb | Endpoint | Description
   ----------|----------|------------
   GET  |/elections | List of available elections to query.
  GET  |/voterinfo | Looks up information relevant to a voter based on the voter's registered address. Required query parameters: address
    GET  | /representatives |	Looks up political geography and representative information for a single address.
    GET  | /representatives/ocdId |Looks up representative information for a single geographic division.


##### Vote Smart API
- Base URL - [http://api.votesmart.org/CandidateBio.getBio?key=<your_key>&candidateId=9490](http://api.votesmart.org/CandidateBio.getBio?key=<your_key>&)

   HTTP Verb | Endpoint | Description
   ----------|----------|------------
    `GET`    | /getByElection | gets candidates by the electionId
    `GET`    | /candidateId | gets specific candidate by :id