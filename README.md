# MyRuns
An Android based fitness app that captures and record your exercise.

MyRuns consists of two big components: MyRunsAndroid and MyRunsServer. The former is the client app running on Android smartphone, whereas the latter is the server part based on Google App Engine. 

MyRunsAndroid: 
The client app has three modes:
(1) Manual mode: user can manually input the exercise record, including the exercise type(running, swimming, etc. ), duration, speed, distance, etc., into the app.
(2) GPS mode: a Google map is displayed on the screen and on the map a trajectory will keep updated while the user is moving. After user stops exercising, the trajectory will be stored in the phone with other informations, like time, duration, etc.
(3) Automatic mode: on the top of GPS mode, automatic mode could furthermore infer user's exercise type automatically. For example, it can tell whether the user is running or walking. This is acheived through machine learning technology based on the data collected by the sensors on the phone.
All exercise entries will be stored in the phone and can be reviewed anytime thereafter.

MyRunsServer:
Each of the exercise entries will at the mean time be uploaded to the server automatically, where any exercise entry can be viewed or deleted. Once an entry is deleted on the server, server will notify the specific smart phone who, in turn, will delete that entry automatically. 
