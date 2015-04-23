#!/bin/bash
#
# if the release already exists in place, we can remove and re-install with "--force"
#

[ -f `dirname $0`/../src/main/bash/std.shlib ] && \
. `dirname $0`/../src/main/bash/std.shlib || \
{ echo `dirname $0`"/../src/main/bash/std.shlib not found" 1>&2; exit 1; }

if [ "${RUNTIME_DIR}" = "" ]; then
    echo "'RUNTIME_DIR' environment variable not set. Set it and try again" 1>&2;
    exit 1
fi

if [ ! -d ${RUNTIME_DIR} ]; then
    echo "'RUNTIME_DIR' ${RUNTIME_DIR} does not exist" 1>&2;
    exit 2
fi

reldir=`dirname $0`

function main()
{
    local version;
    local force=false;

    while [ "$1" != "" ]; do
        if [ "$1" = "--force" ]; then
            force=true
        elif [ "${version}" = "" ]; then
            version=$1
        fi
        shift;
    done

    [ "${version}" = "" ] && fail "must specifiy version to install"

    project_home=`dirname $0`/..

    rf=${project_home}/target/gca-${version}.zip

    [ ! -f ${rf} ] && fail "no release bundle file found ${rf}"

    if [ -d ${RUNTIME_DIR}/gca-${version} ]; then
        if ${force}; then
            rm -r ${RUNTIME_DIR}/gca-${version} && \
                info "gca-${version} was installed already, but --force was used so it was removed" || \
                fail "failed to remove existing ${RUNTIME_DIR}/gca-${version}"
        else
            fail "gca-${version} already installed in ${RUNTIME_DIR} and --force flag was not used"
        fi
    fi

    unzip ${rf} -d ${RUNTIME_DIR} || fail "failed to unzip into ${RUNTIME_DIR}"

    if [ -h ${RUNTIME_DIR}/gca ]; then
        rm ${RUNTIME_DIR}/gca && info "existing link ${RUNTIME_DIR}/gca was deleted" || \
            fail "failed to delete the symbolic link ${RUNTIME_DIR}/gca"
    fi

    (cd ${RUNTIME_DIR}; ln -s gca-${version} gca) && info "installation successful"
}

main $@;



