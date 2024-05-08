# AttendEase

An android app designed for seamless event organization and attendee management.

## Table of Contents:

- [Team Members](#team-members)
- [About The Project](#about-the-project)
- [Compatibility](#compatibility)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Wiki Page and Acknowledgments](#wiki-page-and-acknowledgments)
- [Demonstration Video](#demonstration-video)

## Team Members:

| Name | CCID | GitHub Username |
| ------------- | ------------- | ------------- |
| Sean Hill | srhill1 | [TonyWonder27](https://github.com/TonyWonder27) |
| Rushabh Shah | rnshah | [rushabhshah02](https://github.com/rushabhshah02) |
| Imran Amin | iamin | [papanarmi](https://github.com/papanarmi) |
| Muhammad Hamza | mhamza2 | [muhammadhamza14210](https://github.com/muhammadhamza14210) |
| Atharva Tamore | atamore | [atharvatamore](https://github.com/atharvatamore) |
| Aaron DeCosta | aarondom | [aarondecosta](https://github.com/aarondecosta) |


## About The Project:

An android app for event management, enabling quick QR code-based check-ins and efficient attendee tracking.
This application supports multi-user roles, integrates with Firebase for real-time updates, and offers optional geolocation functionalities.
This project is part of the University of Alberta CMPUT 301 course built by Team 54.

## Compatibility:

All android phones with API greater than API 24 (i.e most phones in the past several years)

## Tech Stack:

<img src = "https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white"/> <img src = "https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/> <img src = "https://img.shields.io/badge/firebase-ffca28?style=for-the-badge&logo=firebase&logoColor=black"/>

## Features:

- **QR Code Scanning:**  
  Enables attendees to quickly check in by scanning event-specific QR codes on their mobile devices.

- **Firebase Integration:**  
  Leverages Firebase to store event details, maintain attendee lists, and provide real-time updates on check-ins.

- **Multi-User Interaction:**  
  Supports distinct roles and permissions for organizers and attendees to enhance user interaction and management capabilities.

- **Geolocation Verification:**  
  Offers optional geolocation services to verify the physical presence of attendees at the event location during check-ins.

- **Image Upload:**  
  Allows organizers to upload event posters and attendees to upload profile pictures, adding a personal touch to the event experience.

## Wiki Page and Acknowledgments:

<!-- MAKE JAVADOCS PUBLIC AND ADD HERE! -->
[See our Wiki page here!](https://github.com/CMPUT301-W24-T54/AttendEase/wiki)

Libraries Used:
- https://github.com/zxing/zxing - For QR Code Scanning and Generation
- https://github.com/bumptech/glide - For URL to Image converison
- https://chat.openai.com/ - Used OpenAI ChatGPT 3.5 to generate Javadocs for some of our source code


Reference Links:
- Conversion to an image format from a URL:  
  https://medium.com/@cpvasani48/how-to-load-an-image-from-url-in-android-using-glide-8a067e3a00db

- Share images to other apps:  
  https://medium.com/@atifsayings/get-bitmap-from-imageview-android-studio-java-39b77cec0db6
  https://developer.android.com/training/basics/intents/sending

- Tests pass when run individually but not when the whole test class run:  
  https://stackoverflow.com/questions/26561511/tests-pass-when-run-individually-but-not-when-the-whole-test-class-run

## Demonstration Video:

https://github.com/CMPUT301-W24-T54/AttendEase/assets/151209547/2b553c7f-3f9e-45b7-995a-5fdd98f9d27e


