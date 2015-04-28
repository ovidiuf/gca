#!/bin/bash

[ -f `dirname $0`/../src/main/bash/std.shlib ] && \
. `dirname $0`/../src/main/bash/std.shlib || \
{ echo `dirname $0`"/../src/main/bash/std.shlib not found" 1>&2; exit 1; }

reldir=`dirname $0`

# stouts the release bundle path
function create_release_bundle()
{
    version=`get_version` || fail "failed to get project version"

    local project_home=${reldir}/..

    local rd="${project_home}/target/gca-${version}"

    [ -d ${rd} ] && fail "release directory ${rd} exists, delete it manually and try again"

    local rf="${rd}/gca-${version}.zip"

    [ -f ${rf} ] && fail "release file ${rf} exists, delete it manually and try again"

    [ "${M2}" = "" ] && fail "M2 environment variable not defined, cannot find the local Maven repository"

    mkdir -p ${rd}/bin || fail "failed to create ${rd}/bin"
    mkdir -p ${rd}/lib || fail "failed to create ${rd}/lib"

    cp ${project_home}/src/main/bash/gca ${rd}/bin || fail "failed to copy ${project_home}/src/main/bash/std.shlib to ${rd}/lib"
    chmod a+x ${rd}/bin/gca || fail "failed to chmod a+x ${rd}/bin/gca"

    cp ${project_home}/src/main/bash/std.shlib ${rd}/lib || \
        fail "failed to copy ${project_home}/src/main/bash/std.shlib to ${rd}/lib"
    cp ${project_home}/src/main/resources/log4j.xml ${rd}/lib || \
        fail "failed to copy ${project_home}/src/main/resources/log4j.xml to ${rd}/lib"
    cp ${project_home}/target/gca.jar ${rd}/lib || \
        fail "failed to copy ${project_home}/target/gca.jar to ${rd}/lib"
    cp ${M2}/log4j/log4j/1.2.16/log4j-1.2.16.jar ${rd}/lib || \
        fail "failed to copy ${M2}/log4j/log4j/1.2.16/log4j-1.2.16.jar to ${rd}/lib"
    cp ${M2}/com/novaordis/series/series/1.3/series-1.3.jar ${rd}/lib || \
        fail "failed to copy ${M2}/com/novaordis/series/series/1.3-SNAPSHOT/series-1.3-SNAPSHOT.jar to ${rd}/lib"

    info ""
    info "zipping ${project_home}/target/gca-${version}.zip ..."

    (cd ${project_home}/target; zip -r ./gca-${version}.zip `basename ${rd}` 1>/dev/null) || fail "failed to create gca-${version}.zip"

    echo "${project_home}/target/gca-${version}.zip"
}

# stdouts the version
function get_version()
{
    local pom=`dirname $0`/../pom.xml

    [ -f ${pom} ] || fail "POM file ${pom} not found"

    local lines=14
    result=`head -n${lines} ${pom} | grep "^ *<version>.*</version>" | sed -e 's/^.*<version>\(.*\)<\/version>.*$/\1/'`

    [ "${result}" = "" ] && fail "not able to extract project version from the first ${lines} lines of ${pom}"

    echo "${result}"
}

function main()
{
    while [ "$1" != "" ]; do
        if [ "$1" = "--no-tests" ]; then
            export maven_test_config="-Dmaven.test.skip=true"
        fi
        shift;
    done

    (cd ${reldir}/..; mvn clean package ${maven_test_config}) || fail "failed to build the project"

    release_bundle=`create_release_bundle`

    if [ -f ${release_bundle} ]; then

        info ""
        info "SUCCESS, release bundle: ${release_bundle}"
        info ""

        version=`basename ${release_bundle} .zip` || fail "fail to read version"
        version=${version#gca-}

        info "to install it locally:"
        info ""

        info "./util/install-locally.sh --force ${version}"

    else
        fail "failure, cannot find the release bundle ${release_bundle}"
    fi
}

main $@;



