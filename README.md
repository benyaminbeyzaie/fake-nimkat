# fake-nimkat

Fake Nimkat is an android app mostly inspired by [Nimkat](https://cafebazaar.ir/app/ir.hezardastan.nimkat) .

Nimkat is a free, educational-purpose application, originally written in Flutter.Students of 1st grade up to 12 grade can use this app to ask their educational questions and verified educators answer these questions.(There exists another app for educators. [Nimkat](https://cafebazaar.ir/app/ir.hezardastan.nimkat) is being used as client for students.)

All you have to do to use Nimkat(as a student) is:
1. Download and install [Nimkat](https://cafebazaar.ir/app/ir.hezardastan.nimkat)
2. Create a free account. Enter your mobile phone, then you receive a verification code via sms. Use this verification code to enter the app.
    * If you login to your account for the very first time, it will ask for your name and grade.
3. Ask your question!
    * You can take a picure with built-in camera inside the app.
    * You can take a picture with original Camera app of your phone, or select a picture from gallery.
    * You can write a text question.
4. Send your question.
    * The app will show you simillar questions if there exists. you can check these simillar question and find your answer.
    * If your question was not in simillar questions list, you can send your question for educators to answer.
5. When an educator answers your question, the app will notify you with a notification.

  
As the final project of our Mobile programming course(at Sharif University of Technology), we decided to recreate a simple version of Nimkat, using Android. We implement Almost every important feature of [Nimkat](https://cafebazaar.ir/app/ir.hezardastan.nimkat).Just some animations are not implemented. We also add "Delete Account" feature which is not presented by [Nimkat](https://cafebazaar.ir/app/ir.hezardastan.nimkat) yet.

Here are some screenshots from our app:


Startup            |  Menu          | Dark Theme
:-------------------------:|:-------------------------:|:-------------------------:
![startup](/Screenshots/startup.png) |  ![menu](/Screenshots/drawer.png) |  ![menu](/Screenshots/darkTheme.png)



- We use kotlin as main programming language for our app.
- Project architecture is based on MVVM
- We use [jetpack compse toolkit](https://developer.android.com/jetpack/compose) for building UI.
- We use [firebase](https://firebase.google.com/)

# Contributors
Benyamin Beyzaie 98100356

Mohammad Ali Olama 98100497

Anita Alikhani 99107635
