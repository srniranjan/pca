# Welcome to AskSpock PCA workshop setup

This setup is necessary to ensure you can code along during the workshop and get the most out of it.
You will also be writing code, during the workshop, in the same project folder.

You can follow the workshop in 2 programming languages. Please follow the setup instructions for the programming language of your choice.
If at anytime you are stuck, you can reach out to one of our experts and chat with them at real time here: https://www.askspock.co/

# Java
- Change directory to the `java` folder
- Make sure you have Maven installed and availble on your PATH
- Change directory to the Maven project folder:  `askspock-pca`
- Execute `mvn compile` and ensure it succeeds
- Execute `mvn package` and ensure it succeeds
- Execute `mvn exec:java -D exec.mainClass=com.askspock.pca.Test`
- Ensure the following is printed on your screen:
> All dependencies have been met!

Not seeing the success message but some error? One of our experts can help you. Just click here: https://www.askspock.co/

# Python
- Change directory to the `python` folder
- Make sure you have pip installed and availble on your PATH
- Execute `pip install -r requirements.txt` and ensure it succeeds
- Execute `python3 test.py`
- Ensure the following is printed on your screen:
> All dependencies have been met!

Not seeing the success message but some error? One of our experts can help you. Just click here: https://www.askspock.co/