# LoadApp
Third project in [Udacity Android Kotlin Developer Nanodegree (ND940)](https://www.udacity.com/course/android-kotlin-developer-nanodegree--nd940).

## Project Overview
In this project students will create an app to download a file from the Internet by clicking on a custom-built button where:
- width of the button gets animated from left to right
- text gets changed based on different states of the button
- circle gets be animated from 0 to 360 degrees

A notification will be sent once the download is complete. When a user clicks on the notification, the user lands on detail activity 
and the notification gets dismissed. In detail activity, the status of the download will be displayed and animated via MotionLayout upon opening the activity.

[The final look of the app](https://youtu.be/a2l2cuMWh20)

## Project Steps
1. Create a radio list of the following options where one of them can be selected for downloading:
      - https://github.com/bumptech/glide
      - https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter
      - https://github.com/square/retrofit
2. Create a custom loading button by extending View class and assigning custom attributes to it
3. Animate properties of the custom button once it’s clicked
4. Add the custom button to the main screen, set on click listener and call download() function with selected Url
5. If there is no selected option, display a Toast to let the user know to select one.
6. Once the download is complete, send a notification with custom style and design
7. Add a button with action to the notification, that opens a detailed screen of a downloaded repository
8. Create the details screen and display the name of the repository and status of the download
9. Use declarative XML with motionLayout to coordinate animations across the views on the detail screen
10. Add a button to the detail screen to return back to the main screen.

[*Project Rubric*](https://review.udacity.com/#!/rubrics/2852/view)
