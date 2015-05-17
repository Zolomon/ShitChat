### Welcome to ShitChat :shit:
![ShitChat](https://raw.githubusercontent.com/Zolomon/ShitChat/master/images/shitchat.gif)

**ShitChat** is a cross-platform multithreaded multiuser client-server chat using functional style programming while adhering to the reactive manifesto to the best of its capabilities. ****cough cough****

**ShitChat** was written by: [@HannaBjorgvins](http://github.com/HannaBjorgvins) :neckbeard:, [@3amice](http://github.com/3amice) :moneybag::penguin::tophat::smiley_cat:, [@RobinSeibold](http://github.com/RobinSeibold) :dolphin::whale2::camel:, [@Zolomon](http://github.com/Zolomon) :coffee::feelsgood::squirrel::doughnut:.

### Features
*  **Accounts:** create an account, login to an existing account!
*  **Profiles:** create a unique profile and read the profiles of your friends!
*  **Channels:** create a channel, join an existing one, send messages to a whole channel!
*  **Buddies:** see a list of connected buddies!
*  **Whisper:** send private messages to your special friends!
*  **ShitChat:** just ordinary chat!

#### How to install?
##### Dependencies
*  Gradle
*  Git
*  Java 8

```
$ git clone https://github.com/Zolomon/ShitChat.git
$ gradle uberjar_server # to build the server
$ gradle uberjar_client # to build the client
$ java -jar build/libs/server.jar 8888 # run server on port 8888
$ java -jar build/libs/client.jar 192.168.0.150 8888 # connect client to server on 192.168.0.150 on port 8888.
```
Start :shit: chatting! 

#### Available commands:

*  `/help` - list all available commands.
*  `/quit` - quit the chat client.
*  `/login <username>` - login with the username provided.
*  `/whisper <recipient> <message>` - send a private message to recipient.
*  `/join <channel>` - join a channel.
*  `/leave <channel>` - leave the channel.
*  `/msg <channel> <message>` - send message to all users in channel. 
*  `/finger <username>` - view the profile of a user.
*  `/editprofile <name> <title> <location> <avatar URI>` - create/edit profile.

### Design
**ShitChat** consists of two parts. One server, and one client. They communicate via a text-based protocol over TCP. Each connection is handled separately on its own thread, and full duplex communication is enabled due to two extra threads created to handle input and output respectively for each connection.

### Evaluation

A list of potential features was created early on in the beginning of the project. Most of these features did not end up in the end product. Architecting the protocol was an interesting and fun task. The decision was made to use a text-based protocol over a binary protocol, or a third-party serialization library like Google's Protobufs.
