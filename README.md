<img src="https://github.com/adrianseraspi12/Racoon/blob/master/app/src/main/res/mipmap-xxxhdpi/racoon_launcher.png" alt="logo" />

# Racoon
A firebase demo chat app

Link to google playstore https://play.google.com/store/apps/details?id=com.suzei.racoon

## Features
1. Show list of public chats that you can join
2. Create Private chat for one on one conversation
3. Show list of alerts
4. You can add friend and create a group with your friends

## Screenshot
User login/register to enter the app

<img src="https://github.com/adrianseraspi12/Racoon/blob/master/Screenshot/login_screen.jpg" alt="login" height="300px" />

Main screen of the app. Shows the list of World chats, Private/Group Chats, Friends, Alerts.


<img src="https://github.com/adrianseraspi12/Racoon/blob/master/Screenshot/public_chat.jpg" alt="login" height="300px" />

Visit the profile of the user

<img src="https://github.com/adrianseraspi12/Racoon/blob/master/Screenshot/visit_profile.jpg" alt="profile" height="300px" />

Send pictures, emojis, text in the chatroom

<img src="https://github.com/adrianseraspi12/Racoon/blob/master/Screenshot/chats_with_emojis.jpg" alt="profile" height="300px" />

## Library used
- support library
- recyclerrefreshlayout (dinuscxj)
- roundedimageview (makeramen)
- ahbottomnavigation (aurelhubert)
- firebase database
- firebase auth
- firebase storage
- firebase-ads
- firebase-ui-database
- crashlytics
- multidex
- butterknife
- timber
- picasso
- compressor (zelory)
- emoji-one (vanniktech)
- joda

## Architecture Pattern
MVP (Model, View, Presenter)

<img src="https://github.com/adrianseraspi12/Racoon/blob/master/mvp.png" alt="profile" height="300px" />

Model - consist of read/write functionalities

View - Displaying the data from the Presenter

Presenter - The bridge for the View to communicate to Model. Act as a controller.

## Dummy account for testing:
Email - user123@gmail.com

Pass - 123456

Email - user456@gmail.com

Pass - qwerty


Email - user098@gmail.com

Pass - 123asd

## References/Teachers
1. https://antonioleiva.com/mvp-android/
2. https://firebase.google.com/docs/

### Note
I am a beginner in the android and architectural pattern and I'm just curios about the MVP 
so I tried it. Feedbacks and suggestions would be a big help for me to improve my career in android dev.
