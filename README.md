MyKnowledge is a Kotlin application where it enables users to retrieve their weather information at their corresponding GPS location
In this project:

### Registration

1. Users will be required to register user through FirebaseAuthentication. The required data for registration are name, email 
and password, where email and password will be validated prior to registration.

### Profile Upload

2. Users will be able to upload their profile pictures. This is done by asking user permissions to accress mobile camera and storage.
The photo will be displayed for the user to see with the help of glide, where user can choose to upload to Firestore storage.

### Data Sorage

3. User data will be stored in Firestore storage which will be further used to authenticate their login.

### Weather Data Request

4. Users can request their weather information by sending their GPS location data through OpenWeatherMap API. The data received
 is displayed into a cardView. User can choose to save their location data to Firestore where it will be shared with other App users.
 
### Weather History

5. Users can request history of weather data saved by him or other users. The saved data will be displayed via recyclerView.

