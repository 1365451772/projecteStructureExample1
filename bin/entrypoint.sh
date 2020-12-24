#!/bin/bash
set -eo pipefail
shopt -s nullglob

if [[ $# -eq 0 ]] || [[ "${1:0:1}" = '-' ]] ; then
  /data/app/start.sh "$@"
else
  exec "$@"
fi