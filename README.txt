


mvn clean package; ant; cp target/gc-analyzer.zip /c/tmp; (cd /c/tmp; rm -rf gc-analyzer; unzip gc-analyzer.zip; rm gc-analyzer.zip)


    mvn -Dmaven.surefire.debug="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=12348" test -Dtest=CollectedTest


To install on da25: ------------------------------------------------------------------------------------------------------------------------

mvn clean package; ant; scp target/gc-analyzer.zip jmon@10.153.164.97:~; ssh jmon@10.153.164.97 rm -r \~/gc-analyzer\; unzip gc-analyzer.zip\; chmod a+x gc-analyzer/gc-analyzer\; rm gc-analyzer.zip


To create a release bundle for distribution -----------------------------------------------------------------------------------------------

Modify Configuration.VERSION to match the pom.xml version.

Then, run:

    ./util/release.sh [--no-tests]

This will do the maven build, zip the release bundle and place it in ./target.

To install a new version in "/c/projects/runtime" ------------------------------------------------------------------------------------------

Run:

    ./util/install-locally.sh <version-to-install>

This will do the maven build, zip the release bundle and install in the right location.


After that you should be able to:

    gca version

and get the new version.
