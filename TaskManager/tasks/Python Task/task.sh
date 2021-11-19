#!/bin/bash

echo "Python is starting..."

# Python must be invoked with unbuffered flag -u
# in order to read stdout and stderr properly.
# Otherwise, no data will be received until the
# data buffer is filled or flushed, or the python
# program terminates.
python2 -u clienttest.py