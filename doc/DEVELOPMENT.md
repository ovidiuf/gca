

# Release Procedure


* Make sure all tests pass


    mvn clean install


* Write down the number of tests, we'll use it in the commit message.

* Update version information in pom.xml

* Check in using the following pattern:


    git add ...
    git commit -m "release 1.2. 327/0/0."

* Create the artifact and save target/gca-&lt;version&gt;.zip on the outside target system. It can be installed by unzipping in /usr/local and linking.


    ./util/release.sh

* You can install locally with:


    ./util/install-locally.sh [--force] <version>


* Increment version pom.xml to a "-SNAPSHOT" version.

* Check in with:

    git add ...
    git commit -m "preparing new version"





