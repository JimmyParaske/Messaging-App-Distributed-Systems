# Messaging-App-Distributed-Systems

Assignment for course: "**Distributed Systems**"
<br>*Department of Computer Science, Aueb*

A simple local messaging app for Android.


### **How it works** 
* Firstly Java files Broker, Broker1 and Broker2 must be up and running listening on a port for users to send messages.<br>
When the application starts each user must enter a **username** and then press **login**. Each user **must** enter a username otherwise login cannot happen (username field must not be blank)<br>
The user can create a **new** topic or join an existing one, where everyone can enter and communicate via **text** or send **photos** and **videos**.
Those photos or videos are being sent to the other users Android device.
The app **splits** the photo/video into **smaller pieces** before sending to other user and then it **combines** the pieces back all togother, making the original photo/video.<br>
Each user can see the **history** of messages and who sent each message in a topic once he enters a specific topic.<br>
If the user wants to go **back**, then there is a button to go back to topic selection/creation, aka the **main page**.
Besides the available topics, the main page also provides a **logout button**, in case the user wants to logout.
If said button is pressed, then the user is **redirected back** to the login page where he must enter a username again to login.

