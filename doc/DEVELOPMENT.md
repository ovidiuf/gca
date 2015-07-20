

# Release Procedure


* Make sure all tests pass


    mvn clean install


* Write down the number of tests, we'll use it in the commit message.

* Update version information in pom.xml

* Check in using the following pattern:


    git add ...
    git commit -m "release 1.2. <short summary of new features>. 327/0/0."

* Install locally


    ./install


* Verify the version of the newly installed release.


    gca version

* Increment version pom.xml to a "-SNAPSHOT" version.

* Check in with:


    git add ...
    git commit -m "starting ...-SNAPSHOT"





